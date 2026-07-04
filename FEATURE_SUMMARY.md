# Feature Summary - Oracle to Excel Exporter

The **Oracle to Excel Exporter** is a robust command-line utility designed to automate and streamline the extraction of data from Oracle databases into professionally formatted Excel workbooks. It handles everything from simple single-query exports to complex, multi-file batch operations with dynamic partitioning and styling.

## Core Capabilities

### 1. Flexible Export Structures
- **Multi-Format Export**: Support for Excel (.xlsx), CSV (.csv), and TSV (.tsv) output formats.
- **Multi-Sheet Export**: Execute multiple queries and save them as individual worksheets within a single Excel file, or separate files for CSV/TSV.
- **Multi-File Export**: Define multiple export tasks in a single configuration, each targeting a different output file.
- **Data Partitioning (UC2)**: Automatically split large result sets across multiple sheets based on a configurable row limit (e.g., 10,000 rows per sheet).

### 2. Dynamic Data Organization
- **Dynamic Sheet Naming**: Automatically name worksheets based on values in specific columns of the query result (e.g., creating a separate sheet for each `DEPARTMENT`).
- **Dynamic File Naming**: Split data into separate Excel files based on column values (e.g., `report_DEPT10.xlsx`, `report_DEPT20.xlsx`).
- **Column Selection & Ordering**: Explicitly choose which columns to export and define their exact sequence in the Excel sheet, regardless of the SQL `SELECT` order.

### 3. CSV/TSV Configuration
- **Custom Delimiters**: Full control over value separators (e.g., comma, semicolon, tab).
- **Quoting & Escaping**: Configurable quote characters and escape sequences for complex text data.

### 4. Advanced Formatting & Styling
- **Page Titles**: Add high-impact, stylized title rows (font size 20, bold) at the top of sheets.
- **Column Highlighting**: Apply custom hex background colors to specific columns to emphasize key data.
- **Layout Control**: Specify `top_offset` and `left_offset` to position the data table exactly where needed on the grid.
- **Auto-Formatting**: Automatically enables Excel filters, bolds header rows, and auto-sizes columns for immediate readability.
- **Pivot Tables**: Generate companion sheets containing ready-to-use pivot tables based on the exported data.

### 5. Enterprise-Ready Features
- **Audit Logging**: Maintain a comprehensive execution log (status, query, row counts, errors) appended as a dedicated sheet to every generated file.
- **External SQL Support**: Keep SQL logic clean by referencing external `.sql` files instead of embedding long queries in the configuration.
- **Resilient Execution**: Continues processing remaining tasks even if a specific query encounters a database error, ensuring maximum data retrieval.

---

## Technical Stack

- **Language**: Java 17+
- **Excel Engine**: Apache POI (XSSF)
- **Database Connectivity**: JDBC (Oracle Thin Driver)
- **Configuration**: TOML (via `org.tomlj`)
- **CLI Framework**: picocli
- **Build System**: Maven (produces a single, standalone 'fat' JAR)

---

## Supported Use Cases

| ID | Use Case | Description | Status |
|---|---|---|---|
| **UC1** | **Simple Export** | Single query to one sheet in one file. | ✅ |
| **UC2** | **Data Split** | Splitting large data by row count or category into multiple sheets/files. | ✅ |
| **UC3** | **Consolidated Report** | Appending multiple different queries into a single worksheet. | ⏳ Planned |
| **UC4** | **Complex Batch Export** | Coordinating multiple files and sheets in a single execution run. | ✅ |
