package com.solidtoxic.gui.panel;

import com.solidtoxic.gui.component.FieldDescriptor;

import java.util.List;
import java.util.Map;

public class ResiduoPanel extends MaintenancePanel {

    public ResiduoPanel() {
        super("Hazardous Waste — Residuo", List.of(
                FieldDescriptor.pk("codResiduo", "Cod_Residuo", 20),
                new FieldDescriptor("nifEmpresa", "NIF_Empresa", 20),
                new FieldDescriptor("codEstandar", "Cod_Estandar", 10),
                new FieldDescriptor("idToxicidad", "ID_Toxicidad", 10),
                new FieldDescriptor("cantidadTotal", "Cantidad_Total", 15),
                FieldDescriptor.large("otrosDatos", "Otros_Datos"),
                new FieldDescriptor("estReg", "State", 1)
        ));
    }

    @Override protected String getBasePath() { return "/api/v1/residuo"; }

    @Override
    protected String buildIdPath(Map<String, Object> row) {
        return row.get("codResiduo").toString();
    }
}
