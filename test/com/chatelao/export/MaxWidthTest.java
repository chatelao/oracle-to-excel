package com.chatelao.export;

import com.chatelao.model.SheetData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class MaxWidthTest {

    @TempDir
    Path tempDir;

    @Test
    public void testMaximumColumnWidth() throws Exception {
        Path outputPath = tempDir.resolve("max_width_test.xlsx");

        // Create a very long string to trigger large column width
        StringBuilder longString = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            longString.append("W");
        }

        List<String> columnNames = Arrays.asList("LongColumn");
        List<List<Object>> rows = Arrays.asList(
                Arrays.asList(longString.toString())
        );
        SheetData sheetData = new SheetData("Sheet1", columnNames, rows);

        ExcelExporter exporter = new ExcelExporter();

        // This is expected to throw IllegalArgumentException before fix
        assertDoesNotThrow(() -> {
            exporter.export(Arrays.asList(sheetData), outputPath);
        });
    }
}
