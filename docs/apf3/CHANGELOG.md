# Versión 0.3.0

24 de noviembre de 2026. Incremento académico de gestión del servicio.

- Borrado lógico de productos y almacenes sin borrar existencias ni movimientos.
- Refresh tokens opacos con hash SHA-256 en PostgreSQL, rotación, revocación por familia y detección de reutilización.
- Access token de 15 minutos y familia de refresh con duración absoluta de 7 días. Logout revoca la renovación; un access token ya emitido conserva vigencia hasta su vencimiento si la cuenta sigue habilitada.
- Swagger UI con esquema global Bearer JWT y operaciones públicas diferenciadas.
- Migraciones V1 a V3 y validación del esquema. Las bases APF2 existentes requieren respaldo y baseline manual verificado antes de migrar.
- Despliegue local reproducible, plantillas de solicitudes/cambios y plan de capacidad.

Los pedidos, flota, rutas, facturación y frontend Angular siguen fuera de este incremento. El entorno final es académico local y no constituye producción corporativa de Backus.
