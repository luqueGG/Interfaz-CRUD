package com.solidtoxic.gui.util;

import javafx.scene.control.Control;
import javafx.scene.control.TextInputControl;

import java.util.ArrayList;
import java.util.List;

/**
 * Client-side field validation — mirrors backend rules so the operator gets
 * instant feedback before any HTTP request is sent. Req 7-1 to 7-3 on client side.
 */
public final class FormValidator {

    private static final String ERROR_STYLE = "-fx-border-color: red; -fx-border-width: 1.5;";
    private static final String NORMAL_STYLE = "";

    private FormValidator() {}

    /**
     * Validates that the given control is not blank.
     * Returns the error message if invalid, null if valid.
     */
    public static String requireNotBlank(TextInputControl field, String fieldName) {
        if (field.getText() == null || field.getText().isBlank()) {
            field.setStyle(ERROR_STYLE);
            return fieldName + " is required.";
        }
        field.setStyle(NORMAL_STYLE);
        return null;
    }

    /**
     * Validates that the given control does not exceed maxLength.
     */
    public static String requireMaxLength(TextInputControl field, String fieldName, int maxLength) {
        if (field.getText() != null && field.getText().length() > maxLength) {
            field.setStyle(ERROR_STYLE);
            return fieldName + " must not exceed " + maxLength + " characters.";
        }
        field.setStyle(NORMAL_STYLE);
        return null;
    }

    /**
     * Collects all non-null messages from a batch of validations.
     */
    public static List<String> collect(String... results) {
        List<String> errors = new ArrayList<>();
        for (String r : results) {
            if (r != null) errors.add(r);
        }
        return errors;
    }

    /**
     * Resets all border styles on a list of controls.
     */
    public static void clearStyles(Control... controls) {
        for (Control c : controls) {
            c.setStyle(NORMAL_STYLE);
        }
    }
}
