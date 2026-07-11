package com.solidtoxic.repository;

import com.solidtoxic.model.ResiduoConstituyente;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ResiduoConstituyenteRepository {

    private final JdbcTemplate jdbc;

    public ResiduoConstituyenteRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<ResiduoConstituyente> rowMapper = (rs, rn) -> new ResiduoConstituyente(
            rs.getString("Cod_Residuo"),
            rs.getString("Cod_Constituyente"),
            rs.getBigDecimal("Cantidad"),
            rs.getString("estReg")
    );

    public List<ResiduoConstituyente> findByState(String state) {
        return jdbc.query(
                "SELECT Cod_Residuo, Cod_Constituyente, Cantidad, estReg " +
                "FROM Residuo_Constituyente WHERE estReg = ?",
                rowMapper, state);
    }

    public List<ResiduoConstituyente> findByResiduo(String codResiduo) {
        return jdbc.query(
                "SELECT Cod_Residuo, Cod_Constituyente, Cantidad, estReg " +
                "FROM Residuo_Constituyente WHERE Cod_Residuo = ?",
                rowMapper, codResiduo);
    }

    public Optional<ResiduoConstituyente> findByCompositeKey(String codResiduo, String codConstituyente) {
        List<ResiduoConstituyente> results = jdbc.query(
                "SELECT Cod_Residuo, Cod_Constituyente, Cantidad, estReg FROM Residuo_Constituyente " +
                "WHERE Cod_Residuo = ? AND Cod_Constituyente = ?",
                rowMapper, codResiduo, codConstituyente);
        return results.stream().findFirst();
    }

    public boolean existsByCompositeKey(String codResiduo, String codConstituyente) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM Residuo_Constituyente WHERE Cod_Residuo = ? AND Cod_Constituyente = ?",
                Integer.class, codResiduo, codConstituyente);
        return count != null && count > 0;
    }

    public int insert(ResiduoConstituyente e) {
        return jdbc.update(
                "INSERT INTO Residuo_Constituyente (Cod_Residuo, Cod_Constituyente, Cantidad, estReg) " +
                "VALUES (?, ?, ?, 'A')",
                e.getCodResiduo(), e.getCodConstituyente(), e.getCantidad());
    }

    public int update(ResiduoConstituyente e) {
        return jdbc.update(
                "UPDATE Residuo_Constituyente SET Cantidad = ? WHERE Cod_Residuo = ? AND Cod_Constituyente = ?",
                e.getCantidad(), e.getCodResiduo(), e.getCodConstituyente());
    }

    public int updateState(String codResiduo, String codConstituyente, String newState) {
        return jdbc.update(
                "UPDATE Residuo_Constituyente SET estReg = ? WHERE Cod_Residuo = ? AND Cod_Constituyente = ?",
                newState, codResiduo, codConstituyente);
    }
}
