package com.chatelao.export;

import com.chatelao.model.SheetData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
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
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Map<String, CellStyle> styleCache = new HashMap<>();

            for (SheetData sheetData : sheetDataList) {
                Sheet sheet = workbook.createSheet(sheetData.getSheetName());
                Map<String, String> columnColors = new HashMap<>();
                if (sheetData.getColumnColors() != null) {
                    for (Map.Entry<String, String> entry : sheetData.getColumnColors().entrySet()) {
                        columnColors.put(entry.getKey().toUpperCase(), entry.getValue());
                    }
                }

                // Create header row
                Row headerRow = sheet.createRow(0);
                List<String> columnNames = sheetData.getColumnNames();
                for (int i = 0; i < columnNames.size(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columnNames.get(i));
                    cell.setCellStyle(headerStyle);
                }

                // Create data rows
                List<List<Object>> rows = sheetData.getRows();
                for (int i = 0; i < rows.size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    List<Object> rowData = rows.get(i);
                    for (int j = 0; j < rowData.size(); j++) {
                        Cell cell = row.createCell(j);

                        // Apply column colors
                        if (!columnColors.isEmpty()) {
                            String columnName = columnNames.get(j);
                            String hexColor = columnColors.get(columnName.toUpperCase());
                            if (hexColor != null) {
                                CellStyle style = styleCache.computeIfAbsent(hexColor, color -> {
                                    CellStyle s = workbook.createCellStyle();
                                    if (s instanceof XSSFCellStyle xssfStyle) {
                                        byte[] rgb = hexToRgb(color);
                                        if (rgb != null) {
                                            XSSFColor xssfColor = new XSSFColor(rgb, null);
                                            xssfStyle.setFillForegroundColor(xssfColor);
                                            xssfStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                                        }
                                    }
                                    return s;
                                });
                                cell.setCellStyle(style);
                            }
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

                // Set auto filter
                if (!columnNames.isEmpty()) {
                    int lastRow = Math.max(0, rows.size());
                    sheet.setAutoFilter(new CellRangeAddress(0, lastRow, 0, columnNames.size() - 1));
                }

                // Auto-fit columns
                for (int i = 0; i < columnNames.size(); i++) {
                    sheet.autoSizeColumn(i);
                    // Add extra width for the filter arrow
                    sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
                }

                // Create pivot table if requested
                if (sheetData.isPivotTable() && !rows.isEmpty()) {
                    String pivotSheetName = "Pivot_" + sheetData.getSheetName();
                    // Excel sheet name limit is 31 chars
                    if (pivotSheetName.length() > 31) {
                        pivotSheetName = pivotSheetName.substring(0, 31);
                    }
                    XSSFSheet pivotSheet = (XSSFSheet) workbook.createSheet(pivotSheetName);

                    int lastRow = rows.size();
                    int lastCol = columnNames.size() - 1;

                    String sourceRef = "'" + sheetData.getSheetName() + "'!A1:" +
                                     new CellReference(lastRow, lastCol).formatAsString();

                    AreaReference source = new AreaReference(sourceRef, workbook.getSpreadsheetVersion());
                    CellReference position = new CellReference("A1");

                    XSSFPivotTable pivotTable = pivotSheet.createPivotTable(source, position);
                    // By default, add the first column as a row label
                    if (lastCol >= 0) {
                        pivotTable.addRowLabel(0);
                    }
                    // And the last column as a sum if there are at least two columns
                    if (lastCol >= 1) {
                        pivotTable.addColumnLabel(DataConsolidateFunction.SUM, lastCol);
                    }
                }
            }

            try (FileOutputStream fileOut = new FileOutputStream(outputPath.toFile())) {
                workbook.write(fileOut);
            }
        }
    }

    private byte[] hexToRgb(String hex) {
        if (hex == null) return null;
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        if (hex.length() == 3) {
            hex = "" + hex.charAt(0) + hex.charAt(0) + hex.charAt(1) + hex.charAt(1) + hex.charAt(2) + hex.charAt(2);
        }
        if (hex.length() != 6) {
            return null;
        }
        try {
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);
            return new byte[]{(byte) r, (byte) g, (byte) b};
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
