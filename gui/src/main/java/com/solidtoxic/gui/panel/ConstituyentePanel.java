package com.solidtoxic.gui.panel;

import com.solidtoxic.gui.component.FieldDescriptor;

import java.util.List;
import java.util.Map;

public class ConstituyentePanel extends MaintenancePanel {

    public ConstituyentePanel() {
        super("Constituyentes Químicos — Constituyente", List.of(
                FieldDescriptor.pk("codConstituyente", "Cod_Constituyente", 20),
                new FieldDescriptor("nombreConstituyente", "Nombre_Constituyente", 100),
                FieldDescriptor.large("otrosDatos", "Otros_Datos"),
                new FieldDescriptor("estReg", "Estado", 1)
        ));
    }

    @Override protected String getBasePath() { return "/api/v1/constituyente"; }

    @Override
    protected String buildIdPath(Map<String, Object> row) {
        return row.get("codConstituyente").toString();
    }
}
