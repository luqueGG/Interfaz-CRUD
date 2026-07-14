INSERT INTO TR_Nivel_Toxicidad VALUES 
(1, 'Bajo', 'Riesgo mínimo', 'A'), (2, 'Medio', 'Riesgo moderado', 'A'), (3, 'Alto', 'Riesgo alto', 'A'), (4, 'Crítico', 'Riesgo severo', 'A'), (5, 'Extremo', 'Peligro inminente', 'A'),
(6, 'Nulo', 'Sin riesgo', 'A'), (7, 'Inflamable', 'Peligro de fuego', 'A'), (8, 'Corrosivo', 'Daña materiales', 'A'), (9, 'Reactivo', 'Reacciona al agua', 'A'), (10, 'Biológico', 'Riesgo infeccioso', 'A');

INSERT INTO TR_Tipo_Envase VALUES 
(1, 'Cilindro Metálico', '200 Litros', 'A'), (2, 'Cilindro Plástico', '100 Litros', 'A'), (3, 'Caja de Cartón', 'Para sólidos', 'A'), (4, 'Contenedor IBC', '1000 Litros', 'A'), (5, 'Bolsa Roja', 'Residuos Biocontaminados', 'A'),
(6, 'Bolsa Negra', 'Comunes', 'A'), (7, 'Tanque', 'Transporte a granel', 'A'), (8, 'Frasco Vidrio', 'Químicos puros', 'A'), (9, 'Saco', 'Material en polvo', 'A'), (10, 'Pallet', 'Para cajas apiladas', 'A');

INSERT INTO TR_Tipo_Tratamiento VALUES 
(1, 'Incineración', 'Quema controlada', 'A'), (2, 'Reciclaje', 'Reaprovechamiento', 'A'), (3, 'Confinamiento', 'Celda de seguridad', 'A'), (4, 'Neutralización', 'Tratamiento químico', 'A'), (5, 'Compostaje', 'Orgánicos', 'A'),
(6, 'Esterilización', 'Autoclave', 'A'), (7, 'Trituración', 'Reducción de volumen', 'A'), (8, 'Desinfección', 'Limpieza química', 'A'), (9, 'Encapsulamiento', 'En bloques de cemento', 'A'), (10, 'Destilación', 'Recuperación de solventes', 'A');

INSERT INTO TR_Tipo_Transporte VALUES 
(1, 'Camión Baranda', 'Abierto', 'A'), (2, 'Camión Cisterna', 'Líquidos', 'A'), (3, 'Camión Furgón', 'Cerrado', 'A'), (4, 'Camioneta', 'Cargas pequeñas', 'A'), (5, 'Volquete', 'Granel', 'A'),
(6, 'Tren', 'Larga distancia', 'A'), (7, 'Barco', 'Flete marítimo', 'A'), (8, 'Trailer', 'Gran capacidad', 'A'), (9, 'Motocarga', 'Local', 'A'), (10, 'Montacargas', 'Interno', 'A');

INSERT INTO Residuo_Estandarizado VALUES 
(101, 'Aceite Industrial Usado', 'A'), (102, 'Baterías de Plomo', 'A'), (103, 'Pinturas y Solventes', 'A'), (104, 'Tierras Contaminadas', 'A'), (105, 'Restos de Cobre', 'A'),
(106, 'Chatarra Ferrosa', 'A'), (107, 'Llantas Usadas', 'A'), (108, 'Residuos Clínicos', 'A'), (109, 'Plástico PET', 'A'), (110, 'Cartón y Papel', 'A');

INSERT INTO Constituyente VALUES 
('C01', 'Plomo', '', 'A'), ('C02', 'Mercurio', '', 'A'), ('C03', 'Cadmio', '', 'A'), ('C04', 'Hidrocarburos', '', 'A'), ('C05', 'Ácido Sulfúrico', '', 'A'),
('C06', 'Cianuro', '', 'A'), ('C07', 'Arsénico', '', 'A'), ('C08', 'Benceno', '', 'A'), ('C09', 'Cloro', '', 'A'), ('C10', 'Sílice', '', 'A');

INSERT INTO Region VALUES 
(1, 'Arequipa', 'A'), (2, 'Moquegua', 'A'), (3, 'Tacna', 'A'), (4, 'Puno', 'A'), (5, 'Cusco', 'A'), (6, 'Lima', 'A'), (7, 'Ica', 'A'), (8, 'Piura', 'A'), (9, 'Trujillo', 'A'), (10, 'Cajamarca', 'A');

INSERT INTO Empresa_Productora VALUES 
('PROD-01', 'Minera Cerro Verde', 'Arequipa', 'Minería', '', 'A'), ('PROD-02', 'Southern Peru', 'Moquegua', 'Minería', '', 'A'), ('PROD-03', 'Yura SA', 'Arequipa', 'Cemento', '', 'A'),
('PROD-04', 'Gloria SA', 'Arequipa', 'Lácteos', '', 'A'), ('PROD-05', 'Inca Tops', 'Arequipa', 'Textil', '', 'A'), ('PROD-06', 'Aceros Arequipa', 'Pisco', 'Siderúrgica', '', 'A'),
('PROD-07', 'Alicorp', 'Lima', 'Alimentos', '', 'A'), ('PROD-08', 'Siderperu', 'Chimbote', 'Siderúrgica', '', 'A'), ('PROD-09', 'Antamina', 'Huaraz', 'Minería', '', 'A'), ('PROD-10', 'Las Bambas', 'Apurimac', 'Minería', '', 'A');

INSERT INTO Empresa_Transportista VALUES 
('TRAN-01', 'Transportes Cruz', 'Arequipa', '', 'A'), ('TRAN-02', 'Logística Sur', 'Tacna', '', 'A'), ('TRAN-03', 'Transpesa', 'Lima', '', 'A'), ('TRAN-04', 'Rutas del Sur', 'Arequipa', '', 'A'), ('TRAN-05', 'Carga Rápida', 'Lima', '', 'A'),
('TRAN-06', 'TransMina', 'Cusco', '', 'A'), ('TRAN-07', 'EcoTrans', 'Arequipa', '', 'A'), ('TRAN-08', 'Pesados del Sur', 'Moquegua', '', 'A'), ('TRAN-09', 'Logistic Peru', 'Lima', '', 'A'), ('TRAN-10', 'Vial Andes', 'Puno', '', 'A');

INSERT INTO Destino VALUES 
('DEST-01', 1, 'Planta Yura', 'Arequipa', 50000, 15000, '', 'A'), ('DEST-02', 6, 'Relleno Zapallal', 'Lima', 100000, 85000, '', 'A'), ('DEST-03', 1, 'Incineradora Sur', 'Arequipa', 20000, 5000, '', 'A'),
('DEST-04', 2, 'Celda Cuajone', 'Moquegua', 40000, 39000, '', 'A'), ('DEST-05', 7, 'Recicla Ica', 'Ica', 15000, 2000, '', 'A'), ('DEST-06', 1, 'EcoPlanta AQP', 'Arequipa', 10000, 1000, '', 'A'),
('DEST-07', 5, 'Compostera Cusco', 'Cusco', 5000, 4000, '', 'A'), ('DEST-08', 8, 'Planta Piura', 'Piura', 30000, 15000, '', 'A'), ('DEST-09', 3, 'Tratamiento Tacna', 'Tacna', 25000, 12000, '', 'A'), ('DEST-10', 6, 'Seguridad Lurin', 'Lima', 80000, 79000, '', 'A');

INSERT INTO Residuo VALUES 
('RES-01', 'PROD-01', 101, 3, 5000.00, '', 'A'), ('RES-02', 'PROD-02', 102, 4, 2500.00, '', 'A'), ('RES-03', 'PROD-03', 106, 1, 10000.00, '', 'A'), ('RES-04', 'PROD-04', 109, 1, 1200.00, '', 'A'), ('RES-05', 'PROD-05', 103, 7, 800.00, '', 'A'),
('RES-06', 'PROD-06', 104, 2, 6000.00, '', 'A'), ('RES-07', 'PROD-07', 110, 6, 3000.00, '', 'A'), ('RES-08', 'PROD-08', 105, 1, 4500.00, '', 'A'), ('RES-09', 'PROD-09', 107, 2, 8000.00, '', 'A'), ('RES-10', 'PROD-10', 108, 10, 500.00, '', 'A');

INSERT INTO Residuo_Constituyente VALUES 
('RES-01', 'C04', 4000.00, 'A'), ('RES-02', 'C01', 2000.00, 'A'), ('RES-02', 'C05', 500.00, 'A'), ('RES-05', 'C08', 300.00, 'A'), ('RES-06', 'C07', 100.00, 'A'),
('RES-08', 'C01', 4000.00, 'A'), ('RES-09', 'C04', 1000.00, 'A'), ('RES-10', 'C09', 200.00, 'A'), ('RES-01', 'C01', 50.00, 'A'), ('RES-03', 'C10', 800.00, 'A');

INSERT INTO Traslado VALUES 
(1, 'RES-01', 'DEST-01', '2026-06-01', 500.00, 1, 1, '2026-06-02', '', 'A'), (2, 'RES-02', 'DEST-04', '2026-06-03', 1000.00, 4, 3, '2026-06-04', '', 'A'),
(3, 'RES-03', 'DEST-02', '2026-06-05', 2000.00, 5, 2, '2026-06-06', '', 'A'), (4, 'RES-04', 'DEST-05', '2026-06-07', 500.00, 6, 2, '2026-06-08', '', 'A'),
(5, 'RES-05', 'DEST-03', '2026-06-09', 800.00, 1, 1, '2026-06-09', '', 'A'), (6, 'RES-06', 'DEST-10', '2026-06-10', 1500.00, 4, 3, '2026-06-11', '', 'A'),
(7, 'RES-07', 'DEST-05', '2026-06-12', 3000.00, 10, 2, '2026-06-13', '', 'A'), (8, 'RES-08', 'DEST-01', '2026-06-14', 1000.00, 5, 2, '2026-06-15', '', 'A'),
(9, 'RES-09', 'DEST-09', '2026-06-16', 4000.00, 7, 7, '2026-06-17', '', 'A'), (10, 'RES-10', 'DEST-03', '2026-06-18', 250.00, 5, 1, '2026-06-18', '', 'A');

INSERT INTO Traslado_Transportista VALUES 
(1, 'TRAN-01', 2, 150.50, 500.00, 'A'), (2, 'TRAN-08', 3, 200.00, 800.00, 'A'), (3, 'TRAN-03', 8, 1000.00, 3500.00, 'A'), (4, 'TRAN-07', 3, 400.00, 1200.00, 'A'), (5, 'TRAN-04', 1, 50.00, 200.00, 'A'),
(6, 'TRAN-05', 8, 850.00, 3000.00, 'A'), (7, 'TRAN-09', 1, 300.00, 950.00, 'A'), (8, 'TRAN-01', 5, 100.00, 400.00, 'A'), (9, 'TRAN-02', 8, 500.00, 1500.00, 'A'), (10, 'TRAN-04', 3, 30.00, 100.00, 'A');