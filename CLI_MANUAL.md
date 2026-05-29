# CLI Manual - Oracle to Excel Exporter

This manual provides a comprehensive guide on how to use the Oracle to Excel Exporter CLI tool, including configuration examples for various use cases and advanced features.

## Table of Contents
1. [Overview](#overview)
2. [Installation](#installation)
3. [Basic Usage](#basic-usage)
4. [Configuration Guide](#configuration-guide)
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
| `--help` | `-h` | Show help message and exit. | No |
| `--version` | `-V` | Print version information and exit. | No |

---

## Configuration Guide

The configuration is defined in a [TOML](https://toml.io/) file.

### Database Configuration
The `[database]` section defines the connection details.

```toml
[database]
url = "jdbc:oracle:thin:@//localhost:1521/FREEPDB1"
username = "system"
password = "password"
```

### Export Configuration
The `[[exports]]` array defines one or more export tasks (files).

```toml
[[exports]]
filename = "output_file.xlsx"
```

### Sheet Configuration
The `[[exports.sheets]]` array (nested under an export) defines the sheets within that file.

| Field | Type | Description |
| --- | --- | --- |
| `name` | String | The base name for the Excel sheet. |
| `query` | String | The SQL query to execute. |
| `partition_size` | Integer | (Optional) Max rows per sheet. Splits into `name_1`, `name_2`, etc. |
| `columns` | Array | (Optional) List of columns to include and their order. |
| `name_columns` | Array | (Optional) Columns used to dynamically name sheets. |
| `filename_columns` | Array | (Optional) Columns used to dynamically name files. |

---

## Use Case Examples

### UC1: Simple Export
Export a single query to one sheet in one file.

```toml
[[exports]]
filename = "employees.xlsx"
  [[exports.sheets]]
  name = "All_Employees"
  query = "SELECT * FROM employees"
```

### UC2: Data Split (Row Partitioning)
Split a large result set into multiple sheets of 10,000 rows each.

```toml
[[exports]]
filename = "large_report.xlsx"
  [[exports.sheets]]
  name = "Data"
  query = "SELECT * FROM large_table"
  partition_size = 10000
```
This will create sheets named `Data_1`, `Data_2`, etc.

### UC4: Complex Batch Export
Multiple queries into multiple sheets and files.

```toml
[[exports]]
filename = "hr_reports.xlsx"
  [[exports.sheets]]
  name = "Departments"
  query = "SELECT * FROM departments"
  [[exports.sheets]]
  name = "Jobs"
  query = "SELECT * FROM jobs"

[[exports]]
filename = "financial_reports.xlsx"
  [[exports.sheets]]
  name = "Salaries"
  query = "SELECT * FROM salary_history"
```

---

## Advanced Features

### Dynamic Naming
You can split data into separate sheets or even separate files based on the values in certain columns.

#### Dynamic Sheet Naming
```toml
[[exports]]
filename = "sales_by_region.xlsx"
  [[exports.sheets]]
  name = "Sales"
  query = "SELECT * FROM sales"
  name_columns = ["REGION"]
```
If `REGION` has values 'North' and 'South', you will get sheets `Sales_North` and `Sales_South`.

#### Dynamic File Naming
```toml
[[exports]]
filename = "sales_report.xlsx"
  [[exports.sheets]]
  name = "Data"
  query = "SELECT * FROM sales"
  filename_columns = ["YEAR"]
```
If `YEAR` has 2023 and 2024, it will create `sales_report_2023.xlsx` and `sales_report_2024.xlsx`.

### Column Selection and Ordering
Use the `columns` field to filter which columns are exported and define their order in the Excel file.

```toml
[[exports]]
filename = "contact_list.xlsx"
  [[exports.sheets]]
  name = "Contacts"
  query = "SELECT first_name, last_name, email, phone_number, id FROM users"
  columns = ["LAST_NAME", "FIRST_NAME", "EMAIL"]
```
Only `LAST_NAME`, `FIRST_NAME`, and `EMAIL` will be exported, in that specific order.
