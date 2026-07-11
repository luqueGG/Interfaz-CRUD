package com.solidtoxic.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResiduoEstandarizadoDTO {

    private Integer codEstandar;

    @NotBlank(message = "Nombre_Estandar is required")
    @Size(max = 100, message = "Nombre_Estandar must not exceed 100 characters")
    private String nombreEstandar;

    private String estReg;

    public ResiduoEstandarizadoDTO() {}

    public Integer getCodEstandar() { return codEstandar; }
    public void setCodEstandar(Integer codEstandar) { this.codEstandar = codEstandar; }

    public String getNombreEstandar() { return nombreEstandar; }
    public void setNombreEstandar(String nombreEstandar) { this.nombreEstandar = nombreEstandar; }

    public String getEstReg() { return estReg; }
    public void setEstReg(String estReg) { this.estReg = estReg; }
}
