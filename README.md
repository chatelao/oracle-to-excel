# Oracle to Excel Exporter

A command-line tool to export Oracle database queries into Excel workbooks. Supports multiple queries, multiple sheets, and automated row-based partitioning.

## Features

- **Multi-Sheet Export**: Run multiple queries and save them into separate sheets within a single Excel file.
- **Multi-File Export**: Configure multiple export tasks, each targeting a different file.
- **Row Partitioning**: Automatically split large query results across multiple sheets based on a configurable row limit.
- **TOML Configuration**: Easy-to-use configuration format for defining database connections and export tasks.
- **Standalone JAR**: Distributed as a single executable JAR file with all dependencies included.

## Installation

### Prerequisites

- Java 17 or higher.
- Maven (for building from source).

### Build from Source

1. Clone the repository.
2. Run the build command:
   ```bash
   mvn package
   ```
3. The executable JAR will be generated at `target/oracle-to-excel-0.1.0-SNAPSHOT.jar`.

## Usage

Run the tool by providing a path to your TOML configuration file:

```bash
java -jar target/oracle-to-excel-0.1.0-SNAPSHOT.jar --config path/to/your/config.toml
```

### CLI Options

| Option | Short | Description | Required |
| --- | --- | --- | --- |
| `--config` | `-c` | Path to the TOML configuration file. | Yes |
| `--audit-sheet` | | Name of the sheet to write audit information to. | No |
| `--help` | `-h` | Show help message and exit. | No |
| `--version` | `-V` | Print version information and exit. | No |

## Configuration Options

The tool is configured via a TOML file. Below are the available sections and keys.

### Database Settings (`[database]`)
- `url`: JDBC connection URL for the Oracle database.
- `username`: Database username.
- `password`: Database password.

### Audit Settings (`[audit]`)
- `sheet`: (Optional) Global name for the execution audit sheet added to all generated files.

### Export Tasks (`[[exports]]`)
- `filename`: Target Excel filename.
- `sheets`: Array of sheet configurations.

### Sheet Settings (`[[exports.sheets]]`)
- `name`: Base name for the worksheet.
- `query`: SQL query string or path to a `.sql` file (relative to config).
- `partition_size`: (Optional) Max rows per sheet for partitioning.
- `columns`: (Optional) List of columns to include and their order.
- `sheetname_columns`: (Optional) Columns used for dynamic sheet naming.
- `filename_columns`: (Optional) Columns used for dynamic file naming.
- `column_colors`: (Optional) Mapping of column names to hex colors (e.g., `{ TOTAL = "#90EE90" }`).
- `top_offset`: (Optional) Number of empty rows at the top of the sheet.
- `left_offset`: (Optional) Number of empty columns at the left of the sheet.
- `pivot_table`: (Optional) Boolean to generate a companion pivot table sheet.
- `page_title`: (Optional) Boolean to add a large, stylized title row at the top.

### Configuration Example (`config.toml`)

```toml
[database]
url = "jdbc:oracle:thin:@your-host:1521:your-service"
username = "scott"
password = "tiger"

[audit]
sheet = "Execution_Log"

[[exports]]
filename = "hr_report.xlsx"

  [[exports.sheets]]
  name = "Employee_Data"
  query = "SELECT * FROM EMP"
  # Optional: Split results into sheets of 10,000 rows each (Employee_Data_1, Employee_Data_2, etc.)
  partition_size = 10000
  page_title = true
  column_colors = { REVENUE = "#ADD8E6" }

  [[exports.sheets]]
  name = "Department_Summary"
  query = "SELECT DNAME, COUNT(*) FROM DEPT GROUP BY DNAME"

[[exports]]
filename = "audit_log.xlsx"
  [[exports.sheets]]
  name = "Full_Audit"
  query = "SELECT * FROM EMP"

## Documentation

- [CLI Manual](CLI_MANUAL.md) - Detailed guide and advanced features.
- [Concept](CONCEPT.md)
- [Design](DESIGN.md)
- [Roadmap](ROADMAP.md)

## License

This project is licensed under the MIT License.
