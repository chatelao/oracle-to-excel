package com.chatelao.data;

import com.chatelao.model.SheetData;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataProcessorTest {

    @Test
    public void testProcessData() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test_processor;DB_CLOSE_DELAY=-1", "sa", "")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE test_table (id INT, name VARCHAR(255))");
                stmt.execute("INSERT INTO test_table VALUES (1, 'Alice')");
                stmt.execute("INSERT INTO test_table VALUES (2, 'Bob')");

                try (ResultSet rs = stmt.executeQuery("SELECT * FROM test_table ORDER BY id")) {
                    DataProcessor processor = new DataProcessor();
                    SheetData data = processor.processData(rs, "TestSheet");

                    assertEquals("TestSheet", data.getSheetName());
                    assertEquals(2, data.getColumnNames().size());
                    assertEquals("ID", data.getColumnNames().get(0));
                    assertEquals("NAME", data.getColumnNames().get(1));

                    List<List<Object>> rows = data.getRows();
                    assertEquals(2, rows.size());
                    assertEquals(1, rows.get(0).get(0));
                    assertEquals("Alice", rows.get(0).get(1));
                    assertEquals(2, rows.get(1).get(0));
                    assertEquals("Bob", rows.get(1).get(1));
                }
            }
        }
    }
}
