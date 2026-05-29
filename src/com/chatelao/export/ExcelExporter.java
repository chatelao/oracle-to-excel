package com.chatelao.export;

import com.chatelao.model.SheetData;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
            for (SheetData sheetData : sheetDataList) {
                Sheet sheet = workbook.getSheet(sheetData.getSheetName());
                int startRow;

                if (sheet == null) {
                    sheet = workbook.createSheet(sheetData.getSheetName());
                    // Create header row
                    Row headerRow = sheet.createRow(0);
                    List<String> columnNames = sheetData.getColumnNames();
                    for (int i = 0; i < columnNames.size(); i++) {
                        Cell cell = headerRow.createCell(i);
                        cell.setCellValue(columnNames.get(i));
                    }
                    startRow = 1;
                } else {
                    startRow = sheet.getLastRowNum() + 1;
                }

                // Create data rows
                List<List<Object>> rows = sheetData.getRows();
                for (int i = 0; i < rows.size(); i++) {
                    Row row = sheet.createRow(startRow + i);
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
            }

            try (FileOutputStream fileOut = new FileOutputStream(outputPath.toFile())) {
                workbook.write(fileOut);
            }
        }
    }
}
