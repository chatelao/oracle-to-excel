package com.chatelao.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Responsibility: Manages database connections and executes SQL queries.
 */
public interface QueryEngine {
    /**
     * Executes a SQL query and returns the ResultSet.
     *
     * @param sql             The SQL statement to execute.
     * @param connectionProps Connection properties including URL, username, and password.
     * @return The resulting ResultSet.
     * @throws SQLException If a database access error occurs.
     */
    ResultSet executeQuery(String sql, Properties connectionProps) throws SQLException;
}
