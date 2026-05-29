package com.chatelao.export;

import com.chatelao.model.SheetData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsibility: Creates Excel workbooks and sheets, writes data, and saves files.
 */
public class ExcelExporter {

    /**
     * Exports a list of SheetData objects to a single Excel workbook.
     *
     * @param sheetDataList The list of data to export to different sheets.
     * @param outputPath    The path where the Excel file will be saved.
     * @throws IOException If an I/O error occurs during file writing.
     */
    public void export(List<SheetData> sheetDataList, Path outputPath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Map<String, CellStyle> styleCache = new HashMap<>();

            for (SheetData sheetData : sheetDataList) {
                Sheet sheet = workbook.createSheet(sheetData.getSheetName());

                List<String> columnNames = sheetData.getColumnNames();
                Map<String, String> columnColors = sheetData.getColumnColors();
                CellStyle[] columnStyles = new CellStyle[columnNames.size()];

                if (columnColors != null) {
                    for (int i = 0; i < columnNames.size(); i++) {
                        String color = columnColors.get(columnNames.get(i));
                        if (color != null) {
                            columnStyles[i] = getOrCreateStyle(workbook, styleCache, color);
                        }
                    }
                }

                // Create header row
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < columnNames.size(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columnNames.get(i));
                    if (columnStyles[i] != null) {
                        cell.setCellStyle(columnStyles[i]);
                    }
                }

                // Create data rows
                List<List<Object>> rows = sheetData.getRows();
                for (int i = 0; i < rows.size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    List<Object> rowData = rows.get(i);
                    for (int j = 0; j < rowData.size(); j++) {
                        Cell cell = row.createCell(j);
                        if (columnStyles[j] != null) {
                            cell.setCellStyle(columnStyles[j]);
                        }
                        Object value = rowData.get(j);
                        if (value != null) {
                            if (value instanceof Number) {
                                cell.setCellValue(((Number) value).doubleValue());
                            } else if (value instanceof Boolean) {
                                cell.setCellValue((Boolean) value);
                            } else {
                                cell.setCellValue(value.toString());
                            }
                        }
                    }
                }
            }

            try (FileOutputStream fileOut = new FileOutputStream(outputPath.toFile())) {
                workbook.write(fileOut);
            }
        }
    }

    private CellStyle getOrCreateStyle(Workbook workbook, Map<String, CellStyle> styleCache, String hexColor) {
        return styleCache.computeIfAbsent(hexColor, k -> {
            CellStyle style = workbook.createCellStyle();
            if (style instanceof XSSFCellStyle xssfStyle) {
                try {
                    byte[] rgb = hexToRgb(k);
                    xssfStyle.setFillForegroundColor(new XSSFColor(rgb, new DefaultIndexedColorMap()));
                    xssfStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                } catch (Exception e) {
                    // Ignore invalid colors, return default style
                }
            }
            return style;
        });
    }

    private byte[] hexToRgb(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        if (hex.length() != 6) {
            throw new IllegalArgumentException("Invalid hex color: " + hex);
        }
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        return new byte[]{(byte) r, (byte) g, (byte) b};
    }
}
