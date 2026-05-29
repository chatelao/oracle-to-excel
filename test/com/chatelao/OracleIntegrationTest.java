package com.chatelao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OracleIntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    @EnabledIfSystemProperty(named = "oracle.test.enabled", matches = "true")
    public void testWithRealOracle() throws Exception {
        String url = System.getProperty("oracle.test.url", "jdbc:oracle:thin:@127.0.0.1:1521/FREEPDB1");
        String user = System.getProperty("oracle.test.user", "system");
        String password = System.getProperty("oracle.test.password", "password");

        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", password);

        try (Connection conn = DriverManager.getConnection(url, props)) {
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM test_data ORDER BY id")) {

                assertTrue(rs.next());
                assertEquals(1, rs.getInt("id"));
                assertEquals("Test Item 1", rs.getString("name"));

                assertTrue(rs.next());
                assertEquals(2, rs.getInt("id"));
                assertEquals("Test Item 2", rs.getString("name"));

                assertFalse(rs.next());
            }

            Path configPath = tempDir.resolve("oracle_test_config.toml");
            Path exportPath = tempDir.resolve("oracle_export.xlsx");

            String configContent = "[database]\n" +
                    "url = \"" + url + "\"\n" +
                    "username = \"" + user + "\"\n" +
                    "password = \"" + password + "\"\n" +
                    "\n" +
                    "[[exports]]\n" +
                    "filename = \"" + exportPath.toString().replace("\\", "\\\\") + "\"\n" +
                    "sheets = [\n" +
                    "    { name = \"OracleData\", query = \"SELECT * FROM test_data\" },\n" +
                    "    { name = \"LargeOracleData\", query = \"SELECT * FROM large_test_data\" }\n" +
                    "]\n";
            Files.writeString(configPath, configContent);

            int exitCode = Main.execute(new String[]{"-c", configPath.toString()});
            assertEquals(0, exitCode, "Application should exit with 0 when connecting to Oracle");

            assertTrue(Files.exists(exportPath), "Export file should exist at " + exportPath);
        }
    }
}
