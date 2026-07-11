package com.solidtoxic.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NivelToxicidadDTO {

    private Integer idToxicidad;

    @NotBlank(message = "Nivel is required")
    @Size(max = 20, message = "Nivel must not exceed 20 characters")
    private String nivel;

    @Size(max = 250, message = "Descripcion must not exceed 250 characters")
    private String descripcion;

    private String estReg;

    public NivelToxicidadDTO() {}

    public Integer getIdToxicidad() { return idToxicidad; }
    public void setIdToxicidad(Integer idToxicidad) { this.idToxicidad = idToxicidad; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstReg() { return estReg; }
    public void setEstReg(String estReg) { this.estReg = estReg; }
}
