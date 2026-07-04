package com.chatelao.config;

import java.util.List;

public class ExportConfig {
    private String filename;
    private List<SheetConfig> sheets;
    private String delimiter;
    private String quoteChar;
    private String escapeChar;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<SheetConfig> getSheets() {
        return sheets;
    }

    public void setSheets(List<SheetConfig> sheets) {
        this.sheets = sheets;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getQuoteChar() {
        return quoteChar;
    }

    public void setQuoteChar(String quoteChar) {
        this.quoteChar = quoteChar;
    }

    public String getEscapeChar() {
        return escapeChar;
    }

    public void setEscapeChar(String escapeChar) {
        this.escapeChar = escapeChar;
    }
}
