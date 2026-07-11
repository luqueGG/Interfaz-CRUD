
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