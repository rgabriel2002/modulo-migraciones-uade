#!/usr/bin/env bash
#
# preflight.sh - Verificaciones previas antes de levantar la demo.
#
# Ejecutar desde la raíz del proyecto (donde están docker-compose.yml y pom.xml).
# Sale con código 0 si todo está listo, 1 si algo falta.
#

set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_ROOT"

echo "=== Preflight check del Simulador ==="
echo ""

ERRORS=0

# 1. Docker activo
echo -n "[1/6] Docker activo... "
if docker info >/dev/null 2>&1; then
    echo "OK"
else
    echo "FALLO: Docker no está corriendo"
    ERRORS=$((ERRORS + 1))
fi

# 2. Archivo .env presente
echo -n "[2/6] Archivo .env... "
if [[ -f .env ]]; then
    echo "OK"
else
    echo "FALLO: .env no existe. Copialo de .env.example"
    ERRORS=$((ERRORS + 1))
fi

# 3. Puertos libres
echo -n "[3/6] Puertos libres (8080, 5433)... "
OCCUPIED=$(ss -ltnp 2>/dev/null | grep -E ':8080|:5433' || true)
if [[ -z "$OCCUPIED" ]]; then
    echo "OK"
else
    echo "FALLO: alguito ya usa los puertos"
    echo "$OCCUPIED"
    ERRORS=$((ERRORS + 1))
fi

# 4. docker-compose válido
echo -n "[4/6] docker-compose válido... "
if docker compose config --quiet >/dev/null 2>&1; then
    echo "OK"
else
    echo "FALLO: docker-compose config tiene errores"
    ERRORS=$((ERRORS + 1))
fi

# 5. Imagen de postgres disponible o descargable
echo -n "[5/6] Imagen postgres:16-alpine... "
if docker image inspect postgres:16-alpine >/dev/null 2>&1; then
    echo "OK (local)"
else
    echo "OK (se descargará al levantar)"
fi

# 6. mvnw ejecutable
echo -n "[6/6] mvnw ejecutable... "
if [[ -x mvnw ]]; then
    echo "OK"
else
    echo "FALLO: mvnw no tiene permiso de ejecución (chmod +x mvnw)"
    ERRORS=$((ERRORS + 1))
fi

echo ""
if [[ $ERRORS -eq 0 ]]; then
    echo "=== Todo listo para levantar el proyecto ==="
    exit 0
else
    echo "=== Se detectaron $ERRORS problema(s). Resolvelos antes de continuar ==="
    exit 1
fi
