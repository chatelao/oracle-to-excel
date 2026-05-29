package com.chatelao.data;

import com.chatelao.config.SheetConfig;
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
    public void testProcessDataWithoutPartitioning() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test_processor;DB_CLOSE_DELAY=-1", "sa", "")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE test_table (id INT, name VARCHAR(255))");
                stmt.execute("INSERT INTO test_table VALUES (1, 'Alice')");
                stmt.execute("INSERT INTO test_table VALUES (2, 'Bob')");

                try (ResultSet rs = stmt.executeQuery("SELECT * FROM test_table ORDER BY id")) {
                    DataProcessor processor = new DataProcessor();
                    SheetConfig config = new SheetConfig();
                    config.setName("TestSheet");
                    config.setQuery("SELECT * FROM test_table ORDER BY id");

                    List<SheetData> sheets = processor.processData(rs, config);

                    assertEquals(1, sheets.size());
                    SheetData data = sheets.get(0);
                    assertEquals("TestSheet", data.getSheetName());
                    assertEquals(2, data.getColumnNames().size());
                    assertEquals("ID", data.getColumnNames().get(0).toUpperCase());
                    assertEquals("NAME", data.getColumnNames().get(1).toUpperCase());

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

    @Test
    public void testProcessDataWithPartitioning() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test_partition;DB_CLOSE_DELAY=-1", "sa", "")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE test_table_partition (id INT, name VARCHAR(255))");
                stmt.execute("INSERT INTO test_table_partition VALUES (1, 'Alice')");
                stmt.execute("INSERT INTO test_table_partition VALUES (2, 'Bob')");
                stmt.execute("INSERT INTO test_table_partition VALUES (3, 'Charlie')");

                try (ResultSet rs = stmt.executeQuery("SELECT * FROM test_table_partition ORDER BY id")) {
                    DataProcessor processor = new DataProcessor();
                    SheetConfig config = new SheetConfig();
                    config.setName("PartSheet");
                    config.setPartitionSize(2);

                    List<SheetData> sheets = processor.processData(rs, config);

                    assertEquals(2, sheets.size());

                    assertEquals("PartSheet_1", sheets.get(0).getSheetName());
                    assertEquals(2, sheets.get(0).getRows().size());

                    assertEquals("PartSheet_2", sheets.get(1).getSheetName());
                    assertEquals(1, sheets.get(1).getRows().size());
                }
            }
        }
    }
}
