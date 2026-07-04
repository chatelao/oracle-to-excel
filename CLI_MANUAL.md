# CLI Manual - Oracle to Excel Exporter

This manual provides a comprehensive guide on how to use the Oracle to Excel Exporter CLI tool, including configuration examples for various use cases and advanced features.

## Table of Contents
1. [Overview](#overview)
2. [Installation](#installation)
3. [Basic Usage](#basic-usage)
4. [Configuration Guide](#configuration-guide)
    - [Comments and Variables](#comments-and-variables)
    - [Database Configuration](#database-configuration)
    - [Export Configuration](#export-configuration)
    - [Sheet Configuration](#sheet-configuration)
5. [Use Case Examples](#use-case-examples)
    - [UC1: Simple Export](#uc1-simple-export)
    - [UC2: Data Split (Row Partitioning)](#uc2-data-split)
    - [UC4: Complex Batch Export](#uc4-complex-batch-export)
6. [Advanced Features](#advanced-features)
    - [Dynamic Naming](#dynamic-naming)
    - [Column Selection and Ordering](#column-selection-and-ordering)

---

## Overview

The Oracle to Excel Exporter is a command-line utility designed to automate the process of extracting data from Oracle databases into Excel workbooks. It supports complex export scenarios, including splitting data into multiple sheets or files based on row counts or data categories.

## Installation

### Prerequisites
- **Java 17** or higher must be installed.

### Build
If you are building from source:
```bash
mvn package
```
The executable JAR will be located at `target/oracle-to-excel-0.1.0-SNAPSHOT.jar`.

---

## Basic Usage

The tool is executed by providing a path to a TOML configuration file:

```bash
java -jar target/oracle-to-excel-0.1.0-SNAPSHOT.jar --config path/to/config.toml
```

### CLI Arguments
| Argument | Short | Description | Required |
| --- | --- | --- | --- |
| `--config` | `-c` | Path to the TOML configuration file. | Yes |
| `--audit-sheet` | | Name of the sheet to write audit information to. | No |
| `--help` | `-h` | Show help message and exit. | No |
| `--version` | `-V` | Print version information and exit. | No |

---

## Configuration Guide

The configuration is defined in a [TOML](https://toml.io/) file.

### Comments and Variables
- **Comments**: Use the `#` symbol to add comments. Everything from the `#` to the end of the line is ignored.
- **Variables**: Values (such as database credentials or file paths) are defined directly as key-value pairs within the TOML file.

### Database Configuration
The `[database]` section defines the connection details.

```toml
[database]
# The JDBC connection URL for the Oracle database
url = "jdbc:oracle:thin:@//localhost:1521/FREEPDB1"
# Database user account
username = "scott"
# Password for the database user (stored in plain text; ensure file permissions are restricted)
password = "tiger"
```

### Audit Configuration
The `[audit]` section defines global audit settings.

```toml
[audit]
# The name of the sheet where query execution details will be recorded.
# If specified, this sheet will be added to every exported Excel file.
sheet = "Execution_Audit"
```

### Export Configuration
The `[[exports]]` array defines one or more export tasks, each corresponding to a physical Excel file.

```toml
[[exports]]
# The name of the target Excel file
filename = "output_file.xlsx"
```

### Sheet Configuration
The `[[exports.sheets]]` array (nested under an export) defines the sheets within that file.

| Field | Type | Description |
| --- | --- | --- |
| `name` | String | The base name for the Excel sheet. |
| `query` | String | The SQL query to execute. Can also be a path to a `.sql` file relative to the TOML configuration. |
| `partition_size` | Integer | (Optional) Max rows per sheet. Splits into `name_1`, `name_2`, etc. |
| `top_offset` | Integer | (Optional) Number of empty rows at the top of the sheet. |
| `left_offset` | Integer | (Optional) Number of empty columns at the left of the sheet. |
| `columns` | Array | (Optional) List of columns to include and their order. |
| `include_columns` | Array | (Optional) Alias for `columns`. |
| `exclude_columns` | Array | (Optional) List of columns to exclude from the final sheet. |
| `sheetname_columns` | Array | (Optional) Columns used to dynamically name sheets. |
| `filename_columns` | Array | (Optional) Columns used to dynamically name files. |
| `column_colors` | Table | (Optional) Map of column names to hex background colors (e.g., `#RRGGBB`). |
| `pivot_table` | Boolean | (Optional) If `true`, generates a companion sheet with a pivot table. |
| `page_title` | Boolean | (Optional) If `true`, adds two title rows at the top. The first row contains the sheet name in font size 20 and bold. |

### CSV/TSV Specific Options (in `[[exports]]`)
| Field | Type | Description |
| --- | --- | --- |
| `delimiter` | String | (Optional) The character used to separate values (default: `,` for CSV, `\t` for TSV). |
| `quote_char` | String | (Optional) The character used to wrap fields (default: `"`). |
| `escape_char` | String | (Optional) The character used to escape the quote character within a field (default: `"`). |

---

## Use Case Examples

### UC1: Simple Export
Export a single query to one sheet in one file.

```toml
[[exports]]
# Target filename
filename = "emp_report.xlsx"
  [[exports.sheets]]
  # Name of the worksheet
  name = "All_Employees"
  # SQL query to fetch data
  query = "SELECT * FROM EMP"
```

### UC2: Data Split (Row Partitioning)
Split a large result set into multiple sheets of 10,000 rows each.

```toml
[[exports]]
filename = "large_emp_report.xlsx"
  [[exports.sheets]]
  name = "Data"
  query = "SELECT * FROM EMP"
  # Maximum number of rows per worksheet
  partition_size = 10000
```
This will create sheets named `Data_1`, `Data_2`, etc.

### UC4: Complex Batch Export
Multiple queries into multiple sheets and files.

```toml
# First file: HR reports
[[exports]]
filename = "hr_reports.xlsx"
  [[exports.sheets]]
  name = "Departments"
  query = "SELECT * FROM DEPT"
  [[exports.sheets]]
  name = "Employees"
  query = "SELECT * FROM EMP"

# Second file: Salary reports
[[exports]]
filename = "salary_reports.xlsx"
  [[exports.sheets]]
  name = "Salaries"
  query = "SELECT * FROM EMP"
```

### CSV/TSV Export
Export data to CSV or TSV format with custom formatting.

```toml
[[exports]]
filename = "emp_data.csv"
# Use semicolon as delimiter
delimiter = ";"
# Use single quote for wrapping fields
quote_char = "'"
# Use backslash for escaping
escape_char = "\\"
  [[exports.sheets]]
  name = "All_Employees"
  query = "SELECT * FROM EMP"
```

For TSV files, the tab delimiter is automatically used if not explicitly specified:

```toml
[[exports]]
filename = "emp_data.tsv"
  [[exports.sheets]]
  name = "All_Employees"
  query = "SELECT * FROM EMP"
```

---

## Advanced Features

### Dynamic Naming
You can split data into separate sheets or even separate files based on the values in certain columns.

#### Dynamic Sheet Naming
```toml
[[exports]]
filename = "emp_by_job.xlsx"
  [[exports.sheets]]
  name = "Emp"
  query = "SELECT * FROM EMP"
  # Column(s) whose values will be appended to the sheet name
  sheetname_columns = ["JOB"]
```
If `JOB` has values 'CLERK' and 'MANAGER', you will get sheets `Emp_CLERK` and `Emp_MANAGER`.

#### Dynamic File Naming
```toml
[[exports]]
filename = "emp_report.xlsx"
  [[exports.sheets]]
  name = "Data"
  query = "SELECT * FROM EMP"
  # Column(s) whose values will be appended to the filename
  filename_columns = ["DEPTNO"]
```
If `DEPTNO` has 10 and 20, it will create `emp_report_10.xlsx` and `emp_report_20.xlsx`.

### Column Selection and Ordering
Use the `columns` (or `include_columns`) field to filter which columns are exported and define their order in the Excel file. Use `exclude_columns` to remove specific columns from the output.

#### Include Columns
```toml
[[exports]]
filename = "emp_contact_list.xlsx"
  [[exports.sheets]]
  name = "Contacts"
  query = "SELECT ENAME, JOB, SAL, COMM, EMPNO FROM EMP"
  # Explicitly select and order columns for export
  include_columns = ["ENAME", "JOB", "SAL"]
```
Only `ENAME`, `JOB`, and `SAL` will be exported, in that specific order.

#### Exclude Columns
You can exclude columns that might be used for other purposes like dynamic naming, but should not appear in the final table.

```toml
[[exports]]
filename = "emp_report.xlsx"
  [[exports.sheets]]
  name = "Data"
  query = "SELECT * FROM EMP"
  # Exclude internal IDs and columns used for sheet naming
  exclude_columns = ["EMPNO", "DEPTNO"]
  sheetname_columns = ["DEPTNO"]
```
The `DEPTNO` column will be used to name the sheets (e.g., `Data_10`, `Data_20`), but neither `DEPTNO` nor `EMPNO` will appear as columns in the Excel sheets.

### Page Title
You can add a large, bold title at the top of your sheets by enabling `page_title`.

```toml
[[exports]]
filename = "styled_report.xlsx"
  [[exports.sheets]]
  name = "Employees_2024"
  query = "SELECT * FROM EMP"
  # Enable page title
  page_title = true
```
This will add two rows at the top. The first row will contain "Employees_2024" in font size 20, and the second row will be empty, followed by your data table.

### Column Background Colors
You can highlight specific columns by defining background colors in the `column_colors` table. Colors are specified as hex codes.

```toml
[[exports]]
filename = "salary_report.xlsx"
  [[exports.sheets]]
  name = "Salaries"
  query = "SELECT ENAME, SAL, COMM, JOB FROM EMP"
  [exports.sheets.column_colors]
  # Highlight the SAL column in light blue
  SAL = "#ADD8E6"
  # Highlight the COMM column in light green
  COMM = "#90EE90"
```

### External SQL Files
Instead of embedding long SQL queries directly in the TOML file, you can reference external `.sql` files. The path to the SQL file should be relative to the location of the TOML configuration file.

**`config.toml`**
```toml
[[exports]]
filename = "emp_report.xlsx"
  [[exports.sheets]]
  name = "Employees"
  # Path to the external SQL file
  query = "queries/all_emp.sql"
```

**`queries/all_emp.sql`**
```sql
SELECT
    ENAME,
    JOB,
    SAL,
    HIREDATE
FROM
    EMP
WHERE
    DEPTNO = 10
ORDER BY
    ENAME
```
