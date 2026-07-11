package com.solidtoxic.repository;

import com.solidtoxic.model.EmpresaProductora;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EmpresaProductoraRepository {

    private final JdbcTemplate jdbc;

    public EmpresaProductoraRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<EmpresaProductora> rowMapper = (rs, rn) -> new EmpresaProductora(
            rs.getString("NIF_Empresa"),
            rs.getString("Nombre_Empresa"),
            rs.getString("Ciudad_Empresa"),
            rs.getString("Actividad"),
            rs.getString("Otros_Datos"),
            rs.getString("estReg")
    );

    public List<EmpresaProductora> findByState(String state) {
        return jdbc.query(
                "SELECT NIF_Empresa, Nombre_Empresa, Ciudad_Empresa, Actividad, Otros_Datos, estReg " +
                "FROM Empresa_Productora WHERE estReg = ?",
                rowMapper, state);
    }

    public Optional<EmpresaProductora> findById(String id) {
        List<EmpresaProductora> results = jdbc.query(
                "SELECT NIF_Empresa, Nombre_Empresa, Ciudad_Empresa, Actividad, Otros_Datos, estReg " +
                "FROM Empresa_Productora WHERE NIF_Empresa = ?",
                rowMapper, id);
        return results.stream().findFirst();
    }

    public boolean existsById(String id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM Empresa_Productora WHERE NIF_Empresa = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public boolean existsByIdAndActive(String id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM Empresa_Productora WHERE NIF_Empresa = ? AND estReg = 'A'", Integer.class, id);
        return count != null && count > 0;
    }

    public int insert(EmpresaProductora e) {
        return jdbc.update(
                "INSERT INTO Empresa_Productora (NIF_Empresa, Nombre_Empresa, Ciudad_Empresa, Actividad, Otros_Datos, estReg) " +
                "VALUES (?, ?, ?, ?, ?, 'A')",
                e.getNifEmpresa(), e.getNombreEmpresa(), e.getCiudadEmpresa(),
                e.getActividad(), e.getOtrosDatos());
    }

    public int update(EmpresaProductora e) {
        return jdbc.update(
                "UPDATE Empresa_Productora SET Nombre_Empresa = ?, Ciudad_Empresa = ?, Actividad = ?, Otros_Datos = ? " +
                "WHERE NIF_Empresa = ?",
                e.getNombreEmpresa(), e.getCiudadEmpresa(), e.getActividad(),
                e.getOtrosDatos(), e.getNifEmpresa());
    }

    public int updateState(String id, String newState) {
        return jdbc.update(
                "UPDATE Empresa_Productora SET estReg = ? WHERE NIF_Empresa = ?",
                newState, id);
    }
}
