package com.chatelao.data;

import com.chatelao.config.SheetConfig;
import com.chatelao.model.SheetData;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsibility: Transforms ResultSet data into a structure suitable for Excel export.
 * Handles partitioning of data into multiple sheets if configured.
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
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnName(i));
        }

        List<SheetData> sheets = new ArrayList<>();
        List<List<Object>> currentRows = new ArrayList<>();
        Integer partitionSize = sheetConfig.getPartitionSize();
        int partitionIndex = 1;

        while (rs.next()) {
            List<Object> row = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                row.add(rs.getObject(i));
            }
            currentRows.add(row);

            if (partitionSize != null && partitionSize > 0 && currentRows.size() >= partitionSize) {
                sheets.add(new SheetData(sheetConfig.getName() + "_" + partitionIndex, columnNames, currentRows));
                currentRows = new ArrayList<>();
                partitionIndex++;
            }
        }

        if (!currentRows.isEmpty() || sheets.isEmpty()) {
            String name = (partitionIndex > 1) ? sheetConfig.getName() + "_" + partitionIndex : sheetConfig.getName();
            sheets.add(new SheetData(name, columnNames, currentRows));
        }

        return sheets;
    }
}
