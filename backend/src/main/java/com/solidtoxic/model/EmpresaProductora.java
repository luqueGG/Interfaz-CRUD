package com.solidtoxic.model;

public class EmpresaProductora {
    private String nifEmpresa;
    private String nombreEmpresa;
    private String ciudadEmpresa;
    private String actividad;
    private String otrosDatos;
    private String estReg;

    public EmpresaProductora() {}

    public EmpresaProductora(String nifEmpresa, String nombreEmpresa, String ciudadEmpresa,
                              String actividad, String otrosDatos, String estReg) {
        this.nifEmpresa = nifEmpresa;
        this.nombreEmpresa = nombreEmpresa;
        this.ciudadEmpresa = ciudadEmpresa;
        this.actividad = actividad;
        this.otrosDatos = otrosDatos;
        this.estReg = estReg;
    }

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
