
-- 1 TABLAS REFERENCIALES 

CREATE TABLE TR_Nivel_Toxicidad (
    ID_Toxicidad INT PRIMARY KEY,
    Nivel VARCHAR(20) NOT NULL,
    Descripcion VARCHAR(250),
    estReg CHAR(1) NOT NULL DEFAULT 'A'
);

CREATE TABLE TR_Tipo_Envase (
    ID_Envase INT PRIMARY KEY,
    Nombre_Envase VARCHAR(50) NOT NULL,
    Descripcion VARCHAR(250),
    estReg CHAR(1) NOT NULL DEFAULT 'A'
);

CREATE TABLE TR_Tipo_Tratamiento (
    ID_Tratamiento INT PRIMARY KEY,
    Nombre_Tratamiento VARCHAR(50) NOT NULL,
    Descripcion VARCHAR(250),
    estReg CHAR(1) NOT NULL DEFAULT 'A'
);

CREATE TABLE TR_Tipo_Transporte (
    ID_Tipo_Transporte INT PRIMARY KEY,
    Nombre_Transporte VARCHAR(50) NOT NULL,
    Descripcion VARCHAR(250),
    estReg CHAR(1) NOT NULL DEFAULT 'A'
);

CREATE TABLE Residuo_Estandarizado (
    Cod_Estandar INT PRIMARY KEY,
    Nombre_Estandar VARCHAR(100) NOT NULL,
    estReg CHAR(1) NOT NULL DEFAULT 'A'
);

CREATE TABLE Constituyente (
    Cod_Constituyente VARCHAR(20) PRIMARY KEY,
    Nombre_Constituyente VARCHAR(100) NOT NULL,
    Otros_Datos TEXT,
    estReg CHAR(1) NOT NULL DEFAULT 'A'
);

CREATE TABLE Region (
    ID_Region INT PRIMARY KEY,
    Nombre_Region VARCHAR(50) NOT NULL,
    estReg CHAR(1) NOT NULL DEFAULT 'A'
);

-- 2 TABLAS MAESTRAS

CREATE TABLE Empresa_Productora (
    NIF_Empresa VARCHAR(20) PRIMARY KEY,
    Nombre_Empresa VARCHAR(100) NOT NULL,
    Ciudad_Empresa VARCHAR(50) NOT NULL,
    Actividad VARCHAR(100) NOT NULL,
    Otros_Datos TEXT,
    estReg CHAR(1) NOT NULL DEFAULT 'A'
);

CREATE TABLE Empresa_Transportista (
    NIF_Transportista VARCHAR(20) PRIMARY KEY,
    Nombre_Transportista VARCHAR(100) NOT NULL,
    Ciudad_Transportista VARCHAR(50) NOT NULL,
    Otros_Datos TEXT,
    estReg CHAR(1) NOT NULL DEFAULT 'A'
);

CREATE TABLE Destino (
    Cod_Destino VARCHAR(20) PRIMARY KEY,
    ID_Region INT NOT NULL,
    Nombre_Destino VARCHAR(100) NOT NULL,
    Ciudad_Destino VARCHAR(50) NOT NULL,
    Capacidad_Maxima DECIMAL(12,2) NOT NULL,
    Capacidad_Actual DECIMAL(12,2) NOT NULL,
    Otros_Datos TEXT,
    estReg CHAR(1) NOT NULL DEFAULT 'A',
    FOREIGN KEY (ID_Region) REFERENCES Region(ID_Region)
);

CREATE TABLE Residuo (
    Cod_Residuo VARCHAR(20) PRIMARY KEY,
    NIF_Empresa VARCHAR(20) NOT NULL,
    Cod_Estandar INT NOT NULL,
    ID_Toxicidad INT NOT NULL,
    Cantidad_Total DECIMAL(12,2) NOT NULL,
    Otros_Datos TEXT,
    estReg CHAR(1) NOT NULL DEFAULT 'A',
    FOREIGN KEY (NIF_Empresa) REFERENCES Empresa_Productora(NIF_Empresa),
    FOREIGN KEY (Cod_Estandar) REFERENCES Residuo_Estandarizado(Cod_Estandar),
    FOREIGN KEY (ID_Toxicidad) REFERENCES TR_Nivel_Toxicidad(ID_Toxicidad)
);

-- 3. TABLAS ASOCIATIVAS Y TRANSACCIONALES

CREATE TABLE Residuo_Constituyente (
    Cod_Residuo VARCHAR(20),
    Cod_Constituyente VARCHAR(20),
    Cantidad DECIMAL(10,2) NOT NULL,
    estReg CHAR(1) NOT NULL DEFAULT 'A',
    PRIMARY KEY (Cod_Residuo, Cod_Constituyente),
    FOREIGN KEY (Cod_Residuo) REFERENCES Residuo(Cod_Residuo),
    FOREIGN KEY (Cod_Constituyente) REFERENCES Constituyente(Cod_Constituyente)
);

CREATE TABLE Traslado (
    ID_Traslado INT PRIMARY KEY,
    Cod_Residuo VARCHAR(20) NOT NULL,
    Cod_Destino VARCHAR(20) NOT NULL,
    Fecha_Envio DATE NOT NULL,
    Cantidad_Trasladada DECIMAL(12,2) NOT NULL,
    ID_Envase INT NOT NULL,
    ID_Tratamiento INT NOT NULL,
    Fecha_Llegada DATE,
    Otros_Datos TEXT,
    estReg CHAR(1) NOT NULL DEFAULT 'A',
    FOREIGN KEY (Cod_Residuo) REFERENCES Residuo(Cod_Residuo),
    FOREIGN KEY (Cod_Destino) REFERENCES Destino(Cod_Destino),
    FOREIGN KEY (ID_Envase) REFERENCES TR_Tipo_Envase(ID_Envase),
    FOREIGN KEY (ID_Tratamiento) REFERENCES TR_Tipo_Tratamiento(ID_Tratamiento),
    CONSTRAINT UQ_Traslado_Fecha UNIQUE (Cod_Residuo, Fecha_Envio, Cod_Destino)
);

CREATE TABLE Traslado_Transportista (
    ID_Traslado INT,
    NIF_Transportista VARCHAR(20),
    ID_Tipo_Transporte INT NOT NULL,
    Kms_Recorridos DECIMAL(8,2) NOT NULL,
    Costo DECIMAL(10,2) NOT NULL,
    estReg CHAR(1) NOT NULL DEFAULT 'A',
    PRIMARY KEY (ID_Traslado, NIF_Transportista),
    FOREIGN KEY (ID_Traslado) REFERENCES Traslado(ID_Traslado),
    FOREIGN KEY (NIF_Transportista) REFERENCES Empresa_Transportista(NIF_Transportista),
    FOREIGN KEY (ID_Tipo_Transporte) REFERENCES TR_Tipo_Transporte(ID_Tipo_Transporte)
);

-- INSERCION DE DATOS DE PRUEBA

INSERT INTO TR_Nivel_Toxicidad VALUES
(1, 'Bajo', 'Residuo de baja peligrosidad', 'A'),
(2, 'Medio', 'Residuo de peligrosidad media', 'A'),
(3, 'Alto', 'Residuo altamente peligroso', 'A');

INSERT INTO TR_Tipo_Envase VALUES
(1, 'Tambor Metálico', 'Tambor de acero de 200L', 'A'),
(2, 'Contenedor Plástico', 'Contenedor HDPE', 'A'),
(3, 'Big Bag', 'Bolsa industrial', 'A');

INSERT INTO TR_Tipo_Tratamiento VALUES
(1, 'Reciclaje', 'Proceso de reciclaje', 'A'),
(2, 'Incineración', 'Destrucción térmica', 'A'),
(3, 'Neutralización', 'Tratamiento químico', 'A');

INSERT INTO TR_Tipo_Transporte VALUES
(1, 'Camión', 'Transporte terrestre', 'A'),
(2, 'Ferrocarril', 'Transporte ferroviario', 'A'),
(3, 'Barco', 'Transporte marítimo', 'A');

INSERT INTO Residuo_Estandarizado VALUES
(101, 'Aceite Usado', 'A'),
(102, 'Baterías de Plomo', 'A'),
(103, 'Solventes Orgánicos', 'A');

INSERT INTO Constituyente VALUES
('PB', 'Plomo', 'Metal pesado', 'A'),
('HG', 'Mercurio', 'Metal pesado', 'A'),
('BEN', 'Benceno', 'Compuesto orgánico', 'A'),
('ACE', 'Acetona', 'Solvente', 'A');

INSERT INTO Region VALUES
(1, 'Lima', 'A'),
(2, 'Arequipa', 'A'),
(3, 'La Libertad', 'A');


INSERT INTO Empresa_Productora VALUES
('20111111111', 'Química Andina SAC', 'Lima', 'Industria Química', NULL, 'A'),
('20222222222', 'Metalúrgica Sur SAC', 'Arequipa', 'Metalurgia', NULL, 'A');

INSERT INTO Empresa_Transportista VALUES
('20666666661', 'Transportes Eco SAC', 'Lima', NULL, 'A'),
('20666666662', 'Logística Ambiental SAC', 'Arequipa', NULL, 'A');

INSERT INTO Destino VALUES
('DST001', 1, 'Planta Lima Norte', 'Lima', 10000.00, 4200.00, NULL, 'A'),
('DST002', 2, 'Centro Ambiental Sur', 'Arequipa', 15000.00, 8700.00, NULL, 'A');

INSERT INTO Residuo VALUES
('RES001', '20111111111', 101, 2, 1200.00, 'Aceite de maquinaria', 'A'),
('RES002', '20222222222', 102, 3, 450.00, 'Baterías industriales', 'A'),
('RES003', '20111111111', 103, 3, 800.00, 'Solventes contaminados', 'A');


INSERT INTO Residuo_Constituyente VALUES
('RES001', 'ACE', 900.00, 'A'),
('RES002', 'PB', 350.00, 'A'),
('RES002', 'HG', 15.00, 'A'),
('RES003', 'BEN', 500.00, 'A'),
('RES003', 'ACE', 250.00, 'A');



INSERT INTO Traslado VALUES
(1, 'RES001', 'DST001', '2026-07-01', 600.00, 1, 1, '2026-07-02', NULL, 'A'),
(2, 'RES002', 'DST002', '2026-07-03', 450.00, 2, 2, '2026-07-05', NULL, 'A'),
(3, 'RES003', 'DST001', '2026-07-04', 300.00, 3, 3, '2026-07-06', NULL, 'A');

INSERT INTO Traslado_Transportista VALUES
(1, '20666666661', 1, 35.5, 450.00, 'A'),
(2, '20666666662', 1, 980.0, 3500.00, 'A'),
(3, '20666666661', 2, 120.0, 1200.00, 'A');