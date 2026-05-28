# TECHNICAL_DEBTS.md

This file tracks technical debts, such as outdated components, security flaws, and old patterns, identified during the development of the Oracle to Excel Exporter.

## Current Technical Debts
- **Non-standard project structure**: The project uses `/src` and `/test` instead of the standard Maven `/src/main/java` and `/src/test/java`. This has been configured in `pom.xml` but might cause confusion for developers familiar with standard Maven layouts.
