package com.chatelao.model;

import java.util.List;
import java.util.Map;

public class SheetData {
    private String sheetName;
    private List<String> columnNames;
    private List<List<Object>> rows;
    private String targetFileName;
    private Map<String, String> columnColors;
    private int topOffset;
    private int leftOffset;

    public SheetData(String sheetName, List<String> columnNames, List<List<Object>> rows) {
        this(sheetName, columnNames, rows, null, null, 0, 0);
    }

    public SheetData(String sheetName, List<String> columnNames, List<List<Object>> rows, String targetFileName) {
        this(sheetName, columnNames, rows, targetFileName, null, 0, 0);
    }

    public SheetData(String sheetName, List<String> columnNames, List<List<Object>> rows, String targetFileName, Map<String, String> columnColors) {
        this(sheetName, columnNames, rows, targetFileName, columnColors, 0, 0);
    }

    public SheetData(String sheetName, List<String> columnNames, List<List<Object>> rows, String targetFileName, Map<String, String> columnColors, int topOffset, int leftOffset) {
        this.sheetName = sheetName;
        this.columnNames = columnNames;
        this.rows = rows;
        this.targetFileName = targetFileName;
        this.columnColors = columnColors;
        this.topOffset = topOffset;
        this.leftOffset = leftOffset;
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

    public int getTopOffset() {
        return topOffset;
    }

    public int getLeftOffset() {
        return leftOffset;
    }
}
