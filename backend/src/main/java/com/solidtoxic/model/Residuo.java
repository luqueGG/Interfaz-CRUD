package com.solidtoxic.model;

import java.math.BigDecimal;

public class Residuo {
    private String codResiduo;
    private String nifEmpresa;
    private Integer codEstandar;
    private Integer idToxicidad;
    private BigDecimal cantidadTotal;
    private String otrosDatos;
    private String estReg;

    public Residuo() {}

    public Residuo(String codResiduo, String nifEmpresa, Integer codEstandar, Integer idToxicidad,
                   BigDecimal cantidadTotal, String otrosDatos, String estReg) {
        this.codResiduo = codResiduo;
        this.nifEmpresa = nifEmpresa;
        this.codEstandar = codEstandar;
        this.idToxicidad = idToxicidad;
        this.cantidadTotal = cantidadTotal;
        this.otrosDatos = otrosDatos;
        this.estReg = estReg;
    }

    public String getCodResiduo() { return codResiduo; }
    public void setCodResiduo(String codResiduo) { this.codResiduo = codResiduo; }

    public String getNifEmpresa() { return nifEmpresa; }
    public void setNifEmpresa(String nifEmpresa) { this.nifEmpresa = nifEmpresa; }

    public Integer getCodEstandar() { return codEstandar; }
    public void setCodEstandar(Integer codEstandar) { this.codEstandar = codEstandar; }

    public Integer getIdToxicidad() { return idToxicidad; }
    public void setIdToxicidad(Integer idToxicidad) { this.idToxicidad = idToxicidad; }

    public BigDecimal getCantidadTotal() { return cantidadTotal; }
    public void setCantidadTotal(BigDecimal cantidadTotal) { this.cantidadTotal = cantidadTotal; }

    public String getOtrosDatos() { return otrosDatos; }
    public void setOtrosDatos(String otrosDatos) { this.otrosDatos = otrosDatos; }

    public String getEstReg() { return estReg; }
    public void setEstReg(String estReg) { this.estReg = estReg; }
}
