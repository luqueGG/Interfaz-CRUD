package com.solidtoxic.gui.component;

import com.solidtoxic.gui.util.AlertUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.*;
import java.util.function.Consumer;

/**
 * Generic form panel that renders fields from FieldDescriptor list and exposes
 * the full 7-button command bar with state-machine-driven enable/disable logic.
 * Req 1, 3, 5, 6.
 */
public class RegisterForm extends VBox {

    // ── State ─────────────────────────────────────────────────────────────────
    private OperationMode mode = OperationMode.IDLE;
    private Map<String, Object> selectedRow = null;

    // ── Fields ────────────────────────────────────────────────────────────────
    private final List<FieldDescriptor> descriptors;
    private final Map<String, TextInputControl> fieldControls = new LinkedHashMap<>();

    // ── State badge ───────────────────────────────────────────────────────────
    private final Label stateBadge = new Label("ACTIVE");

    // ── Buttons ───────────────────────────────────────────────────────────────
    private final Button btnAdd        = new Button("Add");
    private final Button btnModify     = new Button("Modify");
    private final Button btnDelete     = new Button("Delete");
    private final Button btnInactivate = new Button("Inactivate");
    private final Button btnReactivate = new Button("Reactivate");
    private final Button btnUpdate     = new Button("Update");
    private final Button btnCancel     = new Button("Cancel");

    // ── Callbacks ─────────────────────────────────────────────────────────────
    private Consumer<Map<String, Object>> onAdd;
    private Consumer<Map<String, Object>> onModify;
    private Consumer<Map<String, Object>> onDelete;
    private Consumer<Map<String, Object>> onInactivate;
    private Consumer<Map<String, Object>> onReactivate;
    private Runnable onCancel;

    public RegisterForm(List<FieldDescriptor> descriptors) {
        this.descriptors = descriptors;
        setSpacing(10);
        setPadding(new Insets(10));
        setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-background-color: #fafafa;");

        getChildren().add(buildStateBadgeRow());
        getChildren().add(buildFieldGrid());
        getChildren().add(buildButtonBar());

        wireButtons();
        refreshButtonStates();
    }

    // ── Build helpers ─────────────────────────────────────────────────────────

    private HBox buildStateBadgeRow() {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getChildren().addAll(new Label("State:"), stateBadge);
        applyBadgeStyle("A");
        return row;
    }

    private GridPane buildFieldGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(5, 0, 5, 0));
        ColumnConstraints labelCol = new ColumnConstraints(130);
        ColumnConstraints fieldCol = new ColumnConstraints();
        fieldCol.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(labelCol, fieldCol);

        int row = 0;
        for (FieldDescriptor fd : descriptors) {
            if ("estReg".equals(fd.key())) continue; // shown as badge only
            Label lbl = new Label(fd.label() + ":");
            TextInputControl control;
            if (fd.isLarge()) {
                TextArea ta = new TextArea();
                ta.setPrefRowCount(2);
                ta.setWrapText(true);
                control = ta;
            } else {
                control = new TextField();
                if (fd.maxLength() > 0) {
                    final int max = fd.maxLength();
                    control.textProperty().addListener((obs, o, n) -> {
                        if (n != null && n.length() > max) ((TextField) control).setText(o);
                    });
                }
            }
            fieldControls.put(fd.key(), control);
            grid.add(lbl, 0, row);
            grid.add(control, 1, row);
            row++;
        }
        return grid;
    }

    private HBox buildButtonBar() {
        HBox bar = new HBox(6);
        bar.setPadding(new Insets(6, 0, 0, 0));
        bar.getChildren().addAll(btnAdd, btnModify, btnDelete, btnInactivate, btnReactivate,
                new Separator(), btnUpdate, btnCancel);

        btnAdd.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnModify.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        btnDelete.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        btnInactivate.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        btnReactivate.setStyle("-fx-background-color: #009688; -fx-text-fill: white;");
        btnUpdate.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white;");
        btnCancel.setStyle("-fx-background-color: #607D8B; -fx-text-fill: white;");
        return bar;
    }

    // ── Button wiring ─────────────────────────────────────────────────────────

    private void wireButtons() {
        // Add — clears form, enters ADD mode (Req 1-1)
        btnAdd.setOnAction(e -> {
            clearFields();
            setAllEditable(true);
            lockPkFields(false);
            mode = OperationMode.ADD;
            selectedRow = null;
            applyBadgeStyle("A");
            refreshButtonStates();
        });

        // Modify — loads row, locks PK, enters MODIFY mode (Req 3-1..4)
        btnModify.setOnAction(e -> {
            if (selectedRow == null) return;
            populateFields(selectedRow);
            setAllEditable(true);
            lockPkFields(true);
            lockEstReg(true);
            mode = OperationMode.MODIFY;
            refreshButtonStates();
        });

        // Delete — immediate PATCH estReg='*' (Req 1-3, 4-1..2)
        btnDelete.setOnAction(e -> {
            if (selectedRow != null && onDelete != null) onDelete.accept(selectedRow);
        });

        // Inactivate — immediate PATCH estReg='I' (Req 1-4, 4-3)
        btnInactivate.setOnAction(e -> {
            if (selectedRow != null && onInactivate != null) onInactivate.accept(selectedRow);
        });

        // Reactivate — immediate PATCH estReg='A' (Req 1-5, 4-4)
        btnReactivate.setOnAction(e -> {
            if (selectedRow != null && onReactivate != null) onReactivate.accept(selectedRow);
        });

        // Update — confirms pending operation (Req 5-1..4)
        btnUpdate.setOnAction(e -> {
            if (mode == OperationMode.IDLE) {
                AlertUtil.showWarning("No pending operation to confirm.");
                return;
            }
            Map<String, Object> data = getData();
            if (mode == OperationMode.ADD && onAdd != null) onAdd.accept(data);
            else if (mode == OperationMode.MODIFY && onModify != null) onModify.accept(data);
        });

        // Cancel — discards changes, resets (Req 6-1..4)
        btnCancel.setOnAction(e -> {
            clearFields();
            mode = OperationMode.IDLE;
            selectedRow = null;
            refreshButtonStates();
            if (onCancel != null) onCancel.run();
        });
    }

    // ── Button enable/disable state machine ───────────────────────────────────

    public void refreshButtonStates() {
        boolean rowSelected = selectedRow != null;
        String estReg = rowSelected ? getEstRegFromRow() : null;

        btnAdd.setDisable(false);                                          // always enabled
        btnModify.setDisable(!rowSelected);
        btnDelete.setDisable(!rowSelected);
        btnInactivate.setDisable(!rowSelected || !"A".equals(estReg));
        btnReactivate.setDisable(!rowSelected || !"I".equals(estReg));
        btnUpdate.setDisable(mode == OperationMode.IDLE);
        btnCancel.setDisable(mode == OperationMode.IDLE);
    }

    // ── Data access ───────────────────────────────────────────────────────────

    public Map<String, Object> getData() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, TextInputControl> entry : fieldControls.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getText());
        }
        return result;
    }

    public void setData(Map<String, Object> row) {
        this.selectedRow = row;
        populateFields(row);
        if (row != null) applyBadgeStyle(getEstRegFromRow());
        refreshButtonStates();
    }

    public void clearAndReset() {
        clearFields();
        mode = OperationMode.IDLE;
        selectedRow = null;
        refreshButtonStates();
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    private void populateFields(Map<String, Object> row) {
        for (Map.Entry<String, TextInputControl> entry : fieldControls.entrySet()) {
            Object val = row.get(entry.getKey());
            entry.getValue().setText(val != null ? val.toString() : "");
        }
        if (row.containsKey("estReg")) applyBadgeStyle(row.get("estReg").toString());
    }

    private void clearFields() {
        fieldControls.values().forEach(c -> c.setText(""));
        applyBadgeStyle("A");
    }

    private void setAllEditable(boolean editable) {
        fieldControls.values().forEach(c -> c.setEditable(editable));
    }

    private void lockPkFields(boolean locked) {
        for (FieldDescriptor fd : descriptors) {
            if (fd.isPk()) {
                TextInputControl ctrl = fieldControls.get(fd.key());
                if (ctrl != null) ctrl.setEditable(!locked);
            }
        }
    }

    private void lockEstReg(boolean locked) {
        TextInputControl ctrl = fieldControls.get("estReg");
        if (ctrl != null) ctrl.setEditable(!locked);
    }

    private String getEstRegFromRow() {
        if (selectedRow == null) return null;
        Object v = selectedRow.get("estReg");
        return v != null ? v.toString() : null;
    }

    private void applyBadgeStyle(String estReg) {
        switch (estReg == null ? "" : estReg) {
            case "A" -> {
                stateBadge.setText("ACTIVE");
                stateBadge.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                        "-fx-padding: 2 8 2 8; -fx-background-radius: 4;");
            }
            case "I" -> {
                stateBadge.setText("INACTIVE");
                stateBadge.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; " +
                        "-fx-padding: 2 8 2 8; -fx-background-radius: 4;");
            }
            case "*" -> {
                stateBadge.setText("DELETED");
                stateBadge.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; " +
                        "-fx-padding: 2 8 2 8; -fx-background-radius: 4;");
            }
            default -> {
                stateBadge.setText("—");
                stateBadge.setStyle("-fx-background-color: #ccc; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");
            }
        }
    }

    // ── Callback setters ──────────────────────────────────────────────────────

    public void setOnAdd(Consumer<Map<String, Object>> handler)        { this.onAdd = handler; }
    public void setOnModify(Consumer<Map<String, Object>> handler)     { this.onModify = handler; }
    public void setOnDelete(Consumer<Map<String, Object>> handler)     { this.onDelete = handler; }
    public void setOnInactivate(Consumer<Map<String, Object>> handler) { this.onInactivate = handler; }
    public void setOnReactivate(Consumer<Map<String, Object>> handler) { this.onReactivate = handler; }
    public void setOnCancel(Runnable handler)                          { this.onCancel = handler; }

    public OperationMode getMode() { return mode; }
}
