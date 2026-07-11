package com.solidtoxic.gui.panel;

import com.solidtoxic.gui.component.FieldDescriptor;

import java.util.List;
import java.util.Map;

public class ResiduoConstituyentePanel extends MaintenancePanel {

    public ResiduoConstituyentePanel() {
        super("Waste Composition — Residuo_Constituyente", List.of(
                FieldDescriptor.pk("codResiduo", "Cod_Residuo", 20),
                FieldDescriptor.pk("codConstituyente", "Cod_Constituyente", 20),
                new FieldDescriptor("cantidad", "Cantidad", 15),
                new FieldDescriptor("estReg", "State", 1)
        ));
    }

    @Override protected String getBasePath() { return "/api/v1/residuo-constituyente"; }

    @Override
    protected String buildIdPath(Map<String, Object> row) {
        // Composite key: /codResiduo/codConstituyente
        return row.get("codResiduo") + "/" + row.get("codConstituyente");
    }
}
