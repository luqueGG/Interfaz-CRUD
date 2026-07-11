package com.solidtoxic.model;

public class TipoTratamiento {
    private Integer idTratamiento;
    private String nombreTratamiento;
    private String descripcion;
    private String estReg;

    public TipoTratamiento() {}

    public TipoTratamiento(Integer idTratamiento, String nombreTratamiento, String descripcion, String estReg) {
        this.idTratamiento = idTratamiento;
        this.nombreTratamiento = nombreTratamiento;
        this.descripcion = descripcion;
        this.estReg = estReg;
    }

    public Integer getIdTratamiento() { return idTratamiento; }
    public void setIdTratamiento(Integer idTratamiento) { this.idTratamiento = idTratamiento; }

    public String getNombreTratamiento() { return nombreTratamiento; }
    public void setNombreTratamiento(String nombreTratamiento) { this.nombreTratamiento = nombreTratamiento; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstReg() { return estReg; }
    public void setEstReg(String estReg) { this.estReg = estReg; }
}
