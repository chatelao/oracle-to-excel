package com.chatelao.db;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcQueryEngineTest {

    @Test
    public void testExecuteQuery() throws SQLException {
        JdbcQueryEngine engine = new JdbcQueryEngine();
        Properties props = new Properties();
        props.setProperty("url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        props.setProperty("user", "sa");
        props.setProperty("password", "");

        try (ResultSet rs = engine.executeQuery("SELECT 1 AS val", props)) {
            assertTrue(rs.next());
            assertEquals(1, rs.getInt("val"));
            assertFalse(rs.next());
        }
    }

    @Test
    public void testExecuteQueryNoUrl() {
        JdbcQueryEngine engine = new JdbcQueryEngine();
        Properties props = new Properties();
        assertThrows(SQLException.class, () -> engine.executeQuery("SELECT 1", props));
    }
}
