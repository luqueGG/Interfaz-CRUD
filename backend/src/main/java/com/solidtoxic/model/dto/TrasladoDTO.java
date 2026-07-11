package com.solidtoxic.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TrasladoDTO {

    private Integer idTraslado;

    @NotBlank(message = "Cod_Residuo is required")
    private String codResiduo;

    @NotBlank(message = "Cod_Destino is required")
    private String codDestino;

    @NotNull(message = "Fecha_Envio is required")
    private LocalDate fechaEnvio;

    @NotNull(message = "Cantidad_Trasladada is required")
    @Positive(message = "Cantidad_Trasladada must be positive")
    private BigDecimal cantidadTrasladada;

    @NotNull(message = "ID_Envase is required")
    private Integer idEnvase;

    @NotNull(message = "ID_Tratamiento is required")
    private Integer idTratamiento;

    private LocalDate fechaLlegada;
    private String otrosDatos;
    private String estReg;

    public TrasladoDTO() {}

    public Integer getIdTraslado() { return idTraslado; }
    public void setIdTraslado(Integer idTraslado) { this.idTraslado = idTraslado; }

    public String getCodResiduo() { return codResiduo; }
    public void setCodResiduo(String codResiduo) { this.codResiduo = codResiduo; }

    public String getCodDestino() { return codDestino; }
    public void setCodDestino(String codDestino) { this.codDestino = codDestino; }

    public LocalDate getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDate fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public BigDecimal getCantidadTrasladada() { return cantidadTrasladada; }
    public void setCantidadTrasladada(BigDecimal cantidadTrasladada) { this.cantidadTrasladada = cantidadTrasladada; }

    public Integer getIdEnvase() { return idEnvase; }
    public void setIdEnvase(Integer idEnvase) { this.idEnvase = idEnvase; }

    public Integer getIdTratamiento() { return idTratamiento; }
    public void setIdTratamiento(Integer idTratamiento) { this.idTratamiento = idTratamiento; }

    public LocalDate getFechaLlegada() { return fechaLlegada; }
    public void setFechaLlegada(LocalDate fechaLlegada) { this.fechaLlegada = fechaLlegada; }

    public String getOtrosDatos() { return otrosDatos; }
    public void setOtrosDatos(String otrosDatos) { this.otrosDatos = otrosDatos; }

    public String getEstReg() { return estReg; }
    public void setEstReg(String estReg) { this.estReg = estReg; }
}
