package com.solidtoxic.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TipoEnvaseDTO {

    private Integer idEnvase;

    @NotBlank(message = "Nombre_Envase is required")
    @Size(max = 50, message = "Nombre_Envase must not exceed 50 characters")
    private String nombreEnvase;

    @Size(max = 250, message = "Descripcion must not exceed 250 characters")
    private String descripcion;

    private String estReg;

    public TipoEnvaseDTO() {}

    public Integer getIdEnvase() { return idEnvase; }
    public void setIdEnvase(Integer idEnvase) { this.idEnvase = idEnvase; }

    public String getNombreEnvase() { return nombreEnvase; }
    public void setNombreEnvase(String nombreEnvase) { this.nombreEnvase = nombreEnvase; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstReg() { return estReg; }
    public void setEstReg(String estReg) { this.estReg = estReg; }
}
