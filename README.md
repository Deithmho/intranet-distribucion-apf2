# Intranet de distribución APF2

Backend del proyecto académico **Implementación de una intranet para la gestión de distribución en una empresa Backus**. Continuación individual del avance 01 para el curso Desarrollo Web Integrado, enfocada en persistencia y seguridad.

- Autor del APF2: Mathias Sebastian Herrera Gutierrez — U23263869.
- Sección: 60618.
- Docente: Cesar Augusto Cardenas la torre.
- Procedencia del código grupal: [ORIGEN.md](ORIGEN.md).
- Caso académico con datos sintéticos; sin acceso a sistemas reales de Backus.

## Tecnologías

Java 21, Spring Boot 3.5.16, Gradle Wrapper 8.8, Spring Data JPA/Hibernate, PostgreSQL 17.11, Spring Security, JJWT 0.12.6, BCrypt y Testcontainers 1.21.4. Se mantiene Gradle del proyecto original; no se necesita instalar Maven.

## Ejecución local

Requisitos: JDK 21, Docker con Compose, Git, Bash y OpenSSL. El primer arranque descarga dependencias desde sus repositorios oficiales.

```bash
git clone https://github.com/Deithmho/intranet-distribucion-apf2.git
cd intranet-distribucion-apf2
bash scripts/iniciar.sh
```

El script genera `.env` con contraseñas y clave JWT aleatorias, levanta PostgreSQL en `localhost:55432` y ejecuta la API en `localhost:8080`. El usuario inicial es el valor `ADMIN_EMAIL`; consulta `ADMIN_PASSWORD` en tu `.env` para hacer login. Estos valores pertenecen a tu instalación local y no se incluyen en el repositorio ni en la colección.

Para usar PostgreSQL ya instalado, ejecuta `bash scripts/configurar-entorno.sh`, ajusta `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` en `.env` y carga las variables antes de `./gradlew bootRun`. Las propiedades leen esas variables; Spring Boot no carga `.env` por sí solo. Para Supabase configura un `DB_URL` con el host y SSL de tu propia cuenta.

```bash
set -a
source .env
set +a
./gradlew bootRun --no-daemon
```

`spring.jpa.hibernate.ddl-auto=update` permite observar las tablas creadas por Hibernate en esta entrega académica. Para un despliegue productivo posterior se recomiendan migraciones y `validate`.

## Arquitectura por capas

```text
com.dwi.intranetdistribucion
├── config/              configuración de CORS e inicialización
│   └── security/         JWT, filtro y SecurityFilterChain
├── controller/          API REST
├── dto/
│   ├── request/          entradas validadas
│   └── response/         respuestas sin contraseña ni entidades completas
├── exception/           errores HTTP consistentes
├── model/               entidades JPA
├── repository/          JpaRepository y consultas JPQL
├── service/             contratos de negocio
│   └── impl/            lógica y transacciones
└── util/                reservado para extensiones del proyecto base
```

## Alcance implementado

| RF del avance 01 | Estado en APF2 |
|---|---|
| RF-01 Inicio de sesión | JWT firmado y expirable, BCrypt, cinco fallos bloquean la cuenta |
| RF-02 Roles y permisos | CRUD de roles; permisos validados; ADMIN, ALMACENERO y AUDITOR |
| RF-03 Usuarios | CRUD administrativo con asignación de rol y estado activo |
| RF-04 Sedes | CRUD y restricción de borrado cuando tiene almacenes |
| RF-05 Almacenes | CRUD, relación con sede y conservación del historial |
| RF-06 Productos | CRUD, código único, categoría y stock mínimo |
| RF-07 Categorías | CRUD; no se borran categorías referenciadas |
| RF-08 Stock | Entradas, traslados, consulta de stock crítico e historial; salidas registradas por traslado |

RF-09 a RF-26 permanecen en el backlog para siguientes avances: clientes, pedidos, aprobación, comprobantes, rutas, entregas, flota, exportaciones y dashboard. El frontend Angular se conserva como propuesta del APF1; este repositorio entrega la API backend y sus pruebas.

## Endpoints y permisos

Base: `/api/v1`. Solo `POST /auth/login` y `GET /public/estado` son públicos. `POST /auth/register` requiere `USUARIOS_CREAR`; no existe autoasignación pública de roles.

| Recurso | Métodos | Lectura | Escritura |
|---|---|---|---|
| `/usuarios` y `/{id}` | GET POST PUT DELETE | USUARIOS_VER | USUARIOS_CREAR |
| `/roles`, `/{id}`, `/roles/permisos` | GET POST PUT DELETE | ROLES_GESTIONAR | ROLES_GESTIONAR |
| `/sedes`, `/almacenes`, `/categorias`, `/productos` | GET POST PUT DELETE | INVENTARIO_VER | INVENTARIO_GESTIONAR |
| `/inventario`, `/inventario/movimientos` | GET | INVENTARIO_VER | — |
| `/inventario/entradas`, `/inventario/traslados` | POST | — | INVENTARIO_GESTIONAR |

`GET /productos?categoriaId=1&texto=producto` y `GET /inventario?almacenId=1&categoriaId=1&critico=true` ejecutan JPQL parametrizado. Sustituye los IDs por los obtenidos al crear datos.

ADMIN dispone de todos los permisos iniciales; ALMACENERO gestiona inventario; AUDITOR consulta inventario. Los permisos se vuelven a leer desde la base de datos en cada solicitud, así que un token no mantiene privilegios de un usuario eliminado, bloqueado o inactivo.

Los traslados bloquean la fila del producto (`PESSIMISTIC_WRITE`) dentro de una transacción. La restricción única producto-almacén y el campo `@Version` refuerzan la consistencia. Se realiza la salida, la entrada y el movimiento como una unidad; un destino inexistente revierte incluso una salida ya enviada a PostgreSQL.

## Pruebas y evidencias

```bash
./gradlew clean test bootJar --no-daemon
```

Las 14 pruebas de integración levantan PostgreSQL aislado con Testcontainers; requieren Docker y no utilizan tu base de desarrollo. Incluyen CRUD, JPQL, 401/403, JWT expirado/firma incorrecta, bloqueo, usuario inactivo, conservación del stock, rollback y traslados simultáneos. Resultados locales: `build/reports/tests/test/index.html`. GitHub Actions ejecuta la misma validación.

Con la API iniciada, en otra terminal:

```bash
set -a
source .env
set +a
python3 scripts/validar-api.py
docker compose exec -T db psql -U intranet -d intranet -f - < scripts/evidencia-bd.sql
```

`validar-api.py` ejecuta 36 solicitudes mediante cURL, genera datos sintéticos y guarda respuestas redactadas en `docs/evidencias`. Está restringido a localhost. Cada ejecución usa códigos diferentes; no borra los movimientos históricos. El script requiere Python 3 estándar y cURL. Ver [evidencias](docs/evidencias/resultados-api.md) y [resultado de integración](docs/evidencias/resultados-integracion.json).

## Postman

Importa `postman/APF2.postman_collection.json` y `postman/APF2.postman_environment.json`. Selecciona el entorno APF2 local y completa `adminPassword` con tu valor local. Ejecuta la colección en orden con Collection Runner. El login guarda el token y las respuestas guardan los IDs de cada registro. La colección incluye pruebas de HTTP y rollback; las contraseñas y tokens no tienen valores exportados. Su ejecución con Newman 6.2.3 completó 22 solicitudes y 24 comprobaciones sin fallos.

## Detener el entorno

Detén Spring Boot con Ctrl+C. Ejecuta `docker compose stop db` para detener PostgreSQL y conservar los datos. Para reanudar: `bash scripts/iniciar.sh`.

## Informe y referencias

El informe técnico del APF2 se encuentra en `docs/informe/`, con portada individual y evidencias reproducibles.

- [Spring Boot 3.5 requisitos](https://docs.spring.io/spring-boot/3.5/system-requirements.html)
- [Spring Data JPA y JPQL](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html)
- [Spring Security](https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/index.html)
- [JJWT](https://github.com/jwtk/jjwt)
