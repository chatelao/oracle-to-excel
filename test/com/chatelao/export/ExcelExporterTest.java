package com.chatelao.export;

import com.chatelao.model.SheetData;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
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
                Arrays.asList(1, "Alice"),
                Arrays.asList(2, "Bob")
        );
        SheetData sheetData = new SheetData("Sheet1", columnNames, rows);
        Map<String, String> columnColors = new HashMap<>();
        columnColors.put("ID", "#FF0000");
        sheetData.setColumnColors(columnColors);

        ExcelExporter exporter = new ExcelExporter();
        exporter.export(Arrays.asList(sheetData), outputPath);

        assertTrue(outputPath.toFile().exists());

        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(outputPath.toFile()))) {
            assertEquals(1, workbook.getNumberOfSheets());
            assertEquals("Sheet1", workbook.getSheetName(0));
            assertEquals("ID", workbook.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
            assertEquals(1.0, workbook.getSheetAt(0).getRow(1).getCell(0).getNumericCellValue());
            assertEquals("Alice", workbook.getSheetAt(0).getRow(1).getCell(1).getStringCellValue());

            // Check color
            XSSFCellStyle style = (XSSFCellStyle) workbook.getSheetAt(0).getRow(1).getCell(0).getCellStyle();
            assertEquals(FillPatternType.SOLID_FOREGROUND, style.getFillPattern());
            XSSFColor color = style.getFillForegroundColorColor();
            byte[] rgb = color.getRGB();
            assertEquals((byte) 0xFF, rgb[0]);
            assertEquals((byte) 0x00, rgb[1]);
            assertEquals((byte) 0x00, rgb[2]);

            // Check other column has no color (or at least not this color)
            XSSFCellStyle style2 = (XSSFCellStyle) workbook.getSheetAt(0).getRow(1).getCell(1).getCellStyle();
            assertNotEquals(FillPatternType.SOLID_FOREGROUND, style2.getFillPattern());
        }
    }
}
