package com.solidtoxic.model;

public class Constituyente {
    private String codConstituyente;
    private String nombreConstituyente;
    private String otrosDatos;
    private String estReg;

    public Constituyente() {}

    public Constituyente(String codConstituyente, String nombreConstituyente, String otrosDatos, String estReg) {
        this.codConstituyente = codConstituyente;
        this.nombreConstituyente = nombreConstituyente;
        this.otrosDatos = otrosDatos;
        this.estReg = estReg;
    }

    public String getCodConstituyente() { return codConstituyente; }
    public void setCodConstituyente(String codConstituyente) { this.codConstituyente = codConstituyente; }

    public String getNombreConstituyente() { return nombreConstituyente; }
    public void setNombreConstituyente(String nombreConstituyente) { this.nombreConstituyente = nombreConstituyente; }

    public String getOtrosDatos() { return otrosDatos; }
    public void setOtrosDatos(String otrosDatos) { this.otrosDatos = otrosDatos; }

    public String getEstReg() { return estReg; }
    public void setEstReg(String estReg) { this.estReg = estReg; }
}
