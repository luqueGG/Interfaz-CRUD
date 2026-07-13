
# Requirements Document

## Introduction

This document defines the requirements for the **SolidToxic CRUD** feature — a complete Create, Read, Update, and Delete data management system.

## Glossary

- **Operator**: A person authorized to interact with the system to perform data management operations such as creating, modifying, deleting, inactivating, reactivating, and viewing records.
- **System**: The software application responsible for managing data, enforcing business rules, processing user requests, and maintaining information in the database.
- **Grid_Data**: The graphical component that displays a tabular list of records stored in the system and allows the Operator to select records for maintenance operations.
- **Register_Form**: The graphical component that contains the input fields used by the Operator to create, view, or modify the information of a single record.
- **State_Record**: A field that indicates the current operational status of a record. Valid values are `A` (Active), `I` (Inactive), and `*` (Deleted).
- **database**: The persistent storage system used by the application to store, retrieve, update, and maintain all managed records.

---

## Requirements

### Requirement 1: Maintain Tables

**User Story:** As an Operator, I want to maintain tables, so that organizational data can be managed.

#### Acceptance Criteria

1. WHEN an Operator adds a record, THEN THE System SHALL clean up the Register_Form, except the state record which is always ‘A’, means Active, in this context.
2. WHEN an Operator selects a record in order to modify it, THEN THE System SHALL show the Register_Form with the loaded data, and THE System SHALL enable the operator to modify these fields, with an exception for the ID and the State_Record.
3. WHEN an Operator selects any record in order to eliminate it, THEN THE System SHALL modify the State_Record in ‘*’, which means logic elimination.
4. WHEN an Operator selects any record in order to inactivate it, THEN THE System SHALL modify the State_Record in ‘I’, which means Inactivation.
5. WHEN an Operator selects any record in order to reactivate it, THEN THE System SHALL modify the State_Record in ‘A’, which means Reactivation.
6. If an Operator wants to execute the operation, THEN THE System SHALL execute that operation and refresh the grid.
7. If an Operator wants to cancel the operation, THEN THE System SHALL will delete all current modifications if these are not saved.

---

### Requirement 2: Show Table Grid

**User Story:** As an Operator, I want to view the Grid_Data and select records from the Grid_Data, so that I can perform maintenance operations on them.

#### Acceptance Criteria

1. WHEN THE System starts successfully, THEN THE System SHALL display all active records in the Grid_Data.
2. WHEN an Operator selects a record from the Grid_Data, THEN THE System SHALL load the selected record into the Register_Form.
3. WHEN a record is loaded into the Register_Form, THEN THE System SHALL display all associated field values.
4. WHEN an Operator selects a different record, THEN THE System SHALL replace the previously displayed data with the newly selected record.
5. WHEN no records exist, THEN THE System SHALL display an empty Grid_Data.

---

### Requirement 3: Modify Record 

**User Story:** As an Operator, I want to modify an existing record, so that I can correct or update its information.

#### Acceptance Criteria

1. WHEN an Operator selects a record and activates the Modify command, THEN THE System SHALL load the selected record into the Register_Form.
2. WHEN a record is loaded for modification, THEN THE System SHALL enable editing only for the permitted fields.
3. WHEN a record is loaded for modification, THEN THE System SHALL prevent modification of the record identifier.
4. WHEN a record is loaded for modification, THEN THE System SHALL prevent manual modification of the State_Record field.
5. WHEN an Operator confirms the modification by selecting Update, THEN THE System SHALL store the modified values in the database.
6. WHEN the modification is completed successfully, THEN THE System SHALL refresh the Grid_Data with the updated information.

---

### Requirement 4: Record State Management 

**User Story:** As an Operator, I want to manage the operational state of records, so that inactive or deleted records are properly controlled.

#### Acceptance Criteria

1. WHEN an Operator selects the Delete command, THEN THE System SHALL perform a logical deletion instead of physically removing the record.
2. WHEN a record is logically deleted, THEN THE System SHALL assign the value "*" to the State_Record.
3. WHEN an Operator selects the Inactivate command, THEN THE System SHALL assign the value "I" to the State_Record.
4. WHEN an Operator selects the Reactivate command, THEN THE System SHALL assign the value "A" to the State_Record.
5. WHEN any state change is successfully completed, THEN THE System SHALL update the database.
6. WHEN any state change is successfully completed, THEN THE System SHALL refresh the Grid_Data.

---

### Requirement 5: Update Confirmation

**User Story:** As an Operator, I want updates to be confirmed before they are stored, so that unintended changes are avoided.

#### Acceptance Criteria

1. WHEN an Operator selects the Update command, THEN THE System SHALL verify that an operation is currently pending.
2. WHEN no pending operation exists, THEN THE System SHALL display an informative message indicating that no update can be performed.
3. WHEN an update is completed successfully, THEN THE System SHALL clear the Register_Form.
4. WHEN an update is completed successfully, THEN THE System SHALL reset the internal update state.

---

### Requirement 6: Cancel Operation

**User Story:** As an Operator, I want to cancel the current operation, so that I can abandon unintended changes.

#### Acceptance Criteria

1. WHEN an Operator selects the Cancel command, THEN THE System SHALL clear all fields in the Register_Form.
2. WHEN the current operation is cancelled, THEN THE System SHALL disable the active operation mode.
3. WHEN the current operation is cancelled, THEN THE System SHALL discard all unsaved changes.
4. WHEN the current operation is cancelled, THEN THE System SHALL reset the update state.

---

### Requirement 7: Input Validation

**User Story:** As an Operator, I want the system to validate all entered data, so that only valid records are stored.

#### Acceptance Criteria

1. WHEN an Operator submits a record with missing required fields, THEN THE System SHALL reject the operation.
2. WHEN an Operator submits a duplicate record identifier, THEN THE System SHALL reject the operation.
3. WHEN an Operator submits a field exceeding its maximum permitted length, THEN THE System SHALL reject the operation.
4. WHEN validation fails, THEN THE System SHALL identify every invalid field.
5. WHEN validation fails, THEN THE System SHALL prevent any modification of the database.

---

### Requirement 8: Database Connectivity

**User Story:** As an Operator, I want the application to communicate reliably with the database, so that all maintenance operations can be completed.

#### Acceptance Criteria

1. WHEN THE System starts, THEN THE System SHALL establish a connection with the database.
2. WHEN the database connection cannot be established, THEN THE System SHALL notify the Operator.
3. WHEN the database connection is unavailable, THEN THE System SHALL prevent maintenance operations.
4. WHEN the database connection is restored, THEN THE System SHALL allow maintenance operations to continue.

---

### Requirement 9: Error Handling

**User Story:** As an Operator, I want meaningful error messages, so that I can understand and resolve operational problems.

#### Acceptance Criteria

1. WHEN an unexpected error occurs, THEN THE System SHALL display an informative error message.
2. WHEN a database operation fails, THEN THE System SHALL notify the Operator without exposing internal implementation details.
3. WHEN an operation fails, THEN THE System SHALL preserve database consistency.
4. WHEN an operation fails, THEN THE System SHALL allow the Operator to continue using the application whenever possible.

