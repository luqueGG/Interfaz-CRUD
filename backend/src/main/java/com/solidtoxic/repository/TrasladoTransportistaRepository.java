package com.solidtoxic.repository;

import com.solidtoxic.model.TrasladoTransportista;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrasladoTransportistaRepository {

    private final JdbcTemplate jdbc;

    public TrasladoTransportistaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<TrasladoTransportista> rowMapper = (rs, rn) -> new TrasladoTransportista(
            rs.getInt("ID_Traslado"),
            rs.getString("NIF_Transportista"),
            rs.getInt("ID_Tipo_Transporte"),
            rs.getBigDecimal("Kms_Recorridos"),
            rs.getBigDecimal("Costo"),
            rs.getString("estReg")
    );

    public List<TrasladoTransportista> findByState(String state) {
        return jdbc.query(
                "SELECT ID_Traslado, NIF_Transportista, ID_Tipo_Transporte, Kms_Recorridos, Costo, estReg " +
                "FROM Traslado_Transportista WHERE estReg = ?",
                rowMapper, state);
    }

    public List<TrasladoTransportista> findByTraslado(int idTraslado) {
        return jdbc.query(
                "SELECT ID_Traslado, NIF_Transportista, ID_Tipo_Transporte, Kms_Recorridos, Costo, estReg " +
                "FROM Traslado_Transportista WHERE ID_Traslado = ?",
                rowMapper, idTraslado);
    }

    public Optional<TrasladoTransportista> findByCompositeKey(int idTraslado, String nifTransportista) {
        List<TrasladoTransportista> results = jdbc.query(
                "SELECT ID_Traslado, NIF_Transportista, ID_Tipo_Transporte, Kms_Recorridos, Costo, estReg " +
                "FROM Traslado_Transportista WHERE ID_Traslado = ? AND NIF_Transportista = ?",
                rowMapper, idTraslado, nifTransportista);
        return results.stream().findFirst();
    }

    public boolean existsByCompositeKey(int idTraslado, String nifTransportista) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM Traslado_Transportista WHERE ID_Traslado = ? AND NIF_Transportista = ?",
                Integer.class, idTraslado, nifTransportista);
        return count != null && count > 0;
    }

    public int insert(TrasladoTransportista e) {
        return jdbc.update(
                "INSERT INTO Traslado_Transportista (ID_Traslado, NIF_Transportista, ID_Tipo_Transporte, " +
                "Kms_Recorridos, Costo, estReg) VALUES (?, ?, ?, ?, ?, 'A')",
                e.getIdTraslado(), e.getNifTransportista(), e.getIdTipoTransporte(),
                e.getKmsRecorridos(), e.getCosto());
    }

    public int update(TrasladoTransportista e) {
        return jdbc.update(
                "UPDATE Traslado_Transportista SET ID_Tipo_Transporte = ?, Kms_Recorridos = ?, Costo = ? " +
                "WHERE ID_Traslado = ? AND NIF_Transportista = ?",
                e.getIdTipoTransporte(), e.getKmsRecorridos(), e.getCosto(),
                e.getIdTraslado(), e.getNifTransportista());
    }

    public int updateState(int idTraslado, String nifTransportista, String newState) {
        return jdbc.update(
                "UPDATE Traslado_Transportista SET estReg = ? WHERE ID_Traslado = ? AND NIF_Transportista = ?",
                newState, idTraslado, nifTransportista);
    }
}
