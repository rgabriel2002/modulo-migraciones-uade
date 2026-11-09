# Simulador de Migración — Frontend

Interfaz web del simulador de equivalencias de la UADE. Permite ingresar un
legajo, ver el plan de origen del alumno, elegir un plan de destino y ejecutar
la simulación contra la API real.

El front **no implementa reglas de negocio**: el cruce de materias,
equivalencias y horas lo resuelve el backend. Acá solo se consume la API y se
muestra el resultado.

---

## Stack

| Tecnología | Uso |
|---|---|
| React 19 | Interfaz |
| TypeScript | Tipado (configuración laxa) |
| Vite 8 | Dev server y build |
| React Router 7 | Navegación con `HashRouter` |
| TanStack Query 5 | Fetching, caché y mutaciones |
| Tailwind CSS 4 | Estilos y tema UADE |
| Oxlint | Lint |

---

## Requisitos

- Node 20 o superior
- Backend corriendo en `http://localhost:8080`
- Docker (solo para el despliegue final)

Verificación rápida del backend:

```bash
curl http://localhost:8080/actuator/health
```

---

## Desarrollo

```bash
cd frontend
npm install
npm run dev
```

Abre **http://localhost:5173**.

Vite proxya `/api` hacia `http://localhost:8080`, por lo que no hace falta
configurar CORS.

---

## Build y despliegue

### Build normal

```bash
npm run build
```

Genera `frontend/dist/`.

### Build para Spring Boot

```bash
npm run build:static
```

El script hace:

1. `tsc -b && vite build`.
2. Limpia `../src/main/resources/static/`.
3. Copia `dist/` ahí.

Resultado:

```text
src/main/resources/static/
├── index.html
├── uade-logo.png
└── assets/
    ├── index-*.js
    └── index-*.css
```

Después hay que reconstruir el backend:

```bash
cd ..
docker compose up -d --build backend
```

El front queda servido en **http://localhost:8080/**.

> `src/main/resources/static/` es salida generada. No se edita a mano.

---

## Cómo se usa

1. Entrar a `http://localhost:8080/`.
2. Ingresar el legajo y presionar **Enter** o la lupa.
3. El bloque **Origen** se completa solo con facultad, carrera y plan:
   - con un solo plan activo, queda fijo y de solo lectura;
   - con varios planes activos (alumno multi-carrera), aparece el selector
     **Carrera de origen**.
4. En **Destino**, elegir facultad, carrera y plan (cascada de tres niveles;
   el plan elegido como origen queda excluido de los destinos).
5. Presionar **Buscar**.
6. Ver el resultado en dos pestañas:
   - **Informe de equivalencias**: KPIs, avance del título intermedio, tabla de materias del plan destino con estado y reconocimiento, y optativas aprobadas sin correspondencia.
   - **Mapa de correlativas (Plan de carrera)**: materias agrupadas por año y cuatrimestre, con flechas de correlatividad y los estados de la simulación.
7. Presionar **Descargar PDF** para bajar el reporte en PDF (acta de
   equivalencias con las materias reconocidas).

El legajo se persiste en `localStorage` con la clave `simulador-legajo`. La
pestaña activa se persiste en la URL con `?vista=mapa` (sin el parámetro se
abre el informe).

---

## Mapa de correlativas

La pestaña **Mapa de correlativas (Plan de carrera)** se arma con:

- `GET /api/v1/planes/{codigo}`: detalle del plan destino.
- Los estados de `POST /api/v1/simulaciones` (`items[]`).

No modifica datos ni agrega endpoints: es una vista de solo lectura.

### Cómo se lee

- Filas por año y cuatrimestre; las materias anuales (`cuatrimestre: null`)
  van al final de su año.
- Cada materia se ubica bajo su primera correlativa cuando hay lugar.
- Flechas azules: correlativa → materia. El tipo (`CURSADA` / `FINAL`) se
  anuncia en el `aria-label` de la materia.
- Colores: verde aprobada/otorgada, amarillo final pendiente, celeste
  cursando, blanco a cursar.
- Borde punteado: optativa.
- Hover (mouse) o tap (touch) resalta la materia, sus correlativas y las
  materias que dependen de ella.
- Teclado: `Tab` recorre las materias y `Escape` limpia el resaltado. Las
  pestañas se navegan con `←` / `→`, `Home` y `End`.
- **En mobile (≤640px)** la grilla se reemplaza por una lista vertical a
  ancho completo: sección por año/cuatrimestre, cada materia como tarjeta
  con su estado y las correlativas como etiquetas “Requiere: …” — sin
  flechas ni scroll horizontal. El informe pasa de tabla a tarjetas
  apiladas y los KPIs a 2 columnas.

### Estado en la URL

| URL | Vista |
|---|---|
| `/#/simulacion/1100001/1121/1621` | Informe |
| `/#/simulacion/1100001/1121/1621?vista=mapa` | Mapa |

### Implementación

- `utils/mapa.ts`: layout puro (filas, columnas, `ncols`).
- `componentes/MapaCorrelativas.tsx`: cajas, leyenda y flechas SVG propias
  (sin `react-flow` ni librerías de grafos).
- `hooks/usePlanDetalle.ts`: solo pide el plan cuando la pestaña Mapa está
  activa.
- Las flechas se recalculan con `ResizeObserver`, `resize` y
  `document.fonts.ready`.
- Impresión: se ocultan pestañas y ayudas, y el mapa pasa a ancho completo.

---

## Datos demo

| Dato | Valor |
|---|---|
| Legajo | `1100001` |
| Alumno | Alumno De Prueba |
| Plan activo | `1121` — Tecnicatura Universitaria en Desarrollo de Software (1 sola carrera) |
| Legajo multi-carrera | `1100002` |
| Alumna | Alumna Multi Carrera |
| Planes activos | `1621` — Ingeniería en Informática y `5010` — Contador Público |

Facultades: **Facultad de Ingeniería y Ciencias Exactas** (planes `1121`
Tecnicatura, `1621` Ingeniería, `5040` Biotecnología) y **Facultad de
Ciencias Económicas** (planes `5010`/`5015` Contador Público, `5020`
Licenciatura en Administración de Empresas, `5030` Licenciatura en Finanzas).
Todos los planes tienen las materias reales de UADE (211 materias, 186
correlatividades).

Resultado esperado para `1100001` (`1121` → `1621`, plan real de 51 materias):

- 5 materias reconocidas (1 equivalencia + 4 misma materia).
- 3 aprobadas.
- 1 con final pendiente (`3.4.075`), 1 cursando (`3.4.207`).
- 47 pendientes de cursado.
- Título intermedio (Analista): 3 de 31 materias (10 %).
- `3.4.225` se reconoce como `3.4.069` por `Res. 123/21`.
- `OPT-TDS-I` queda como optativa sin correspondencia.

Con `1100002` hay dos casos extra: cambio de facultad (`1621` → `5010`, con
`Res. 456/22`: `3.4.164` → `1.1.144`) y actualización de plan de la misma
carrera (`5010` → `5015`, Contador Público plan 2026).

Casos de error:

| Caso | Resultado |
|---|---|
| Legajo `9999999` | `404` — no existe el estudiante |
| Sin `X-Legajo` | `400` |
| Plan destino igual al actual (`1121`) | `409` |
| Plan inexistente | `404` |

---

## Endpoints consumidos

| Método | Endpoint | Header | Body | Hook |
|---|---|---|---|---|
| `GET` | `/api/v1/estudiantes/situacion` | `X-Legajo` | — | `useSituacion` |
| `GET` | `/api/v1/planes?excluir={codigo}` | `X-Legajo` | — | `usePlanes` |
| `GET` | `/api/v1/planes/{codigo}` | — | — | `usePlanDetalle` |
| `POST` | `/api/v1/simulaciones` | `X-Legajo` | `{ "codigoPlanOrigen": "1121", "codigoPlanDestino": "1621" }` | `useSimulacion` |
| `POST` | `/api/v1/reportes/simulacion` | `X-Legajo` | `{ "codigoPlanOrigen": "1121", "codigoPlanDestino": "1621" }` | `descargarReportePdf` |

`GET /planes/{codigo}` se usa solo para el mapa de correlativas. El backend
expone además `GET /estudiantes/historial` y `GET /equivalencias`, pero este
flujo no los necesita: el `POST` ya devuelve el cruce resuelto. El PDF se
genera recalculando la simulación en el servidor (`descargarReportePdf`):
el documento sale de datos reales, no de lo que envíe el cliente.

### Ejemplo del POST

```http
POST /api/v1/simulaciones
X-Legajo: 1100001
Content-Type: application/json

{
  "codigoPlanOrigen": "1121",
  "codigoPlanDestino": "1621"
}
```

---

## Manejo de errores

`api/cliente.ts` lanza un `ApiError` con:

- `status`: código HTTP (`0` si no hubo conexión).
- `body`: cuerpo crudo del backend.
- `message`: mensaje legible.

| Status | Significado |
|---|---|
| `0` | No se pudo conectar con el backend |
| `400` | Falta `X-Legajo` o body inválido |
| `404` | Legajo o plan inexistente |
| `409` | Mismo plan o plan inactivo |

El front muestra el mensaje en un `Banner` y permite reintentar.

---

## Rutas

Se usa **`HashRouter`** para que los deep links funcionen servidos desde Spring
Boot sin necesidad de un fallback del servidor.

| Ruta | Pantalla |
|---|---|
| `/` | `Seleccion` |
| `/#/simulacion/:legajo/:planOrigen/:planDestino` | `Resultado` |
| `*` | `NoEncontrado` |

Estado del flujo:

- **Legajo** → `localStorage`.
- **Plan origen y plan destino** → URL.
- **Pestaña activa del resultado** → URL (`?vista=mapa`; el informe es el default).
- **Facultad, carrera y plan seleccionados** → estado local de React.

---

## Estructura del frontend

```text
frontend/
├── .gitignore
├── .oxlintrc.json
├── README.md
├── index.html
├── package.json
├── package-lock.json
├── tsconfig.json
├── tsconfig.app.json
├── tsconfig.node.json
├── vite.config.ts
├── public/
│   └── uade-logo.png
└── src/
    ├── main.tsx
    ├── App.tsx
    ├── index.css
    ├── api/
    │   ├── cliente.ts
    │   └── tipos.ts
    ├── hooks/
    │   ├── useLegajo.ts
    │   ├── useSituacion.ts
    │   ├── usePlanes.ts
    │   ├── usePlanDetalle.ts
    │   └── useSimulacion.ts
    ├── componentes/
    │   ├── Layout.tsx
    │   ├── Header.tsx
    │   ├── Footer.tsx
    │   ├── Boton.tsx
    │   ├── Badge.tsx
    │   ├── Banner.tsx
    │   ├── CajaPlan.tsx
    │   ├── SelectorFacultad.tsx
    │   ├── SelectorCarrera.tsx
    │   ├── SelectorOrigen.tsx
    │   ├── SelectorPlan.tsx
    │   ├── Kpis.tsx
    │   ├── TablaResultado.tsx
    │   ├── EstadoBadge.tsx
    │   ├── Pestanas.tsx
    │   ├── MapaCorrelativas.tsx
    │   └── Iconos.tsx
    ├── paginas/
    │   ├── Seleccion.tsx
    │   ├── Resultado.tsx
    │   └── NoEncontrado.tsx
    └── utils/
        ├── cn.ts
        └── mapa.ts
```

| Carpeta / archivo | Responsabilidad |
|---|---|
| `main.tsx` | Punto de entrada; monta React e importa `index.css` |
| `App.tsx` | `HashRouter` + `QueryClientProvider` + rutas |
| `index.css` | Tailwind, paleta UADE y clases base |
| `api/cliente.ts` | `fetch`, header `X-Legajo`, `ApiError`, `{ status, body }` y descarga del PDF |
| `api/tipos.ts` | Tipos espejo de los DTOs Java |
| `hooks/useLegajo.ts` | Persistencia del legajo en `localStorage` |
| `hooks/useSituacion.ts` | `GET /estudiantes/situacion` (planes activos) |
| `hooks/usePlanes.ts` | `GET /planes?excluir=` |
| `hooks/usePlanDetalle.ts` | `GET /planes/{codigo}` para el mapa |
| `hooks/useSimulacion.ts` | `POST /simulaciones` |
| `componentes/` | Piezas de UI reutilizables |
| `componentes/Pestanas.tsx` | Pestañas accesibles del resultado |
| `componentes/MapaCorrelativas.tsx` | Mapa de correlativas con SVG propio |
| `paginas/` | Pantallas del flujo |
| `utils/cn.ts` | Unión condicional de clases |
| `utils/mapa.ts` | Layout puro del mapa (filas y columnas) |

---

## Organización del repo

```text
simulador/
├── api.http
├── docker-compose.yml
├── Dockerfile
├── .dockerignore
├── .env
├── .env.example
├── .gitignore
├── .mvn/
├── mvnw
├── mvnw.cmd
├── pom.xml
├── README.md
├── docs/
│   ├── arquitectura.md
│   ├── DEMO.md
│   └── PASO-A-PASO-DEMO.txt
├── scripts/
│   └── preflight.sh
├── frontend/
│   └── ... (ver estructura del frontend)
├── src/
│   ├── main/
│   │   ├── java/ar/edu/uade/simulador/
│   │   │   ├── api/            # Controllers REST
│   │   │   ├── dominio/        # Entidades JPA y enums
│   │   │   ├── dto/            # Records de request/response
│   │   │   ├── errores/        # Excepciones y manejo global
│   │   │   ├── integracion/    # Fuente de situación académica
│   │   │   ├── motor/          # Motor y reglas de equivalencia
│   │   │   ├── repositorio/    # Spring Data
│   │   │   ├── seguridad/      # Spring Security
│   │   │   ├── servicio/       # Lógica de aplicación
│   │   │   └── SimuladorApplication.java
│   │   └── resources/
│   │       ├── application.yaml
│   │       ├── db/migration/   # Flyway
│   │       ├── static/         # Front generado por build:static
│   │       └── templates/
│   └── test/java/ar/edu/uade/simulador/
│       ├── api/
│       └── motor/
└── target/                     # Generado por Maven, ignorado
```

---

## Estilos

Tailwind v4 con `@theme` en `src/index.css`.

| Token | Uso |
|---|---|
| `bg` | Fondo general |
| `surface` | Tarjetas |
| `ink` | Texto principal |
| `muted` | Texto secundario |
| `line` | Bordes |
| `blue` / `blue-d` | Acciones y énfasis |
| `tint` | Fondos suaves |
| `warn` / `ok` | Estados |
| `--ease-out` / `--ease-in-out` | Curvas fuertes para transiciones de UI |

Tipografías:

- **Public Sans** → texto general.
- **Montserrat** → navegación del header.

Clases base: `.card`, `.field`, `.label`, `.btn`, `.btn-primary`,
`.btn-secondary`, `.tabs`, `.tab`.

El mapa usa `.mapa`, `.mapa-grid`, `.mapa-mat`, `.mapa-fila`,
`.mapa-anio`, `.mapa-flechas` y `.mapa-pie`. Los estados se pintan con
`.es-aprobada`, `.es-final`, `.es-cursando` y `.es-a-cursar`; las optativas
suman `.es-optativa`.

Feedback de presión en `:active`, entradas de 150–200 ms y curvas
`--ease-out`. Todo el movimiento respeta `prefers-reduced-motion`: se
conservan los fades y se quita el desplazamiento.

---

## Scripts

| Script | Qué hace |
|---|---|
| `npm run dev` | Dev server en `5173` con proxy a `8080` |
| `npm run build` | Type-check + build a `dist/` |
| `npm run build:static` | Build + copia a `src/main/resources/static/` |
| `npm run lint` | Oxlint |
| `npm run preview` | Preview del build de Vite |

---

## Decisiones de diseño

- **Front delgado**: no replica reglas del motor; solo consume y muestra.
- **Origen**: se deriva del legajo vía `situacion` (planes activos); si hay
  más de una carrera, el alumno elige la de origen.
- **Destino en cascada**: facultad → carrera → plan, agrupando `GET /planes`;
  el plan elegido como origen se excluye del listado (`?excluir=`).
- **`{ status, body }`**: el cliente tipa la respuesta real de la simulación.
- **`HashRouter`**: evita tocar el backend con un fallback SPA.
- **“Descargar PDF”**: el backend recalcula la simulación en el servidor
  (`POST /reportes/simulacion` con `X-Legajo` y origen/destino) y entrega el
  acta; el documento no depende de datos enviados por el cliente.
- **Mapa de solo lectura**: los estados vienen de la simulación; no se editan
  desde el mapa.
- **Pestaña en la URL**: `?vista=mapa` permite deep links y sobrevive al
  refresh.

---

## Limitaciones y próximos pasos

- El PDF del reporte es un acta simple (sin plantilla institucional ni firma
  digital); una versión oficial se definirá con la facultad.
- Consume 5 de los 7 endpoints.
- Hay dos legajos y siete planes en los datos demo (dos facultades, planes
  reales); el legajo `1100002` cubre multi-carrera, cambio de facultad y
  actualización de plan (`5010` → `5015`).
- El backend no expone el año del plan, así que el título del mapa muestra
  carrera y código (`Ingeniería en Informática · Plan 1621`), sin “Año 2021”.
- No hay tests automatizados del front.
- URLs limpias (`/simulacion/...`) requerirían un fallback SPA en Spring.
- Si se quiere mostrar el historial detallado, se puede sumar `GET /historial`.

---

## Problemas comunes

| Problema | Causa / solución |
|---|---|
| “No se pudo conectar con el servidor” | Backend apagado; ejecutar `docker compose up -d` |
| `404` al buscar legajo | El legajo no existe en la base |
| `409` al simular | Plan destino igual al actual o inactivo |
| El front no refleja cambios | Falta `npm run build:static` y reconstruir el contenedor |
| Ruta `/#/...` en vez de `/...` | Es esperado por `HashRouter` |
| Fuentes distintas | Sin internet, cae a fuentes del sistema |
| Cambios no visibles en el navegador | Hard reload (`Ctrl+Shift+R`) |
