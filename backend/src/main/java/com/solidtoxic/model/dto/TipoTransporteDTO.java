package com.solidtoxic.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TipoTransporteDTO {

    private Integer idTipoTransporte;

    @NotBlank(message = "Nombre_Transporte is required")
    @Size(max = 50, message = "Nombre_Transporte must not exceed 50 characters")
    private String nombreTransporte;

    @Size(max = 250, message = "Descripcion must not exceed 250 characters")
    private String descripcion;

    private String estReg;

    public TipoTransporteDTO() {}

    public Integer getIdTipoTransporte() { return idTipoTransporte; }
    public void setIdTipoTransporte(Integer idTipoTransporte) { this.idTipoTransporte = idTipoTransporte; }

    public String getNombreTransporte() { return nombreTransporte; }
    public void setNombreTransporte(String nombreTransporte) { this.nombreTransporte = nombreTransporte; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstReg() { return estReg; }
    public void setEstReg(String estReg) { this.estReg = estReg; }
}
