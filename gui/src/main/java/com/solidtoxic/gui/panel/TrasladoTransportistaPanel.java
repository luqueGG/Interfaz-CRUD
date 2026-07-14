package com.solidtoxic.gui.panel;

import com.solidtoxic.gui.component.FieldDescriptor;

import java.util.List;
import java.util.Map;

public class TrasladoTransportistaPanel extends MaintenancePanel {

    public TrasladoTransportistaPanel() {
        super("Transportistas de Traslado — Traslado_Transportista", List.of(
                FieldDescriptor.fk("idTraslado", "Traslado", "traslado", "idTraslado", "idTraslado"),
                FieldDescriptor.fk("nifTransportista", "Transportista", "transportista", "nifTransportista", "nombreTransportista"),
                FieldDescriptor.fk("idTipoTransporte", "Tipo Transporte", "transporte", "idTipoTransporte", "nombreTransporte"),
                new FieldDescriptor("kmsRecorridos", "Kms_Recorridos", 10),
                new FieldDescriptor("costo", "Costo", 12),
                new FieldDescriptor("estReg", "Estado", 1)
        ));
    }

    @Override protected String getBasePath() { return "/api/v1/traslado-transportista"; }

    @Override
    protected String buildIdPath(Map<String, Object> row) {
        return row.get("idTraslado") + "/" + row.get("nifTransportista");
    }
}
