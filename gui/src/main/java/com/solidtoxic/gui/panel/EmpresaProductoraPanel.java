package com.solidtoxic.gui.panel;

import com.solidtoxic.gui.component.FieldDescriptor;

import java.util.List;
import java.util.Map;

public class EmpresaProductoraPanel extends MaintenancePanel {

    public EmpresaProductoraPanel() {
        super("Producer Companies — Empresa_Productora", List.of(
                FieldDescriptor.pk("nifEmpresa", "NIF_Empresa", 20),
                new FieldDescriptor("nombreEmpresa", "Nombre_Empresa", 100),
                new FieldDescriptor("ciudadEmpresa", "Ciudad_Empresa", 50),
                new FieldDescriptor("actividad", "Actividad", 100),
                FieldDescriptor.large("otrosDatos", "Otros_Datos"),
                new FieldDescriptor("estReg", "State", 1)
        ));
    }

    @Override protected String getBasePath() { return "/api/v1/productora"; }

    @Override
    protected String buildIdPath(Map<String, Object> row) {
        return row.get("nifEmpresa").toString();
    }
}
