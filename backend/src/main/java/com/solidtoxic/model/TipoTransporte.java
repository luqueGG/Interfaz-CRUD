package com.solidtoxic.model;

public class TipoTransporte {
    private Integer idTipoTransporte;
    private String nombreTransporte;
    private String descripcion;
    private String estReg;

    public TipoTransporte() {}

    public TipoTransporte(Integer idTipoTransporte, String nombreTransporte, String descripcion, String estReg) {
        this.idTipoTransporte = idTipoTransporte;
        this.nombreTransporte = nombreTransporte;
        this.descripcion = descripcion;
        this.estReg = estReg;
    }

    public Integer getIdTipoTransporte() { return idTipoTransporte; }
    public void setIdTipoTransporte(Integer idTipoTransporte) { this.idTipoTransporte = idTipoTransporte; }

    public String getNombreTransporte() { return nombreTransporte; }
    public void setNombreTransporte(String nombreTransporte) { this.nombreTransporte = nombreTransporte; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstReg() { return estReg; }
    public void setEstReg(String estReg) { this.estReg = estReg; }
}
