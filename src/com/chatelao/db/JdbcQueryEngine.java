package com.chatelao.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class JdbcQueryEngine implements QueryEngine {

    @Override
    public ResultSet executeQuery(String sql, Properties connectionProps) throws SQLException {
        String url = connectionProps.getProperty("url");
        if (url == null) {
            throw new SQLException("Database URL not specified in connection properties.");
        }

        Connection conn = DriverManager.getConnection(url, connectionProps);
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }
}
