package com.chatelao.export;

import com.chatelao.model.SheetData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WidthTest {

    @TempDir
    Path tempDir;

    @Test
    public void testColumnWidthWithFilter() throws Exception {
        Path outputPath = tempDir.resolve("width_test.xlsx");

        // Use a very short header and data to ensure autoSizeColumn makes it narrow
        List<String> columnNames = Arrays.asList("A");
        List<List<Object>> rows = Arrays.asList(
                Arrays.asList("1")
        );
        SheetData sheetData = new SheetData("Sheet1", columnNames, rows);

        ExcelExporter exporter = new ExcelExporter();
        exporter.export(Arrays.asList(sheetData), outputPath);

        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(outputPath.toFile()))) {
            Sheet sheet = workbook.getSheetAt(0);
            int width = sheet.getColumnWidth(0);

            // autoSizeColumn for "A" (bold) and "1" usually results in a width around 1000-1500.
            // The filter arrow needs about 700-800 extra units.
            // We want to ensure that the width is significantly larger than just the text.

            System.out.println("Column 0 width: " + width);

            // For "A" bold, autoSizeColumn gives ~1100-1200.
            // If we add 700, it should be > 1800.
            // Without the fix, it will be around 1100-1200.
            assertTrue(width > 1500, "Column width should be wide enough for the filter arrow. Current width: " + width);
        }
    }
}
