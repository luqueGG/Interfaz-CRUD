package com.solidtoxic.model;

import java.math.BigDecimal;

public class Destino {
    private String codDestino;
    private Integer idRegion;
    private String nombreDestino;
    private String ciudadDestino;
    private BigDecimal capacidadMaxima;
    private BigDecimal capacidadActual;
    private String otrosDatos;
    private String estReg;

    public Destino() {}

    public Destino(String codDestino, Integer idRegion, String nombreDestino, String ciudadDestino,
                   BigDecimal capacidadMaxima, BigDecimal capacidadActual, String otrosDatos, String estReg) {
        this.codDestino = codDestino;
        this.idRegion = idRegion;
        this.nombreDestino = nombreDestino;
        this.ciudadDestino = ciudadDestino;
        this.capacidadMaxima = capacidadMaxima;
        this.capacidadActual = capacidadActual;
        this.otrosDatos = otrosDatos;
        this.estReg = estReg;
    }

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
