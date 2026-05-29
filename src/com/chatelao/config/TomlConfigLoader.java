package com.chatelao.config;

import org.tomlj.Toml;
import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TomlConfigLoader {

    public Config loadConfig(Path configPath) throws IOException {
        TomlParseResult result = Toml.parse(configPath);
        if (result.hasErrors()) {
            throw new IOException("Errors parsing TOML: " + result.errors());
        }

        Config config = new Config();

        TomlTable databaseTable = result.getTable("database");
        if (databaseTable != null) {
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setUrl(databaseTable.getString("url"));
            dbConfig.setUsername(databaseTable.getString("username"));
            dbConfig.setPassword(databaseTable.getString("password"));
            config.setDatabase(dbConfig);
        }

        TomlArray exportsArray = result.getArray("exports");
        if (exportsArray != null) {
            List<ExportConfig> exports = new ArrayList<>();
            for (int i = 0; i < exportsArray.size(); i++) {
                TomlTable exportTable = exportsArray.getTable(i);
                ExportConfig exportConfig = new ExportConfig();
                exportConfig.setFilename(exportTable.getString("filename"));

                TomlArray sheetsArray = exportTable.getArray("sheets");
                if (sheetsArray != null) {
                    List<SheetConfig> sheets = new ArrayList<>();
                    for (int j = 0; j < sheetsArray.size(); j++) {
                        TomlTable sheetTable = sheetsArray.getTable(j);
                        SheetConfig sheetConfig = new SheetConfig();
                        sheetConfig.setName(sheetTable.getString("name"));
                        sheetConfig.setQuery(sheetTable.getString("query"));
                        Long partitionSize = sheetTable.getLong("partition_size");
                        if (partitionSize != null) {
                            sheetConfig.setPartitionSize(partitionSize.intValue());
                        }

                        TomlArray columnsArray = sheetTable.getArray("columns");
                        if (columnsArray != null) {
                            sheetConfig.setColumns(toStringList(columnsArray));
                        }

                        TomlArray nameColumnsArray = sheetTable.getArray("name_columns");
                        if (nameColumnsArray != null) {
                            sheetConfig.setNameColumns(toStringList(nameColumnsArray));
                        }

                        TomlArray filenameColumnsArray = sheetTable.getArray("filename_columns");
                        if (filenameColumnsArray != null) {
                            sheetConfig.setFilenameColumns(toStringList(filenameColumnsArray));
                        }

                        sheets.add(sheetConfig);
                    }
                    exportConfig.setSheets(sheets);
                }
                exports.add(exportConfig);
            }
            config.setExports(exports);
        }

        return config;
    }

    private List<String> toStringList(TomlArray array) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            list.add(array.getString(i));
        }
        return list;
    }
}
