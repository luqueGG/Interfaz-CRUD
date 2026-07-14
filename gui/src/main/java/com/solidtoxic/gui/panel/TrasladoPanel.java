package com.solidtoxic.gui.panel;

import com.solidtoxic.gui.component.FieldDescriptor;

import java.util.List;
import java.util.Map;

public class TrasladoPanel extends MaintenancePanel {

    public TrasladoPanel() {
        super("Shipments — Traslado", List.of(
                FieldDescriptor.pk("idTraslado", "ID_Traslado", 10),
                FieldDescriptor.fk("codResiduo", "Residuo", "residuo", "codResiduo", "codResiduo"),
                FieldDescriptor.fk("codDestino", "Destino", "destino", "codDestino", "nombreDestino"),
                new FieldDescriptor("fechaEnvio", "Fecha_Envio (YYYY-MM-DD)", 10),
                new FieldDescriptor("cantidadTrasladada", "Cantidad_Trasladada", 15),
                FieldDescriptor.fk("idEnvase", "Tipo Envase", "envase", "idEnvase", "nombreEnvase"),
                FieldDescriptor.fk("idTratamiento", "Tipo Tratamiento", "tratamiento", "idTratamiento", "nombreTratamiento"),
                new FieldDescriptor("fechaLlegada", "Fecha_Llegada (YYYY-MM-DD)", 10),
                FieldDescriptor.large("otrosDatos", "Otros_Datos"),
                new FieldDescriptor("estReg", "State", 1)
        ));
    }

    @Override protected String getBasePath() { return "/api/v1/traslado"; }

    @Override
    protected String buildIdPath(Map<String, Object> row) {
        return row.get("idTraslado").toString();
    }
}
