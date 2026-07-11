package com.solidtoxic.model;

public class ResiduoEstandarizado {
    private Integer codEstandar;
    private String nombreEstandar;
    private String estReg;

    public ResiduoEstandarizado() {}

    public ResiduoEstandarizado(Integer codEstandar, String nombreEstandar, String estReg) {
        this.codEstandar = codEstandar;
        this.nombreEstandar = nombreEstandar;
        this.estReg = estReg;
    }

    public Integer getCodEstandar() { return codEstandar; }
    public void setCodEstandar(Integer codEstandar) { this.codEstandar = codEstandar; }

    public String getNombreEstandar() { return nombreEstandar; }
    public void setNombreEstandar(String nombreEstandar) { this.nombreEstandar = nombreEstandar; }

    public String getEstReg() { return estReg; }
    public void setEstReg(String estReg) { this.estReg = estReg; }
}
