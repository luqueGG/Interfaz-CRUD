package com.solidtoxic.gui.panel;

import com.solidtoxic.gui.component.FieldDescriptor;

import java.util.List;
import java.util.Map;

public class TipoTransportePanel extends MaintenancePanel {

    public TipoTransportePanel() {
        super("Tipos de Transporte — TR_Tipo_Transporte", List.of(
                FieldDescriptor.pk("idTipoTransporte", "ID_Tipo_Transporte", 10),
                new FieldDescriptor("nombreTransporte", "Nombre_Transporte", 50),
                new FieldDescriptor("descripcion", "Descripcion", 250),
                new FieldDescriptor("estReg", "Estado", 1)
        ));
    }

    @Override protected String getBasePath() { return "/api/v1/transporte"; }

    @Override
    protected String buildIdPath(Map<String, Object> row) {
        return row.get("idTipoTransporte").toString();
    }
}
