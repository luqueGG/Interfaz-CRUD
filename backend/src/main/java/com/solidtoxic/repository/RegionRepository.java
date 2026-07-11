package com.solidtoxic.repository;

import com.solidtoxic.model.Region;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RegionRepository {

    private final JdbcTemplate jdbc;

    public RegionRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Region> rowMapper = (rs, rn) -> new Region(
            rs.getInt("ID_Region"),
            rs.getString("Nombre_Region"),
            rs.getString("estReg")
    );

    public List<Region> findByState(String state) {
        return jdbc.query(
                "SELECT ID_Region, Nombre_Region, estReg FROM Region WHERE estReg = ?",
                rowMapper, state);
    }

    public Optional<Region> findById(int id) {
        List<Region> results = jdbc.query(
                "SELECT ID_Region, Nombre_Region, estReg FROM Region WHERE ID_Region = ?",
                rowMapper, id);
        return results.stream().findFirst();
    }

    public boolean existsById(int id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM Region WHERE ID_Region = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public boolean existsByIdAndActive(int id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM Region WHERE ID_Region = ? AND estReg = 'A'", Integer.class, id);
        return count != null && count > 0;
    }

    public int insert(Region e) {
        return jdbc.update(
                "INSERT INTO Region (ID_Region, Nombre_Region, estReg) VALUES (?, ?, 'A')",
                e.getIdRegion(), e.getNombreRegion());
    }

    public int update(Region e) {
        return jdbc.update(
                "UPDATE Region SET Nombre_Region = ? WHERE ID_Region = ?",
                e.getNombreRegion(), e.getIdRegion());
    }

    public int updateState(int id, String newState) {
        return jdbc.update(
                "UPDATE Region SET estReg = ? WHERE ID_Region = ?",
                newState, id);
    }
}
