package com.solidtoxic.gui.panel;

import com.solidtoxic.gui.component.FieldDescriptor;

import java.util.List;
import java.util.Map;

public class NivelToxicidadPanel extends MaintenancePanel {

    public NivelToxicidadPanel() {
        super("Toxicity Levels — TR_Nivel_Toxicidad", List.of(
                FieldDescriptor.pk("idToxicidad", "ID_Toxicidad", 10),
                new FieldDescriptor("nivel", "Nivel", 20),
                new FieldDescriptor("descripcion", "Descripcion", 250),
                new FieldDescriptor("estReg", "State", 1)
        ));
    }

    @Override protected String getBasePath() { return "/api/v1/toxicidad"; }

    @Override
    protected String buildIdPath(Map<String, Object> row) {
        return row.get("idToxicidad").toString();
    }
}
