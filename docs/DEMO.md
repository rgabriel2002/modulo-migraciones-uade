# Cheat sheet de la demo local

> Documento de una página para usar durante la presentación. Lee el `README.md`
> para la documentación completa del proyecto.

## Datos de prueba

| Campo | Valor |
|---|---|
| Legajo | `1100001` (1 plan activo) |
| Plan activo | `1121` — Tecnicatura en Desarrollo de Software |
| Plan destino | `1621` — Ingeniería en Informática |
| Legajo multi-carrera | `1100002` (2 planes activos: `1621` + `5010`) |
| Plan nuevo (actualización) | `5015` — Contador Público 2026 |
| Facultades | Ingeniería y Ciencias Exactas · Ciencias Económicas |
| Equivalencia | `Res. 123/21`: `3.4.225` → `3.4.069` |
| Equivalencia entre facultades | `Res. 456/22`: `3.4.164` → `1.1.144` (`1621` → `5010`) |

## Puertos

| Servicio | Puerto host | Puerto contenedor |
|---|---|---|
| Backend | `8080` | `8080` |
| PostgreSQL | `5433` | `5432` |

Base: `migracion_qa` · Usuario: `postgres`

## Comandos clave

```bash
# Levantar todo (desde cero, borra datos)
docker compose down -v
docker compose up -d --build

# Levantar todo (sin rebuild)
docker compose up -d

# Ver estado
docker compose ps

# Logs del backend
docker compose logs -f backend

# Health check
curl -s http://localhost:8080/actuator/health

# Swagger UI
# Abrir http://localhost:8080/swagger-ui.html

# Ejecutar preflight
./scripts/preflight.sh
```

## Guion de la demo (5-8 minutos)

1. **`docker compose ps`** — mostrar db healthy y backend up.
2. **`docker compose logs --tail=50 backend`** — mostrar Flyway V1 a V4 y "Started SimuladorApplication".
3. **Health** — `curl http://localhost:8080/actuator/health` → `UP`.
4. **Swagger** — abrir `http://localhost:8080/swagger-ui.html` y mostrar los endpoints agrupados.
5. **Planes** — `curl -H "X-Legajo: 1100001" "http://localhost:8080/api/v1/planes?excluir=1121"` → `1621`, `5010`, `5015`, `5020`, `5030`, `5040` con facultad.
5. **Detalle plan** — `curl http://localhost:8080/api/v1/planes/1621` → 51 materias + correlatividades.
6. **Situación** — `curl -H "X-Legajo: 1100001" http://localhost:8080/api/v1/estudiantes/situacion` → `planesActivos: [1121]`; con `1100002` → `[1621, 5010]` (multi-carrera).
7. **Historial** — `curl -H "X-Legajo: 1100001" http://localhost:8080/api/v1/estudiantes/historial` → recursadas de `3.4.075`.
8. **Simulación** — `POST /api/v1/simulaciones` con `codigoPlanOrigen: "1121"` y `codigoPlanDestino: "1621"` → 51 ítems, 3 aprobadas, equivalencia `Res. 123/21`, título intermedio 3/31 = 10%.
9. **Cambio de facultad** — `POST` con `X-Legajo: 1100002`, origen `1621` y destino `5010` → reconocimientos entre facultades (`Res. 456/22`).
10. **Actualización de plan** — `POST` con `X-Legajo: 1100002`, origen `5010` y destino `5015` (Contador 2026, misma carrera) → materias compartidas reconocidas por MISMA_MATERIA.
11. **Equivalencias** — `curl http://localhost:8080/api/v1/equivalencias` → `Res. 456/22` y `Res. 123/21`.
12. **Errores** — 400 (sin header), 404 (legajo/plan inexistente, origen no activo para el legajo), 409 (mismo plan).

## IntelliJ: `api.http`

Todos los endpoints están en `api.http`. Ejecutar con un clic en el botón de Play junto a cada request.

## Plan B

| Problema | Solución |
|---|---|
| Puerto ocupado | Cambiar `APP_PORT` / `DB_HOST_PORT` en `.env` |
| Backend no responde | Esperar a "Started SimuladorApplication" en los logs |
| Datos faltan | `docker compose down -v` y volver a levantar |
| No hay red | Todo es local, no se necesita Internet tras el primer build |

## Más info

- `README.md` — documentación completa (1164 líneas).
- `docs/PASO-A-PASO-DEMO.txt` — ensayo de arranque desde cero.
- `docs/arquitectura.md` — diagrama de capas y flujo.
