package com.chatelao;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DynamicNamingIntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    public void testDynamicNamingAndConsolidation() throws Exception {
        String dbUrl = "jdbc:h2:mem:dynamic_test;DB_CLOSE_DELAY=-1";
        try (Connection conn = DriverManager.getConnection(dbUrl, "sa", "")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE source_data (id INT, category VARCHAR(255), target_file VARCHAR(255), val INT)");
                stmt.execute("INSERT INTO source_data VALUES (1, 'Cat1', 'file1.xlsx', 10)");
                stmt.execute("INSERT INTO source_data VALUES (2, 'Cat1', 'file1.xlsx', 20)");
                stmt.execute("INSERT INTO source_data VALUES (3, 'Cat2', 'file1.xlsx', 30)");
                stmt.execute("INSERT INTO source_data VALUES (4, 'Cat3', 'file2.xlsx', 40)");

                stmt.execute("CREATE TABLE extra_data (id INT, info VARCHAR(255))");
                stmt.execute("INSERT INTO extra_data VALUES (100, 'Extra Info')");
            }

            Path configPath = tempDir.resolve("dynamic_config.toml");
            Path file1Path = tempDir.resolve("file1.xlsx");
            Path file2Path = tempDir.resolve("file2.xlsx");

            // UC2: Split by category into sheets
            // UC3: Multiple queries into same file (file1.xlsx)
            // UC4: Multiple files (file1.xlsx, file2.xlsx)
            String configContent = "[database]\n" +
                    "url = \"" + dbUrl + "\"\n" +
                    "username = \"sa\"\n" +
                    "password = \"\"\n" +
                    "\n" +
                    "[[exports]]\n" +
                    "filename = \"" + file1Path.toString().replace("\\", "\\\\") + "\"\n" +
                    "sheets = [\n" +
                    "    { name = \"DynamicSheet\", query = \"SELECT * FROM source_data WHERE target_file = 'file1.xlsx'\", sheet_name_column = \"category\" },\n" +
                    "    { name = \"Consolidated\", query = \"SELECT id, info FROM extra_data\" }\n" +
                    "]\n" +
                    "\n" +
                    "[[exports]]\n" +
                    "sheets = [\n" +
                    "    { query = \"SELECT * FROM source_data WHERE category = 'Cat3'\", file_name_column = \"target_file\", name = \"FixedName\" }\n" +
                    "]\n";

            // Note: In the second export, we rely on file_name_column.
            // We need to handle the path properly. For simplicity in test, I'll update the data to contain absolute paths.
            try (Connection conn2 = DriverManager.getConnection(dbUrl, "sa", "")) {
                try (Statement stmt = conn2.createStatement()) {
                    stmt.execute("UPDATE source_data SET target_file = '" + file2Path.toString().replace("\\", "\\\\") + "' WHERE target_file = 'file2.xlsx'");
                }
            }

            Files.writeString(configPath, configContent);

            int exitCode = Main.execute(new String[]{"-c", configPath.toString()});
            assertEquals(0, exitCode, "Application should exit with 0");

            // Verify file1.xlsx
            assertTrue(Files.exists(file1Path), "file1.xlsx should exist");
            try (Workbook wb1 = new XSSFWorkbook(new FileInputStream(file1Path.toFile()))) {
                // Should have Cat1, Cat2, and Consolidated sheets
                assertEquals(3, wb1.getNumberOfSheets());
                assertTrue(hasSheet(wb1, "Cat1"));
                assertTrue(hasSheet(wb1, "Cat2"));
                assertTrue(hasSheet(wb1, "Consolidated"));

                assertEquals(2, wb1.getSheet("Cat1").getLastRowNum()); // Header + 2 rows
                assertEquals(1, wb1.getSheet("Cat2").getLastRowNum()); // Header + 1 row
                assertEquals(1, wb1.getSheet("Consolidated").getLastRowNum()); // Header + 1 row
            }

            // Verify file2.xlsx
            assertTrue(Files.exists(file2Path), "file2.xlsx should exist");
            try (Workbook wb2 = new XSSFWorkbook(new FileInputStream(file2Path.toFile()))) {
                assertEquals(1, wb2.getNumberOfSheets());
                assertEquals("FixedName", wb2.getSheetName(0));
                assertEquals(1, wb2.getSheetAt(0).getLastRowNum()); // Header + 1 row
            }
        }
    }

    @Test
    public void testAppendToSameSheet() throws Exception {
         String dbUrl = "jdbc:h2:mem:append_test;DB_CLOSE_DELAY=-1";
        try (Connection conn = DriverManager.getConnection(dbUrl, "sa", "")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE t1 (id INT, val VARCHAR(255))");
                stmt.execute("INSERT INTO t1 VALUES (1, 'A')");
                stmt.execute("CREATE TABLE t2 (id INT, val VARCHAR(255))");
                stmt.execute("INSERT INTO t2 VALUES (2, 'B')");
            }

            Path configPath = tempDir.resolve("append_config.toml");
            Path exportPath = tempDir.resolve("append_export.xlsx");

            String configContent = "[database]\n" +
                    "url = \"" + dbUrl + "\"\n" +
                    "username = \"sa\"\n" +
                    "password = \"\"\n" +
                    "\n" +
                    "[[exports]]\n" +
                    "filename = \"" + exportPath.toString().replace("\\", "\\\\") + "\"\n" +
                    "sheets = [\n" +
                    "    { name = \"SameSheet\", query = \"SELECT * FROM t1\" },\n" +
                    "    { name = \"SameSheet\", query = \"SELECT * FROM t2\" }\n" +
                    "]\n";
            Files.writeString(configPath, configContent);

            int exitCode = Main.execute(new String[]{"-c", configPath.toString()});
            assertEquals(0, exitCode);

            try (Workbook wb = new XSSFWorkbook(new FileInputStream(exportPath.toFile()))) {
                assertEquals(1, wb.getNumberOfSheets());
                assertEquals(2, wb.getSheet("SameSheet").getLastRowNum()); // Header + 2 rows
                assertEquals(1.0, wb.getSheet("SameSheet").getRow(1).getCell(0).getNumericCellValue());
                assertEquals(2.0, wb.getSheet("SameSheet").getRow(2).getCell(0).getNumericCellValue());
            }
        }
    }

    private boolean hasSheet(Workbook wb, String name) {
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            if (wb.getSheetName(i).equals(name)) return true;
        }
        return false;
    }
}
