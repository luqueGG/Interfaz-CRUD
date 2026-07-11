package com.solidtoxic.repository;

import com.solidtoxic.model.TipoTransporte;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TipoTransporteRepository {

    private final JdbcTemplate jdbc;

    public TipoTransporteRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<TipoTransporte> rowMapper = (rs, rn) -> new TipoTransporte(
            rs.getInt("ID_Tipo_Transporte"),
            rs.getString("Nombre_Transporte"),
            rs.getString("Descripcion"),
            rs.getString("estReg")
    );

    public List<TipoTransporte> findByState(String state) {
        return jdbc.query(
                "SELECT ID_Tipo_Transporte, Nombre_Transporte, Descripcion, estReg FROM TR_Tipo_Transporte WHERE estReg = ?",
                rowMapper, state);
    }

    public Optional<TipoTransporte> findById(int id) {
        List<TipoTransporte> results = jdbc.query(
                "SELECT ID_Tipo_Transporte, Nombre_Transporte, Descripcion, estReg FROM TR_Tipo_Transporte WHERE ID_Tipo_Transporte = ?",
                rowMapper, id);
        return results.stream().findFirst();
    }

    public boolean existsById(int id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM TR_Tipo_Transporte WHERE ID_Tipo_Transporte = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public boolean existsByIdAndActive(int id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM TR_Tipo_Transporte WHERE ID_Tipo_Transporte = ? AND estReg = 'A'", Integer.class, id);
        return count != null && count > 0;
    }

    public int insert(TipoTransporte e) {
        return jdbc.update(
                "INSERT INTO TR_Tipo_Transporte (ID_Tipo_Transporte, Nombre_Transporte, Descripcion, estReg) VALUES (?, ?, ?, 'A')",
                e.getIdTipoTransporte(), e.getNombreTransporte(), e.getDescripcion());
    }

    public int update(TipoTransporte e) {
        return jdbc.update(
                "UPDATE TR_Tipo_Transporte SET Nombre_Transporte = ?, Descripcion = ? WHERE ID_Tipo_Transporte = ?",
                e.getNombreTransporte(), e.getDescripcion(), e.getIdTipoTransporte());
    }

    public int updateState(int id, String newState) {
        return jdbc.update(
                "UPDATE TR_Tipo_Transporte SET estReg = ? WHERE ID_Tipo_Transporte = ?",
                newState, id);
    }
}
