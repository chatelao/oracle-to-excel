package com.chatelao.config;

import java.util.List;
import java.util.Map;

public class SheetConfig {
    private String name;
    private String query;
    private Integer partitionSize;
    private List<String> columns;
    private List<String> sheetnameColumns;
    private List<String> filenameColumns;
    private Map<String, String> columnColors;
    private Integer topOffset;
    private Integer leftOffset;
    private boolean pivotTable;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getPartitionSize() {
        return partitionSize;
    }

    public void setPartitionSize(Integer partitionSize) {
        this.partitionSize = partitionSize;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<String> getSheetnameColumns() {
        return sheetnameColumns;
    }

    public void setSheetnameColumns(List<String> sheetnameColumns) {
        this.sheetnameColumns = sheetnameColumns;
    }

    public List<String> getFilenameColumns() {
        return filenameColumns;
    }

    public void setFilenameColumns(List<String> filenameColumns) {
        this.filenameColumns = filenameColumns;
    }

    public Map<String, String> getColumnColors() {
        return columnColors;
    }

    public void setColumnColors(Map<String, String> columnColors) {
        this.columnColors = columnColors;
    }

    public Integer getTopOffset() {
        return topOffset;
    }

    public void setTopOffset(Integer topOffset) {
        this.topOffset = topOffset;
    }

    public Integer getLeftOffset() {
        return leftOffset;
    }

    public void setLeftOffset(Integer leftOffset) {
        this.leftOffset = leftOffset;
    }

    public boolean isPivotTable() {
        return pivotTable;
    }

    public void setPivotTable(boolean pivotTable) {
        this.pivotTable = pivotTable;
    }
}
