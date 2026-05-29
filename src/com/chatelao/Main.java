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
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

@Command(name = "oracle-to-excel", mixinStandardHelpOptions = true, version = "0.1.0",
        description = "Exports Oracle database queries to Excel workbooks.")
public class Main implements Callable<Integer> {

    @Option(names = {"-c", "--config"}, description = "Path to the TOML configuration file.", required = true)
    private Path configPath;

    public static void main(String[] args) {
        int exitCode = execute(args);
        System.exit(exitCode);
    }

    public static int execute(String[] args) {
        return new CommandLine(new Main()).execute(args);
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("Loading configuration from: " + configPath);
        TomlConfigLoader loader = new TomlConfigLoader();
        Config config;
        try {
            config = loader.loadConfig(configPath);
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

        for (ExportConfig export : config.getExports()) {
            System.out.println("Processing export: " + export.getFilename());
            List<SheetData> sheets = new ArrayList<>();
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
                        sheets.addAll(dataList);
                    } catch (SQLException e) {
                        System.err.println("  Error executing query for sheet " + sheetConfig.getName() + ": " + e.getMessage());
                        return 1;
                    }
                }
            } catch (SQLException e) {
                System.err.println("  Database connection error: " + e.getMessage());
                return 1;
            }

            try {
                exporter.export(sheets, Path.of(export.getFilename()));
                System.out.println("  Export completed: " + export.getFilename());
            } catch (IOException e) {
                System.err.println("  Error exporting to " + export.getFilename() + ": " + e.getMessage());
                return 1;
            }
        }

        System.out.println("All exports completed successfully.");
        return 0;
    }
}
