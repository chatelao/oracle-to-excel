# ROADMAP.md

This roadmap outlines the plan to implement the Oracle to Excel Exporter, as defined in the `CONCEPT.md`, `DESIGN.md`, and `GEMINI.md`.

## Progress Overview

| Phase | Description | Status |
|-------|-------------|--------|
| 1 | Foundation & Documentation | ✅ |
| 2 | Project Infrastructure | ✅ |
| 3 | Core Engine Development | ✅ |
| 4 | Data & Export Logic | ✅ |
| 5 | CLI & Integration | ✅ |
| 6 | Validation & Release | ✅ |

## Goals

- **UC1: Simple Export**: Download one query into one sheet and/or file ✅
- **UC2: Data Split**: Download one query into multiple sheets and/or file ✅
- **UC3: Consolidated Report**: Download multiple queries into one sheet and/or file ⏳
- **UC4: Complex Batch Export**: Download multiple queries into multiple sheets and/or files ⏳
- **Automated CLI Tool**: Standalone JAR for easy distribution ⏳

---

## Phase 1: Foundation & Documentation
Focus on defining the system architecture and requirements.

- [x] Create `CONCEPT.md`: Define goals, use cases, and high-level architecture.
- [x] Create `DESIGN.md`: Technical stack and component design.
- [x] Create `GEMINI.md`: Project structure and development guidelines.
- [x] Create `TOP_ARCHITECTURE.puml`: Visual representation of components.
- [x] Create `ROADMAP.md`: Project planning and tracking.
- [x] Create `TECHNICAL_DEBTS.md`: Initialize tracking of technical debts. (2026-05-28)

## Phase 2: Project Infrastructure ✅
Setup the development environment and CI/CD pipelines.

- [x] Initialize Maven project with `pom.xml`. (2026-05-28)
- [x] Create `src/install.sh` for build tools. (2026-05-28)
- [x] Create `test/install.sh` for test tools. (2026-05-28)
- [x] Setup GitHub Actions CI/CD workflow with compilation & tests. (2026-05-29)
- [x] Configure Oracle Database integration for CI. (2026-05-29)
- [x] Configure ReadTheDocs (RTD) for documentation publishing. (2026-05-29)

## Phase 3: Core Engine Development ✅
Define interfaces and implement database connectivity and configuration parsing.

- [x] **Configuration Manager** (2026-05-30)
  - [x] Define `Config` internal models. (2026-05-29)
  - [x] Implement TOML parsing using `tomlj`. (2026-05-30)
- [x] **Oracle Query Engine** (2026-05-30)
  - [x] Define `QueryEngine` interface. (2026-05-29)
  - [x] Implement JDBC connection management. (2026-05-30)
  - [x] Implement SQL execution and `ResultSet` handling. (2026-05-30)

## Phase 4: Data & Export Logic ✅
Implement the logic for data transformation and Excel generation.

- [x] **Data Processing Layer** (2026-05-29)
  - [x] Define mapping and partitioning logic. (2026-05-29)
  - [x] Implement `ResultSet` to `SheetData` transformation. (2026-05-30)
- [x] **Excel Export Engine** (2026-05-30)
  - [x] Implement Workbook/Sheet creation using Apache POI. (2026-05-30)
  - [x] Implement data writing and file saving logic. (2026-05-30)

## Phase 5: CLI & Integration ✅
Tie the components together and provide a user-friendly interface.

- [x] **CLI Orchestrator** (2026-05-29)
  - [x] Implement command-line argument parsing with `picocli`. (2026-05-29)
  - [x] Coordinate data flow between components. (2026-05-29)
  - [x] Support both CLI options and TOML configuration files. (2026-05-29)

## Phase 6: Validation & Release ✅
Ensure quality and distribute the tool.

- [x] Implement unit and integration tests in `/test/`. (2026-05-29)
- [x] Implement real Oracle Database integration test. (2026-05-29)
- [x] Verify use cases (UC1-UC4). (2026-05-29)
- [x] Generate standalone JAR as a release asset. (2026-05-29)
- [x] Finalize `README.md` with usage instructions and GitHub Pages link. (2026-05-29)
