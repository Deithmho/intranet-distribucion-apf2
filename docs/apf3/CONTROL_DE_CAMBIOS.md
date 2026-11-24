# Control de cambios del servicio

Responsable individual: Mathias Sebastian Herrera Gutierrez. Fecha del incremento: 24 de noviembre de 2026.

## Flujo

1. Registrar RFC usando la plantilla de GitHub Issues; incluir motivo, alcance, activos, riesgo, responsables y criterios de aceptación.
2. Revisar impacto en aplicación, base, infraestructura, documentación y calendario. Distinguir cambio estándar, normal o emergencia.
3. Preparar rama y respaldo, probar la migración en una copia y comprobar plan de reversión.
4. Autorizar la ventana después de evaluar evidencia. En este trabajo individual Mathias prepara y ejecuta; una revisión independiente queda como control propuesto para una futura operación con varios responsables.
5. Ejecutar CI y exigir compilación, pruebas y artefactos correctos.
6. Desplegar, verificar estado, login, catálogo, renovación y migraciones. Registrar versión y evidencias.
7. Cerrar o revertir. Ante una migración incompatible, restaurar la copia previa y la versión compatible en la ventana acordada. No ejecutar clean ni borrar volúmenes.

## RFC ejecutadas

| ID | Activo | Resultado | Verificación |
|---|---|---|---|
| RFC-001 | Aplicación y base | Archivar productos y almacenes con historial | SQL activo=false, GET 404 y movimientos conservados |
| RFC-002 | Seguridad | Refresh tokens rotatorios y cierre de familia | Rotación 200, reutilización 401 y descendiente 401 |
| RFC-003 | Documentación API | Esquema Bearer global y Swagger | Swagger 200 y especificación OpenAPI |
| RFC-004 | Persistencia | Flyway V1 a V3 y ddl-auto=validate | Tres migraciones, prueba de repetición y CI |
| RFC-005 | Infraestructura | Java y PostgreSQL aislado en loopback | Estado 200 y prueba de restauración |

El bloqueo de Docker motivó un ajuste de infraestructura: se verificó el despliegue nativo local con PostgreSQL 17.12. La alternativa Docker se entrega como configuración pendiente de validación local. No se movió una fecha contractual ni se atribuyó al docente un cambio de plazo.
