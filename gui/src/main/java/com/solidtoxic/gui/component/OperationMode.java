package com.solidtoxic.gui.component;

/**
 * Internal state machine for the RegisterForm.
 * IDLE   — no pending operation; Update/Cancel disabled.
 * ADD    — operator clicked Add; all editable fields enabled; Update/Cancel enabled.
 * MODIFY — operator clicked Modify; editable fields enabled, PK locked; Update/Cancel enabled.
 */
public enum OperationMode {
    IDLE, ADD, MODIFY
}
