package com.chatelao.export;

import com.chatelao.model.SheetData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

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

            for (SheetData sheetData : sheetDataList) {
                Sheet sheet = workbook.createSheet(sheetData.getSheetName());

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
            }

            try (FileOutputStream fileOut = new FileOutputStream(outputPath.toFile())) {
                workbook.write(fileOut);
            }
        }
    }
}
