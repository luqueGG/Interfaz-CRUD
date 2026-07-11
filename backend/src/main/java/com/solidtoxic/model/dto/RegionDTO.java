package com.solidtoxic.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegionDTO {

    private Integer idRegion;

    @NotBlank(message = "Nombre_Region is required")
    @Size(max = 50, message = "Nombre_Region must not exceed 50 characters")
    private String nombreRegion;

    private String estReg;

    public RegionDTO() {}

    public Integer getIdRegion() { return idRegion; }
    public void setIdRegion(Integer idRegion) { this.idRegion = idRegion; }

    public String getNombreRegion() { return nombreRegion; }
    public void setNombreRegion(String nombreRegion) { this.nombreRegion = nombreRegion; }

    public String getEstReg() { return estReg; }
    public void setEstReg(String estReg) { this.estReg = estReg; }
}
