package com.solidtoxic.model;

public class NivelToxicidad {
    private Integer idToxicidad;
    private String nivel;
    private String descripcion;
    private String estReg;

    public NivelToxicidad() {}

    public NivelToxicidad(Integer idToxicidad, String nivel, String descripcion, String estReg) {
        this.idToxicidad = idToxicidad;
        this.nivel = nivel;
        this.descripcion = descripcion;
        this.estReg = estReg;
    }

    public Integer getIdToxicidad() { return idToxicidad; }
    public void setIdToxicidad(Integer idToxicidad) { this.idToxicidad = idToxicidad; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstReg() { return estReg; }
    public void setEstReg(String estReg) { this.estReg = estReg; }
}
