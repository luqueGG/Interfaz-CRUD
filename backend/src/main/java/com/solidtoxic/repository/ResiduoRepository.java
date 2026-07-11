package com.solidtoxic.repository;

import com.solidtoxic.model.Residuo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ResiduoRepository {

    private final JdbcTemplate jdbc;

    public ResiduoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Residuo> rowMapper = (rs, rn) -> new Residuo(
            rs.getString("Cod_Residuo"),
            rs.getString("NIF_Empresa"),
            rs.getInt("Cod_Estandar"),
            rs.getInt("ID_Toxicidad"),
            rs.getBigDecimal("Cantidad_Total"),
            rs.getString("Otros_Datos"),
            rs.getString("estReg")
    );

    public List<Residuo> findByState(String state) {
        return jdbc.query(
                "SELECT Cod_Residuo, NIF_Empresa, Cod_Estandar, ID_Toxicidad, " +
                "Cantidad_Total, Otros_Datos, estReg FROM Residuo WHERE estReg = ?",
                rowMapper, state);
    }

    public Optional<Residuo> findById(String id) {
        List<Residuo> results = jdbc.query(
                "SELECT Cod_Residuo, NIF_Empresa, Cod_Estandar, ID_Toxicidad, " +
                "Cantidad_Total, Otros_Datos, estReg FROM Residuo WHERE Cod_Residuo = ?",
                rowMapper, id);
        return results.stream().findFirst();
    }

    public boolean existsById(String id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM Residuo WHERE Cod_Residuo = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public boolean existsByIdAndActive(String id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM Residuo WHERE Cod_Residuo = ? AND estReg = 'A'", Integer.class, id);
        return count != null && count > 0;
    }

    public int insert(Residuo e) {
        return jdbc.update(
                "INSERT INTO Residuo (Cod_Residuo, NIF_Empresa, Cod_Estandar, ID_Toxicidad, " +
                "Cantidad_Total, Otros_Datos, estReg) VALUES (?, ?, ?, ?, ?, ?, 'A')",
                e.getCodResiduo(), e.getNifEmpresa(), e.getCodEstandar(),
                e.getIdToxicidad(), e.getCantidadTotal(), e.getOtrosDatos());
    }

    public int update(Residuo e) {
        return jdbc.update(
                "UPDATE Residuo SET NIF_Empresa = ?, Cod_Estandar = ?, ID_Toxicidad = ?, " +
                "Cantidad_Total = ?, Otros_Datos = ? WHERE Cod_Residuo = ?",
                e.getNifEmpresa(), e.getCodEstandar(), e.getIdToxicidad(),
                e.getCantidadTotal(), e.getOtrosDatos(), e.getCodResiduo());
    }

    public int updateState(String id, String newState) {
        return jdbc.update("UPDATE Residuo SET estReg = ? WHERE Cod_Residuo = ?", newState, id);
    }
}
