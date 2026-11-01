# Simulador de Migración de Planes de Estudio

Backend REST para simular la migración de un estudiante desde su plan de estudios actual hacia otro plan, calculando qué materias se reconocen, cuáles quedan pendientes y cómo avanza el título intermedio.

> **Configuración de este entorno:** la base se llama `migracion_qa` y PostgreSQL está publicado en el puerto `5433`. Si usás el `.env.example` por defecto, la base se llama `simulador` y el puerto es `5432`.

---

## Tabla de contenidos

- [Stack tecnológico](#stack-tecnológico)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Requisitos](#requisitos)
- [Primera vez: levantar el proyecto](#primera-vez-levantar-el-proyecto)
- [Usuarios que ya lo iniciaron: volver a usarlo](#usuarios-que-ya-lo-iniciaron-volver-a-usarlo)
- [Desarrollo local sin Docker para el backend](#desarrollo-local-sin-docker-para-el-backend)
- [Datos demo](#datos-demo)
- [Tests](#tests)
- [API REST](#api-rest)
- [Manejo de errores](#manejo-de-errores)
- [Base de datos](#base-de-datos)
- [Problemas comunes](#problemas-comunes)
- [Limitaciones conocidas](#limitaciones-conocidas)
- [Buenas prácticas para contribuir](#buenas-prácticas-para-contribuir)

---

## Stack tecnológico

- Java 21
- Spring Boot 4.1.2
- Spring Web MVC
- Spring Data JPA / Hibernate
- PostgreSQL 16
- Flyway
- Spring Security
- Spring Boot Actuator
- springdoc-openapi 3.2.0 (Swagger UI)
- Lombok
- Maven Wrapper
- Testcontainers
- Docker / Docker Compose

---

## Estructura del proyecto

```text
simulador/
├── src/main/java/ar/edu/uade/simulador/
│   ├── api/            # Controllers REST
│   ├── servicio/       # Lógica de aplicación y mappers
│   ├── integracion/    # Implementación local de la fuente académica
│   ├── motor/          # Motor de simulación y reglas de equivalencia
│   ├── dominio/        # Entidades JPA y enums
│   ├── repositorio/    # Repositorios Spring Data
│   ├── dto/            # Records de request/response
│   ├── errores/        # Excepciones y manejo global de errores
│   └── seguridad/      # Configuración de Spring Security
├── src/main/resources/
│   ├── application.yaml
│   └── db/migration/
│       ├── V1__esquema_inicial.sql
│       └── V2__datos_demo.sql
├── src/test/java/...
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── mvnw
└── .env.example
```

---

## Requisitos

- JDK 21
- Docker
- Docker Compose
- Git

Verificar:

```bash
java -version
docker --version
docker compose version
```

No es necesario tener Maven instalado: el proyecto incluye `mvnw`.

---

## Primera vez: levantar el proyecto

Todos los comandos se ejecutan desde la raíz del proyecto, donde están `pom.xml` y `docker-compose.yml`.

### 1. Clonar el repositorio

```bash
git clone <URL_DEL_REPO>
cd simulador
```

### 2. Crear el archivo `.env`

```bash
cp .env.example .env
```

Contenido esperado:

```env
DB_NAME=simulador
DB_USER=simulador
DB_PASSWORD=cambiar_esta_clave
DB_HOST_PORT=5432
APP_PORT=8080
JWT_SECRET=cambiar_por_una_clave_larga_de_al_menos_32_caracteres
```

En el entorno actual del proyecto, el `.env` está configurado así:

```env
DB_NAME=migracion_qa
DB_HOST_PORT=5433
APP_PORT=8080
```

Variables:

| Variable | Descripción | Default |
|---|---|---|
| `DB_NAME` | Nombre de la base de datos | `simulador` |
| `DB_USER` | Usuario de PostgreSQL | `simulador` |
| `DB_PASSWORD` | Password de PostgreSQL | — |
| `DB_HOST_PORT` | Puerto de PostgreSQL en el host | `5432` |
| `DB_PORT` | Puerto que usa el backend local para conectarse | `5432` |
| `APP_PORT` | Puerto del backend en el host | `8080` |
| `JWT_SECRET` | Reservado para autenticación JWT | — |

> `DB_PORT` no está en `.env.example`. Solo hace falta para correr el backend localmente cuando `DB_HOST_PORT` es distinto de `5432`. En este entorno, `DB_HOST_PORT=5433`, así que para correr local hay que usar `DB_PORT=5433`.

> `JWT_SECRET` todavía no se usa en el código, pero conviene definirlo.

> `.env` está ignorado por Git. No lo commitees.

### 3. Levantar base y backend

```bash
docker compose up -d --build
```

Esto:

1. Levanta PostgreSQL 16.
2. Construye la imagen del backend.
3. Ejecuta Flyway.
4. Aplica `V1__esquema_inicial.sql` y `V2__datos_demo.sql`.
5. Arranca Spring Boot en `http://localhost:8080`.

La primera vez puede tardar varios minutos porque descarga dependencias de Maven.

### 4. Verificar que arrancó

```bash
docker compose ps
docker compose logs -f backend
```

Esperar:

```text
Started SimuladorApplication
```

### 5. Health check

```bash
curl http://localhost:8080/actuator/health
```

Respuesta esperada:

```json
{"groups":["liveness","readiness"],"status":"UP"}
```

### 6. Probar un endpoint

```bash
curl -H "X-Legajo: 1100001" \
  http://localhost:8080/api/v1/estudiantes/situacion
```

Si devuelve datos del legajo `1100001`, el proyecto está funcionando.

---

## Usuarios que ya lo iniciaron: volver a usarlo

Si ya levantaste el proyecto antes, no hace falta volver a crear `.env` ni reconstruir la imagen, salvo que hayas cambiado código.

### Ver si los contenedores están corriendo

```bash
docker compose ps
```

### Levantarlos si están detenidos

```bash
docker compose up -d
```

### Ver logs

```bash
docker compose logs -f backend
```

### Verificar salud

```bash
curl http://localhost:8080/actuator/health
```

### Si cambiaste código Java o recursos

```bash
docker compose up -d --build backend
```

`docker compose restart backend` **no recompila**. Si tocaste código, usá `--build`.

### Detener sin borrar datos

```bash
docker compose stop
```

### Volver a iniciar

```bash
docker compose start
```

### Bajar todo

```bash
docker compose down
```

La base de datos se conserva en el volumen `db_data`.

### Borrar también la base de datos

```bash
docker compose down -v
docker compose up -d --build
```

> `down -v` elimina el volumen de PostgreSQL y todos los datos. Al volver a levantar, Flyway vuelve a ejecutar `V1` y `V2`, por lo que reaparecen los datos demo.

### Importante sobre los datos

Flyway registra las migraciones aplicadas en `flyway_schema_history`. La `V2__datos_demo.sql` se ejecuta **una sola vez**. Reiniciar el backend no duplica datos.

Si querés volver a cargar los datos demo, usá `docker compose down -v`.

---

## Desarrollo local sin Docker para el backend

Podés dejar PostgreSQL en Docker y correr Spring Boot localmente para desarrollar con debug o hot reload.

### 1. Levantar solo la base

```bash
docker compose up -d db
```

### 2. Liberar el puerto 8080

Si el backend en Docker está corriendo:

```bash
docker compose stop backend
```

### 3. Configurar el puerto de la base

En este entorno, PostgreSQL está publicado en `5433`. El backend local necesita saberlo.

Opción A: agregar a `.env`:

```env
DB_PORT=5433
```

Opción B: pasar la variable al comando:

```bash
DB_PORT=5433 ./mvnw spring-boot:run
```

### 4. Levantar el backend

```bash
./mvnw spring-boot:run
```

### 5. Verificar

```bash
curl http://localhost:8080/actuator/health
```

### 6. Volver al backend en Docker

Cortar el proceso local con `Ctrl+C` y después:

```bash
docker compose start backend
```

### Desde IntelliJ IDEA

- Abrir la carpeta `simulador/` como proyecto Maven.
- Configurar JDK 21.
- Working directory: la raíz `simulador/`.
- Agregar `DB_PORT=5433` como variable de entorno.
- Verificar que Lombok/annotation processing esté habilitado.

---

## Datos demo

La migración `V2__datos_demo.sql` carga automáticamente:

| Dato | Valor |
|---|---|
| Legajo de prueba | `1100001` |
| Plan actual | `1121` — Tecnicatura Universitaria en Desarrollo de Software |
| Plan destino | `1621` — Ingeniería en Informática |
| Equivalencia | `Res. 123/21`: `3.4.225` → `3.4.069` |

Datos cargados:

- 1 facultad.
- 2 carreras.
- 2 planes.
- 9 materias.
- 14 registros de `plan_materia`.
- 3 correlatividades.
- 1 equivalencia.
- 1 estudiante.
- 7 registros de historial.
- 3 cursadas.

### Verificar las migraciones

```bash
docker compose exec db sh -c 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "select version, description, success from flyway_schema_history order by installed_rank;"'
```

### Entrar a PostgreSQL

```bash
docker compose exec db sh -c 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB"'
```

Consultas útiles:

```sql
\dt

select * from estudiante;

select m.codigo, h.estado_global, h.nota_final
from historial_academico h
join materia m on m.id_materia = h.id_materia
join estudiante e on e.id_estudiante = h.id_estudiante
where e.legajo = '1100001'
order by m.codigo;

select version, description, success from flyway_schema_history;
```

### ¿Por qué aparecen datos si no inserté nada?

Porque Flyway ejecuta `V2__datos_demo.sql` automáticamente al arrancar el backend. La base del contenedor se crea vacía, pero el backend la llena con el esquema (`V1`) y los datos demo (`V2`).

### Si querés una base sin datos demo

Opción rápida, conservando el esquema:

```sql
TRUNCATE TABLE
    detalle_cursada,
    historial_academico,
    estudiante_plan,
    estudiante,
    equivalencia_materia,
    equivalencia,
    correlatividad,
    plan_materia,
    materia,
    plan_estudio,
    carrera,
    facultad
RESTART IDENTITY CASCADE;
```

Como Flyway ya registró la `V2`, no vuelve a insertar los datos al reiniciar.

Opción limpia: mover `V2__datos_demo.sql` a una ubicación de demo y activarla solo con un perfil. No edites una migración ya aplicada, porque Flyway detecta un checksum distinto y falla.

---

## Tests

### Tests unitarios del motor, sin Docker

```bash
./mvnw -Dtest=MotorSimulacionTest test
```

### Tests de API, requieren Docker

```bash
./mvnw -Dtest=ApiEndpointsTest test
```

Testcontainers levanta su propio PostgreSQL; no usa el contenedor de `docker-compose`.

### Todos los tests

```bash
./mvnw test
```

Son 17 tests:

- 11 de API.
- 5 del motor.
- 1 de carga de contexto.

Los reportes quedan en:

```text
target/surefire-reports/
```

### Compilar sin tests

```bash
./mvnw clean package -DskipTests
```

---

## API REST

Base URL:

```text
http://localhost:8080
```

Si cambiás `APP_PORT`, ajustá el puerto.

### Headers

Los endpoints de estudiante y simulación requieren:

```http
X-Legajo: 1100001
```

> `X-Legajo` es una identificación temporal para desarrollo. No es autenticación real.

### Documentación interactiva (Swagger UI)

Con el backend levantado en `localhost:8080`:

- UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- OpenAPI YAML: http://localhost:8080/v3/api-docs.yaml

En la UI desplegá cada endpoint y usá **Try it out**. Para los que requieren identidad,
completá el header `X-Legajo` (por ejemplo `1100001`).

> Solo para desarrollo local. En producción conviene apagarlo con
> `springdoc.api-docs.enabled=false` y `springdoc.swagger-ui.enabled=false`.

### Resumen de endpoints

| Método | Endpoint | Header `X-Legajo` | Descripción |
|---|---|---|---|
| `GET` | `/actuator/health` | No | Estado del backend |
| `GET` | `/api/v1/planes` | Sí | Lista planes destino activos para el estudiante |
| `GET` | `/api/v1/planes/{codigo}` | No | Detalle de un plan con materias y correlatividades |
| `GET` | `/api/v1/estudiantes/situacion` | Sí | Situación académica actual del estudiante |
| `GET` | `/api/v1/estudiantes/historial` | Sí | Historial académico con cursadas y recursadas |
| `POST` | `/api/v1/simulaciones` | Sí | Simula la migración a otro plan |
| `GET` | `/api/v1/equivalencias` | No | Lista equivalencias, con filtros opcionales |

---

### `GET /actuator/health`

Devuelve el estado del backend.

```bash
curl http://localhost:8080/actuator/health
```

Respuesta:

```json
{
  "groups": ["liveness", "readiness"],
  "status": "UP"
}
```

---

### `GET /api/v1/planes`

Lista los planes de estudio activos que pueden usarse como destino, excluyendo el plan actual del estudiante.

**Header requerido:** `X-Legajo`

```bash
curl -H "X-Legajo: 1100001" \
  http://localhost:8080/api/v1/planes
```

Respuesta:

```json
[
  {
    "codigoPlan": "1621",
    "carrera": "Ingeniería en Informática",
    "estado": "ACTIVO"
  }
]
```

Campos:

| Campo | Descripción |
|---|---|
| `codigoPlan` | Código del plan |
| `carrera` | Nombre de la carrera |
| `estado` | `ACTIVO` o `INACTIVO` |

Errores:

- `400` si falta `X-Legajo`.
- `404` si el legajo no existe o no tiene plan activo.

---

### `GET /api/v1/planes/{codigo}`

Devuelve el detalle de un plan: materias, año, cuatrimestre, carga horaria, optativas, título intermedio y correlatividades.

**Path param:** `codigo` — código del plan, por ejemplo `1621`.

```bash
curl http://localhost:8080/api/v1/planes/1621
```

Respuesta:

```json
{
  "codigoPlan": "1621",
  "carrera": "Ingeniería en Informática",
  "materias": [
    {
      "codigo": "3.4.069",
      "nombre": "Fundamentos de Informática",
      "anio": 1,
      "cuatrimestre": 1,
      "cargaHoraria": 68,
      "optativa": false,
      "tituloIntermedio": true,
      "correlativas": []
    },
    {
      "codigo": "9.9.003",
      "nombre": "Programación II",
      "anio": 2,
      "cuatrimestre": 1,
      "cargaHoraria": 68,
      "optativa": false,
      "tituloIntermedio": true,
      "correlativas": [
        {
          "codigo": "3.4.069",
          "nombre": "Fundamentos de Informática",
          "tipo": "CURSADA"
        },
        {
          "codigo": "9.9.006",
          "nombre": "Programación I",
          "tipo": "FINAL"
        }
      ]
    }
  ]
}
```

Campos de correlatividad:

| Campo | Descripción |
|---|---|
| `codigo` | Código de la materia requerida |
| `nombre` | Nombre de la materia requerida |
| `tipo` | `CURSADA` o `FINAL` |

Errores:

- `404` si el plan no existe.

---

### `GET /api/v1/estudiantes/situacion`

Devuelve la situación académica del estudiante en su plan activo: estado de cada materia, nota y si tiene cursada registrada.

**Header requerido:** `X-Legajo`

```bash
curl -H "X-Legajo: 1100001" \
  http://localhost:8080/api/v1/estudiantes/situacion
```

Respuesta:

```json
{
  "legajo": "1100001",
  "plan": {
    "codigo": "1121",
    "carrera": "Tecnicatura Universitaria en Desarrollo de Software"
  },
  "materias": [
    {
      "codigo": "3.4.164",
      "nombre": "Sistemas de Información I",
      "anio": 1,
      "cuatrimestre": 1,
      "estado": "APROBADA",
      "nota": 7.00,
      "cursada": true
    },
    {
      "codigo": "9.9.002",
      "nombre": "Base de Datos I",
      "anio": 2,
      "cuatrimestre": 1,
      "estado": "FINAL",
      "nota": null,
      "cursada": true
    }
  ]
}
```

Valores posibles de `estado`:

| Estado | Significado |
|---|---|
| `APROBADA` | Materia aprobada |
| `FINAL` | Cursada aprobada, pendiente de final |
| `CURSANDO` | Actualmente cursando |
| `SIN_CURSAR` | Sin registros académicos |

`cursada` indica si existe un registro en el historial académico.

Errores:

- `400` si falta `X-Legajo`.
- `404` si el legajo no existe o no tiene plan activo.

---

### `GET /api/v1/estudiantes/historial`

Devuelve el historial académico completo, incluyendo cursadas y recursadas.

**Header requerido:** `X-Legajo`

```bash
curl -H "X-Legajo: 1100001" \
  http://localhost:8080/api/v1/estudiantes/historial
```

Respuesta:

```json
[
  {
    "materia": {
      "codigo": "9.9.002",
      "nombre": "Base de Datos I"
    },
    "estado": "FINAL",
    "notaFinal": null,
    "fechaAprobacion": null,
    "cursadas": [
      {
        "anio": 2024,
        "cuatrimestre": 1,
        "condicion": "LIBRE",
        "nota": 3.00
      },
      {
        "anio": 2024,
        "cuatrimestre": 2,
        "condicion": "REGULAR",
        "nota": 6.00
      }
    ]
  }
]
```

Valores posibles de `condicion`:

| Condición | Significado |
|---|---|
| `PROMOCIONADA` | Cursada promocionada |
| `REGULAR` | Cursada regular, pendiente de final |
| `LIBRE` | Cursada libre |

Los resultados se ordenan por código de materia.

Errores:

- `400` si falta `X-Legajo`.
- `404` si el legajo no existe.

---

### `POST /api/v1/simulaciones`

Simula la migración del estudiante desde su plan actual hacia un plan destino.

**Header requerido:** `X-Legajo`

**Body:**

```json
{
  "codigoPlanDestino": "1621"
}
```

```bash
curl -X POST http://localhost:8080/api/v1/simulaciones \
  -H "X-Legajo: 1100001" \
  -H "Content-Type: application/json" \
  -d '{"codigoPlanDestino":"1621"}'
```

Respuesta:

```json
{
  "planOrigen": {
    "codigo": "1121",
    "carrera": "Tecnicatura Universitaria en Desarrollo de Software"
  },
  "planDestino": {
    "codigo": "1621",
    "carrera": "Ingeniería en Informática"
  },
  "items": [
    {
      "codigo": "3.4.069",
      "nombre": "Fundamentos de Informática",
      "anio": 1,
      "cuatrimestre": 1,
      "cargaHoraria": 68,
      "optativa": false,
      "tituloIntermedio": true,
      "estado": "APROBADA",
      "reconocimiento": {
        "tipo": "EQUIVALENCIA",
        "respaldo": "Res. 123/21",
        "materiasOrigen": [
          {
            "codigo": "3.4.225",
            "nombre": "Introducción a la Algoritmia"
          }
        ]
      }
    },
    {
      "codigo": "9.9.001",
      "nombre": "Matemática Discreta",
      "anio": 1,
      "cuatrimestre": 2,
      "cargaHoraria": 102,
      "optativa": false,
      "tituloIntermedio": true,
      "estado": "A_CURSAR",
      "reconocimiento": null
    }
  ],
  "resumen": {
    "aprobadas": 3,
    "pendientesDeFinal": 1,
    "pendientesDeCursado": 3,
    "materiasReconocidas": 5,
    "pendientesPlanActual": 2,
    "pendientesPlanNuevo": 4,
    "tituloIntermedio": {
      "aprobadas": 2,
      "total": 6,
      "avance": 33
    }
  },
  "optativasSinCorrespondencia": [
    {
      "codigo": "9.9.005",
      "nombre": "Seminario de Tendencias"
    }
  ]
}
```

Estados de cada ítem:

| Estado | Significado |
|---|---|
| `APROBADA` | Materia reconocida como aprobada |
| `FINAL` | Materia reconocida con cursada aprobada, pendiente de final |
| `CURSANDO` | Materia reconocida como actualmente cursando |
| `A_CURSAR` | Materia que el estudiante deberá cursar en el plan destino |

Tipos de reconocimiento:

| Tipo | Significado |
|---|---|
| `MISMA_MATERIA` | Misma materia y misma carga horaria |
| `EQUIVALENCIA` | Equivalencia aprobada por resolución |

Campos del resumen:

| Campo | Descripción |
|---|---|
| `aprobadas` | Materias aprobadas en el plan destino |
| `pendientesDeFinal` | Materias con final pendiente |
| `pendientesDeCursado` | Materias cursando o a cursar |
| `materiasReconocidas` | Materias con algún reconocimiento |
| `pendientesPlanActual` | Materias no aprobadas en el plan actual |
| `pendientesPlanNuevo` | Materias no aprobadas en el plan destino |
| `tituloIntermedio.aprobadas` | Materias del título intermedio aprobadas |
| `tituloIntermedio.total` | Total de materias del título intermedio |
| `tituloIntermedio.avance` | Porcentaje de avance del título intermedio |

Reglas de reconocimiento:

1. **Misma materia:** mismo código de materia y misma carga horaria entre planes.
2. **Equivalencia:** debe existir una equivalencia vigente, todas las materias origen deben estar en el historial y la suma de horas origen debe coincidir con la carga horaria destino.
3. Si una materia tiene varios reconocimientos posibles, se elige el mejor estado: `APROBADA` > `FINAL` > `CURSANDO`.
4. Una materia origen solo puede usarse una vez.
5. Las correlatividades se muestran en el detalle del plan, pero **el motor todavía no las usa para bloquear u ordenar materias**.

Errores:

- `400` si falta `X-Legajo`, si el body es inválido o si `codigoPlanDestino` está vacío.
- `404` si el legajo o el plan destino no existen.
- `409` si el plan destino es el mismo que el actual o si no está activo.

---

### `GET /api/v1/equivalencias`

Lista las equivalencias cargadas. Permite filtrar por plan de origen y/o destino.

**Query params opcionales:**

| Parámetro | Descripción |
|---|---|
| `planOrigen` | Código del plan de origen |
| `planDestino` | Código del plan de destino |

Sin filtros:

```bash
curl http://localhost:8080/api/v1/equivalencias
```

Con filtros:

```bash
curl "http://localhost:8080/api/v1/equivalencias?planOrigen=1121&planDestino=1621"
```

Respuesta:

```json
[
  {
    "resolucion": "Res. 123/21",
    "planOrigen": "1121",
    "planDestino": "1621",
    "materiasOrigen": [
      {
        "codigo": "3.4.225",
        "nombre": "Introducción a la Algoritmia"
      }
    ],
    "materiaDestino": {
      "codigo": "3.4.069",
      "nombre": "Fundamentos de Informática"
    },
    "vigencia": {
      "desde": "2021-03-01",
      "hasta": null
    },
    "vigente": true
  }
]
```

Campos:

| Campo | Descripción |
|---|---|
| `resolucion` | Resolución que respalda la equivalencia |
| `planOrigen` | Código del plan de origen |
| `planDestino` | Código del plan de destino |
| `materiasOrigen` | Materias que se reconocen |
| `materiaDestino` | Materia destino |
| `vigencia.desde` | Fecha desde la que rige |
| `vigencia.hasta` | Fecha hasta la que rige; `null` si sigue vigente |
| `vigente` | Si está vigente a la fecha actual |

Si no hay coincidencias, devuelve `[]`.

---

## Manejo de errores

Formato general:

```json
{
  "timestamp": "2026-10-31T20:00:00Z",
  "status": 404,
  "error": "Not Found",
  "mensaje": "No existe el plan 9999",
  "ruta": "/api/v1/planes/9999"
}
```

Códigos:

| Código | Cuándo |
|---|---|
| `400` | Falta `X-Legajo`, JSON malformado, body inválido o tipo de parámetro inválido |
| `404` | No existe el estudiante, el plan o el recurso |
| `409` | Migración no permitida: mismo plan o plan destino inactivo |
| `415` | `Content-Type` no soportado |
| `500` | Error inesperado |

Ejemplos:

```bash
# Falta X-Legajo -> 400
curl -i http://localhost:8080/api/v1/estudiantes/situacion

# Legajo inexistente -> 404
curl -i -H "X-Legajo: 9999999" \
  http://localhost:8080/api/v1/planes

# Plan inexistente -> 404
curl -i http://localhost:8080/api/v1/planes/9999

# Mismo plan actual -> 409
curl -i -X POST http://localhost:8080/api/v1/simulaciones \
  -H "X-Legajo: 1100001" \
  -H "Content-Type: application/json" \
  -d '{"codigoPlanDestino":"1121"}'

# Body inválido -> 400
curl -i -X POST http://localhost:8080/api/v1/simulaciones \
  -H "X-Legajo: 1100001" \
  -H "Content-Type: application/json" \
  -d '{"codigoPlanDestino":""}'
```

---

## Base de datos

Tablas principales:

| Tabla | Descripción |
|---|---|
| `facultad` | Facultades |
| `carrera` | Carreras |
| `plan_estudio` | Planes de estudio |
| `materia` | Materias |
| `plan_materia` | Materias por plan, con año, cuatrimestre y carga horaria |
| `correlatividad` | Requisitos entre materias |
| `equivalencia` | Resoluciones de equivalencia entre planes |
| `equivalencia_materia` | Materias origen/destino de cada equivalencia |
| `estudiante` | Estudiantes |
| `estudiante_plan` | Inscripción del estudiante a un plan |
| `historial_academico` | Estado global por materia |
| `detalle_cursada` | Cursadas y recursadas |

Migraciones:

```text
src/main/resources/db/migration/
├── V1__esquema_inicial.sql
└── V2__datos_demo.sql
```

> No edites una migración ya aplicada. Si necesitás cambiar datos o esquema, creá una migración nueva (`V3__...sql`). Si editás una migración existente, Flyway puede fallar por checksum.

---

## Problemas comunes

### El backend no toma mis cambios

```bash
docker compose up -d --build backend
```

`restart` no recompila.

### `port is already allocated`

Otro proceso está usando `8080` o el puerto de PostgreSQL.

```bash
docker compose ps
```

Podés cambiar en `.env`:

```env
APP_PORT=8081
DB_HOST_PORT=5434
```

Si además corrés el backend local, agregá `DB_PORT` con el mismo valor que `DB_HOST_PORT`.

### `password authentication failed`

Puede haber un volumen viejo con otras credenciales:

```bash
docker compose down -v
docker compose up -d --build
```

> `down -v` borra los datos.

### `relation ... does not exist`

Flyway no aplicó las migraciones. Revisar:

```bash
docker compose logs backend | grep -i flyway
```

Y verificar:

```sql
select version, description, success from flyway_schema_history;
```

### `Migration checksum mismatch`

Editaste una migración ya aplicada. Soluciones:

1. Crear una migración nueva.
2. Resetear la base con `docker compose down -v`.

No se recomienda editar el checksum a mano.

### Aparece `Using generated security password`

Es normal. Spring Security autoconfigura un usuario en memoria porque todavía no hay autenticación real. No afecta a los endpoints porque `SecurityConfig` permite todo. Es una deuda pendiente.

### El backend local se conecta a la base equivocada

En este entorno, PostgreSQL está publicado en `5433`. El backend local necesita:

```env
DB_PORT=5433
```

o:

```bash
DB_PORT=5433 ./mvnw spring-boot:run
```

### El backend no encuentra `.env`

Ejecutá los comandos desde la raíz `simulador/`, donde está `pom.xml`.

### `./mvnw: Permission denied`

```bash
chmod +x mvnw
```

### Los tests de API fallan por Docker

Verificar:

```bash
docker info
```

Los tests de API usan Testcontainers y necesitan Docker corriendo. Los tests del motor no.

---

## Limitaciones conocidas

- **Sin autenticación real:** `SecurityConfig` permite todas las requests y la identidad se toma de `X-Legajo`, que es falsificable. No exponer a Internet sin implementar JWT/OAuth2.
- **Swagger expuesto sin autenticación:** `/swagger-ui.html` y `/v3/api-docs` están abiertos. Apagarlos fuera de desarrollo.
- **Correlatividades no usadas por el motor:** se muestran en el detalle del plan, pero no bloquean ni ordenan la simulación.
- **Datos demo en migraciones principales:** `V2__datos_demo.sql` se ejecuta en cualquier entorno. Conviene separarlo por perfil.
- **Asignación greedy:** el motor recorre las materias destino en orden y asigna la primera mejor candidata; no hace una optimización global.
- **Sin paginación:** los listados devuelven todos los registros.
- **Un solo plan activo por estudiante:** la aplicación lo asume, pero la base no lo restringe.
- **Sin CI:** no hay pipeline de tests/build.

---

## Buenas prácticas para contribuir

1. Trabajar sobre la rama `develop`.
2. Antes de commitear:

```bash
./mvnw test
```

3. No editar migraciones ya aplicadas. Crear una nueva versión (`V3__...sql`).
4. Mantener el estilo existente: paquetes por capa, DTOs como `record`, inyección por constructor.
5. Actualizar este README si se agregan endpoints, variables de entorno o migraciones.
6. No commitear `.env` ni credenciales.

---

## Checklist rápido

- [ ] `.env` creado.
- [ ] `docker compose up -d --build` ejecutado.
- [ ] `docker compose ps` muestra base healthy y backend up.
- [ ] `/actuator/health` devuelve `UP`.
- [ ] `GET /api/v1/planes` con `X-Legajo: 1100001` devuelve `1621`.
- [ ] `GET /api/v1/estudiantes/situacion` devuelve plan `1121`.
- [ ] `POST /api/v1/simulaciones` devuelve 7 ítems y resumen con 3 aprobadas.
- [ ] `GET /api/v1/equivalencias` devuelve `Res. 123/21`.
- [ ] `./mvnw test` pasa los 17 tests.
- [ ] Si corrés local, usás `DB_PORT=5433` cuando corresponde.
