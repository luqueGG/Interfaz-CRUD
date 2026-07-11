package com.solidtoxic.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ResiduoDTO {

    @NotBlank(message = "Cod_Residuo is required")
    @Size(max = 20, message = "Cod_Residuo must not exceed 20 characters")
    private String codResiduo;

    @NotBlank(message = "NIF_Empresa is required")
    private String nifEmpresa;

    @NotNull(message = "Cod_Estandar is required")
    private Integer codEstandar;

    @NotNull(message = "ID_Toxicidad is required")
    private Integer idToxicidad;

    @NotNull(message = "Cantidad_Total is required")
    @Positive(message = "Cantidad_Total must be positive")
    private BigDecimal cantidadTotal;

    private String otrosDatos;
    private String estReg;

    public ResiduoDTO() {}

    public String getCodResiduo() { return codResiduo; }
    public void setCodResiduo(String codResiduo) { this.codResiduo = codResiduo; }

    public String getNifEmpresa() { return nifEmpresa; }
    public void setNifEmpresa(String nifEmpresa) { this.nifEmpresa = nifEmpresa; }

    public Integer getCodEstandar() { return codEstandar; }
    public void setCodEstandar(Integer codEstandar) { this.codEstandar = codEstandar; }

    public Integer getIdToxicidad() { return idToxicidad; }
    public void setIdToxicidad(Integer idToxicidad) { this.idToxicidad = idToxicidad; }

    public BigDecimal getCantidadTotal() { return cantidadTotal; }
    public void setCantidadTotal(BigDecimal cantidadTotal) { this.cantidadTotal = cantidadTotal; }

    public String getOtrosDatos() { return otrosDatos; }
    public void setOtrosDatos(String otrosDatos) { this.otrosDatos = otrosDatos; }

    public String getEstReg() { return estReg; }
    public void setEstReg(String estReg) { this.estReg = estReg; }
}
