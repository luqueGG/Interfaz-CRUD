package com.solidtoxic.model;

public class EmpresaTransportista {
    private String nifTransportista;
    private String nombreTransportista;
    private String ciudadTransportista;
    private String otrosDatos;
    private String estReg;

    public EmpresaTransportista() {}

    public EmpresaTransportista(String nifTransportista, String nombreTransportista,
                                 String ciudadTransportista, String otrosDatos, String estReg) {
        this.nifTransportista = nifTransportista;
        this.nombreTransportista = nombreTransportista;
        this.ciudadTransportista = ciudadTransportista;
        this.otrosDatos = otrosDatos;
        this.estReg = estReg;
    }

    public String getNifTransportista() { return nifTransportista; }
    public void setNifTransportista(String nifTransportista) { this.nifTransportista = nifTransportista; }

    public String getNombreTransportista() { return nombreTransportista; }
    public void setNombreTransportista(String nombreTransportista) { this.nombreTransportista = nombreTransportista; }

    public String getCiudadTransportista() { return ciudadTransportista; }
    public void setCiudadTransportista(String ciudadTransportista) { this.ciudadTransportista = ciudadTransportista; }

    public String getOtrosDatos() { return otrosDatos; }
    public void setOtrosDatos(String otrosDatos) { this.otrosDatos = otrosDatos; }

    public String getEstReg() { return estReg; }
    public void setEstReg(String estReg) { this.estReg = estReg; }
}
