package com.solidtoxic.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class ResiduoConstituyenteDTO {

    @NotBlank(message = "Cod_Residuo is required")
    private String codResiduo;

    @NotBlank(message = "Cod_Constituyente is required")
    private String codConstituyente;

    @NotNull(message = "Cantidad is required")
    @Positive(message = "Cantidad must be positive")
    private BigDecimal cantidad;

    private String estReg;

    public ResiduoConstituyenteDTO() {}

    public String getCodResiduo() { return codResiduo; }
    public void setCodResiduo(String codResiduo) { this.codResiduo = codResiduo; }

    public String getCodConstituyente() { return codConstituyente; }
    public void setCodConstituyente(String codConstituyente) { this.codConstituyente = codConstituyente; }

    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }

    public String getEstReg() { return estReg; }
    public void setEstReg(String estReg) { this.estReg = estReg; }
}
