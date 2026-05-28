# DESIGN.md

## Introduction
This document outlines the detailed design of the Oracle to Excel Exporter tool, deriving technical choices from the `CONCEPT.md`.

## Architecture Overview
![Top Level Architecture](https://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/chatelao/oracle-to-excel/main/TOP_ARCHITECTURE.puml)

## Tech Stack
- **Language**: Java 17 (or higher)
- **Build Tool**: Maven
- **Database Connectivity**: JDBC (Oracle Thin Driver)
- **Excel Manipulation**: Apache POI
- **Configuration Format**: TOML
- **CLI Framework**: picocli

## Detailed Component Design

### 1. CLI Orchestrator
- **Responsibility**: Entry point of the application, parses command-line arguments, and coordinates other components.
- **Interface**: `main(String[] args)`
- **Library**: `info.picocli:picocli`

### 2. Configuration Manager
- **Responsibility**: Reads the TOML configuration file and maps it to internal Java objects.
- **Interface**: `Config loadConfig(Path configPath)`
- **Library**: `org.tomlj:tomlj`

### 3. Oracle Query Engine
- **Responsibility**: Manages database connections and executes SQL queries.
- **Interface**: `ResultSet executeQuery(String sql, Properties connectionProps)`
- **Library**: `com.oracle.database.jdbc:ojdbc8`

### 4. Data Processing Layer
- **Responsibility**: Transforms `ResultSet` data into a structure suitable for Excel export, handling mapping and partitioning.
- **Interface**: `List<SheetData> processData(ResultSet rs, MappingConfig config)`

### 5. Excel Export Engine
- **Responsibility**: Creates Excel workbooks and sheets, writes data, and saves files.
- **Interface**: `void export(List<SheetData> data, Path outputPath)`
- **Library**: `org.apache.poi:poi-ooxml`

## Major Choices

### Choice 1: Build Tool
- **Alternative A: Maven (Chosen)**: Mature, industry standard, excellent dependency management for Java projects.
- **Alternative B: Gradle**: Flexible and powerful, but Maven's XML-based configuration is often more straightforward for simple CLI tools.
- **Alternative C: Ant**: Older and lacks built-in dependency management compared to Maven and Gradle.

### Choice 2: CLI Library
- **Alternative A: picocli (Chosen)**: Modern, supports subcommands, autocompletion, and is very easy to use with annotations.
- **Alternative B: Commons CLI**: A classic choice, but more verbose and lacks some of the modern features of picocli.
- **Alternative C: JCommander**: Similar to picocli but less actively maintained and fewer features.

### Choice 3: TOML Library
- **Alternative A: tomlj (Chosen)**: A lightweight and easy-to-use TOML parser for Java.
- **Alternative B: Toml4j**: Popular, but `tomlj` is often preferred for its simplicity and compliance.
- **Alternative C: Jackson with TOML dataformat**: Powerful but might be overkill for simple configuration reading.

## Discarded Alternatives Summary
- **Gradle/Ant**: Discarded in favor of Maven for its simplicity and standardization.
- **Commons CLI/JCommander**: Discarded in favor of picocli for its modern feature set and ease of use.
- **Toml4j/Jackson**: Discarded in favor of `tomlj` for being lightweight and focused.
