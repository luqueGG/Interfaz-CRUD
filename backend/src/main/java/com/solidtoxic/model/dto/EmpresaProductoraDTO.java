package com.solidtoxic.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EmpresaProductoraDTO {

    @NotBlank(message = "NIF_Empresa is required")
    @Size(max = 20, message = "NIF_Empresa must not exceed 20 characters")
    private String nifEmpresa;

    @NotBlank(message = "Nombre_Empresa is required")
    @Size(max = 100, message = "Nombre_Empresa must not exceed 100 characters")
    private String nombreEmpresa;

    @NotBlank(message = "Ciudad_Empresa is required")
    @Size(max = 50, message = "Ciudad_Empresa must not exceed 50 characters")
    private String ciudadEmpresa;

    @NotBlank(message = "Actividad is required")
    @Size(max = 100, message = "Actividad must not exceed 100 characters")
    private String actividad;

    private String otrosDatos;
    private String estReg;

    public EmpresaProductoraDTO() {}

    public String getNifEmpresa() { return nifEmpresa; }
    public void setNifEmpresa(String nifEmpresa) { this.nifEmpresa = nifEmpresa; }

    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }

    public String getCiudadEmpresa() { return ciudadEmpresa; }
    public void setCiudadEmpresa(String ciudadEmpresa) { this.ciudadEmpresa = ciudadEmpresa; }

    public String getActividad() { return actividad; }
    public void setActividad(String actividad) { this.actividad = actividad; }

    public String getOtrosDatos() { return otrosDatos; }
    public void setOtrosDatos(String otrosDatos) { this.otrosDatos = otrosDatos; }

    public String getEstReg() { return estReg; }
    public void setEstReg(String estReg) { this.estReg = estReg; }
}
