package com.chatelao.config;

import java.util.List;

public class ExportConfig {
    private String filename;
    private List<SheetConfig> sheets;

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
}
