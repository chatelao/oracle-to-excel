package com.chatelao.data;

import com.chatelao.model.SheetData;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsibility: Transforms ResultSet data into a structure suitable for Excel export.
 */
public class DataProcessor {

    /**
     * Processes a ResultSet into a single SheetData object.
     *
     * @param rs        The ResultSet to process.
     * @param sheetName The name of the Excel sheet.
     * @return A SheetData object containing the data.
     * @throws SQLException If a database access error occurs.
     */
    public SheetData processData(ResultSet rs, String sheetName) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        List<String> columnNames = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnName(i));
        }

        List<List<Object>> rows = new ArrayList<>();
        while (rs.next()) {
            List<Object> row = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                row.add(rs.getObject(i));
            }
            rows.add(row);
        }

        return new SheetData(sheetName, columnNames, rows);
    }
}
