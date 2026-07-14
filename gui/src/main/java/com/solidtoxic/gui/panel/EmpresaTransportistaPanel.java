package com.solidtoxic.gui.panel;

import com.solidtoxic.gui.component.FieldDescriptor;

import java.util.List;
import java.util.Map;

public class EmpresaTransportistaPanel extends MaintenancePanel {

    public EmpresaTransportistaPanel() {
        super("Empresas Transportistas — Empresa_Transportista", List.of(
                FieldDescriptor.pk("nifTransportista", "NIF_Transportista", 20),
                new FieldDescriptor("nombreTransportista", "Nombre_Transportista", 100),
                new FieldDescriptor("ciudadTransportista", "Ciudad_Transportista", 50),
                FieldDescriptor.large("otrosDatos", "Otros_Datos"),
                new FieldDescriptor("estReg", "Estado", 1)
        ));
    }

    @Override protected String getBasePath() { return "/api/v1/transportista"; }

    @Override
    protected String buildIdPath(Map<String, Object> row) {
        return row.get("nifTransportista").toString();
    }
}
