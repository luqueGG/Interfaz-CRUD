package com.solidtoxic.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ConstituyenteDTO {

    @NotBlank(message = "Cod_Constituyente is required")
    @Size(max = 20, message = "Cod_Constituyente must not exceed 20 characters")
    private String codConstituyente;

    @NotBlank(message = "Nombre_Constituyente is required")
    @Size(max = 100, message = "Nombre_Constituyente must not exceed 100 characters")
    private String nombreConstituyente;

    private String otrosDatos;
    private String estReg;

    public ConstituyenteDTO() {}

    public String getCodConstituyente() { return codConstituyente; }
    public void setCodConstituyente(String codConstituyente) { this.codConstituyente = codConstituyente; }

    public String getNombreConstituyente() { return nombreConstituyente; }
    public void setNombreConstituyente(String nombreConstituyente) { this.nombreConstituyente = nombreConstituyente; }

    public String getOtrosDatos() { return otrosDatos; }
    public void setOtrosDatos(String otrosDatos) { this.otrosDatos = otrosDatos; }

    public String getEstReg() { return estReg; }
    public void setEstReg(String estReg) { this.estReg = estReg; }
}
