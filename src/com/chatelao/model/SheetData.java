package com.chatelao.model;

import java.util.List;
import java.util.Map;

public class SheetData {
    private String sheetName;
    private List<String> columnNames;
    private List<List<Object>> rows;
    private String targetFileName;
    private Map<String, String> columnColors;

    public SheetData(String sheetName, List<String> columnNames, List<List<Object>> rows) {
        this(sheetName, columnNames, rows, null);
    }

    public SheetData(String sheetName, List<String> columnNames, List<List<Object>> rows, String targetFileName) {
        this.sheetName = sheetName;
        this.columnNames = columnNames;
        this.rows = rows;
        this.targetFileName = targetFileName;
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

    public String getTargetFileName() {
        return targetFileName;
    }

    public void setTargetFileName(String targetFileName) {
        this.targetFileName = targetFileName;
    }

    public Map<String, String> getColumnColors() {
        return columnColors;
    }

    public void setColumnColors(Map<String, String> columnColors) {
        this.columnColors = columnColors;
    }
}
