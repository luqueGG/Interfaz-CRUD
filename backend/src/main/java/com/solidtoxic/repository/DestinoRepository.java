package com.solidtoxic.repository;

import com.solidtoxic.model.Destino;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DestinoRepository {

    private final JdbcTemplate jdbc;

    public DestinoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Destino> rowMapper = (rs, rn) -> new Destino(
            rs.getString("Cod_Destino"),
            rs.getInt("ID_Region"),
            rs.getString("Nombre_Destino"),
            rs.getString("Ciudad_Destino"),
            rs.getBigDecimal("Capacidad_Maxima"),
            rs.getBigDecimal("Capacidad_Actual"),
            rs.getString("Otros_Datos"),
            rs.getString("estReg")
    );

    public List<Destino> findAll() {
        return jdbc.query(
                "SELECT Cod_Destino, ID_Region, Nombre_Destino, Ciudad_Destino, " +
                "Capacidad_Maxima, Capacidad_Actual, Otros_Datos, estReg FROM Destino ORDER BY Cod_Destino",
                rowMapper);
    }

    public List<Destino> findByState(String state) {
        return jdbc.query(
                "SELECT Cod_Destino, ID_Region, Nombre_Destino, Ciudad_Destino, " +
                "Capacidad_Maxima, Capacidad_Actual, Otros_Datos, estReg FROM Destino WHERE estReg = ? ORDER BY Cod_Destino",
                rowMapper, state);
    }

    public Optional<Destino> findById(String id) {
        List<Destino> results = jdbc.query(
                "SELECT Cod_Destino, ID_Region, Nombre_Destino, Ciudad_Destino, " +
                "Capacidad_Maxima, Capacidad_Actual, Otros_Datos, estReg FROM Destino WHERE Cod_Destino = ?",
                rowMapper, id);
        return results.stream().findFirst();
    }

    public boolean existsById(String id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM Destino WHERE Cod_Destino = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public boolean existsByIdAndActive(String id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM Destino WHERE Cod_Destino = ? AND estReg = 'A'", Integer.class, id);
        return count != null && count > 0;
    }

    public int insert(Destino e) {
        return jdbc.update(
                "INSERT INTO Destino (Cod_Destino, ID_Region, Nombre_Destino, Ciudad_Destino, " +
                "Capacidad_Maxima, Capacidad_Actual, Otros_Datos, estReg) VALUES (?, ?, ?, ?, ?, ?, ?, 'A')",
                e.getCodDestino(), e.getIdRegion(), e.getNombreDestino(), e.getCiudadDestino(),
                e.getCapacidadMaxima(), e.getCapacidadActual(), e.getOtrosDatos());
    }

    public int update(Destino e) {
        return jdbc.update(
                "UPDATE Destino SET ID_Region = ?, Nombre_Destino = ?, Ciudad_Destino = ?, " +
                "Capacidad_Maxima = ?, Capacidad_Actual = ?, Otros_Datos = ? WHERE Cod_Destino = ?",
                e.getIdRegion(), e.getNombreDestino(), e.getCiudadDestino(),
                e.getCapacidadMaxima(), e.getCapacidadActual(), e.getOtrosDatos(), e.getCodDestino());
    }

    public int updateState(String id, String newState) {
        return jdbc.update("UPDATE Destino SET estReg = ? WHERE Cod_Destino = ?", newState, id);
    }
}
