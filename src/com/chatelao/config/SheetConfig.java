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
    private Integer marginTop = 0;
    private Integer marginLeft = 0;

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

    public Integer getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(Integer marginTop) {
        this.marginTop = marginTop;
    }

    public Integer getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(Integer marginLeft) {
        this.marginLeft = marginLeft;
    }
}
