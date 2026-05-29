package com.chatelao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
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
}
