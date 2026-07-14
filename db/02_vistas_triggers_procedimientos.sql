-- =====================================================================
-- PROYECTO       : SolidToxic / RESSOL - Gestión de Residuos Sólidos
-- ARCHIVO        : 02_vistas_triggers_procedimientos.sql
-- SGBD           : PostgreSQL 16 (según docker-compose.yml del repo)
-- BASE DE DATOS  : solidtoxic
-- AUTOR(ES)      : << completar >>
-- FECHA CREACIÓN : << dd/mm/aaaa >>
-- ÚLTIMA MOD.    : << dd/mm/aaaa >>
-- REQUIERE       : db/postresiduos.sql ya ejecutado (tablas + datos semilla)
-- NOTA           : Ningún objeto de este archivo hace INSERT/UPDATE/DELETE
--                  sobre tablas de negocio (solo valida o consulta).
-- =====================================================================


-- =====================================================================
-- SECCIÓN 1. VISTAS
-- =====================================================================

-- Vista 1 (simple)
DROP VIEW IF EXISTS v_empresas_productoras;
CREATE VIEW v_empresas_productoras AS
SELECT NIF_Empresa, Nombre_Empresa, Ciudad_Empresa, Actividad
FROM Empresa_Productora
WHERE estReg = 'A';

-- Vista 2 (compleja, tabla fundamental + varias referenciales)
DROP VIEW IF EXISTS v_destinos_regiones;
CREATE VIEW v_destinos_regiones AS
SELECT d.Cod_Destino, d.Nombre_Destino, d.Ciudad_Destino, r.Nombre_Region,
       d.Capacidad_Maxima, d.Capacidad_Actual
FROM Destino d
INNER JOIN Region r ON d.ID_Region = r.ID_Region
WHERE d.estReg = 'A';

DROP VIEW IF EXISTS v_reporte_traslados_completo;
CREATE VIEW v_reporte_traslados_completo AS
SELECT
    t.ID_Traslado,
    t.Fecha_Envio,
    t.Fecha_Llegada,
    e.Nombre_Empresa                                    AS Productor,
    d.Nombre_Destino                                     AS Destino,
    rg.Nombre_Region                                     AS Region_Destino,
    te.Nombre_Envase,
    tt.Nombre_Tratamiento,
    nt.Nivel                                             AS Nivel_Riesgo,
    t.Cantidad_Trasladada,
    ROUND((d.Capacidad_Actual / NULLIF(d.Capacidad_Maxima, 0)) * 100, 2) AS Pct_Capacidad_Destino,
    CASE
        WHEN d.Capacidad_Actual >= d.Capacidad_Maxima THEN 'SATURADO'
        WHEN d.Capacidad_Actual >= d.Capacidad_Maxima * 0.85 THEN 'CRÍTICO'
        ELSE 'NORMAL'
    END                                                   AS Estado_Destino
FROM Traslado t
INNER JOIN Residuo r              ON t.Cod_Residuo = r.Cod_Residuo
INNER JOIN Empresa_Productora e   ON r.NIF_Empresa = e.NIF_Empresa
INNER JOIN TR_Nivel_Toxicidad nt  ON r.ID_Toxicidad = nt.ID_Toxicidad
INNER JOIN Destino d              ON t.Cod_Destino = d.Cod_Destino
INNER JOIN Region rg              ON d.ID_Region = rg.ID_Region
INNER JOIN TR_Tipo_Envase te      ON t.ID_Envase = te.ID_Envase
INNER JOIN TR_Tipo_Tratamiento tt ON t.ID_Tratamiento = tt.ID_Tratamiento;

-- Vista 3 (confidencialidad: enmascara el nombre del productor)
DROP VIEW IF EXISTS v_traslados_publico;
CREATE VIEW v_traslados_publico AS
SELECT
    t.ID_Traslado,
    t.Fecha_Envio,
    LEFT(e.Nombre_Empresa, 1) || REPEAT('*', GREATEST(LENGTH(e.Nombre_Empresa) - 1, 0)) AS Productor_Enmascarado,
    d.Nombre_Destino,
    nt.Nivel AS Nivel_Riesgo,
    t.Cantidad_Trasladada
FROM Traslado t
INNER JOIN Residuo r             ON t.Cod_Residuo = r.Cod_Residuo
INNER JOIN Empresa_Productora e  ON r.NIF_Empresa = e.NIF_Empresa
INNER JOIN TR_Nivel_Toxicidad nt ON r.ID_Toxicidad = nt.ID_Toxicidad
INNER JOIN Destino d              ON t.Cod_Destino = d.Cod_Destino;


-- =====================================================================
-- SECCIÓN 2. TRIGGERS (solo validan, jamás escriben datos)
-- =====================================================================

-- Trigger 1 (simple): capacidad de Destino
DROP TRIGGER IF EXISTS trg_validar_capacidad ON Destino;
DROP FUNCTION IF EXISTS fn_trg_validar_capacidad();

CREATE FUNCTION fn_trg_validar_capacidad()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.Capacidad_Actual < 0 THEN
        RAISE EXCEPTION 'ERROR: La capacidad actual no puede ser negativa (Destino %).', NEW.Cod_Destino;
    ELSIF NEW.Capacidad_Actual > NEW.Capacidad_Maxima THEN
        RAISE EXCEPTION 'ERROR: La capacidad actual (%) supera la máxima (%) del destino %.',
            NEW.Capacidad_Actual, NEW.Capacidad_Maxima, NEW.Cod_Destino;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validar_capacidad
BEFORE INSERT OR UPDATE ON Destino
FOR EACH ROW
EXECUTE FUNCTION fn_trg_validar_capacidad();


-- Trigger 2 (complejo): validación integral de Traslado
DROP TRIGGER IF EXISTS trg_validar_traslado_integral ON Traslado;
DROP FUNCTION IF EXISTS fn_trg_validar_traslado_integral();

CREATE FUNCTION fn_trg_validar_traslado_integral()
RETURNS TRIGGER AS $$
DECLARE
    v_estado_empresa CHAR(1);
    v_cantidad_total  NUMERIC;
    v_ya_trasladado   NUMERIC;
    v_cap_actual      NUMERIC;
    v_cap_maxima      NUMERIC;
BEGIN
    -- a) Coherencia de fechas (Fecha_Llegada es opcional/nullable)
    IF NEW.Fecha_Llegada IS NOT NULL AND NEW.Fecha_Llegada < NEW.Fecha_Envio THEN
        RAISE EXCEPTION 'ERROR: La fecha de llegada (%) es anterior a la de envío (%).',
            NEW.Fecha_Llegada, NEW.Fecha_Envio;
    END IF;

    -- b) Cantidad positiva
    IF NEW.Cantidad_Trasladada <= 0 THEN
        RAISE EXCEPTION 'ERROR: La cantidad trasladada debe ser mayor a cero (recibido: %).', NEW.Cantidad_Trasladada;
    END IF;

    -- c) Empresa productora activa
    SELECT e.estReg, r.Cantidad_Total
      INTO v_estado_empresa, v_cantidad_total
    FROM Residuo r
    INNER JOIN Empresa_Productora e ON r.NIF_Empresa = e.NIF_Empresa
    WHERE r.Cod_Residuo = NEW.Cod_Residuo;

    IF v_estado_empresa IS DISTINCT FROM 'A' THEN
        RAISE EXCEPTION 'ERROR: La empresa productora del residuo % no está activa.', NEW.Cod_Residuo;
    END IF;

    -- d) No exceder el stock disponible del residuo
    SELECT COALESCE(SUM(Cantidad_Trasladada), 0)
      INTO v_ya_trasladado
    FROM Traslado
    WHERE Cod_Residuo = NEW.Cod_Residuo;

    IF (v_ya_trasladado + NEW.Cantidad_Trasladada) > v_cantidad_total THEN
        RAISE EXCEPTION 'ERROR: El traslado supera el stock disponible del residuo % (disponible: %, solicitado: %).',
            NEW.Cod_Residuo, (v_cantidad_total - v_ya_trasladado), NEW.Cantidad_Trasladada;
    END IF;

    -- e) No saturar la capacidad del destino
    SELECT Capacidad_Actual, Capacidad_Maxima
      INTO v_cap_actual, v_cap_maxima
    FROM Destino
    WHERE Cod_Destino = NEW.Cod_Destino;

    IF (v_cap_actual + NEW.Cantidad_Trasladada) > v_cap_maxima THEN
        RAISE EXCEPTION 'ERROR: El destino % no tiene capacidad suficiente (actual: %, máxima: %, solicitado: %).',
            NEW.Cod_Destino, v_cap_actual, v_cap_maxima, NEW.Cantidad_Trasladada;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validar_traslado_integral
BEFORE INSERT OR UPDATE ON Traslado
FOR EACH ROW
EXECUTE FUNCTION fn_trg_validar_traslado_integral();


-- =====================================================================
-- SECCIÓN 3. PROCEDIMIENTO DE VALIDACIÓN (CALL, sin resultados)
-- =====================================================================
DROP PROCEDURE IF EXISTS sp_validar_costos_transporte(NUMERIC, NUMERIC, VARCHAR);

CREATE PROCEDURE sp_validar_costos_transporte(
    IN p_kms   NUMERIC,
    IN p_costo NUMERIC,
    IN p_nif_transportista VARCHAR
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_costo_km_actual   NUMERIC;
    v_costo_km_promedio NUMERIC;
BEGIN
    IF p_kms < 0 OR p_costo < 0 THEN
        RAISE EXCEPTION 'AUDITORÍA: Costos o kilometrajes no pueden ser negativos. Registro bloqueado.';
    END IF;

    IF p_kms = 0 THEN
        RAISE EXCEPTION 'AUDITORÍA: Un traslado no puede registrar 0 kilómetros recorridos.';
    END IF;

    v_costo_km_actual := p_costo / p_kms;

    SELECT AVG(Costo / NULLIF(Kms_Recorridos, 0))
      INTO v_costo_km_promedio
    FROM Traslado_Transportista
    WHERE NIF_Transportista = p_nif_transportista;

    IF v_costo_km_promedio IS NOT NULL
       AND v_costo_km_actual > v_costo_km_promedio * 1.5 THEN
        RAISE EXCEPTION 'AUDITORÍA: Costo/km (%) supera en más de 50%% el promedio histórico (%) del transportista %.',
            ROUND(v_costo_km_actual, 2), ROUND(v_costo_km_promedio, 2), p_nif_transportista;
    END IF;
END;
$$;

-- Procedimiento 2 (simple): valida nivel de toxicidad vs cantidad manejada
DROP PROCEDURE IF EXISTS sp_validar_nivel_toxicidad(VARCHAR);

CREATE PROCEDURE sp_validar_nivel_toxicidad(
    IN p_cod_residuo VARCHAR
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_nivel    VARCHAR;
    v_cantidad NUMERIC;
BEGIN
    SELECT nt.Nivel, r.Cantidad_Total
      INTO v_nivel, v_cantidad
    FROM Residuo r
    INNER JOIN TR_Nivel_Toxicidad nt ON r.ID_Toxicidad = nt.ID_Toxicidad
    WHERE r.Cod_Residuo = p_cod_residuo;

    IF v_nivel IS NULL THEN
        RAISE EXCEPTION 'AUDITORÍA: No existe el residuo %.', p_cod_residuo;
    END IF;

    IF v_cantidad > 500 THEN
        RAISE EXCEPTION 'AUDITORÍA: Residuo % (nivel %) excede el límite seguro de manejo de 500 kg (cantidad: % kg).',
            p_cod_residuo, v_nivel, v_cantidad;
    END IF;

    RAISE NOTICE 'OK: Residuo % validado — nivel %, cantidad % kg dentro de límites.', p_cod_residuo, v_nivel, v_cantidad;
END;
$$;

DROP TRIGGER IF EXISTS trg_auditoria_transporte ON Traslado_Transportista;
DROP FUNCTION IF EXISTS fn_trg_auditoria_transporte();

CREATE FUNCTION fn_trg_auditoria_transporte()
RETURNS TRIGGER AS $$
BEGIN
    CALL sp_validar_costos_transporte(NEW.Kms_Recorridos, NEW.Costo, NEW.NIF_Transportista);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_auditoria_transporte
BEFORE INSERT ON Traslado_Transportista
FOR EACH ROW
EXECUTE FUNCTION fn_trg_auditoria_transporte();


-- =====================================================================
-- SECCIÓN 4. FUNCIONES DE REPORTE (equivalen a "procedimientos" de
-- reporte; se consumen como  SELECT * FROM nombre_funcion(...)  )
-- =====================================================================

DROP FUNCTION IF EXISTS sp_reporte_destinos();
CREATE FUNCTION sp_reporte_destinos()
RETURNS TABLE (
    Cod_Destino      VARCHAR,
    Nombre_Destino   VARCHAR,
    Ciudad_Destino   VARCHAR,
    Nombre_Region    VARCHAR,
    Capacidad_Maxima NUMERIC,
    Capacidad_Actual NUMERIC,
    Pct_Uso          NUMERIC,
    Estado           TEXT
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        v.Cod_Destino, v.Nombre_Destino, v.Ciudad_Destino, v.Nombre_Region,
        v.Capacidad_Maxima, v.Capacidad_Actual,
        ROUND((v.Capacidad_Actual / NULLIF(v.Capacidad_Maxima, 0)) * 100, 2),
        CASE
            WHEN v.Capacidad_Actual >= v.Capacidad_Maxima THEN 'SATURADO'
            WHEN v.Capacidad_Actual >= v.Capacidad_Maxima * 0.85 THEN 'CRÍTICO'
            ELSE 'NORMAL'
        END
    FROM v_destinos_regiones v
    ORDER BY v.Nombre_Region;
END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS sp_buscar_traslados_por_productor(VARCHAR, DATE, DATE);
CREATE FUNCTION sp_buscar_traslados_por_productor(
    p_nombre_empresa VARCHAR,
    p_fecha_desde    DATE DEFAULT NULL,
    p_fecha_hasta    DATE DEFAULT NULL
)
RETURNS SETOF v_reporte_traslados_completo AS $$
BEGIN
    RETURN QUERY
    SELECT *
    FROM v_reporte_traslados_completo
    WHERE Productor ILIKE '%' || p_nombre_empresa || '%'
      AND (p_fecha_desde IS NULL OR Fecha_Envio >= p_fecha_desde)
      AND (p_fecha_hasta IS NULL OR Fecha_Envio <= p_fecha_hasta)
    ORDER BY Fecha_Envio DESC;
END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS sp_dashboard_general();
CREATE FUNCTION sp_dashboard_general()
RETURNS JSON AS $$
DECLARE
    v_resultado JSON;
BEGIN
    SELECT json_build_object(
        'capacidad_total_maxima', (SELECT SUM(Capacidad_Maxima) FROM Destino),
        'capacidad_total_actual', (SELECT SUM(Capacidad_Actual) FROM Destino),
        'kilos_totales_trasladados', (SELECT COALESCE(SUM(Cantidad_Trasladada), 0) FROM Traslado),
        'destinos_criticos_o_saturados', (
            SELECT COUNT(*) FROM Destino WHERE Capacidad_Actual >= Capacidad_Maxima * 0.85
        ),
        'top_5_empresas_por_volumen', (
            SELECT COALESCE(json_agg(t), '[]'::json) FROM (
                SELECT e.Nombre_Empresa, SUM(tr.Cantidad_Trasladada) AS Total_Kilos
                FROM Traslado tr
                INNER JOIN Residuo r ON tr.Cod_Residuo = r.Cod_Residuo
                INNER JOIN Empresa_Productora e ON r.NIF_Empresa = e.NIF_Empresa
                GROUP BY e.Nombre_Empresa
                ORDER BY Total_Kilos DESC
                LIMIT 5
            ) t
        ),
        'residuos_por_nivel_riesgo', (
            SELECT COALESCE(json_agg(t), '[]'::json) FROM (
                SELECT nt.Nivel, COUNT(*) AS Nro_Residuos, SUM(r.Cantidad_Total) AS Kilos_Totales
                FROM Residuo r
                INNER JOIN TR_Nivel_Toxicidad nt ON r.ID_Toxicidad = nt.ID_Toxicidad
                GROUP BY nt.Nivel
            ) t
        )
    )
    INTO v_resultado;
    RETURN v_resultado;
END;
$$ LANGUAGE plpgsql;


-- =====================================================================
-- SECCIÓN 5. CONFIDENCIALIDAD A NIVEL DE ROLES
-- =====================================================================
DROP ROLE IF EXISTS rol_reportes_publicos;
CREATE ROLE rol_reportes_publicos NOLOGIN;
GRANT SELECT ON v_traslados_publico, v_destinos_regiones TO rol_reportes_publicos;

DROP ROLE IF EXISTS rol_auditor;
CREATE ROLE rol_auditor NOLOGIN;
GRANT SELECT ON v_reporte_traslados_completo, v_empresas_productoras TO rol_auditor;

-- =====================================================================
-- FIN
-- =====================================================================
