package com.solidtoxic.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TipoTratamientoDTO {

    private Integer idTratamiento;

    @NotBlank(message = "Nombre_Tratamiento is required")
    @Size(max = 50, message = "Nombre_Tratamiento must not exceed 50 characters")
    private String nombreTratamiento;

    @Size(max = 250, message = "Descripcion must not exceed 250 characters")
    private String descripcion;

    private String estReg;

    public TipoTratamientoDTO() {}

    public Integer getIdTratamiento() { return idTratamiento; }
    public void setIdTratamiento(Integer idTratamiento) { this.idTratamiento = idTratamiento; }

    public String getNombreTratamiento() { return nombreTratamiento; }
    public void setNombreTratamiento(String nombreTratamiento) { this.nombreTratamiento = nombreTratamiento; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstReg() { return estReg; }
    public void setEstReg(String estReg) { this.estReg = estReg; }
}
