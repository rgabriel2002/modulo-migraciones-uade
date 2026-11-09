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
- [Seguridad](#seguridad)
- [Demo desde el celular](#demo-desde-el-celular)
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
- springdoc-openapi 3.2.0 (Swagger UI, deshabilitado por defecto)
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

Para volver a cargar los datos demo, usá `docker compose down -v`.

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
| Plan activo | `1121` — Tecnicatura Universitaria en Desarrollo de Software |
| Plan destino | `1621` — Ingeniería en Informática |
| Legajo multi-carrera | `1100002` — `1621` + `5010` (Contador Público) activos |
| Plan nuevo (actualización) | `5015` — Contador Público resolución 2026 |
| Equivalencias | `Res. 123/21`: `3.4.225` → `3.4.069` · `Res. 456/22`: `3.4.164` → `1.1.144` |

Datos cargados (planes reales de UADE):

- 2 facultades.
- 6 carreras.
- 7 planes (incluye `5015`, plan 2026 de Contador para demo de actualización).
- 211 materias.
- 279 registros de `plan_materia`.
- 186 correlatividades.
- 2 equivalencias.
- 2 estudiantes.
- 15 registros de historial.
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

### Base sin datos demo

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

La UI y el JSON de OpenAPI están **deshabilitados por defecto** (no se exponen
en producción). Para desarrollo local, levantá el backend con:

```bash
SPRINGDOC_API_DOCS_ENABLED=true SPRINGDOC_SWAGGER_UI_ENABLED=true ./mvnw spring-boot:run
```

(o agregá esas variables al contenedor). Con eso habilitado:

- UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

En la UI desplegá cada endpoint y usá **Try it out**. Para los que requieren identidad,
completá el header `X-Legajo` (por ejemplo `1100001`).

### Resumen de endpoints

| Método | Endpoint | Header `X-Legajo` | Descripción |
|---|---|---|---|
| `GET` | `/actuator/health` | No | Estado del backend |
| `GET` | `/api/v1/planes?excluir={codigo}` | Sí | Planes destino activos, excluyendo el origen elegido |
| `GET` | `/api/v1/planes/{codigo}` | No | Detalle de un plan con materias y correlatividades |
| `GET` | `/api/v1/estudiantes/situacion` | Sí | Planes activos del estudiante (multi-carrera) |
| `GET` | `/api/v1/estudiantes/historial` | Sí | Historial académico con cursadas y recursadas |
| `POST` | `/api/v1/simulaciones` | Sí | Simula la migración a otro plan |
| `POST` | `/api/v1/reportes/simulacion` | No | Genera el PDF del reporte desde una respuesta de simulación |
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

Lista los planes de estudio activos que pueden usarse como destino. El
parámetro opcional `excluir` quita el plan elegido como origen (con un legajo
multi-carrera se excluye solo ese, no los demás planes del alumno).

**Header requerido:** `X-Legajo` (valida que el estudiante exista)

```bash
curl -H "X-Legajo: 1100001" \
  "http://localhost:8080/api/v1/planes?excluir=1121"
```

Respuesta:

```json
[
  {
    "codigoPlan": "1621",
    "carrera": "Ingeniería en Informática",
    "facultad": "Facultad de Ingeniería y Ciencias Exactas",
    "estado": "ACTIVO"
  },
  {
    "codigoPlan": "5010",
    "carrera": "Contador Público",
    "facultad": "Facultad de Ciencias Económicas",
    "estado": "ACTIVO"
  }
]
```

Campos:

| Campo | Descripción |
|---|---|
| `codigoPlan` | Código del plan |
| `carrera` | Nombre de la carrera |
| `facultad` | Facultad a la que pertenece la carrera |
| `estado` | `ACTIVO` o `INACTIVO` |

Errores:

- `400` si falta `X-Legajo`.
- `404` si el legajo no existe.

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
      "codigo": "3.4.071",
      "nombre": "Programación I",
      "anio": 1,
      "cuatrimestre": 2,
      "cargaHoraria": 68,
      "optativa": false,
      "tituloIntermedio": true,
      "correlativas": [
        {
          "codigo": "3.4.069",
          "nombre": "Fundamentos de Informática",
          "tipo": "CURSADA"
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

Devuelve los planes activos del estudiante. Un legajo puede estar inscripto en
más de una carrera a la vez (multi-carrera): la lista completa es la que el
front usa para ofrecer el selector de origen.

**Header requerido:** `X-Legajo`

```bash
curl -H "X-Legajo: 1100002" \
  http://localhost:8080/api/v1/estudiantes/situacion
```

Respuesta:

```json
{
  "legajo": "1100002",
  "planesActivos": [
    {
      "codigo": "1621",
      "carrera": "Ingeniería en Informática",
      "facultad": "Facultad de Ingeniería y Ciencias Exactas"
    },
    {
      "codigo": "5010",
      "carrera": "Contador Público",
      "facultad": "Facultad de Ciencias Económicas"
    }
  ]
}
```

Errores:

- `400` si falta `X-Legajo`.
- `404` si el legajo no existe.

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
      "codigo": "3.4.075",
      "nombre": "Sistemas Operativos"
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

Simula la migración del estudiante desde un plan de origen (que debe estar
activo para ese legajo) hacia un plan destino.

**Header requerido:** `X-Legajo`

**Body:**

```json
{
  "codigoPlanOrigen": "1121",
  "codigoPlanDestino": "1621"
}
```

```bash
curl -X POST http://localhost:8080/api/v1/simulaciones \
  -H "X-Legajo: 1100001" \
  -H "Content-Type: application/json" \
  -d '{"codigoPlanOrigen":"1121","codigoPlanDestino":"1621"}'
```

Respuesta:

```json
{
  "planOrigen": {
    "codigo": "1121",
    "carrera": "Tecnicatura Universitaria en Desarrollo de Software",
    "facultad": "Facultad de Ingeniería y Ciencias Exactas"
  },
  "planDestino": {
    "codigo": "1621",
    "carrera": "Ingeniería en Informática",
    "facultad": "Facultad de Ingeniería y Ciencias Exactas"
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
      "codigo": "3.4.072",
      "nombre": "Arquitectura de Computadores",
      "anio": 1,
      "cuatrimestre": 2,
      "cargaHoraria": 68,
      "optativa": false,
      "tituloIntermedio": true,
      "estado": "APROBADA",
      "reconocimiento": {
        "tipo": "MISMA_MATERIA",
        "respaldo": "Misma materia",
        "materiasOrigen": [
          {
            "codigo": "3.4.072",
            "nombre": "Arquitectura de Computadores"
          }
        ]
      }
    }
  ],
  "resumen": {
    "aprobadas": 3,
    "pendientesDeFinal": 1,
    "pendientesDeCursado": 47,
    "materiasReconocidas": 5,
    "pendientesPlanActual": 14,
    "pendientesPlanNuevo": 48,
    "tituloIntermedio": {
      "aprobadas": 3,
      "total": 31,
      "avance": 10
    }
  },
  "optativasSinCorrespondencia": [
    {
      "codigo": "OPT-TDS-I",
      "nombre": "Optativa I"
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

- `400` si falta `X-Legajo`, si el body es inválido o si `codigoPlanOrigen` o `codigoPlanDestino` están vacíos.
- `404` si el legajo, el plan destino o el plan origen (no activo para ese legajo) no existen.
- `409` si el plan destino es el mismo que el origen o si no está activo.

---

### `POST /api/v1/reportes/simulacion`

Genera el **PDF del reporte de equivalencias** a partir de una respuesta de
simulación ya calculada (el cliente reenvía la que obtuvo, así el documento
refleja exactamente lo que vio el usuario, sin re-ejecutar el motor).

**Body:**

```json
{
  "legajo": "1100001",
  "simulacion": { ...respuesta de POST /api/v1/simulaciones... }
}
```

```bash
SIM=$(curl -s -X POST http://localhost:8080/api/v1/simulaciones \
  -H "X-Legajo: 1100001" -H "Content-Type: application/json" \
  -d '{"codigoPlanOrigen":"1121","codigoPlanDestino":"1621"}')
curl -s -X POST http://localhost:8080/api/v1/reportes/simulacion \
  -H "Content-Type: application/json" \
  -d "{\"legajo\":\"1100001\",\"simulacion\":$SIM}" \
  -o reporte.pdf
```

Responde `application/pdf` con `Content-Disposition: attachment` y el nombre
`simulacion-<origen>-a-<destino>.pdf`. El documento contiene el encabezado con
legajo y planes, la tabla del acta (**Código origen · Plan Origen · Materia
origen · Condición · Código destino · Plan · Materia destino** — una fila por
materia destino; sin reconocimiento, las columnas de origen van en “-”) y un
pie con los totales y la fecha. La columna **Condición** usa la misma paleta
de estados que la app (verde aprobada, amarillo final pendiente, celeste
cursando) además de la etiqueta escrita, para que el documento siga siendo
legible impreso en blanco y negro.

Errores:

- `400` si falta el legajo o la simulación en el body.

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
| `400` | Falta `X-Legajo`, legajo con formato inválido (no son 7-15 dígitos), código de plan inválido, JSON malformado, body inválido o tipo de parámetro inválido |
| `404` | No existe el estudiante, el plan o el recurso (incluye rutas inexistentes y endpoints de Actuator no expuestos) |
| `409` | Migración no permitida: mismo plan o plan destino inactivo |
| `415` | `Content-Type` no soportado |
| `429` | Demasiadas solicitudes desde la misma IP (rate limiting) |
| `500` | Error inesperado (sin detalles internos en la respuesta) |

Ejemplos:

```bash
# Falta X-Legajo -> 400
curl -i http://localhost:8080/api/v1/estudiantes/situacion

# Legajo inexistente -> 404
curl -i -H "X-Legajo: 9999999" \
  http://localhost:8080/api/v1/planes

# Plan inexistente -> 404
curl -i http://localhost:8080/api/v1/planes/9999

# Origen no activo para el legajo -> 404
curl -i -X POST http://localhost:8080/api/v1/simulaciones \
  -H "X-Legajo: 1100001" \
  -H "Content-Type: application/json" \
  -d '{"codigoPlanOrigen":"1621","codigoPlanDestino":"5010"}'

# Mismo plan que el origen -> 409
curl -i -X POST http://localhost:8080/api/v1/simulaciones \
  -H "X-Legajo: 1100001" \
  -H "Content-Type: application/json" \
  -d '{"codigoPlanOrigen":"1121","codigoPlanDestino":"1121"}'

# Body inválido -> 400
curl -i -X POST http://localhost:8080/api/v1/simulaciones \
  -H "X-Legajo: 1100001" \
  -H "Content-Type: application/json" \
  -d '{"codigoPlanOrigen":"1121","codigoPlanDestino":""}'

# Legajo con formato inválido -> 400
curl -i -H "X-Legajo: abc" http://localhost:8080/api/v1/estudiantes/situacion
```

---

## Seguridad

Medidas aplicadas al backend (sin autenticación por ahora: el login lo aporta
la facultad cuando el módulo se integre a su flujo real):

| Medida | Estado | Dónde |
|---|---|---|
| Validación server-side del legajo (`\d{7,15}`) y de los códigos de plan | ✅ | `@Pattern` en controllers y `SimulacionRequest`; 400 automático |
| Rate limiting por IP (60 req/min → 429) | ✅ | `seguridad/LimitadorTasaInterceptor` + `ControlTasa` (en memoria, 1 instancia) |
| Actuator restringido a `health` | ✅ | `application.yaml` (`exposure.include: health`) |
| Errores sin detalles internos; rutas inexistentes → 404 | ✅ | `GlobalExceptionHandler` |
| Security headers (nosniff, frame DENY, Referrer-Policy, CSP) | ✅ | `seguridad/SecurityConfig` |
| Swagger/OpenAPI deshabilitado por defecto | ✅ | `application.yaml` (`springdoc.*.enabled: false`; re-activar solo en dev con las variables `SPRINGDOC_API_DOCS_ENABLED`/`SPRINGDOC_SWAGGER_UI_ENABLED`) |
| Postgres publicado solo en `127.0.0.1` (no expuesto a la LAN) | ✅ | `docker-compose.yml` |
| Secretos por `.env`, nunca en el código | ✅ | `docker-compose.yml` + `application.yaml` |

Pendientes / a definir con la facultad:

- **Identidad de sesión**: el header `X-Legajo` es contrato de desarrollo.
  En producción la identidad debe venir de la sesión/cookie del SSO de la
  facultad (el bloque `app.jwt` de `application.yaml` ya espera ese
  contrato). Nunca confiar en un legajo elegible por el cliente.
- **HTTPS/TLS**: lo termina la infraestructura de la facultad (su dominio y
  proxy). Para demos públicas con candado verde sin dominio propio: túnel
  gratuito (`cloudflared tunnel --url http://localhost:8080`).
- **CORS**: hoy no hace falta (SPA y API en el mismo origen). Configurar una
  allowlist exacta solo si WebCampus consume la API desde otro dominio.
- **Rate limiting multi-nodo**: el actual es en memoria (una instancia);
  para varios nodos haría falta Bucket4j + Redis.
- **Dependencias**: revisión periódica con
  `mvn versions:display-dependency-updates` (a la fecha hay updates mayores
  de Flyway y un RC de Spring Boot — evaluar antes de subir).

### Verificación rápida

```bash
# Headers de seguridad en el SPA
curl -s -D - -o /dev/null http://localhost:8080/ | grep -iE "x-content-type|x-frame|referrer-policy|content-security"

# Actuator: solo health
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/actuator/env   # 404

# Validación de legajo
curl -s -o /dev/null -w "%{http_code}\n" -H "X-Legajo: abc" http://localhost:8080/api/v1/estudiantes/situacion  # 400

# Rate limiting: después de 60 requests en un minuto responde 429
for i in $(seq 1 70); do curl -s -o /dev/null http://localhost:8080/api/v1/planes/1621; done
```

---

## Demo desde el celular

Dos formas de mostrar el simulador en un teléfono.

### A. Misma red Wi-Fi (sin instalar nada)

1. Celular conectado al **mismo Wi-Fi** que la notebook.
2. Averiguar la IP de la notebook:
   ```bash
   hostname -I
   ```
3. Abrir en el celular:
   ```
   http://<IP>:8080/        # ej. http://192.168.0.30:8080/
   ```
4. Ingresar el legajo **a mano** (`1100001` o `1100002`): el celular es otro
   dispositivo y no comparte el `localStorage` de la PC.

Si no carga: firewall de la notebook o “AP isolation” del router (probar con
el hotspot del celular). La IP cambia si la notebook se conecta a otra red.

### B. Túnel de Cloudflare (cualquier red, HTTPS válido)

Para la presentación: el público entra desde **sus propios celulares con
datos móviles**, con candado verde y sin depender de tu Wi-Fi.

```bash
# 1) Instalar cloudflared (una sola vez)
curl -L https://github.com/cloudflare/cloudflared/releases/latest/download/cloudflared-linux-amd64.deb -o /tmp/cloudflared.deb
sudo dpkg -i /tmp/cloudflared.deb

# 2) Levantar el túnel (terminal dedicada; el backend ya debe estar corriendo)
cloudflared tunnel --url http://localhost:8080
```

Imprime una URL tipo `https://xxxx.trycloudflare.com`. Compartila o pasala a
QR para la slide:

```bash
sudo apt install -y qrencode
qrencode -o ~/demo-qr.png "https://xxxx.trycloudflare.com"
```

Cerrar con `Ctrl+C` al terminar la demo.

Advertencias:

- La URL **cambia en cada arranque**: generar el QR *después* de levantar el
  túnel, el día de la presentación.
- El túnel sin cuenta no tiene garantía de uptime (está bien para una demo).
- El tráfico pasa por el borde de Cloudflare: usarlo **solo con datos demo**.
  Para producción, la infraestructura de la facultad (dominio y certificado
  propios).
- Cualquiera con la URL puede entrar mientras el túnel esté corriendo: no
  dejarlo abierto de más.

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
├── V2__datos_demo.sql
├── V3__multi_carrera_facultades.sql
└── V4__planes_reales.sql
```

> No edites una migración ya aplicada. Si necesitás cambiar datos o esquema, creá una migración nueva (`V5__...sql`). Si editás una migración existente, Flyway puede fallar por checksum.

> Nota de modelado: en el DDL, `correlatividad` referencia a `plan_materia`
> (materia dentro de un plan), pero la entidad JPA la simplifica apuntando a
> `Materia` — la funcionalidad actual no depende de esa diferencia (las
> correlativas se consultan por plan) y el DDL sigue siendo el contrato real.

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

### La API devuelve `429 Too Many Requests`

Es el rate limiting (60 requests por minuto por IP, ver [Seguridad](#seguridad)).
Esperá un minuto sin hacer requests y volvé a intentar. Si aparece durante la
demo con el túnel de Cloudflare, es porque el tráfico de todo el público entra
con la misma IP pública.

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

3. No editar migraciones ya aplicadas. Crear una nueva versión (`V5__...sql`).
4. Mantener el estilo existente: paquetes por capa, DTOs como `record`, inyección por constructor.
5. Actualizar este README si se agregan endpoints, variables de entorno o migraciones.
6. No commitear `.env` ni credenciales.

---

## Checklist rápido

- [ ] `.env` creado.
- [ ] `docker compose up -d --build` ejecutado.
- [ ] `docker compose ps` muestra base healthy y backend up.
- [ ] `/actuator/health` devuelve `UP`.
- [ ] `GET /api/v1/planes?excluir=1121` con `X-Legajo: 1100001` devuelve `1621`, `5010` y `5020` con facultad.
- [ ] `GET /api/v1/estudiantes/situacion` devuelve `planesActivos` con `1121` (y `1100002` con `1621` + `5010`).
- [ ] `POST /api/v1/simulaciones` con origen `1121` y destino `1621` devuelve 51 ítems, 3 aprobadas y 5 reconocidas.
- [ ] `GET /api/v1/equivalencias` devuelve `Res. 456/22` y `Res. 123/21`.
- [ ] `./mvnw test` pasa los 33 tests.
- [ ] Seguridad: `/actuator/env` devuelve 404 y `curl -s -D - -o /dev/null http://localhost:8080/` muestra los security headers.
- [ ] Si corrés local, usás `DB_PORT=5433` cuando corresponde.
