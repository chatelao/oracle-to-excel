package com.chatelao.export;

import com.chatelao.model.SheetData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ExcelExporterTest {

    @TempDir
    Path tempDir;

    @Test
    public void testExport() throws Exception {
        Path outputPath = tempDir.resolve("test_export.xlsx");

        List<String> columnNames = Arrays.asList("ID", "NAME");
        List<List<Object>> rows = Arrays.asList(
                Arrays.asList(1, "Alice with a very long name that should trigger auto-fit"),
                Arrays.asList(2, "Bob")
        );
        SheetData sheetData = new SheetData("Sheet1", columnNames, rows);

        ExcelExporter exporter = new ExcelExporter();
        exporter.export(Arrays.asList(sheetData), outputPath);

        assertTrue(outputPath.toFile().exists());

        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(outputPath.toFile()))) {
            assertEquals(1, workbook.getNumberOfSheets());
            Sheet sheet = workbook.getSheetAt(0);
            assertEquals("Sheet1", sheet.getSheetName());

            // Verify header bold
            Cell headerCell = sheet.getRow(0).getCell(0);
            assertEquals("ID", headerCell.getStringCellValue());
            int fontIndex = headerCell.getCellStyle().getFontIndex();
            Font font = workbook.getFontAt(fontIndex);
            assertTrue(font.getBold(), "Header font should be bold");

            // Verify auto filter
            assertTrue(((XSSFSheet)sheet).getCTWorksheet().isSetAutoFilter(), "Auto filter should be enabled");

            assertEquals(1.0, sheet.getRow(1).getCell(0).getNumericCellValue());
            assertEquals("Alice with a very long name that should trigger auto-fit", sheet.getRow(1).getCell(1).getStringCellValue());

            // Verify auto-fit: column 1 should be wider than column 0
            assertTrue(sheet.getColumnWidth(1) > sheet.getColumnWidth(0), "Column 1 should be wider than column 0 due to auto-fit");
        }
    }

    @Test
    public void testExportWithColors() throws Exception {
        Path outputPath = tempDir.resolve("test_export_colors.xlsx");

        List<String> columnNames = Arrays.asList("ID", "NAME");
        List<List<Object>> rows = Arrays.asList(
                Arrays.asList(1, "Alice")
        );
        Map<String, String> columnColors = new HashMap<>();
        columnColors.put("ID", "#FF0000"); // Red

        SheetData sheetData = new SheetData("Sheet1", columnNames, rows, null, columnColors);

        ExcelExporter exporter = new ExcelExporter();
        exporter.export(Arrays.asList(sheetData), outputPath);

        assertTrue(outputPath.toFile().exists());

        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(outputPath.toFile()))) {
            Sheet sheet = workbook.getSheetAt(0);
            Cell cell = sheet.getRow(1).getCell(0); // ID column

            CellStyle style = cell.getCellStyle();
            assertEquals(FillPatternType.SOLID_FOREGROUND, style.getFillPattern());

            XSSFColor color = (XSSFColor) style.getFillForegroundColorColor();
            assertNotNull(color);
            assertEquals("FFFF0000", color.getARGBHex());
        }
    }

    @Test
    public void testExportWithColorsCaseInsensitive() throws Exception {
        Path outputPath = tempDir.resolve("test_export_colors_case.xlsx");

        List<String> columnNames = Arrays.asList("id", "name");
        List<List<Object>> rows = Arrays.asList(
                Arrays.asList(1, "Alice")
        );
        Map<String, String> columnColors = new HashMap<>();
        columnColors.put("ID", "#00FF00"); // Green

        SheetData sheetData = new SheetData("Sheet1", columnNames, rows, null, columnColors);

        ExcelExporter exporter = new ExcelExporter();
        exporter.export(Arrays.asList(sheetData), outputPath);

        assertTrue(outputPath.toFile().exists());

        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(outputPath.toFile()))) {
            Sheet sheet = workbook.getSheetAt(0);
            Cell cell = sheet.getRow(1).getCell(0); // id column

            CellStyle style = cell.getCellStyle();
            assertEquals(FillPatternType.SOLID_FOREGROUND, style.getFillPattern());

            XSSFColor color = (XSSFColor) style.getFillForegroundColorColor();
            assertNotNull(color);
            assertEquals("FF00FF00", color.getARGBHex());
        }
    }

    @Test
    public void testExportWithMargins() throws Exception {
        Path outputPath = tempDir.resolve("test_export_margins.xlsx");

        List<String> columnNames = Arrays.asList("ID", "NAME");
        List<List<Object>> rows = Arrays.asList(
                Arrays.asList(1, "Alice"),
                Arrays.asList(2, "Bob")
        );
        // marginTop = 2, marginLeft = 3
        SheetData sheetData = new SheetData("Sheet1", columnNames, rows, null, null, 2, 3);

        ExcelExporter exporter = new ExcelExporter();
        exporter.export(Arrays.asList(sheetData), outputPath);

        assertTrue(outputPath.toFile().exists());

        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(outputPath.toFile()))) {
            Sheet sheet = workbook.getSheetAt(0);
            // Header should be at row 2, col 3
            Row headerRow = sheet.getRow(2);
            assertNotNull(headerRow, "Header row at index 2 should not be null");
            assertEquals("ID", headerRow.getCell(3).getStringCellValue());
            assertEquals("NAME", headerRow.getCell(4).getStringCellValue());

            // Data should be at row 3, col 3
            Row dataRow1 = sheet.getRow(3);
            assertNotNull(dataRow1, "Data row 1 at index 3 should not be null");
            assertEquals(1.0, dataRow1.getCell(3).getNumericCellValue());
            assertEquals("Alice", dataRow1.getCell(4).getStringCellValue());
        }
    }
}
