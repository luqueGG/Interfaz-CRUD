package com.solidtoxic.repository;

import com.solidtoxic.model.TipoEnvase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TipoEnvaseRepository {

    private final JdbcTemplate jdbc;

    public TipoEnvaseRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<TipoEnvase> rowMapper = (rs, rn) -> new TipoEnvase(
            rs.getInt("ID_Envase"),
            rs.getString("Nombre_Envase"),
            rs.getString("Descripcion"),
            rs.getString("estReg")
    );

    public List<TipoEnvase> findByState(String state) {
        return jdbc.query(
                "SELECT ID_Envase, Nombre_Envase, Descripcion, estReg FROM TR_Tipo_Envase WHERE estReg = ?",
                rowMapper, state);
    }

    public Optional<TipoEnvase> findById(int id) {
        List<TipoEnvase> results = jdbc.query(
                "SELECT ID_Envase, Nombre_Envase, Descripcion, estReg FROM TR_Tipo_Envase WHERE ID_Envase = ?",
                rowMapper, id);
        return results.stream().findFirst();
    }

    public boolean existsById(int id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM TR_Tipo_Envase WHERE ID_Envase = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public boolean existsByIdAndActive(int id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM TR_Tipo_Envase WHERE ID_Envase = ? AND estReg = 'A'", Integer.class, id);
        return count != null && count > 0;
    }

    public int insert(TipoEnvase e) {
        return jdbc.update(
                "INSERT INTO TR_Tipo_Envase (ID_Envase, Nombre_Envase, Descripcion, estReg) VALUES (?, ?, ?, 'A')",
                e.getIdEnvase(), e.getNombreEnvase(), e.getDescripcion());
    }

    public int update(TipoEnvase e) {
        return jdbc.update(
                "UPDATE TR_Tipo_Envase SET Nombre_Envase = ?, Descripcion = ? WHERE ID_Envase = ?",
                e.getNombreEnvase(), e.getDescripcion(), e.getIdEnvase());
    }

    public int updateState(int id, String newState) {
        return jdbc.update(
                "UPDATE TR_Tipo_Envase SET estReg = ? WHERE ID_Envase = ?",
                newState, id);
    }
}
