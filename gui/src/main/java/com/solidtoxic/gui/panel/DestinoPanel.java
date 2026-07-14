package com.solidtoxic.gui.panel;

import com.solidtoxic.gui.component.FieldDescriptor;

import java.util.List;
import java.util.Map;

public class DestinoPanel extends MaintenancePanel {

    public DestinoPanel() {
        super("Instalaciones de Destino — Destino", List.of(
                FieldDescriptor.pk("codDestino", "Cod_Destino", 20),
                FieldDescriptor.fk("idRegion", "Región", "region", "idRegion", "nombreRegion"),
                new FieldDescriptor("nombreDestino", "Nombre_Destino", 100),
                new FieldDescriptor("ciudadDestino", "Ciudad_Destino", 50),
                new FieldDescriptor("capacidadMaxima", "Capacidad_Maxima", 15),
                new FieldDescriptor("capacidadActual", "Capacidad_Actual", 15),
                FieldDescriptor.large("otrosDatos", "Otros_Datos"),
                new FieldDescriptor("estReg", "Estado", 1)
        ));
    }

    @Override protected String getBasePath() { return "/api/v1/destino"; }

    @Override
    protected String buildIdPath(Map<String, Object> row) {
        return row.get("codDestino").toString();
    }
}
