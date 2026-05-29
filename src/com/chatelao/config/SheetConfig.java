package com.chatelao.config;

public class SheetConfig {
    private String name;
    private String query;
    private Integer partitionSize;
    private String sheetNameColumn;
    private String fileNameColumn;

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

    public String getSheetNameColumn() {
        return sheetNameColumn;
    }

    public void setSheetNameColumn(String sheetNameColumn) {
        this.sheetNameColumn = sheetNameColumn;
    }

    public String getFileNameColumn() {
        return fileNameColumn;
    }

    public void setFileNameColumn(String fileNameColumn) {
        this.fileNameColumn = fileNameColumn;
    }
}
