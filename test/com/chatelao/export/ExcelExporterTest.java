package com.chatelao.export;

import com.chatelao.model.SheetData;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
