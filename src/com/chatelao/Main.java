package com.chatelao;

import com.chatelao.config.*;
import com.chatelao.data.DataProcessor;
import com.chatelao.export.ExcelExporter;
import com.chatelao.model.SheetData;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;

@Command(name = "oracle-to-excel", mixinStandardHelpOptions = true, version = "0.1.0",
        description = "Exports Oracle database queries to Excel workbooks.",
        footer = {
                "",
                "TOML Configuration Options:",
                "",
                "[database]",
                "  url                The JDBC connection URL for the Oracle database.",
                "  username           Database user account.",
                "  password           Password for the database user.",
                "",
                "[audit]",
                "  sheet              Name of the sheet where query execution details will be recorded.",
                "",
                "[[exports]]",
                "  filename           The name of the target Excel file.",
                "",
                "[[exports.sheets]]",
                "  name               The base name for the Excel sheet.",
                "  query              The SQL query to execute (or path to a .sql file).",
                "  partition_size     (Optional) Max rows per sheet. Splits into name_1, name_2, etc.",
                "  top_offset         (Optional) Number of empty rows at the top of the sheet.",
                "  left_offset        (Optional) Number of empty columns at the left of the sheet.",
                "  columns            (Optional) List of columns to include and their order.",
                "  sheetname_columns  (Optional) Columns used to dynamically name sheets.",
                "  filename_columns   (Optional) Columns used to dynamically name files.",
                "  column_colors      (Optional) Map of column names to hex background colors.",
                "  pivot_table        (Optional) If true, generates a companion sheet with a pivot table.",
                "  page_title         (Optional) If true, adds stylized title rows at the top."
        })
public class Main implements Callable<Integer> {

    @Option(names = {"-c", "--config"}, description = "Path to the TOML configuration file.", required = true)
    private Path configPath;

    @Option(names = {"--audit-sheet"}, description = "Name of the sheet to write audit information to.")
    private String auditSheetName;

    public static void main(String[] args) {
        int exitCode = execute(args);
        System.exit(exitCode);
    }

    public static int execute(String[] args) {
        return new CommandLine(new Main()).execute(args);
    }

    private SheetData createAuditSheet(String sheetName, List<AuditEntry> auditEntries) {
        List<String> columns = List.of("EXPORT_FILE", "SHEET_NAME", "QUERY", "STATUS", "ERROR_MESSAGE");
        List<List<Object>> rows = new ArrayList<>();
        for (AuditEntry entry : auditEntries) {
            rows.add(List.of(
                    entry.exportFilename != null ? entry.exportFilename : "",
                    entry.sheetName != null ? entry.sheetName : "",
                    entry.query != null ? entry.query : "",
                    entry.status != null ? entry.status : "",
                    entry.errorMessage != null ? entry.errorMessage : ""
            ));
        }
        return new SheetData(sheetName, columns, rows, null, null, 0, 0);
    }

    private static class AuditEntry {
        String exportFilename;
        String sheetName;
        String query;
        String status;
        String errorMessage;

        AuditEntry(String exportFilename, String sheetName, String query, String status, String errorMessage) {
            this.exportFilename = exportFilename;
            this.sheetName = sheetName;
            this.query = query;
            this.status = status;
            this.errorMessage = errorMessage;
        }
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("Loading configuration from: " + configPath);
        TomlConfigLoader loader = new TomlConfigLoader();
        Config config;
        try {
            config = loader.loadConfig(configPath);
            if (this.auditSheetName != null) {
                config.setAuditSheetName(this.auditSheetName);
            }
        } catch (IOException e) {
            System.err.println("Failed to load configuration: " + e.getMessage());
            return 1;
        }

        DatabaseConfig dbConfig = config.getDatabase();
        if (dbConfig == null) {
            System.err.println("Database configuration is missing.");
            return 1;
        }

        Properties props = new Properties();
        if (dbConfig.getUrl() != null) props.setProperty("url", dbConfig.getUrl());
        if (dbConfig.getUsername() != null) props.setProperty("user", dbConfig.getUsername());
        if (dbConfig.getPassword() != null) props.setProperty("password", dbConfig.getPassword());

        DataProcessor dataProcessor = new DataProcessor();
        ExcelExporter exporter = new ExcelExporter();

        if (config.getExports() == null || config.getExports().isEmpty()) {
            System.out.println("No exports defined in the configuration.");
            return 0;
        }

        List<AuditEntry> auditEntries = new ArrayList<>();
        boolean anyError = false;

        for (ExportConfig export : config.getExports()) {
            System.out.println("Processing export: " + export.getFilename());
            List<SheetData> allSheetsData = new ArrayList<>();
            if (export.getSheets() == null || export.getSheets().isEmpty()) {
                System.out.println("  No sheets defined for export: " + export.getFilename());
                continue;
            }

            try (Connection conn = DriverManager.getConnection(dbConfig.getUrl(), props)) {
                for (SheetConfig sheetConfig : export.getSheets()) {
                    System.out.println("  Executing query for sheet: " + sheetConfig.getName());
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery(sheetConfig.getQuery())) {

                        List<SheetData> dataList = dataProcessor.processData(rs, sheetConfig);
                        allSheetsData.addAll(dataList);
                        auditEntries.add(new AuditEntry(export.getFilename(), sheetConfig.getName(), sheetConfig.getQuery(), "SUCCESS", null));
                    } catch (SQLException e) {
                        System.err.println("  Error executing query for sheet " + sheetConfig.getName() + ": " + e.getMessage());
                        auditEntries.add(new AuditEntry(export.getFilename(), sheetConfig.getName(), sheetConfig.getQuery(), "FAILED", e.getMessage()));
                        anyError = true;
                        if (config.getAuditSheetName() == null) {
                            return 1;
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("  Database connection error: " + e.getMessage());
                return 1;
            }

            // Group by target filename
            Map<String, List<SheetData>> fileGroups = new LinkedHashMap<>();
            if (allSheetsData.isEmpty() && config.getAuditSheetName() != null) {
                fileGroups.put(export.getFilename(), new ArrayList<>());
            }
            for (SheetData sd : allSheetsData) {
                String targetFilename = sd.getTargetFileName();
                if (targetFilename == null || targetFilename.isEmpty()) {
                    targetFilename = export.getFilename();
                } else {
                    // Inject the category into the base filename if it's not just the category
                    String base = export.getFilename();
                    int lastDot = base.lastIndexOf('.');
                    if (lastDot != -1) {
                        targetFilename = base.substring(0, lastDot) + "_" + targetFilename + base.substring(lastDot);
                    } else {
                        targetFilename = base + "_" + targetFilename;
                    }
                }
                fileGroups.computeIfAbsent(targetFilename, k -> new ArrayList<>()).add(sd);
            }

            for (Map.Entry<String, List<SheetData>> entry : fileGroups.entrySet()) {
                String filename = entry.getKey();
                List<SheetData> sheets = entry.getValue();

                if (config.getAuditSheetName() != null) {
                    sheets.add(createAuditSheet(config.getAuditSheetName(), auditEntries));
                }

                try {
                    exporter.export(sheets, Path.of(filename));
                    System.out.println("  Export completed: " + filename);
                } catch (IOException e) {
                    System.err.println("  Error exporting to " + filename + ": " + e.getMessage());
                    return 1;
                }
            }
        }

        if (anyError) {
            System.err.println("Some exports failed. See audit for details.");
            return 1;
        }

        System.out.println("All exports completed successfully.");
        return 0;
    }
}
