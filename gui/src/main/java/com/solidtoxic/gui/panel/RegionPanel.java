package com.solidtoxic.gui.panel;

import com.solidtoxic.gui.component.FieldDescriptor;

import java.util.List;
import java.util.Map;

public class RegionPanel extends MaintenancePanel {

    public RegionPanel() {
        super("Regiones — Region", List.of(
                FieldDescriptor.pk("idRegion", "ID_Region", 10),
                new FieldDescriptor("nombreRegion", "Nombre_Region", 50),
                new FieldDescriptor("estReg", "Estado", 1)
        ));
    }

    @Override protected String getBasePath() { return "/api/v1/region"; }

    @Override
    protected String buildIdPath(Map<String, Object> row) {
        return row.get("idRegion").toString();
    }
}
