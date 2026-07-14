package com.solidtoxic.gui.panel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.solidtoxic.gui.client.ApiClient;
import com.solidtoxic.gui.client.ApiResponse;
import com.solidtoxic.gui.client.BackendUnavailableException;
import com.solidtoxic.gui.component.FieldDescriptor;
import com.solidtoxic.gui.component.GridData;
import com.solidtoxic.gui.component.RegisterForm;
import com.solidtoxic.gui.util.AlertUtil;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

/**
 * Base class for all maintenance panels.
 * Subclasses provide the endpoint base path, field descriptors, and PK extraction logic.
 *
 * Standard composite-key tables (ResiduoConstituyente, TrasladoTransportista)
 * override buildIdPath() to return the correct path segment.
 */
public abstract class MaintenancePanel extends BorderPane {

    protected final ApiClient api = ApiClient.getInstance();
    protected final GridData gridData;
    protected final RegisterForm form;

    public MaintenancePanel(String title, List<FieldDescriptor> fields) {
        // Title bar
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-padding: 8 0 4 0;");
        VBox top = new VBox(titleLabel);
        top.setPadding(new Insets(8, 10, 4, 10));
        setTop(top);

        // Grid (top half) + Form (bottom half) via SplitPane
        gridData = new GridData(fields);
        form = new RegisterForm(fields);

        SplitPane split = new SplitPane();
        split.setOrientation(Orientation.VERTICAL);
        split.getItems().addAll(gridData, form);
        split.setDividerPositions(0.5);
        setCenter(split);
        setPadding(new Insets(0, 8, 8, 8));

        // Row selection → load into form
        gridData.setOnRowSelected(row -> {
            form.setData(row);
        });

        // Wire form callbacks
        form.setOnAdd(data -> handleCreate(data));
        form.setOnModify(data -> handleUpdate(data));
        form.setOnDelete(row -> handleStateChange(row, "*"));
        form.setOnInactivate(row -> handleStateChange(row, "I"));
        form.setOnReactivate(row -> handleStateChange(row, "A"));
        form.setOnCancel(() -> {});

        // Initial load
        loadGrid();
    }

    // ── Abstract contract ──────────────────────────────────────────────────────

    /** e.g. "/api/v1/toxicidad" */
    protected abstract String getBasePath();

    /** Extracts the ID path segment from a row map, e.g. "1" or "NIF001" */
    protected abstract String buildIdPath(Map<String, Object> row);

    // ── CRUD operations ────────────────────────────────────────────────────────

    protected void loadGrid() {
        try {
            ApiResponse resp = api.get(getBasePath());
            if (resp.isSuccess()) {
                List<Map<String, Object>> rows = api.getMapper()
                        .readValue(resp.getBody(), new TypeReference<>() {});
                gridData.setRows(rows);
            } else {
                AlertUtil.showError("Failed to load records: " + extractErrors(resp.getBody()));
            }
        } catch (BackendUnavailableException e) {
            AlertUtil.showError("Connection Error", "Cannot reach the backend: " + e.getMessage());
        } catch (Exception e) {
            AlertUtil.showError("Unexpected error loading records: " + e.getMessage());
        }
    }

    private void handleCreate(Map<String, Object> data) {
        try {
            ApiResponse resp = api.post(getBasePath(), data);
            if (resp.isSuccess()) {
                AlertUtil.showInfo("Record created successfully.");
                form.clearAndReset();
                loadGrid();
            } else {
                AlertUtil.showError("Validation Error", extractErrors(resp.getBody()));
            }
        } catch (BackendUnavailableException e) {
            AlertUtil.showError("Connection Error", e.getMessage());
        } catch (Exception e) {
            AlertUtil.showError("Unexpected error: " + e.getMessage());
        }
    }

    private void handleUpdate(Map<String, Object> data) {
        Map<String, Object> currentRow = gridData.getSelectedRow();
        if (currentRow == null) return;
        String idPath = buildIdPath(currentRow);
        try {
            ApiResponse resp = api.put(getBasePath() + "/" + idPath, data);
            if (resp.isSuccess()) {
                AlertUtil.showInfo("Record updated successfully.");
                form.clearAndReset();
                loadGrid();
            } else {
                AlertUtil.showError("Validation Error", extractErrors(resp.getBody()));
            }
        } catch (BackendUnavailableException e) {
            AlertUtil.showError("Connection Error", e.getMessage());
        } catch (Exception e) {
            AlertUtil.showError("Unexpected error: " + e.getMessage());
        }
    }

    protected void handleStateChange(Map<String, Object> row, String newState) {
        String idPath = buildIdPath(row);
        try {
            ApiResponse resp = api.patch(getBasePath() + "/" + idPath + "/state",
                    Map.of("estReg", newState));
            if (resp.isSuccess()) {
                form.clearAndReset();
                loadGrid();
            } else {
                AlertUtil.showError("State Change Error", extractErrors(resp.getBody()));
            }
        } catch (BackendUnavailableException e) {
            AlertUtil.showError("Connection Error", e.getMessage());
        } catch (Exception e) {
            AlertUtil.showError("Unexpected error: " + e.getMessage());
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private String extractErrors(String responseBody) {
        try {
            Map<String, Object> body = api.getMapper().readValue(responseBody, new TypeReference<>() {});
            Object errors = body.get("errors");
            if (errors instanceof List<?> list) {
                return String.join("\n", (List<String>) list);
            }
            return responseBody;
        } catch (Exception e) {
            return responseBody;
        }
    }
}
