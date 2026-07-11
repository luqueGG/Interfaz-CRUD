package com.solidtoxic.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class DestinoDTO {

    @NotBlank(message = "Cod_Destino is required")
    @Size(max = 20, message = "Cod_Destino must not exceed 20 characters")
    private String codDestino;

    @NotNull(message = "ID_Region is required")
    private Integer idRegion;

    @NotBlank(message = "Nombre_Destino is required")
    @Size(max = 100, message = "Nombre_Destino must not exceed 100 characters")
    private String nombreDestino;

    @NotBlank(message = "Ciudad_Destino is required")
    @Size(max = 50, message = "Ciudad_Destino must not exceed 50 characters")
    private String ciudadDestino;

    @NotNull(message = "Capacidad_Maxima is required")
    @Positive(message = "Capacidad_Maxima must be positive")
    private BigDecimal capacidadMaxima;

    @NotNull(message = "Capacidad_Actual is required")
    @Positive(message = "Capacidad_Actual must be positive")
    private BigDecimal capacidadActual;

    private String otrosDatos;
    private String estReg;

    public DestinoDTO() {}

    public String getCodDestino() { return codDestino; }
    public void setCodDestino(String codDestino) { this.codDestino = codDestino; }

    public Integer getIdRegion() { return idRegion; }
    public void setIdRegion(Integer idRegion) { this.idRegion = idRegion; }

    public String getNombreDestino() { return nombreDestino; }
    public void setNombreDestino(String nombreDestino) { this.nombreDestino = nombreDestino; }

    public String getCiudadDestino() { return ciudadDestino; }
    public void setCiudadDestino(String ciudadDestino) { this.ciudadDestino = ciudadDestino; }

    public BigDecimal getCapacidadMaxima() { return capacidadMaxima; }
    public void setCapacidadMaxima(BigDecimal capacidadMaxima) { this.capacidadMaxima = capacidadMaxima; }

    public BigDecimal getCapacidadActual() { return capacidadActual; }
    public void setCapacidadActual(BigDecimal capacidadActual) { this.capacidadActual = capacidadActual; }

    public String getOtrosDatos() { return otrosDatos; }
    public void setOtrosDatos(String otrosDatos) { this.otrosDatos = otrosDatos; }

    public String getEstReg() { return estReg; }
    public void setEstReg(String estReg) { this.estReg = estReg; }
}
