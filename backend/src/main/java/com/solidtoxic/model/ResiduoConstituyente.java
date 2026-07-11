package com.solidtoxic.model;

import java.math.BigDecimal;

public class ResiduoConstituyente {
    private String codResiduo;
    private String codConstituyente;
    private BigDecimal cantidad;
    private String estReg;

    public ResiduoConstituyente() {}

    public ResiduoConstituyente(String codResiduo, String codConstituyente, BigDecimal cantidad, String estReg) {
        this.codResiduo = codResiduo;
        this.codConstituyente = codConstituyente;
        this.cantidad = cantidad;
        this.estReg = estReg;
    }

    public String getCodResiduo() { return codResiduo; }
    public void setCodResiduo(String codResiduo) { this.codResiduo = codResiduo; }

    public String getCodConstituyente() { return codConstituyente; }
    public void setCodConstituyente(String codConstituyente) { this.codConstituyente = codConstituyente; }

    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }

    public String getEstReg() { return estReg; }
    public void setEstReg(String estReg) { this.estReg = estReg; }
}
