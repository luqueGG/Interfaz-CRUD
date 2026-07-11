package com.solidtoxic.gui;

import com.solidtoxic.gui.client.ApiClient;
import com.solidtoxic.gui.client.ApiResponse;
import com.solidtoxic.gui.client.BackendUnavailableException;
import com.solidtoxic.gui.panel.*;
import com.solidtoxic.gui.util.AlertUtil;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Root layout: a MenuBar/sidebar for navigation + a centre content area for panels.
 * Panel switching clears any active RegisterForm state to prevent leaks. Req Phase 9.
 */
public class MainWindow extends BorderPane {

    private final StackPane contentArea = new StackPane();
    private Node currentPanel = null;

    // Ordered registry: menu label → panel factory
    private final Map<String, Supplier<Node>> panelRegistry = new LinkedHashMap<>();

    public MainWindow() {
        registerPanels();
        setTop(buildMenuBar());
        setCenter(buildLayout());
        setPadding(new Insets(0));

        // Check backend health on startup (Req 8-1, 8-2)
        checkBackendHealth();
    }

    // ── Panel registry ────────────────────────────────────────────────────────

    private void registerPanels() {
        // Reference tables
        panelRegistry.put("Toxicity Levels",        NivelToxicidadPanel::new);
        panelRegistry.put("Packaging Types",         TipoEnvasePanel::new);
        panelRegistry.put("Treatment Types",         TipoTratamientoPanel::new);
        panelRegistry.put("Transport Types",         TipoTransportePanel::new);
        panelRegistry.put("Standardised Waste",      ResiduoEstandarizadoPanel::new);
        panelRegistry.put("Constituents",            ConstituyentePanel::new);
        panelRegistry.put("Regions",                 RegionPanel::new);
        // Master tables
        panelRegistry.put("Producer Companies",      EmpresaProductoraPanel::new);
        panelRegistry.put("Transport Companies",     EmpresaTransportistaPanel::new);
        panelRegistry.put("Destinations",            DestinoPanel::new);
        panelRegistry.put("Waste Records",           ResiduoPanel::new);
        // Transactional / associative
        panelRegistry.put("Waste Composition",       ResiduoConstituyentePanel::new);
        panelRegistry.put("Shipments",               TrasladoPanel::new);
        panelRegistry.put("Shipment Carriers",       TrasladoTransportistaPanel::new);
    }

    // ── Menu bar ──────────────────────────────────────────────────────────────

    private MenuBar buildMenuBar() {
        MenuBar bar = new MenuBar();

        Menu refMenu    = new Menu("Reference Tables");
        Menu masterMenu = new Menu("Master Tables");
        Menu txMenu     = new Menu("Transactional");

        for (Map.Entry<String, Supplier<Node>> entry : panelRegistry.entrySet()) {
            MenuItem item = new MenuItem(entry.getKey());
            item.setOnAction(e -> switchPanel(entry.getValue()));

            String key = entry.getKey();
            if (key.equals("Producer Companies") || key.equals("Transport Companies")
                    || key.equals("Destinations") || key.equals("Waste Records")) {
                masterMenu.getItems().add(item);
            } else if (key.equals("Waste Composition") || key.equals("Shipments")
                    || key.equals("Shipment Carriers")) {
                txMenu.getItems().add(item);
            } else {
                refMenu.getItems().add(item);
            }
        }
        bar.getMenus().addAll(refMenu, masterMenu, txMenu);
        return bar;
    }

    // ── Layout ────────────────────────────────────────────────────────────────

    private Node buildLayout() {
        Label placeholder = new Label("Select a table from the menu to begin.");
        placeholder.setStyle("-fx-font-size: 14; -fx-text-fill: #888;");
        contentArea.getChildren().add(placeholder);
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        return contentArea;
    }

    // ── Panel switching (Req Phase 9-2) ───────────────────────────────────────

    private void switchPanel(Supplier<Node> factory) {
        contentArea.getChildren().clear();
        currentPanel = factory.get();
        contentArea.getChildren().add(currentPanel);
    }

    // ── Backend health check (Req 8-1, 8-2, 8-3) ─────────────────────────────

    private void checkBackendHealth() {
        try {
            ApiResponse resp = ApiClient.getInstance().get("/api/v1/health");
            if (!resp.isSuccess()) {
                disableAllPanelsWithMessage("Backend returned an unhealthy status. Maintenance operations are disabled.");
            }
        } catch (BackendUnavailableException e) {
            disableAllPanelsWithMessage(
                    "Cannot connect to the backend server.\n" +
                    "Please ensure Docker containers are running and try again.\n\n" +
                    "Details: " + e.getMessage());
        }
    }

    private void disableAllPanelsWithMessage(String message) {
        AlertUtil.showError("Backend Unreachable", message);
        // Replace content area with a persistent error notice
        Label errorLabel = new Label("⚠ Backend unavailable — maintenance operations disabled.");
        errorLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #c00; -fx-padding: 20;");
        contentArea.getChildren().setAll(errorLabel);
        // Disable all menus in the menu bar
        MenuBar bar = (MenuBar) getTop();
        bar.getMenus().forEach(m -> m.setDisable(true));
    }
}
