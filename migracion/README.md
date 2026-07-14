# Sistema de Gestión de Residuos Sólidos (ressol)
**Migración de Arquitectura (MySQL a PostgreSQL) y Componentes Analíticos Avanzados**

Este repositorio documenta el proceso de migración estructural y de datos del sistema **ressol**, así como la implementación de lógicas de negocio avanzadas en el motor de destino. 

Debido a restricciones de compatibilidad arquitectónica y la depreciación del plugin de cifrado `mysql_native_password` en MySQL 9.x, la migración directa mediante binarios nativos no era viable. Se diseñó un pipeline de migración orquestado con **Docker** y **pgloader**, utilizando un servidor MySQL 8 efímero como puente.

---

## Tecnologías Utilizadas
* **SGBD Origen:** MySQL 9.6.0 (ARM64)
* **SGBD Destino:** PostgreSQL (Postgres.app)
* **Orquestación:** Docker Desktop
* **Herramienta de Migración:** `pgloader` (vía imagen oficial de Linux)
* **Administración SGBD:** DBeaver 

---

## Pipeline de Migración 

### 1. Extracción del Respaldo Local (MySQL Origen)
```bash
/usr/local/mysql/bin/mysqldump -u root -p ressol > ressol_export.sql
```
### 2. Despliegue del Contenedor Puente (MySQL 8)
```bash
docker run --name mysql_puente -e MYSQL_ROOT_PASSWORD=migracion123 -e MYSQL_DATABASE=ressol -p 3307:3306 -d mysql:8.0 --default-authentication-plugin=mysql_native_password
```
### 3. Inyección de Datos al Puente
```bash
docker exec -i mysql_puente mysql -uroot -pmigracion123 ressol < ressol_export.sql
```
### 4. Ejecución Automatizada de la Migración(se cambia las credenciales del archivo ressol.load a las de sus BD respectivamente)
```bash
docker run --rm -v $(pwd):/app dimitri/pgloader:latest pgloader /app/ressol.load
```
### 5. Limpieza del Entorno Virtual
```bash
docker rm -f mysql_puente
```
