#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
if [ -f .env ]; then
  echo '.env ya existe; se conserva su configuración.'
  exit 0
fi
umask 077
DB_TASK_PASSWORD=$(openssl rand -hex 24)
JWT_TASK_SECRET=$(openssl rand -base64 48 | tr -d '\n')
ADMIN_TASK_PASSWORD=$(openssl rand -hex 18)
cat > .env <<ENV
DB_URL=jdbc:postgresql://localhost:55432/intranet
DB_USERNAME=intranet
DB_PASSWORD=$DB_TASK_PASSWORD
JWT_SECRET=$JWT_TASK_SECRET
JWT_EXPIRATION=3600000
BOOTSTRAP_ENABLED=true
ADMIN_EMAIL=admin@example.test
ADMIN_PASSWORD=$ADMIN_TASK_PASSWORD
CORS_ORIGINS=http://localhost:4200
ENV
echo 'Entorno creado. Consulta ADMIN_PASSWORD en .env para iniciar sesión; no publiques ese archivo.'
