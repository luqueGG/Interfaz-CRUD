package com.solidtoxic.gui.panel;

import com.solidtoxic.gui.component.FieldDescriptor;

import java.util.List;
import java.util.Map;

public class TipoTratamientoPanel extends MaintenancePanel {

    public TipoTratamientoPanel() {
        super("Tipos de Tratamiento — TR_Tipo_Tratamiento", List.of(
                FieldDescriptor.pk("idTratamiento", "ID_Tratamiento", 10),
                new FieldDescriptor("nombreTratamiento", "Nombre_Tratamiento", 50),
                new FieldDescriptor("descripcion", "Descripcion", 250),
                new FieldDescriptor("estReg", "Estado", 1)
        ));
    }

    @Override protected String getBasePath() { return "/api/v1/tratamiento"; }

    @Override
    protected String buildIdPath(Map<String, Object> row) {
        return row.get("idTratamiento").toString();
    }
}
