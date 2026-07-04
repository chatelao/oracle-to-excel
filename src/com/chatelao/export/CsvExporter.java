package com.chatelao.export;

import com.chatelao.model.SheetData;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Responsibility: Exports data to CSV/TSV files with configurable delimiters, quoting, and escaping.
 */
public class CsvExporter {
    private final String delimiter;
    private final String quoteChar;
    private final String escapeChar;

    public CsvExporter(String delimiter, String quoteChar, String escapeChar) {
        this.delimiter = delimiter != null ? delimiter : ",";
        this.quoteChar = quoteChar != null ? quoteChar : "\"";
        this.escapeChar = escapeChar != null ? escapeChar : "\"";
    }

    public void export(List<SheetData> sheetDataList, Path outputPath) throws IOException {
        for (SheetData sheetData : sheetDataList) {
            Path actualPath = outputPath;
            if (sheetDataList.size() > 1) {
                String filename = outputPath.getFileName().toString();
                int lastDot = filename.lastIndexOf('.');
                String base = lastDot != -1 ? filename.substring(0, lastDot) : filename;
                String ext = lastDot != -1 ? filename.substring(lastDot) : "";
                actualPath = outputPath.resolveSibling(base + "_" + sheetData.getSheetName() + ext);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(actualPath)) {
                // Header
                writer.write(formatRow(sheetData.getColumnNames()));
                writer.newLine();

                // Data
                for (List<Object> row : sheetData.getRows()) {
                    List<String> stringValues = row.stream()
                            .map(o -> o == null ? "" : o.toString())
                            .collect(Collectors.toList());
                    writer.write(formatRow(stringValues));
                    writer.newLine();
                }
            }
        }
    }

    private String formatRow(List<String> values) {
        return values.stream()
                .map(this::formatValue)
                .collect(Collectors.joining(delimiter));
    }

    private String formatValue(String value) {
        if (quoteChar.isEmpty()) {
            // If quoting is disabled, we might still need to escape the delimiter or newlines
            // But usually, one would use a delimiter that doesn't appear in data (like TSV)
            return value;
        }
        boolean needsQuoting = value.contains(delimiter) || value.contains(quoteChar) || value.contains("\n") || value.contains("\r");
        if (needsQuoting) {
            String escaped = value.replace(quoteChar, escapeChar + quoteChar);
            return quoteChar + escaped + quoteChar;
        }
        return value;
    }
}
