package com.solidtoxic.repository;

import com.solidtoxic.model.Traslado;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrasladoRepository {

    private final JdbcTemplate jdbc;

    public TrasladoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Traslado> rowMapper = (rs, rn) -> {
        Traslado t = new Traslado();
        t.setIdTraslado(rs.getInt("ID_Traslado"));
        t.setCodResiduo(rs.getString("Cod_Residuo"));
        t.setCodDestino(rs.getString("Cod_Destino"));
        t.setFechaEnvio(rs.getDate("Fecha_Envio") != null ? rs.getDate("Fecha_Envio").toLocalDate() : null);
        t.setCantidadTrasladada(rs.getBigDecimal("Cantidad_Trasladada"));
        t.setIdEnvase(rs.getInt("ID_Envase"));
        t.setIdTratamiento(rs.getInt("ID_Tratamiento"));
        t.setFechaLlegada(rs.getDate("Fecha_Llegada") != null ? rs.getDate("Fecha_Llegada").toLocalDate() : null);
        t.setOtrosDatos(rs.getString("Otros_Datos"));
        t.setEstReg(rs.getString("estReg"));
        return t;
    };

    public List<Traslado> findByState(String state) {
        return jdbc.query(
                "SELECT ID_Traslado, Cod_Residuo, Cod_Destino, Fecha_Envio, Cantidad_Trasladada, " +
                "ID_Envase, ID_Tratamiento, Fecha_Llegada, Otros_Datos, estReg FROM Traslado WHERE estReg = ?",
                rowMapper, state);
    }

    public Optional<Traslado> findById(int id) {
        List<Traslado> results = jdbc.query(
                "SELECT ID_Traslado, Cod_Residuo, Cod_Destino, Fecha_Envio, Cantidad_Trasladada, " +
                "ID_Envase, ID_Tratamiento, Fecha_Llegada, Otros_Datos, estReg FROM Traslado WHERE ID_Traslado = ?",
                rowMapper, id);
        return results.stream().findFirst();
    }

    public boolean existsById(int id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM Traslado WHERE ID_Traslado = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public boolean existsByIdAndActive(int id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM Traslado WHERE ID_Traslado = ? AND estReg = 'A'", Integer.class, id);
        return count != null && count > 0;
    }

    public int insert(Traslado e) {
        return jdbc.update(
                "INSERT INTO Traslado (ID_Traslado, Cod_Residuo, Cod_Destino, Fecha_Envio, " +
                "Cantidad_Trasladada, ID_Envase, ID_Tratamiento, Fecha_Llegada, Otros_Datos, estReg) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'A')",
                e.getIdTraslado(), e.getCodResiduo(), e.getCodDestino(),
                e.getFechaEnvio(), e.getCantidadTrasladada(), e.getIdEnvase(),
                e.getIdTratamiento(), e.getFechaLlegada(), e.getOtrosDatos());
    }

    public int update(Traslado e) {
        return jdbc.update(
                "UPDATE Traslado SET Cod_Residuo = ?, Cod_Destino = ?, Fecha_Envio = ?, " +
                "Cantidad_Trasladada = ?, ID_Envase = ?, ID_Tratamiento = ?, Fecha_Llegada = ?, Otros_Datos = ? " +
                "WHERE ID_Traslado = ?",
                e.getCodResiduo(), e.getCodDestino(), e.getFechaEnvio(),
                e.getCantidadTrasladada(), e.getIdEnvase(), e.getIdTratamiento(),
                e.getFechaLlegada(), e.getOtrosDatos(), e.getIdTraslado());
    }

    public int updateState(int id, String newState) {
        return jdbc.update("UPDATE Traslado SET estReg = ? WHERE ID_Traslado = ?", newState, id);
    }
}
