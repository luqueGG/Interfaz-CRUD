package com.solidtoxic.repository;

import com.solidtoxic.model.TipoTratamiento;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TipoTratamientoRepository {

    private final JdbcTemplate jdbc;

    public TipoTratamientoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<TipoTratamiento> rowMapper = (rs, rn) -> new TipoTratamiento(
            rs.getInt("ID_Tratamiento"),
            rs.getString("Nombre_Tratamiento"),
            rs.getString("Descripcion"),
            rs.getString("estReg")
    );

    public List<TipoTratamiento> findByState(String state) {
        return jdbc.query(
                "SELECT ID_Tratamiento, Nombre_Tratamiento, Descripcion, estReg FROM TR_Tipo_Tratamiento WHERE estReg = ?",
                rowMapper, state);
    }

    public Optional<TipoTratamiento> findById(int id) {
        List<TipoTratamiento> results = jdbc.query(
                "SELECT ID_Tratamiento, Nombre_Tratamiento, Descripcion, estReg FROM TR_Tipo_Tratamiento WHERE ID_Tratamiento = ?",
                rowMapper, id);
        return results.stream().findFirst();
    }

    public boolean existsById(int id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM TR_Tipo_Tratamiento WHERE ID_Tratamiento = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public boolean existsByIdAndActive(int id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM TR_Tipo_Tratamiento WHERE ID_Tratamiento = ? AND estReg = 'A'", Integer.class, id);
        return count != null && count > 0;
    }

    public int insert(TipoTratamiento e) {
        return jdbc.update(
                "INSERT INTO TR_Tipo_Tratamiento (ID_Tratamiento, Nombre_Tratamiento, Descripcion, estReg) VALUES (?, ?, ?, 'A')",
                e.getIdTratamiento(), e.getNombreTratamiento(), e.getDescripcion());
    }

    public int update(TipoTratamiento e) {
        return jdbc.update(
                "UPDATE TR_Tipo_Tratamiento SET Nombre_Tratamiento = ?, Descripcion = ? WHERE ID_Tratamiento = ?",
                e.getNombreTratamiento(), e.getDescripcion(), e.getIdTratamiento());
    }

    public int updateState(int id, String newState) {
        return jdbc.update(
                "UPDATE TR_Tipo_Tratamiento SET estReg = ? WHERE ID_Tratamiento = ?",
                newState, id);
    }
}
