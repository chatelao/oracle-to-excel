package com.chatelao.data;

import com.chatelao.config.SheetConfig;
import com.chatelao.model.SheetData;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
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

    @Test
    public void testColumnSelectionAndOrdering() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test_columns;DB_CLOSE_DELAY=-1", "sa", "")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE test_table_cols (id INT, name VARCHAR(255), city VARCHAR(255))");
                stmt.execute("INSERT INTO test_table_cols VALUES (1, 'Alice', 'New York')");

                try (ResultSet rs = stmt.executeQuery("SELECT * FROM test_table_cols")) {
                    DataProcessor processor = new DataProcessor();
                    SheetConfig config = new SheetConfig();
                    config.setName("ColSheet");
                    config.setColumns(Arrays.asList("CITY", "ID"));

                    List<SheetData> sheets = processor.processData(rs, config);

                    assertEquals(1, sheets.size());
                    SheetData data = sheets.get(0);
                    assertEquals(2, data.getColumnNames().size());
                    assertEquals("CITY", data.getColumnNames().get(0));
                    assertEquals("ID", data.getColumnNames().get(1));

                    List<Object> row = data.getRows().get(0);
                    assertEquals("New York", row.get(0));
                    assertEquals(1, row.get(1));
                }
            }
        }
    }

    @Test
    public void testCategorySplitting() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test_split;DB_CLOSE_DELAY=-1", "sa", "")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE test_table_split (id INT, name VARCHAR(255), category VARCHAR(255), subcat VARCHAR(255))");
                stmt.execute("INSERT INTO test_table_split VALUES (1, 'Alice', 'Cat1', 'SubA')");
                stmt.execute("INSERT INTO test_table_split VALUES (2, 'Bob', 'Cat1', 'SubB')");
                stmt.execute("INSERT INTO test_table_split VALUES (3, 'Charlie', 'Cat2', 'SubA')");

                try (ResultSet rs = stmt.executeQuery("SELECT * FROM test_table_split ORDER BY category, subcat")) {
                    DataProcessor processor = new DataProcessor();
                    SheetConfig config = new SheetConfig();
                    config.setName("SplitSheet");
                    config.setFilenameColumns(Arrays.asList("CATEGORY"));
                    config.setSheetnameColumns(Arrays.asList("SUBCAT"));

                    List<SheetData> sheets = processor.processData(rs, config);

                    assertEquals(3, sheets.size());

                    // Sheet 1: Cat1, SubA
                    assertEquals("SplitSheet_SubA", sheets.get(0).getSheetName());
                    assertEquals("Cat1", sheets.get(0).getTargetFileName());
                    assertEquals(1, sheets.get(0).getRows().size());

                    // Sheet 2: Cat1, SubB
                    assertEquals("SplitSheet_SubB", sheets.get(1).getSheetName());
                    assertEquals("Cat1", sheets.get(1).getTargetFileName());
                    assertEquals(1, sheets.get(1).getRows().size());

                    // Sheet 3: Cat2, SubA
                    assertEquals("SplitSheet_SubA", sheets.get(2).getSheetName());
                    assertEquals("Cat2", sheets.get(2).getTargetFileName());
                    assertEquals(1, sheets.get(2).getRows().size());
                }
            }
        }
    }

    @Test
    public void testExcludeColumns() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test_exclude;DB_CLOSE_DELAY=-1", "sa", "")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE test_table_exclude (id INT, name VARCHAR(255), category VARCHAR(255))");
                stmt.execute("INSERT INTO test_table_exclude VALUES (1, 'Alice', 'Cat1')");

                try (ResultSet rs = stmt.executeQuery("SELECT * FROM test_table_exclude")) {
                    DataProcessor processor = new DataProcessor();
                    SheetConfig config = new SheetConfig();
                    config.setName("ExcludeSheet");
                    config.setExcludeColumns(Arrays.asList("CATEGORY", "ID"));
                    config.setSheetnameColumns(Arrays.asList("CATEGORY"));

                    List<SheetData> sheets = processor.processData(rs, config);

                    assertEquals(1, sheets.size());
                    SheetData data = sheets.get(0);

                    // NAME should be the only remaining column
                    assertEquals(1, data.getColumnNames().size());
                    assertEquals("NAME", data.getColumnNames().get(0).toUpperCase());

                    // But CATEGORY should still be used for naming
                    assertEquals("ExcludeSheet_Cat1", data.getSheetName());

                    List<Object> row = data.getRows().get(0);
                    assertEquals(1, row.size());
                    assertEquals("Alice", row.get(0));
                }
            }
        }
    }
}
