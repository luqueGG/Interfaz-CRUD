package com.solidtoxic.gui.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Static helpers for showing JavaFX alert dialogs.
 */
public final class AlertUtil {

    private AlertUtil() {}

    public static void showError(String title, String message) {
        show(AlertType.ERROR, title, message);
    }

    public static void showError(String message) {
        show(AlertType.ERROR, "Error", message);
    }

    public static void showWarning(String message) {
        show(AlertType.WARNING, "Warning", message);
    }

    public static void showInfo(String message) {
        show(AlertType.INFORMATION, "Information", message);
    }

    private static void show(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
