package com.solidtoxic.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EmpresaTransportistaDTO {

    @NotBlank(message = "NIF_Transportista is required")
    @Size(max = 20, message = "NIF_Transportista must not exceed 20 characters")
    private String nifTransportista;

    @NotBlank(message = "Nombre_Transportista is required")
    @Size(max = 100, message = "Nombre_Transportista must not exceed 100 characters")
    private String nombreTransportista;

    @NotBlank(message = "Ciudad_Transportista is required")
    @Size(max = 50, message = "Ciudad_Transportista must not exceed 50 characters")
    private String ciudadTransportista;

    private String otrosDatos;
    private String estReg;

    public EmpresaTransportistaDTO() {}

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
