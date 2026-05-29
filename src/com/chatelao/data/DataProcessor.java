package com.chatelao.data;

import com.chatelao.config.SheetConfig;
import com.chatelao.model.SheetData;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Responsibility: Transforms ResultSet data into a structure suitable for Excel export.
 * Handles partitioning of data into multiple sheets if configured.
 * Supports column selection, ordering, and category-based splitting for sheets and files.
 */
public class DataProcessor {

    /**
     * Processes a ResultSet into a list of SheetData objects based on configuration.
     *
     * @param rs          The ResultSet to process.
     * @param sheetConfig The configuration for the current sheet.
     * @return A list of SheetData objects.
     * @throws SQLException If a database access error occurs.
     */
    public List<SheetData> processData(ResultSet rs, SheetConfig sheetConfig) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        // Determine target columns and their order
        List<String> targetColumns = sheetConfig.getColumns();
        if (targetColumns == null || targetColumns.isEmpty()) {
            targetColumns = new ArrayList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                targetColumns.add(metaData.getColumnName(i));
            }
        }

        List<Integer> targetIndices = new ArrayList<>();
        for (String col : targetColumns) {
            targetIndices.add(rs.findColumn(col));
        }

        // Determine indices for category-based splitting
        List<String> nameColumns = sheetConfig.getSheetnameColumns();
        List<Integer> nameIndices = new ArrayList<>();
        if (nameColumns != null) {
            for (String col : nameColumns) {
                nameIndices.add(rs.findColumn(col));
            }
        }

        List<String> filenameColumns = sheetConfig.getFilenameColumns();
        List<Integer> filenameIndices = new ArrayList<>();
        if (filenameColumns != null) {
            for (String col : filenameColumns) {
                filenameIndices.add(rs.findColumn(col));
            }
        }

        // Grouping: Map<FilenameCategory, Map<SheetCategory, List<Rows>>>
        // Use LinkedHashMap to preserve the order of discovery
        Map<String, Map<String, List<List<Object>>>> groupedData = new LinkedHashMap<>();

        boolean hasRows = false;
        while (rs.next()) {
            hasRows = true;
            String filenamePart = getCategoryKey(rs, filenameIndices);
            String sheetPart = getCategoryKey(rs, nameIndices);

            List<Object> row = new ArrayList<>();
            for (int index : targetIndices) {
                row.add(rs.getObject(index));
            }

            groupedData.computeIfAbsent(filenamePart, k -> new LinkedHashMap<>())
                    .computeIfAbsent(sheetPart, k -> new ArrayList<>())
                    .add(row);
        }

        List<SheetData> sheets = new ArrayList<>();
        int marginTop = sheetConfig.getMarginTop() != null ? sheetConfig.getMarginTop() : 0;
        int marginLeft = sheetConfig.getMarginLeft() != null ? sheetConfig.getMarginLeft() : 0;

        if (!hasRows) {
            sheets.add(new SheetData(sheetConfig.getName(), targetColumns, new ArrayList<>(), null, sheetConfig.getColumnColors(), marginTop, marginLeft));
            return sheets;
        }

        Integer partitionSize = sheetConfig.getPartitionSize();

        for (Map.Entry<String, Map<String, List<List<Object>>>> fileEntry : groupedData.entrySet()) {
            String filenamePart = fileEntry.getKey();
            for (Map.Entry<String, List<List<Object>>> sheetEntry : fileEntry.getValue().entrySet()) {
                String sheetPart = sheetEntry.getKey();
                List<List<Object>> allRows = sheetEntry.getValue();

                String baseSheetName = sheetConfig.getName();
                if (!sheetPart.isEmpty()) {
                    baseSheetName += "_" + sheetPart;
                }

                if (partitionSize != null && partitionSize > 0) {
                    int partitionIndex = 1;
                    for (int i = 0; i < allRows.size(); i += partitionSize) {
                        int end = Math.min(i + partitionSize, allRows.size());
                        List<List<Object>> partition = allRows.subList(i, end);
                        String name = baseSheetName + "_" + partitionIndex;
                        SheetData sd = new SheetData(name, targetColumns, new ArrayList<>(partition), null, sheetConfig.getColumnColors(), marginTop, marginLeft);
                        if (!filenamePart.isEmpty()) {
                            sd.setTargetFileName(filenamePart);
                        }
                        sheets.add(sd);
                        partitionIndex++;
                    }
                } else {
                    SheetData sd = new SheetData(baseSheetName, targetColumns, allRows, null, sheetConfig.getColumnColors(), marginTop, marginLeft);
                    if (!filenamePart.isEmpty()) {
                        sd.setTargetFileName(filenamePart);
                    }
                    sheets.add(sd);
                }
            }
        }

        return sheets;
    }

    private String getCategoryKey(ResultSet rs, List<Integer> indices) throws SQLException {
        if (indices.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indices.size(); i++) {
            if (i > 0) {
                sb.append("_");
            }
            Object val = rs.getObject(indices.get(i));
            sb.append(val == null ? "NULL" : val.toString());
        }
        return sb.toString();
    }
}
