package com.chatelao.data;

import com.chatelao.config.SheetConfig;
import com.chatelao.model.SheetData;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Responsibility: Transforms ResultSet data into a structure suitable for Excel export.
 * Handles partitioning and dynamic naming of data into multiple sheets if configured.
 */
public class DataProcessor {

    /**
     * Processes a ResultSet into a list of SheetData objects.
     *
     * @param rs          The ResultSet to process.
     * @param sheetConfig The configuration for the current sheet.
     * @return A list of SheetData objects.
     * @throws SQLException If a database access error occurs.
     */
    public List<SheetData> processData(ResultSet rs, SheetConfig sheetConfig) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        List<String> columnNames = new ArrayList<>();
        int sheetNameColIdx = -1;
        int fileNameColIdx = -1;

        for (int i = 1; i <= columnCount; i++) {
            String colName = metaData.getColumnName(i);
            columnNames.add(colName);
            if (sheetConfig.getSheetNameColumn() != null && sheetConfig.getSheetNameColumn().equalsIgnoreCase(colName)) {
                sheetNameColIdx = i;
            }
            if (sheetConfig.getFileNameColumn() != null && sheetConfig.getFileNameColumn().equalsIgnoreCase(colName)) {
                fileNameColIdx = i;
            }
        }

        Map<GroupKey, List<List<Object>>> groupedRows = new LinkedHashMap<>();

        while (rs.next()) {
            String sheetName = sheetConfig.getName();
            if (sheetNameColIdx != -1) {
                Object val = rs.getObject(sheetNameColIdx);
                if (val != null) {
                    sheetName = val.toString();
                }
            }

            String fileName = null;
            if (fileNameColIdx != -1) {
                Object val = rs.getObject(fileNameColIdx);
                if (val != null) {
                    fileName = val.toString();
                }
            }

            GroupKey key = new GroupKey(sheetName, fileName);
            groupedRows.computeIfAbsent(key, k -> new ArrayList<>()).add(getRowData(rs, columnCount));
        }

        List<SheetData> allSheets = new ArrayList<>();
        Integer partitionSize = sheetConfig.getPartitionSize();

        for (Map.Entry<GroupKey, List<List<Object>>> entry : groupedRows.entrySet()) {
            GroupKey key = entry.getKey();
            List<List<Object>> rows = entry.getValue();

            if (partitionSize != null && partitionSize > 0 && rows.size() > partitionSize) {
                int partitionIndex = 1;
                for (int i = 0; i < rows.size(); i += partitionSize) {
                    int end = Math.min(i + partitionSize, rows.size());
                    List<List<Object>> partition = new ArrayList<>(rows.subList(i, end));
                    SheetData sd = new SheetData(key.sheetName + "_" + partitionIndex, columnNames, partition);
                    sd.setTargetFileName(key.fileName);
                    allSheets.add(sd);
                    partitionIndex++;
                }
            } else {
                SheetData sd = new SheetData(key.sheetName, columnNames, rows);
                sd.setTargetFileName(key.fileName);
                allSheets.add(sd);
            }
        }

        // Ensure at least one empty sheet if no data
        if (allSheets.isEmpty()) {
            SheetData sd = new SheetData(sheetConfig.getName(), columnNames, new ArrayList<>());
            allSheets.add(sd);
        }

        return allSheets;
    }

    private List<Object> getRowData(ResultSet rs, int columnCount) throws SQLException {
        List<Object> row = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
            row.add(rs.getObject(i));
        }
        return row;
    }

    private static class GroupKey {
        final String sheetName;
        final String fileName;

        GroupKey(String sheetName, String fileName) {
            this.sheetName = sheetName;
            this.fileName = fileName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GroupKey groupKey = (GroupKey) o;
            return Objects.equals(sheetName, groupKey.sheetName) && Objects.equals(fileName, groupKey.fileName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sheetName, fileName);
        }
    }
}
