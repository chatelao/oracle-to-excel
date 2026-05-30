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

### Configuration Example (`config.toml`)

```toml
[database]
url = "jdbc:oracle:thin:@your-host:1521:your-service"
username = "scott"
password = "tiger"

[[exports]]
filename = "hr_report.xlsx"

  [[exports.sheets]]
  name = "Employee_Data"
  query = "SELECT * FROM EMP"
  # Optional: Split results into sheets of 10,000 rows each (Employee_Data_1, Employee_Data_2, etc.)
  partition_size = 10000

  [[exports.sheets]]
  name = "Department_Summary"
  query = "SELECT DNAME, COUNT(*) FROM DEPT GROUP BY DNAME"

[[exports]]
filename = "audit_log.xlsx"
  [[exports.sheets]]
  name = "Full_Audit"
  query = "SELECT * FROM EMP"
```

## Documentation

- [Concept](CONCEPT.md)
- [Design](DESIGN.md)
- [Roadmap](ROADMAP.md)

## License

This project is licensed under the MIT License.
