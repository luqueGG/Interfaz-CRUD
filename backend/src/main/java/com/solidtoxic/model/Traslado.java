package com.solidtoxic.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Traslado {
    private Integer idTraslado;
    private String codResiduo;
    private String codDestino;
    private LocalDate fechaEnvio;
    private BigDecimal cantidadTrasladada;
    private Integer idEnvase;
    private Integer idTratamiento;
    private LocalDate fechaLlegada;
    private String otrosDatos;
    private String estReg;

    public Traslado() {}

    public Traslado(Integer idTraslado, String codResiduo, String codDestino, LocalDate fechaEnvio,
                    BigDecimal cantidadTrasladada, Integer idEnvase, Integer idTratamiento,
                    LocalDate fechaLlegada, String otrosDatos, String estReg) {
        this.idTraslado = idTraslado;
        this.codResiduo = codResiduo;
        this.codDestino = codDestino;
        this.fechaEnvio = fechaEnvio;
        this.cantidadTrasladada = cantidadTrasladada;
        this.idEnvase = idEnvase;
        this.idTratamiento = idTratamiento;
        this.fechaLlegada = fechaLlegada;
        this.otrosDatos = otrosDatos;
        this.estReg = estReg;
    }

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
