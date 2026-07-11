package com.solidtoxic.gui.panel;

import com.solidtoxic.gui.component.FieldDescriptor;

import java.util.List;
import java.util.Map;

public class TipoEnvasePanel extends MaintenancePanel {

    public TipoEnvasePanel() {
        super("Packaging Types — TR_Tipo_Envase", List.of(
                FieldDescriptor.pk("idEnvase", "ID_Envase", 10),
                new FieldDescriptor("nombreEnvase", "Nombre_Envase", 50),
                new FieldDescriptor("descripcion", "Descripcion", 250),
                new FieldDescriptor("estReg", "State", 1)
        ));
    }

    @Override protected String getBasePath() { return "/api/v1/envase"; }

    @Override
    protected String buildIdPath(Map<String, Object> row) {
        return row.get("idEnvase").toString();
    }
}
