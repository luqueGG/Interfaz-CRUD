# MANTENIMIENTO RESIDUOS SOLIDOS

## Laboratorio A Base de Datos - Subgrupo 3

## Integrantes

- Anco Aymara Jean Pierre
- Cuyo Ccapa Jhon Deyvis
- Luque Guevara Fernando Gerson

## Stack Tecnologico
- PostgreSQL 16
- Java 21
- JavaFX v21.0.3
- Jackson v2.17.1
- Apache Maven v3.9.x
- Spring Boot v3.3.0
- Docker 29.5.3

## Lo minimo que debes tener
- Docker instalado
- JDK java
- Maven
El resto se carga en los contenedores y/o cargados en Maven de 
forma automatica

## Features

## Instrucciones

Primero se levantan los contenedores (para el backend y la base de datos)
```
docker compose up --build
```

Luego en otra terminal, se compila y ejecuta la seccion gui
```
mvn:javafx:run
```
