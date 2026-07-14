package com.solidtoxic.gui;

import com.solidtoxic.gui.client.ApiClient;
import com.solidtoxic.gui.client.ApiResponse;
import com.solidtoxic.gui.client.BackendUnavailableException;
import com.solidtoxic.gui.panel.*;
import com.solidtoxic.gui.report.ReportService;
import com.solidtoxic.gui.report.ReportService.ColumnDef;
import com.solidtoxic.gui.util.AlertUtil;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Root layout: a MenuBar/sidebar for navigation + a centre content area for panels.
 * Panel switching clears any active RegisterForm state to prevent leaks. Req Phase 9.
 */
public class MainWindow extends BorderPane {

    private final StackPane contentArea = new StackPane();
    private Node currentPanel = null;
    private final ReportService reportService = new ReportService();

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
        panelRegistry.put("Niveles de Toxicidad",    NivelToxicidadPanel::new);
        panelRegistry.put("Tipos de Envase",          TipoEnvasePanel::new);
        panelRegistry.put("Tipos de Tratamiento",     TipoTratamientoPanel::new);
        panelRegistry.put("Tipos de Transporte",      TipoTransportePanel::new);
        panelRegistry.put("Residuos Estandarizados",  ResiduoEstandarizadoPanel::new);
        panelRegistry.put("Constituyentes",           ConstituyentePanel::new);
        panelRegistry.put("Regiones",                 RegionPanel::new);
        // Master tables
        panelRegistry.put("Empresas Productoras",     EmpresaProductoraPanel::new);
        panelRegistry.put("Empresas Transportistas",  EmpresaTransportistaPanel::new);
        panelRegistry.put("Destinos",                 DestinoPanel::new);
        panelRegistry.put("Residuos",                 ResiduoPanel::new);
        // Transactional / associative
        panelRegistry.put("Composición de Residuos",  ResiduoConstituyentePanel::new);
        panelRegistry.put("Traslados",                TrasladoPanel::new);
        panelRegistry.put("Transportistas de Traslado", TrasladoTransportistaPanel::new);
    }

    // ── Menu bar ──────────────────────────────────────────────────────────────

    private MenuBar buildMenuBar() {
        MenuBar bar = new MenuBar();

        Menu refMenu    = new Menu("Tablas de Referencia");
        Menu masterMenu = new Menu("Tablas Maestras");
        Menu txMenu     = new Menu("Transaccional");
        Menu repMenu    = new Menu("Reportes");

        for (Map.Entry<String, Supplier<Node>> entry : panelRegistry.entrySet()) {
            MenuItem item = new MenuItem(entry.getKey());
            item.setOnAction(e -> switchPanel(entry.getValue()));

            String key = entry.getKey();
            if (key.equals("Empresas Productoras") || key.equals("Empresas Transportistas")
                    || key.equals("Destinos") || key.equals("Residuos")) {
                masterMenu.getItems().add(item);
            } else if (key.equals("Composición de Residuos") || key.equals("Traslados")
                    || key.equals("Transportistas de Traslado")) {
                txMenu.getItems().add(item);
            } else {
                refMenu.getItems().add(item);
            }
        }

        // ── Reportes ──────────────────────────────────────────────────────────
        repMenu.getItems().addAll(
                reportItem("Empresas Productoras",
                        "/api/v1/productora",
                        List.of(
                                new ColumnDef("nifEmpresa",     "NIF Empresa"),
                                new ColumnDef("nombreEmpresa",  "Nombre"),
                                new ColumnDef("ciudadEmpresa",  "Ciudad"),
                                new ColumnDef("actividad",      "Actividad"),
                                new ColumnDef("otrosDatos",     "Otros Datos"),
                                new ColumnDef("estReg",         "Estado")
                        )),
                reportItem("Empresas Transportistas",
                        "/api/v1/transportista",
                        List.of(
                                new ColumnDef("nifTransportista",    "NIF Transportista"),
                                new ColumnDef("nombreTransportista", "Nombre"),
                                new ColumnDef("ciudadTransportista", "Ciudad"),
                                new ColumnDef("otrosDatos",          "Otros Datos"),
                                new ColumnDef("estReg",              "Estado")
                        )),
                reportItem("Destinos",
                        "/api/v1/destino",
                        List.of(
                                new ColumnDef("codDestino",       "Cod. Destino"),
                                new ColumnDef("idRegion",         "ID Región"),
                                new ColumnDef("nombreDestino",    "Nombre"),
                                new ColumnDef("ciudadDestino",    "Ciudad"),
                                new ColumnDef("capacidadMaxima",  "Cap. Máxima"),
                                new ColumnDef("capacidadActual",  "Cap. Actual"),
                                new ColumnDef("otrosDatos",       "Otros Datos"),
                                new ColumnDef("estReg",           "Estado")
                        )),
                reportItem("Residuos",
                        "/api/v1/residuo",
                        List.of(
                                new ColumnDef("codResiduo",     "Cod. Residuo"),
                                new ColumnDef("nifEmpresa",     "NIF Empresa"),
                                new ColumnDef("codEstandar",    "Cod. Estándar"),
                                new ColumnDef("idToxicidad",    "ID Toxicidad"),
                                new ColumnDef("cantidadTotal",  "Cantidad Total"),
                                new ColumnDef("otrosDatos",     "Otros Datos"),
                                new ColumnDef("estReg",         "Estado")
                        ))
        );

        bar.getMenus().addAll(refMenu, masterMenu, txMenu, repMenu);
        return bar;
    }

    /** Builds a MenuItem that triggers a PDF export for the given endpoint and columns. */
    private MenuItem reportItem(String label, String apiPath, List<ColumnDef> columns) {
        MenuItem item = new MenuItem(label + "…");
        item.setOnAction(e -> reportService.exportToPdf(
                getScene().getWindow(),
                label,
                apiPath,
                columns));
        return item;
    }

    // ── Layout ────────────────────────────────────────────────────────────────

    private Node buildLayout() {
        Label placeholder = new Label("Selecciona una tabla del menú para comenzar.");
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
                disableAllPanelsWithMessage("El backend respondió con un estado no saludable. Las operaciones de mantenimiento están deshabilitadas.");
            }
        } catch (BackendUnavailableException e) {
            disableAllPanelsWithMessage(
                    "No se pudo conectar con el servidor backend.\n" +
                    "Verifica que los contenedores de Docker estén corriendo e inténtalo de nuevo.\n\n" +
                    "Detalles: " + e.getMessage());
        }
    }

    private void disableAllPanelsWithMessage(String message) {
        AlertUtil.showError("Backend No Disponible", message);
        // Replace content area with a persistent error notice
        Label errorLabel = new Label("⚠ Backend no disponible — operaciones de mantenimiento deshabilitadas.");
        errorLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #c00; -fx-padding: 20;");
        contentArea.getChildren().setAll(errorLabel);
        // Disable all menus in the menu bar
        MenuBar bar = (MenuBar) getTop();
        bar.getMenus().forEach(m -> m.setDisable(true));
    }
}
