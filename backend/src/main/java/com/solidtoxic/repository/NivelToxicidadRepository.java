package com.solidtoxic.repository;

import com.solidtoxic.model.NivelToxicidad;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class NivelToxicidadRepository {

    private final JdbcTemplate jdbc;

    public NivelToxicidadRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<NivelToxicidad> rowMapper = (rs, rn) -> new NivelToxicidad(
            rs.getInt("ID_Toxicidad"),
            rs.getString("Nivel"),
            rs.getString("Descripcion"),
            rs.getString("estReg")
    );

    public List<NivelToxicidad> findByState(String state) {
        return jdbc.query(
                "SELECT ID_Toxicidad, Nivel, Descripcion, estReg FROM TR_Nivel_Toxicidad WHERE estReg = ?",
                rowMapper, state);
    }

    public Optional<NivelToxicidad> findById(int id) {
        List<NivelToxicidad> results = jdbc.query(
                "SELECT ID_Toxicidad, Nivel, Descripcion, estReg FROM TR_Nivel_Toxicidad WHERE ID_Toxicidad = ?",
                rowMapper, id);
        return results.stream().findFirst();
    }

    public boolean existsById(int id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM TR_Nivel_Toxicidad WHERE ID_Toxicidad = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public boolean existsByIdAndActive(int id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM TR_Nivel_Toxicidad WHERE ID_Toxicidad = ? AND estReg = 'A'", Integer.class, id);
        return count != null && count > 0;
    }

    public int insert(NivelToxicidad e) {
        return jdbc.update(
                "INSERT INTO TR_Nivel_Toxicidad (ID_Toxicidad, Nivel, Descripcion, estReg) VALUES (?, ?, ?, 'A')",
                e.getIdToxicidad(), e.getNivel(), e.getDescripcion());
    }

    public int update(NivelToxicidad e) {
        return jdbc.update(
                "UPDATE TR_Nivel_Toxicidad SET Nivel = ?, Descripcion = ? WHERE ID_Toxicidad = ?",
                e.getNivel(), e.getDescripcion(), e.getIdToxicidad());
    }

    public int updateState(int id, String newState) {
        return jdbc.update(
                "UPDATE TR_Nivel_Toxicidad SET estReg = ? WHERE ID_Toxicidad = ?",
                newState, id);
    }
}
