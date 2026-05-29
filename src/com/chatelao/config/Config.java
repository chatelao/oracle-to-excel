package com.chatelao.config;

import java.util.List;

public class Config {
    private DatabaseConfig database;
    private List<ExportConfig> exports;
    private String auditSheetName;

    public DatabaseConfig getDatabase() {
        return database;
    }

    public void setDatabase(DatabaseConfig database) {
        this.database = database;
    }

    public List<ExportConfig> getExports() {
        return exports;
    }

    public void setExports(List<ExportConfig> exports) {
        this.exports = exports;
    }

    public String getAuditSheetName() {
        return auditSheetName;
    }

    public void setAuditSheetName(String auditSheetName) {
        this.auditSheetName = auditSheetName;
    }
}
