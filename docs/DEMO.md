# Cheat sheet de la demo local

> Documento de una página para usar durante la presentación. Lee el `README.md`
> para la documentación completa del proyecto.

## Datos de prueba

| Campo | Valor |
|---|---|
| Legajo | `1100001` |
| Plan actual | `1121` — Tecnicatura en Desarrollo de Software |
| Plan destino | `1621` — Ingeniería en Informática |
| Equivalencia | `Res. 123/21`: `3.4.225` → `3.4.069` |

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

# Ejecutar preflight
./scripts/preflight.sh
```

## Guion de la demo (5-8 minutos)

1. **`docker compose ps`** — mostrar db healthy y backend up.
2. **`docker compose logs --tail=50 backend`** — mostrar Flyway V1/V2 y "Started SimuladorApplication".
3. **Health** — `curl http://localhost:8080/actuator/health` → `UP`.
4. **Planes** — `curl -H "X-Legajo: 1100001" http://localhost:8080/api/v1/planes` → `1621`.
5. **Detalle plan** — `curl http://localhost:8080/api/v1/planes/1621` → 7 materias + correlatividades.
6. **Situación** — `curl -H "X-Legajo: 1100001" http://localhost:8080/api/v1/estudiantes/situacion` → plan `1121`, 7 materias.
7. **Historial** — `curl -H "X-Legajo: 1100001" http://localhost:8080/api/v1/estudiantes/historial` → recursadas de `9.9.002`.
8. **Simulación** — `POST /api/v1/simulaciones` con `codigoPlanDestino: "1621"` → 7 ítems, 3 aprobadas, equivalencia `Res. 123/21`, título intermedio 2/6 = 33%.
9. **Equivalencias** — `curl http://localhost:8080/api/v1/equivalencias` → `Res. 123/21`.
10. **Errores** — 400 (sin header), 404 (legajo/plan inexistente), 409 (mismo plan).

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
