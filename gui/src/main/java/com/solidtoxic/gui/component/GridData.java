package com.solidtoxic.gui.component;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Generic TableView that displays a list of rows as Map<String, String>.
 * Fires a row-selected callback when the operator selects a row. Req 2-2, 2-5.
 */
public class GridData extends StackPane {

    private final TableView<Map<String, Object>> tableView = new TableView<>();
    private final ObservableList<Map<String, Object>> data = FXCollections.observableArrayList();
    private Consumer<Map<String, Object>> onRowSelected;

    public GridData(List<FieldDescriptor> columns) {
        tableView.setItems(data);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Empty state label (Req 2-5)
        Label emptyLabel = new Label("No active records found.");
        emptyLabel.setStyle("-fx-text-fill: #888; -fx-font-style: italic;");
        tableView.setPlaceholder(emptyLabel);

        for (FieldDescriptor fd : columns) {
            TableColumn<Map<String, Object>, String> col = new TableColumn<>(fd.label());
            col.setCellValueFactory(cellData -> {
                Object val = cellData.getValue().get(fd.key());
                return new SimpleStringProperty(val != null ? val.toString() : "");
            });
            tableView.getColumns().add(col);
        }

        // Row selection listener (Req 2-2, 2-4)
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldRow, newRow) -> {
            if (newRow != null && onRowSelected != null) {
                onRowSelected.accept(newRow);
            }
        });

        getChildren().add(tableView);
        setPadding(new Insets(0));
    }

    /** Replace all rows with a fresh list. */
    public void setRows(List<Map<String, Object>> rows) {
        data.setAll(rows);
        tableView.getSelectionModel().clearSelection();
    }

    /** Clear the grid and deselect everything. */
    public void clear() {
        data.clear();
        tableView.getSelectionModel().clearSelection();
    }

    /** Register callback for row selection events. */
    public void setOnRowSelected(Consumer<Map<String, Object>> handler) {
        this.onRowSelected = handler;
    }

    /** Returns the currently selected row, or null if none. */
    public Map<String, Object> getSelectedRow() {
        return tableView.getSelectionModel().getSelectedItem();
    }

    public TableView<Map<String, Object>> getTableView() {
        return tableView;
    }
}
