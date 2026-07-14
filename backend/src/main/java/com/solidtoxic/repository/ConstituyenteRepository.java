package com.solidtoxic.repository;

import com.solidtoxic.model.Constituyente;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ConstituyenteRepository {

    private final JdbcTemplate jdbc;

    public ConstituyenteRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Constituyente> rowMapper = (rs, rn) -> new Constituyente(
            rs.getString("Cod_Constituyente"),
            rs.getString("Nombre_Constituyente"),
            rs.getString("Otros_Datos"),
            rs.getString("estReg")
    );

    public List<Constituyente> findAll() {
        return jdbc.query(
                "SELECT Cod_Constituyente, Nombre_Constituyente, Otros_Datos, estReg FROM Constituyente ORDER BY Cod_Constituyente",
                rowMapper);
    }

    public List<Constituyente> findByState(String state) {
        return jdbc.query(
                "SELECT Cod_Constituyente, Nombre_Constituyente, Otros_Datos, estReg FROM Constituyente WHERE estReg = ? ORDER BY Cod_Constituyente",
                rowMapper, state);
    }

    public Optional<Constituyente> findById(String id) {
        List<Constituyente> results = jdbc.query(
                "SELECT Cod_Constituyente, Nombre_Constituyente, Otros_Datos, estReg FROM Constituyente WHERE Cod_Constituyente = ?",
                rowMapper, id);
        return results.stream().findFirst();
    }

    public boolean existsById(String id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM Constituyente WHERE Cod_Constituyente = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public boolean existsByIdAndActive(String id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM Constituyente WHERE Cod_Constituyente = ? AND estReg = 'A'", Integer.class, id);
        return count != null && count > 0;
    }

    public int insert(Constituyente e) {
        return jdbc.update(
                "INSERT INTO Constituyente (Cod_Constituyente, Nombre_Constituyente, Otros_Datos, estReg) VALUES (?, ?, ?, 'A')",
                e.getCodConstituyente(), e.getNombreConstituyente(), e.getOtrosDatos());
    }

    public int update(Constituyente e) {
        return jdbc.update(
                "UPDATE Constituyente SET Nombre_Constituyente = ?, Otros_Datos = ? WHERE Cod_Constituyente = ?",
                e.getNombreConstituyente(), e.getOtrosDatos(), e.getCodConstituyente());
    }

    public int updateState(String id, String newState) {
        return jdbc.update(
                "UPDATE Constituyente SET estReg = ? WHERE Cod_Constituyente = ?",
                newState, id);
    }
}
