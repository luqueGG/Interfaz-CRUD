package com.solidtoxic.gui.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.solidtoxic.gui.client.ApiClient;
import com.solidtoxic.gui.client.BackendUnavailableException;
import com.solidtoxic.gui.util.AlertUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;
import java.util.function.Consumer;

/**
 * Generic form panel that renders fields from FieldDescriptor list.
 * Plain fields → TextField / TextArea.
 * FK fields    → ComboBox populated from GET /api/v1/{fkEndpoint}?state=A.
 *
 * Exposes the full 7-button command bar with state-machine-driven enable/disable logic.
 * Req 1, 3, 5, 6, 10-1..10-4.
 */
public class RegisterForm extends VBox {

    // ── State ─────────────────────────────────────────────────────────────────
    private OperationMode mode = OperationMode.IDLE;
    private Map<String, Object> selectedRow = null;

    // ── Field descriptors ─────────────────────────────────────────────────────
    private final List<FieldDescriptor> descriptors;

    /**
     * Holds the control for each field key.
     * Value is either a TextInputControl (plain/large) or a FkComboBox (FK).
     */
    private final Map<String, Control> fieldControls = new LinkedHashMap<>();

    // ── State badge ───────────────────────────────────────────────────────────
    private final Label stateBadge = new Label("ACTIVO");

    // ── Buttons ───────────────────────────────────────────────────────────────
    private final Button btnAdd        = new Button("Agregar");
    private final Button btnModify     = new Button("Modificar");
    private final Button btnDelete     = new Button("Eliminar");
    private final Button btnInactivate = new Button("Inactivar");
    private final Button btnReactivate = new Button("Reactivar");
    private final Button btnUpdate     = new Button("Actualizar");
    private final Button btnCancel     = new Button("Cancelar");

    // ── Callbacks ─────────────────────────────────────────────────────────────
    private Consumer<Map<String, Object>> onAdd;
    private Consumer<Map<String, Object>> onModify;
    private Consumer<Map<String, Object>> onDelete;
    private Consumer<Map<String, Object>> onInactivate;
    private Consumer<Map<String, Object>> onReactivate;
    private Runnable onCancel;

    // ── API client (for FK loading) ───────────────────────────────────────────
    private final ApiClient api = ApiClient.getInstance();

    // ─────────────────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────────────────

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

        // Load all FK ComboBoxes on construction
        loadAllFkComboBoxes();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Build helpers
    // ─────────────────────────────────────────────────────────────────────────

    private HBox buildStateBadgeRow() {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getChildren().addAll(new Label("Estado:"), stateBadge);
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

            if (fd.isFk()) {
                // ── FK ComboBox ───────────────────────────────────────────
                FkComboBox combo = new FkComboBox(fd);
                fieldControls.put(fd.key(), combo);
                grid.add(lbl, 0, row);
                grid.add(combo, 1, row);
            } else if (fd.isLarge()) {
                // ── TextArea ──────────────────────────────────────────────
                TextArea ta = new TextArea();
                ta.setPrefRowCount(2);
                ta.setWrapText(true);
                fieldControls.put(fd.key(), ta);
                grid.add(lbl, 0, row);
                grid.add(ta, 1, row);
            } else {
                // ── TextField ─────────────────────────────────────────────
                TextField tf = new TextField();
                if (fd.maxLength() > 0) {
                    final int max = fd.maxLength();
                    tf.textProperty().addListener((obs, o, n) -> {
                        if (n != null && n.length() > max) tf.setText(o);
                    });
                }
                fieldControls.put(fd.key(), tf);
                grid.add(lbl, 0, row);
                grid.add(tf, 1, row);
            }
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

    // ─────────────────────────────────────────────────────────────────────────
    // FK ComboBox loading
    // ─────────────────────────────────────────────────────────────────────────

    /** Loads all FK ComboBoxes. Called once at construction; can be re-called to refresh. */
    public void loadAllFkComboBoxes() {
        for (FieldDescriptor fd : descriptors) {
            if (fd.isFk()) {
                loadFkComboBox((FkComboBox) fieldControls.get(fd.key()), fd);
            }
        }
    }

    private void loadFkComboBox(FkComboBox combo, FieldDescriptor fd) {
        try {
            var resp = api.get("/api/v1/" + fd.fkEndpoint() + "?state=A");
            if (resp.isSuccess()) {
                List<Map<String, Object>> rows = api.getMapper()
                        .readValue(resp.getBody(), new TypeReference<>() {});
                combo.setItems(rows, fd.fkValueKey(), fd.fkLabelKey());
                combo.setDisable(false);
            } else {
                combo.setDisable(true);
                AlertUtil.showWarning("No se pudieron cargar las opciones para \"" + fd.label() + "\" — campo deshabilitado.");
            }
        } catch (BackendUnavailableException e) {
            combo.setDisable(true);
            AlertUtil.showWarning("Backend no disponible: ComboBox de \"" + fd.label() + "\" deshabilitado.");
        } catch (Exception e) {
            combo.setDisable(true);
            AlertUtil.showWarning("Error al cargar \"" + fd.label() + "\": " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Button wiring
    // ─────────────────────────────────────────────────────────────────────────

    private void wireButtons() {
        // Add — clears form, enters ADD mode (Req 1-1)
        btnAdd.setOnAction(e -> {
            clearFields();
            setAllEditable(true);
            lockPkFields(false);
            mode = OperationMode.ADD;
            selectedRow = null;
            applyBadgeStyle("A");
            loadAllFkComboBoxes();
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
                AlertUtil.showWarning("No hay ninguna operación pendiente por confirmar.");
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

    // ─────────────────────────────────────────────────────────────────────────
    // Button enable/disable state machine
    // ─────────────────────────────────────────────────────────────────────────

    public void refreshButtonStates() {
        boolean rowSelected = selectedRow != null;
        String estReg = rowSelected ? getEstRegFromRow() : null;

        btnAdd.setDisable(false);
        btnModify.setDisable(!rowSelected);
        btnDelete.setDisable(!rowSelected);
        btnInactivate.setDisable(!rowSelected || !"A".equals(estReg));
        btnReactivate.setDisable(!rowSelected || !"I".equals(estReg));
        btnUpdate.setDisable(mode == OperationMode.IDLE);
        btnCancel.setDisable(mode == OperationMode.IDLE);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Data access
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns form values as a map keyed by field key.
     * FK fields contribute their stored value (the PK of the referenced row).
     */
    public Map<String, Object> getData() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, Control> entry : fieldControls.entrySet()) {
            Control ctrl = entry.getValue();
            if (ctrl instanceof FkComboBox fk) {
                result.put(entry.getKey(), fk.getSelectedValue());
            } else if (ctrl instanceof TextInputControl tic) {
                result.put(entry.getKey(), tic.getText());
            }
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

    // ─────────────────────────────────────────────────────────────────────────
    // Internal helpers
    // ─────────────────────────────────────────────────────────────────────────

    private void populateFields(Map<String, Object> row) {
        for (Map.Entry<String, Control> entry : fieldControls.entrySet()) {
            Object val = row.get(entry.getKey());
            String strVal = val != null ? val.toString() : "";
            Control ctrl = entry.getValue();
            if (ctrl instanceof FkComboBox fk) {
                fk.selectByValue(strVal);
            } else if (ctrl instanceof TextInputControl tic) {
                tic.setText(strVal);
            }
        }
        if (row.containsKey("estReg")) applyBadgeStyle(row.get("estReg").toString());
    }

    private void clearFields() {
        for (Control ctrl : fieldControls.values()) {
            if (ctrl instanceof FkComboBox fk) {
                fk.clearSelection();
            } else if (ctrl instanceof TextInputControl tic) {
                tic.setText("");
            }
        }
        applyBadgeStyle("A");
    }

    private void setAllEditable(boolean editable) {
        for (Control ctrl : fieldControls.values()) {
            if (ctrl instanceof FkComboBox fk) {
                fk.setDisable(!editable || fk.isFailed());
            } else if (ctrl instanceof TextInputControl tic) {
                tic.setEditable(editable);
            }
        }
    }

    private void lockPkFields(boolean locked) {
        for (FieldDescriptor fd : descriptors) {
            if (fd.isPk()) {
                Control ctrl = fieldControls.get(fd.key());
                if (ctrl instanceof TextInputControl tic) tic.setEditable(!locked);
                else if (ctrl instanceof FkComboBox fk) fk.setDisable(locked || fk.isFailed());
            }
        }
    }

    private void lockEstReg(boolean locked) {
        Control ctrl = fieldControls.get("estReg");
        if (ctrl instanceof TextInputControl tic) tic.setEditable(!locked);
    }

    private String getEstRegFromRow() {
        if (selectedRow == null) return null;
        Object v = selectedRow.get("estReg");
        return v != null ? v.toString() : null;
    }

    private void applyBadgeStyle(String estReg) {
        switch (estReg == null ? "" : estReg) {
            case "A" -> {
                stateBadge.setText("ACTIVO");
                stateBadge.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                        "-fx-padding: 2 8 2 8; -fx-background-radius: 4;");
            }
            case "I" -> {
                stateBadge.setText("INACTIVO");
                stateBadge.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; " +
                        "-fx-padding: 2 8 2 8; -fx-background-radius: 4;");
            }
            case "*" -> {
                stateBadge.setText("ELIMINADO");
                stateBadge.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; " +
                        "-fx-padding: 2 8 2 8; -fx-background-radius: 4;");
            }
            default -> {
                stateBadge.setText("—");
                stateBadge.setStyle("-fx-background-color: #ccc; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Callback setters
    // ─────────────────────────────────────────────────────────────────────────

    public void setOnAdd(Consumer<Map<String, Object>> handler)        { this.onAdd = handler; }
    public void setOnModify(Consumer<Map<String, Object>> handler)     { this.onModify = handler; }
    public void setOnDelete(Consumer<Map<String, Object>> handler)     { this.onDelete = handler; }
    public void setOnInactivate(Consumer<Map<String, Object>> handler) { this.onInactivate = handler; }
    public void setOnReactivate(Consumer<Map<String, Object>> handler) { this.onReactivate = handler; }
    public void setOnCancel(Runnable handler)                          { this.onCancel = handler; }

    public OperationMode getMode() { return mode; }

    // ─────────────────────────────────────────────────────────────────────────
    // Inner class: FkComboBox
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * A ComboBox that displays "ID — Label" strings but stores and returns the raw FK value.
     * Keeps a parallel list of raw value strings aligned with the display items.
     */
    public static class FkComboBox extends ComboBox<String> {

        private final FieldDescriptor fd;
        /** Raw values (FK ids) aligned by index with the ComboBox items list. */
        private final List<String> rawValues = new ArrayList<>();
        private boolean failed = false;

        public FkComboBox(FieldDescriptor fd) {
            this.fd = fd;
            setMaxWidth(Double.MAX_VALUE);
            setPromptText("— seleccionar " + fd.label() + " —");
        }

        /**
         * Populates the ComboBox from a list of rows fetched from the referenced table.
         *
         * @param rows       list of maps from the API
         * @param valueKey   key whose value is stored/submitted (the FK id)
         * @param labelKey   key whose value is shown alongside the id
         */
        public void setItems(List<Map<String, Object>> rows, String valueKey, String labelKey) {
            getItems().clear();
            rawValues.clear();
            failed = false;
            for (Map<String, Object> row : rows) {
                Object rawVal = row.get(valueKey);
                Object labelVal = row.get(labelKey);
                String display = (rawVal != null ? rawVal.toString() : "?")
                        + " — "
                        + (labelVal != null ? labelVal.toString() : "");
                getItems().add(display);
                rawValues.add(rawVal != null ? rawVal.toString() : "");
            }
        }

        /** Returns the raw FK value of the currently selected item, or empty string if none. */
        public String getSelectedValue() {
            int idx = getSelectionModel().getSelectedIndex();
            if (idx < 0 || idx >= rawValues.size()) return "";
            return rawValues.get(idx);
        }

        /** Selects the item whose raw value matches {@code value}. */
        public void selectByValue(String value) {
            if (value == null || value.isBlank()) {
                getSelectionModel().clearSelection();
                return;
            }
            for (int i = 0; i < rawValues.size(); i++) {
                if (value.equals(rawValues.get(i))) {
                    getSelectionModel().select(i);
                    return;
                }
            }
            // Value not in list (e.g. inactive FK) — add a read-only placeholder
            getItems().add(value + " — (no está en la lista activa)");
            rawValues.add(value);
            getSelectionModel().select(getItems().size() - 1);
        }

        public void clearSelection() {
            getSelectionModel().clearSelection();
        }

        /** True if the last load attempt failed (endpoint unreachable). */
        public boolean isFailed() { return failed; }
        public void markFailed()  { this.failed = true; }
    }
}
