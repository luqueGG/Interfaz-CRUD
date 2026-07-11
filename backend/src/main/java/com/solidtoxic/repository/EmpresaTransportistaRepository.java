package com.solidtoxic.repository;

import com.solidtoxic.model.EmpresaTransportista;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EmpresaTransportistaRepository {

    private final JdbcTemplate jdbc;

    public EmpresaTransportistaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<EmpresaTransportista> rowMapper = (rs, rn) -> new EmpresaTransportista(
            rs.getString("NIF_Transportista"),
            rs.getString("Nombre_Transportista"),
            rs.getString("Ciudad_Transportista"),
            rs.getString("Otros_Datos"),
            rs.getString("estReg")
    );

    public List<EmpresaTransportista> findByState(String state) {
        return jdbc.query(
                "SELECT NIF_Transportista, Nombre_Transportista, Ciudad_Transportista, Otros_Datos, estReg " +
                "FROM Empresa_Transportista WHERE estReg = ?",
                rowMapper, state);
    }

    public Optional<EmpresaTransportista> findById(String id) {
        List<EmpresaTransportista> results = jdbc.query(
                "SELECT NIF_Transportista, Nombre_Transportista, Ciudad_Transportista, Otros_Datos, estReg " +
                "FROM Empresa_Transportista WHERE NIF_Transportista = ?",
                rowMapper, id);
        return results.stream().findFirst();
    }

    public boolean existsById(String id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM Empresa_Transportista WHERE NIF_Transportista = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public boolean existsByIdAndActive(String id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM Empresa_Transportista WHERE NIF_Transportista = ? AND estReg = 'A'",
                Integer.class, id);
        return count != null && count > 0;
    }

    public int insert(EmpresaTransportista e) {
        return jdbc.update(
                "INSERT INTO Empresa_Transportista (NIF_Transportista, Nombre_Transportista, Ciudad_Transportista, Otros_Datos, estReg) " +
                "VALUES (?, ?, ?, ?, 'A')",
                e.getNifTransportista(), e.getNombreTransportista(),
                e.getCiudadTransportista(), e.getOtrosDatos());
    }

    public int update(EmpresaTransportista e) {
        return jdbc.update(
                "UPDATE Empresa_Transportista SET Nombre_Transportista = ?, Ciudad_Transportista = ?, Otros_Datos = ? " +
                "WHERE NIF_Transportista = ?",
                e.getNombreTransportista(), e.getCiudadTransportista(),
                e.getOtrosDatos(), e.getNifTransportista());
    }

    public int updateState(String id, String newState) {
        return jdbc.update(
                "UPDATE Empresa_Transportista SET estReg = ? WHERE NIF_Transportista = ?",
                newState, id);
    }
}
