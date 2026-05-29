package com.chatelao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainIntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    public void testMainOrchestration() throws Exception {
        String dbUrl = "jdbc:h2:mem:integration_test;DB_CLOSE_DELAY=-1";
        try (Connection conn = DriverManager.getConnection(dbUrl, "sa", "")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE test_table1 (id INT, name VARCHAR(255))");
                stmt.execute("INSERT INTO test_table1 VALUES (1, 'Alice')");
                stmt.execute("CREATE TABLE test_table2 (id INT, city VARCHAR(255))");
                stmt.execute("INSERT INTO test_table2 VALUES (1, 'New York')");
            }

            Path configPath = tempDir.resolve("test_integration_config.toml");
            Path exportPath = tempDir.resolve("integration_export.xlsx");

            String configContent = "[database]\n" +
                    "url = \"" + dbUrl + "\"\n" +
                    "username = \"sa\"\n" +
                    "password = \"\"\n" +
                    "\n" +
                    "[[exports]]\n" +
                    "filename = \"" + exportPath.toString().replace("\\", "\\\\") + "\"\n" +
                    "sheets = [\n" +
                    "    { name = \"Sheet1\", query = \"SELECT * FROM test_table1\" },\n" +
                    "    { name = \"Sheet2\", query = \"SELECT * FROM test_table2\" }\n" +
                    "]\n";
            Files.writeString(configPath, configContent);

            int exitCode = Main.execute(new String[]{"-c", configPath.toString()});
            assertEquals(0, exitCode, "Application should exit with 0");

            assertTrue(Files.exists(exportPath), "Export file should exist at " + exportPath);
        }
    }

    @Test
    public void testLargeDatasetExport() throws Exception {
        String dbUrl = "jdbc:h2:mem:large_test;DB_CLOSE_DELAY=-1";
        try (Connection conn = DriverManager.getConnection(dbUrl, "sa", "")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE large_test_table (id INT PRIMARY KEY, category VARCHAR(255), val1 DOUBLE, val2 VARCHAR(255), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
                for (int i = 1; i <= 1000; i++) {
                    stmt.addBatch(String.format(Locale.ROOT, "INSERT INTO large_test_table (id, category, val1, val2) VALUES (%d, 'Category %d', %.2f, 'Value %d')", i, i % 10, i * 1.5, i));
                }
                stmt.executeBatch();
            }

            Path configPath = tempDir.resolve("large_test_config.toml");
            Path exportPath = tempDir.resolve("large_export.xlsx");

            String configContent = "[database]\n" +
                    "url = \"" + dbUrl + "\"\n" +
                    "username = \"sa\"\n" +
                    "password = \"\"\n" +
                    "\n" +
                    "[[exports]]\n" +
                    "filename = \"" + exportPath.toString().replace("\\", "\\\\") + "\"\n" +
                    "sheets = [\n" +
                    "    { name = \"LargeSheet\", query = \"SELECT * FROM large_test_table\" }\n" +
                    "]\n";
            Files.writeString(configPath, configContent);

            int exitCode = Main.execute(new String[]{"-c", configPath.toString()});
            assertEquals(0, exitCode, "Application should exit with 0 for large dataset");

            assertTrue(Files.exists(exportPath), "Large export file should exist at " + exportPath);
        }
    }

    @Test
    public void testColumnMappingAndSplitting() throws Exception {
        String dbUrl = "jdbc:h2:mem:mapping_test;DB_CLOSE_DELAY=-1";
        try (Connection conn = DriverManager.getConnection(dbUrl, "sa", "")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE map_test (id INT, name VARCHAR(255), country VARCHAR(255), region VARCHAR(255))");
                stmt.execute("INSERT INTO map_test VALUES (1, 'Alice', 'USA', 'West')");
                stmt.execute("INSERT INTO map_test VALUES (2, 'Bob', 'USA', 'East')");
                stmt.execute("INSERT INTO map_test VALUES (3, 'Charlie', 'UK', 'London')");
            }

            Path configPath = tempDir.resolve("mapping_config.toml");
            Path baseExportPath = tempDir.resolve("report.xlsx");

            String configContent = "[database]\n" +
                    "url = \"" + dbUrl + "\"\n" +
                    "username = \"sa\"\n" +
                    "password = \"\"\n" +
                    "\n" +
                    "[[exports]]\n" +
                    "filename = \"" + baseExportPath.toString().replace("\\", "\\\\") + "\"\n" +
                    "sheets = [\n" +
                    "    { name = \"Data\", query = \"SELECT * FROM map_test\", columns = [\"NAME\", \"ID\"], filename_columns = [\"COUNTRY\"], name_columns = [\"REGION\"] }\n" +
                    "]\n";
            Files.writeString(configPath, configContent);

            int exitCode = Main.execute(new String[]{"-c", configPath.toString()});
            assertEquals(0, exitCode);

            // Expected files: report_USA.xlsx, report_UK.xlsx
            Path usaExport = tempDir.resolve("report_USA.xlsx");
            Path ukExport = tempDir.resolve("report_UK.xlsx");

            assertTrue(Files.exists(usaExport), "USA export should exist");
            assertTrue(Files.exists(ukExport), "UK export should exist");
        }
    }
}
