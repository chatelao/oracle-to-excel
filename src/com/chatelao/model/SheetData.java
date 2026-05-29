package com.chatelao.model;

import java.util.List;

public class SheetData {
    private String sheetName;
    private List<String> columnNames;
    private List<List<Object>> rows;

    public SheetData(String sheetName, List<String> columnNames, List<List<Object>> rows) {
        this.sheetName = sheetName;
        this.columnNames = columnNames;
        this.rows = rows;
    }

    public String getSheetName() {
        return sheetName;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public List<List<Object>> getRows() {
        return rows;
    }
}
