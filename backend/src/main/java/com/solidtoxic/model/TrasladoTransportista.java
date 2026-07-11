package com.solidtoxic.model;

import java.math.BigDecimal;

public class TrasladoTransportista {
    private Integer idTraslado;
    private String nifTransportista;
    private Integer idTipoTransporte;
    private BigDecimal kmsRecorridos;
    private BigDecimal costo;
    private String estReg;

    public TrasladoTransportista() {}

    public TrasladoTransportista(Integer idTraslado, String nifTransportista, Integer idTipoTransporte,
                                  BigDecimal kmsRecorridos, BigDecimal costo, String estReg) {
        this.idTraslado = idTraslado;
        this.nifTransportista = nifTransportista;
        this.idTipoTransporte = idTipoTransporte;
        this.kmsRecorridos = kmsRecorridos;
        this.costo = costo;
        this.estReg = estReg;
    }

    public Integer getIdTraslado() { return idTraslado; }
    public void setIdTraslado(Integer idTraslado) { this.idTraslado = idTraslado; }

    public String getNifTransportista() { return nifTransportista; }
    public void setNifTransportista(String nifTransportista) { this.nifTransportista = nifTransportista; }

    public Integer getIdTipoTransporte() { return idTipoTransporte; }
    public void setIdTipoTransporte(Integer idTipoTransporte) { this.idTipoTransporte = idTipoTransporte; }

    public BigDecimal getKmsRecorridos() { return kmsRecorridos; }
    public void setKmsRecorridos(BigDecimal kmsRecorridos) { this.kmsRecorridos = kmsRecorridos; }

    public BigDecimal getCosto() { return costo; }
    public void setCosto(BigDecimal costo) { this.costo = costo; }

    public String getEstReg() { return estReg; }
    public void setEstReg(String estReg) { this.estReg = estReg; }
}
