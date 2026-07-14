package com.solidtoxic.gui.panel;

import com.solidtoxic.gui.component.FieldDescriptor;

import java.util.List;
import java.util.Map;

public class ResiduoEstandarizadoPanel extends MaintenancePanel {

    public ResiduoEstandarizadoPanel() {
        super("Residuos Estandarizados — Residuo_Estandarizado", List.of(
                FieldDescriptor.pk("codEstandar", "Cod_Estandar", 10),
                new FieldDescriptor("nombreEstandar", "Nombre_Estandar", 100),
                new FieldDescriptor("estReg", "Estado", 1)
        ));
    }

    @Override protected String getBasePath() { return "/api/v1/estandar"; }

    @Override
    protected String buildIdPath(Map<String, Object> row) {
        return row.get("codEstandar").toString();
    }
}
