package com.solidtoxic.gui.panel;

import com.solidtoxic.gui.component.FieldDescriptor;

import java.util.List;
import java.util.Map;

public class TrasladoTransportistaPanel extends MaintenancePanel {

    public TrasladoTransportistaPanel() {
        super("Shipment Carriers — Traslado_Transportista", List.of(
                FieldDescriptor.pk("idTraslado", "ID_Traslado", 10),
                FieldDescriptor.pk("nifTransportista", "NIF_Transportista", 20),
                new FieldDescriptor("idTipoTransporte", "ID_Tipo_Transporte", 10),
                new FieldDescriptor("kmsRecorridos", "Kms_Recorridos", 10),
                new FieldDescriptor("costo", "Costo", 12),
                new FieldDescriptor("estReg", "State", 1)
        ));
    }

    @Override protected String getBasePath() { return "/api/v1/traslado-transportista"; }

    @Override
    protected String buildIdPath(Map<String, Object> row) {
        // Composite key: /idTraslado/nifTransportista
        return row.get("idTraslado") + "/" + row.get("nifTransportista");
    }
}
