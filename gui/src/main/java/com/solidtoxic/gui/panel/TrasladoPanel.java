package com.solidtoxic.gui.panel;

import com.solidtoxic.gui.component.FieldDescriptor;

import java.util.List;
import java.util.Map;

public class TrasladoPanel extends MaintenancePanel {

    public TrasladoPanel() {
        super("Shipments — Traslado", List.of(
                FieldDescriptor.pk("idTraslado", "ID_Traslado", 10),
                new FieldDescriptor("codResiduo", "Cod_Residuo", 20),
                new FieldDescriptor("codDestino", "Cod_Destino", 20),
                new FieldDescriptor("fechaEnvio", "Fecha_Envio (YYYY-MM-DD)", 10),
                new FieldDescriptor("cantidadTrasladada", "Cantidad_Trasladada", 15),
                new FieldDescriptor("idEnvase", "ID_Envase", 10),
                new FieldDescriptor("idTratamiento", "ID_Tratamiento", 10),
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
