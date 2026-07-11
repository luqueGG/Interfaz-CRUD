package com.solidtoxic.model;

public class Region {
    private Integer idRegion;
    private String nombreRegion;
    private String estReg;

    public Region() {}

    public Region(Integer idRegion, String nombreRegion, String estReg) {
        this.idRegion = idRegion;
        this.nombreRegion = nombreRegion;
        this.estReg = estReg;
    }

    public Integer getIdRegion() { return idRegion; }
    public void setIdRegion(Integer idRegion) { this.idRegion = idRegion; }

    public String getNombreRegion() { return nombreRegion; }
    public void setNombreRegion(String nombreRegion) { this.nombreRegion = nombreRegion; }

    public String getEstReg() { return estReg; }
    public void setEstReg(String estReg) { this.estReg = estReg; }
}
