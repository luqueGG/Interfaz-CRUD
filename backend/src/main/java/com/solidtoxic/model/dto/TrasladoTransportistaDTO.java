package com.solidtoxic.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class TrasladoTransportistaDTO {

    @NotNull(message = "ID_Traslado is required")
    private Integer idTraslado;

    @NotBlank(message = "NIF_Transportista is required")
    private String nifTransportista;

    @NotNull(message = "ID_Tipo_Transporte is required")
    private Integer idTipoTransporte;

    @NotNull(message = "Kms_Recorridos is required")
    @Positive(message = "Kms_Recorridos must be positive")
    private BigDecimal kmsRecorridos;

    @NotNull(message = "Costo is required")
    @Positive(message = "Costo must be positive")
    private BigDecimal costo;

    private String estReg;

    public TrasladoTransportistaDTO() {}

    public Integer getIdTraslado() { return idTraslado; }
    public void setIdTraslado(Integer idTraslado) { this.idTraslado = idTraslado; }

    public String getNifTransportista() { return nifTransportista; }
    public void setNifTransportista(String nifTransportista) { this.nifTransportista = nifTransportista; }

    public Integer getIdTipoTransporte() { return idTipoTransporte; }
    public void setIdTipoTransporte(Integer idTipoTransporte) { this.idTipoTransporte = idTipoTransporte; }

    public BigDecimal getKmsRecorridos() { return kmsRecorridos; }
    public void setKmsRecorridos(BigDecimal kmsRecorridos) { this.kmsRecorridos = kmsRecorridos; }

    public BigDecimal getCosto() { return costo; }
    public void setCosto(BigDecimal costo) { this.costo = costo; }

    public String getEstReg() { return estReg; }
    public void setEstReg(String estReg) { this.estReg = estReg; }
}
