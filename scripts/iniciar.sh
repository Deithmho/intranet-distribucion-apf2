#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
bash scripts/configurar-entorno.sh
set -a
source .env
set +a
docker compose up -d --wait db
exec ./gradlew bootRun --no-daemon
