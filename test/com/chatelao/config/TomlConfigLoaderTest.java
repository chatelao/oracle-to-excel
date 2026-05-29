package com.chatelao.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class TomlConfigLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    public void testLoadConfig() throws IOException {
        String tomlContent = "[database]\n" +
                "url = \"jdbc:oracle:thin:@localhost:1521:xe\"\n" +
                "username = \"scott\"\n" +
                "password = \"tiger\"\n" +
                "\n" +
                "[[exports]]\n" +
                "filename = \"report1.xlsx\"\n" +
                "[[exports.sheets]]\n" +
                "name = \"Sheet1\"\n" +
                "query = \"SELECT * FROM table1\"\n" +
                "partition_size = 1000\n" +
                "[[exports.sheets]]\n" +
                "name = \"Sheet2\"\n" +
                "query = \"SELECT * FROM table2\"\n";

        Path configPath = tempDir.resolve("config.toml");
        Files.writeString(configPath, tomlContent);

        TomlConfigLoader loader = new TomlConfigLoader();
        Config config = loader.loadConfig(configPath);

        assertNotNull(config.getDatabase());
        assertEquals("jdbc:oracle:thin:@localhost:1521:xe", config.getDatabase().getUrl());
        assertEquals("scott", config.getDatabase().getUsername());
        assertEquals("tiger", config.getDatabase().getPassword());

        assertNotNull(config.getExports());
        assertEquals(1, config.getExports().size());
        ExportConfig export = config.getExports().get(0);
        assertEquals("report1.xlsx", export.getFilename());

        assertNotNull(export.getSheets());
        assertEquals(2, export.getSheets().size());
        assertEquals("Sheet1", export.getSheets().get(0).getName());
        assertEquals("SELECT * FROM table1", export.getSheets().get(0).getQuery());
        assertEquals(1000, export.getSheets().get(0).getPartitionSize());
        assertEquals("Sheet2", export.getSheets().get(1).getName());
        assertEquals("SELECT * FROM table2", export.getSheets().get(1).getQuery());
    }

    @Test
    public void testLoadConfigWithOffsets() throws IOException {
        String tomlContent = "[[exports]]\n" +
                "filename = \"report.xlsx\"\n" +
                "[[exports.sheets]]\n" +
                "name = \"Sheet1\"\n" +
                "query = \"SELECT * FROM table1\"\n" +
                "top_offset = 5\n" +
                "left_offset = 2\n";

        Path configPath = tempDir.resolve("config_offsets.toml");
        Files.writeString(configPath, tomlContent);

        TomlConfigLoader loader = new TomlConfigLoader();
        Config config = loader.loadConfig(configPath);

        SheetConfig sheet = config.getExports().get(0).getSheets().get(0);
        assertEquals(5, sheet.getTopOffset());
        assertEquals(2, sheet.getLeftOffset());
    }

    @Test
    public void testLoadConfigWithColumnColors() throws IOException {
        String tomlContent = "[[exports]]\n" +
                "filename = \"report.xlsx\"\n" +
                "[[exports.sheets]]\n" +
                "name = \"Sheet1\"\n" +
                "query = \"SELECT * FROM table1\"\n" +
                "[exports.sheets.column_colors]\n" +
                "ID = \"#FF0000\"\n" +
                "NAME = \"#00FF00\"\n";

        Path configPath = tempDir.resolve("config_colors.toml");
        Files.writeString(configPath, tomlContent);

        TomlConfigLoader loader = new TomlConfigLoader();
        Config config = loader.loadConfig(configPath);

        SheetConfig sheet = config.getExports().get(0).getSheets().get(0);
        assertNotNull(sheet.getColumnColors());
        assertEquals("#FF0000", sheet.getColumnColors().get("ID"));
        assertEquals("#00FF00", sheet.getColumnColors().get("NAME"));
    }

    @Test
    public void testLoadNonExistentFile() {
        TomlConfigLoader loader = new TomlConfigLoader();
        Path nonExistentPath = tempDir.resolve("non_existent.toml");
        assertThrows(IOException.class, () -> loader.loadConfig(nonExistentPath));
    }
}
