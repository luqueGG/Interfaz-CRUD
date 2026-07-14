package com.solidtoxic.repository;

import com.solidtoxic.model.ResiduoEstandarizado;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ResiduoEstandarizadoRepository {

    private final JdbcTemplate jdbc;

    public ResiduoEstandarizadoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<ResiduoEstandarizado> rowMapper = (rs, rn) -> new ResiduoEstandarizado(
            rs.getInt("Cod_Estandar"),
            rs.getString("Nombre_Estandar"),
            rs.getString("estReg")
    );

    public List<ResiduoEstandarizado> findAll() {
        return jdbc.query(
                "SELECT Cod_Estandar, Nombre_Estandar, estReg FROM Residuo_Estandarizado ORDER BY Cod_Estandar",
                rowMapper);
    }

    public List<ResiduoEstandarizado> findByState(String state) {
        return jdbc.query(
                "SELECT Cod_Estandar, Nombre_Estandar, estReg FROM Residuo_Estandarizado WHERE estReg = ? ORDER BY Cod_Estandar",
                rowMapper, state);
    }

    public Optional<ResiduoEstandarizado> findById(int id) {
        List<ResiduoEstandarizado> results = jdbc.query(
                "SELECT Cod_Estandar, Nombre_Estandar, estReg FROM Residuo_Estandarizado WHERE Cod_Estandar = ?",
                rowMapper, id);
        return results.stream().findFirst();
    }

    public boolean existsById(int id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM Residuo_Estandarizado WHERE Cod_Estandar = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public boolean existsByIdAndActive(int id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM Residuo_Estandarizado WHERE Cod_Estandar = ? AND estReg = 'A'", Integer.class, id);
        return count != null && count > 0;
    }

    public int insert(ResiduoEstandarizado e) {
        return jdbc.update(
                "INSERT INTO Residuo_Estandarizado (Cod_Estandar, Nombre_Estandar, estReg) VALUES (?, ?, 'A')",
                e.getCodEstandar(), e.getNombreEstandar());
    }

    public int update(ResiduoEstandarizado e) {
        return jdbc.update(
                "UPDATE Residuo_Estandarizado SET Nombre_Estandar = ? WHERE Cod_Estandar = ?",
                e.getNombreEstandar(), e.getCodEstandar());
    }

    public int updateState(int id, String newState) {
        return jdbc.update(
                "UPDATE Residuo_Estandarizado SET estReg = ? WHERE Cod_Estandar = ?",
                newState, id);
    }
}
