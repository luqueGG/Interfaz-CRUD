package com.solidtoxic.gui.panel;

import com.solidtoxic.gui.component.FieldDescriptor;

import java.util.List;
import java.util.Map;

public class ResiduoConstituyentePanel extends MaintenancePanel {

    public ResiduoConstituyentePanel() {
        super("Composición de Residuos — Residuo_Constituyente", List.of(
                FieldDescriptor.fk("codResiduo", "Residuo", "residuo", "codResiduo", "codResiduo"),
                FieldDescriptor.fk("codConstituyente", "Constituyente", "constituyente", "codConstituyente", "nombreConstituyente"),
                new FieldDescriptor("cantidad", "Cantidad", 15),
                new FieldDescriptor("estReg", "Estado", 1)
        ));
    }

    @Override protected String getBasePath() { return "/api/v1/residuo-constituyente"; }

    @Override
    protected String buildIdPath(Map<String, Object> row) {
        return row.get("codResiduo") + "/" + row.get("codConstituyente");
    }
}
