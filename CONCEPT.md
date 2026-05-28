# CONCEPT.md

## Project Goal
Download one or more queries from an Oracle-DB into multiple sheets of an Excel.

## Business Case
The manual extraction of data from Oracle databases into Excel spreadsheets is time-consuming and error-prone. This project aims to provide an automated, configurable tool to streamline this process, ensuring data consistency and saving valuable time for business analysts and developers.

## Use Cases
- **UC1: Simple Export**: Download one query into one sheet and/or file.
- **UC2: Data Split**: Download one query into multiple sheets and/or files (e.g., based on data volume or categories).
- **UC3: Consolidated Report**: Download multiple queries into one sheet and/or file.
- **UC4: Complex Batch Export**: Download multiple queries into multiple sheets and/or files.

## High-Level Architecture
The system consists of the following top-level functional components:

1.  **Configuration Manager**: Reads and validates the system configuration (TOML/.ini).
2.  **Oracle Query Engine**: Connects to the Oracle database and executes SQL queries.
3.  **Data Processing Layer**: Manages the mapping between query results and Excel structures.
4.  **Excel Export Engine**: Handles the creation and population of Excel files and sheets.
5.  **CLI Orchestrator**: The main entry point that coordinates the components based on user input.

### Business Interfaces
- **Config Interface**: Accepts configuration files defining sources, targets, and mappings.
- **Query Interface**: Accepts SQL statements and database credentials.
- **Export Interface**: Accepts data streams and sheet/file specifications.

## Major Choices

### Choice 1: Programming Language
- **Alternative A: Java (Chosen)**: Excellent library support for Oracle (JDBC) and Excel (Apache POI). High performance and portability via JAR.
- **Alternative B: Python**: Great for data tasks, but distribution as a standalone CLI tool can be more complex than a JAR.
- **Alternative C: Go**: Fast and easy to distribute, but Excel library ecosystem is less mature than Java's.

### Choice 2: Configuration Format
- **Alternative A: TOML (Chosen)**: Human-readable, supports nested structures, and specifically requested.
- **Alternative B: JSON**: Standard, but less user-friendly for manual configuration editing.
- **Alternative C: YAML**: Very flexible but can be prone to indentation errors.

### Choice 3: Excel Library
- **Alternative A: Apache POI (Chosen)**: The industry standard for Java, highly flexible and well-documented.
- **Alternative B: JXLS**: Good for template-based exports, but might be less flexible for dynamic multi-sheet scenarios.
- **Alternative C: FastExcel**: Focuses on performance and low memory footprint, but has fewer features than POI.

## Discarded Alternatives Summary
- **Python/Go**: Discarded in favor of Java for better ecosystem alignment and distribution requirements.
- **JSON/YAML**: Discarded in favor of TOML for better balance of readability and structure.
- **JXLS/FastExcel**: Discarded in favor of Apache POI for its comprehensive feature set and reliability.
