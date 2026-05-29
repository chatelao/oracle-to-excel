package com.chatelao.config;

import java.util.List;

public class SheetConfig {
    private String name;
    private String query;
    private Integer partitionSize;
    private List<String> columns;
    private List<String> nameColumns;
    private List<String> filenameColumns;

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

    public List<String> getNameColumns() {
        return nameColumns;
    }

    public void setNameColumns(List<String> nameColumns) {
        this.nameColumns = nameColumns;
    }

    public List<String> getFilenameColumns() {
        return filenameColumns;
    }

    public void setFilenameColumns(List<String> filenameColumns) {
        this.filenameColumns = filenameColumns;
    }
}
