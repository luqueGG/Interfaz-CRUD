package com.solidtoxic.model;

public class TipoEnvase {
    private Integer idEnvase;
    private String nombreEnvase;
    private String descripcion;
    private String estReg;

    public TipoEnvase() {}

    public TipoEnvase(Integer idEnvase, String nombreEnvase, String descripcion, String estReg) {
        this.idEnvase = idEnvase;
        this.nombreEnvase = nombreEnvase;
        this.descripcion = descripcion;
        this.estReg = estReg;
    }

    public Integer getIdEnvase() { return idEnvase; }
    public void setIdEnvase(Integer idEnvase) { this.idEnvase = idEnvase; }

    public String getNombreEnvase() { return nombreEnvase; }
    public void setNombreEnvase(String nombreEnvase) { this.nombreEnvase = nombreEnvase; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstReg() { return estReg; }
    public void setEstReg(String estReg) { this.estReg = estReg; }
}
