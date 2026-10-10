# Evidencias de API APF2

Fecha: 2026-10-09T21:31:30.342271-05:00
Entorno: http://localhost:8080
Herramienta: cURL. Contraseñas y tokens omitidos. Datos sintéticos.

## 01 Acceso protegido sin JWT
GET /api/v1/productos
Authorization: sin token
HTTP 401 | esperado 401 | OK
```json
{
  "status": 401,
  "mensaje": "Se requiere un token válido"
}
```

## 02 Inicio de sesión y generación JWT
POST /api/v1/auth/login
Authorization: sin token
HTTP 200 | esperado 200 | OK
```json
{
  "accessToken": "[OMITIDO]",
  "tokenType": "Bearer"
}
```

## 03 Mismo endpoint con JWT válido
GET /api/v1/productos
Authorization: Bearer [OMITIDO]
HTTP 200 | esperado 200 | OK
```json
[]
```

## 04 JWT malformado
GET /api/v1/productos
Authorization: Bearer [OMITIDO]
HTTP 401 | esperado 401 | OK
```json
{
  "status": 401,
  "mensaje": "Token inválido, expirado o usuario no habilitado"
}
```

## 05 Firma JWT alterada
GET /api/v1/productos
Authorization: Bearer [OMITIDO]
HTTP 401 | esperado 401 | OK
```json
{
  "status": 401,
  "mensaje": "Token inválido, expirado o usuario no habilitado"
}
```

## 06 Crear categoría
POST /api/v1/categorias
Authorization: Bearer [OMITIDO]
HTTP 201 | esperado 201 | OK
```json
{
  "id": 1,
  "nombre": "Bebidas-317601",
  "descripcion": "Datos sintu00e9ticos APF2"
}
```

## 07 Crear sede
POST /api/v1/sedes
Authorization: Bearer [OMITIDO]
HTTP 201 | esperado 201 | OK
```json
{
  "id": 1,
  "codigo": "S-317601",
  "nombre": "Sede acadu00e9mica Lima",
  "direccion": "Direcciu00f3n de demostraciu00f3n"
}
```

## 08 Crear almacén origen
POST /api/v1/almacenes
Authorization: Bearer [OMITIDO]
HTTP 201 | esperado 201 | OK
```json
{
  "id": 1,
  "codigo": "A-317601",
  "nombre": "Almacu00e9n origen",
  "sedeId": 1
}
```

## 09 Crear almacén destino
POST /api/v1/almacenes
Authorization: Bearer [OMITIDO]
HTTP 201 | esperado 201 | OK
```json
{
  "id": 2,
  "codigo": "B-317601",
  "nombre": "Almacu00e9n destino",
  "sedeId": 1
}
```

## 10 Crear producto
POST /api/v1/productos
Authorization: Bearer [OMITIDO]
HTTP 201 | esperado 201 | OK
```json
{
  "id": 1,
  "codigo": "P-317601",
  "nombre": "Producto demostrativo",
  "unidadMedida": "caja",
  "stockMinimo": 10,
  "categoriaId": 1,
  "categoria": "Bebidas-317601"
}
```

## 11 Consultar producto
GET /api/v1/productos/1
Authorization: Bearer [OMITIDO]
HTTP 200 | esperado 200 | OK
```json
{
  "id": 1,
  "codigo": "P-317601",
  "nombre": "Producto demostrativo",
  "unidadMedida": "caja",
  "stockMinimo": 10,
  "categoriaId": 1,
  "categoria": "Bebidas-317601"
}
```

## 12 Actualizar producto
PUT /api/v1/productos/1
Authorization: Bearer [OMITIDO]
HTTP 200 | esperado 200 | OK
```json
{
  "id": 1,
  "codigo": "P-317601",
  "nombre": "Producto actualizado",
  "unidadMedida": "caja",
  "stockMinimo": 10,
  "categoriaId": 1,
  "categoria": "Bebidas-317601"
}
```

## 13 Búsqueda JPQL por categoría y nombre
GET /api/v1/productos?categoriaId=1&texto=actualizado
Authorization: Bearer [OMITIDO]
HTTP 200 | esperado 200 | OK
```json
[
  {
    "id": 1,
    "codigo": "P-317601",
    "nombre": "Producto actualizado",
    "unidadMedida": "caja",
    "stockMinimo": 10,
    "categoriaId": 1,
    "categoria": "Bebidas-317601"
  }
]
```

## 14 Rechazar categoría referenciada
DELETE /api/v1/categorias/1
Authorization: Bearer [OMITIDO]
HTTP 409 | esperado 409 | OK
```json
{
  "timestamp": "2026-10-09T21:31:29.443276",
  "mensaje": "La operación contradice una regla de integridad o de negocio",
  "detalles": "CONFLICT"
}
```

## 15 Rechazar código duplicado
POST /api/v1/productos
Authorization: Bearer [OMITIDO]
HTTP 409 | esperado 409 | OK
```json
{
  "timestamp": "2026-10-09T21:31:29.477744",
  "mensaje": "La operación contradice una regla de integridad o de negocio",
  "detalles": "CONFLICT"
}
```

## 16 Rechazar datos inválidos
POST /api/v1/productos
Authorization: Bearer [OMITIDO]
HTTP 400 | esperado 400 | OK
```json
{
  "timestamp": "2026-10-09T21:31:29.510555",
  "mensaje": "Datos de entrada no válidos",
  "detalles": "BAD_REQUEST"
}
```

## 17 Entrada de existencias
POST /api/v1/inventario/entradas
Authorization: Bearer [OMITIDO]
HTTP 201 | esperado 201 | OK
```json
{
  "id": 1,
  "productoId": 1,
  "origenId": null,
  "destinoId": 1,
  "cantidad": 20,
  "tipo": "ENTRADA",
  "motivo": "Ingreso acadu00e9mico",
  "responsable": "admin@example.test",
  "fecha": "2026-10-09T21:31:29.556444"
}
```

## 18 Traslado atómico entre almacenes
POST /api/v1/inventario/traslados
Authorization: Bearer [OMITIDO]
HTTP 201 | esperado 201 | OK
```json
{
  "id": 2,
  "productoId": 1,
  "origenId": 1,
  "destinoId": 2,
  "cantidad": 7,
  "tipo": "TRASLADO",
  "motivo": "Despacho acadu00e9mico",
  "responsable": "admin@example.test",
  "fecha": "2026-10-09T21:31:29.609046"
}
```

## 19 Consultar stock origen después del traslado
GET /api/v1/inventario?almacenId=1
Authorization: Bearer [OMITIDO]
HTTP 200 | esperado 200 | OK
```json
[
  {
    "id": 1,
    "productoId": 1,
    "codigo": "P-317601",
    "producto": "Producto actualizado",
    "almacenId": 1,
    "almacen": "Almacu00e9n origen",
    "cantidad": 13,
    "stockMinimo": 10,
    "critico": false
  }
]
```

## 20 Consulta JPQL de stock crítico
GET /api/v1/inventario?almacenId=2&categoriaId=1&critico=true
Authorization: Bearer [OMITIDO]
HTTP 200 | esperado 200 | OK
```json
[
  {
    "id": 2,
    "productoId": 1,
    "codigo": "P-317601",
    "producto": "Producto actualizado",
    "almacenId": 2,
    "almacen": "Almacu00e9n destino",
    "cantidad": 7,
    "stockMinimo": 10,
    "critico": true
  }
]
```

## 21 Rechazar traslado por stock insuficiente
POST /api/v1/inventario/traslados
Authorization: Bearer [OMITIDO]
HTTP 409 | esperado 409 | OK
```json
{
  "timestamp": "2026-10-09T21:31:29.714874",
  "mensaje": "La operación contradice una regla de integridad o de negocio",
  "detalles": "CONFLICT"
}
```

## 22 Rollback con destino inexistente
POST /api/v1/inventario/traslados
Authorization: Bearer [OMITIDO]
HTTP 404 | esperado 404 | OK
```json
{
  "timestamp": "2026-10-09T21:31:29.750856",
  "mensaje": "Almacén no encontrado",
  "detalles": "NOT_FOUND"
}
```

## 23 Stock conservado tras la reversión
GET /api/v1/inventario?almacenId=1
Authorization: Bearer [OMITIDO]
HTTP 200 | esperado 200 | OK
```json
[
  {
    "id": 1,
    "productoId": 1,
    "codigo": "P-317601",
    "producto": "Producto actualizado",
    "almacenId": 1,
    "almacen": "Almacu00e9n origen",
    "cantidad": 13,
    "stockMinimo": 10,
    "critico": false
  }
]
```

## 24 Historial de movimientos
GET /api/v1/inventario/movimientos
Authorization: Bearer [OMITIDO]
HTTP 200 | esperado 200 | OK
```json
[
  {
    "id": 1,
    "productoId": 1,
    "origenId": null,
    "destinoId": 1,
    "cantidad": 20,
    "tipo": "ENTRADA",
    "motivo": "Ingreso acadu00e9mico",
    "responsable": "admin@example.test",
    "fecha": "2026-10-09T21:31:29.556444"
  },
  {
    "id": 2,
    "productoId": 1,
    "origenId": 1,
    "destinoId": 2,
    "cantidad": 7,
    "tipo": "TRASLADO",
    "motivo": "Despacho acadu00e9mico",
    "responsable": "admin@example.test",
    "fecha": "2026-10-09T21:31:29.609046"
  }
]
```

## 25 Roles y permisos
GET /api/v1/roles
Authorization: Bearer [OMITIDO]
HTTP 200 | esperado 200 | OK
```json
[
  {
    "id": 1,
    "nombre": "ADMIN",
    "descripcion": "Rol base ADMIN",
    "permisos": [
      {
        "id": 5,
        "nombre": "INVENTARIO_GESTIONAR",
        "descripcion": "INVENTARIO GESTIONAR",
        "modulo": "INVENTARIO"
      },
      {
        "id": 2,
        "nombre": "USUARIOS_CREAR",
        "descripcion": "USUARIOS CREAR",
        "modulo": "SEGURIDAD"
      },
      {
        "id": 1,
        "nombre": "USUARIOS_VER",
        "descripcion": "USUARIOS VER",
        "modulo": "SEGURIDAD"
      },
      {
        "id": 3,
        "nombre": "ROLES_GESTIONAR",
        "descripcion": "ROLES GESTIONAR",
        "modulo": "SEGURIDAD"
      },
      {
        "id": 4,
        "nombre": "INVENTARIO_VER",
        "descripcion": "INVENTARIO VER",
        "modulo": "INVENTARIO"
      }
    ]
  },
  {
    "id": 2,
    "nombre": "ALMACENERO",
    "descripcion": "Rol base ALMACENERO",
    "permisos": [
      {
        "id": 5,
        "nombre": "INVENTARIO_GESTIONAR",
        "descripcion": "INVENTARIO GESTIONAR",
        "modulo": "INVENTARIO"
      },
      {
        "id": 4,
        "nombre": "INVENTARIO_VER",
        "descripcion": "INVENTARIO VER",
        "modulo": "INVENTARIO"
      }
    ]
  },
  {
    "id": 3,
    "nombre": "AUDITOR",
    "descripcion": "Rol base AUDITOR",
    "permisos": [
      {
        "id": 4,
        "nombre": "INVENTARIO_VER",
        "descripcion": "INVENTARIO VER",
        "modulo": "INVENTARIO"
      }
    ]
  }
]
```

## 26 Administrador crea auditor
POST /api/v1/usuarios
Authorization: Bearer [OMITIDO]
HTTP 201 | esperado 201 | OK
```json
{
  "id": 2,
  "rolId": 3,
  "rolNombre": "AUDITOR",
  "nombre": "Auditor",
  "apellido": "Acadu00e9mico",
  "email": "auditor-317601@example.test",
  "esActivo": true,
  "fechaCreacion": "2026-10-09T21:31:29.981237"
}
```

## 27 Inicio de sesión del auditor
POST /api/v1/auth/login
Authorization: sin token
HTTP 200 | esperado 200 | OK
```json
{
  "accessToken": "[OMITIDO]",
  "tokenType": "Bearer"
}
```

## 28 Auditor consulta inventario
GET /api/v1/inventario
Authorization: Bearer [OMITIDO]
HTTP 200 | esperado 200 | OK
```json
[
  {
    "id": 1,
    "productoId": 1,
    "codigo": "P-317601",
    "producto": "Producto actualizado",
    "almacenId": 1,
    "almacen": "Almacu00e9n origen",
    "cantidad": 13,
    "stockMinimo": 10,
    "critico": false
  },
  {
    "id": 2,
    "productoId": 1,
    "codigo": "P-317601",
    "producto": "Producto actualizado",
    "almacenId": 2,
    "almacen": "Almacu00e9n destino",
    "cantidad": 7,
    "stockMinimo": 10,
    "critico": true
  }
]
```

## 29 Auditor no puede modificar catálogo
POST /api/v1/categorias
Authorization: Bearer [OMITIDO]
HTTP 403 | esperado 403 | OK
```json
{
  "timestamp": "2026-10-09T21:31:30.176418",
  "mensaje": "Permiso insuficiente",
  "detalles": "FORBIDDEN"
}
```

## 30 Auditor no puede administrar usuarios
GET /api/v1/usuarios
Authorization: Bearer [OMITIDO]
HTTP 403 | esperado 403 | OK
```json
{
  "timestamp": "2026-10-09T21:31:30.194209",
  "mensaje": "Permiso insuficiente",
  "detalles": "FORBIDDEN"
}
```

## 31 Administrador elimina usuario de prueba
DELETE /api/v1/usuarios/2
Authorization: Bearer [OMITIDO]
HTTP 204 | esperado 204 | OK
```json
""
```

## 32 Token de usuario eliminado es rechazado
GET /api/v1/productos
Authorization: Bearer [OMITIDO]
HTTP 401 | esperado 401 | OK
```json
{
  "status": 401,
  "mensaje": "Token inválido, expirado o usuario no habilitado"
}
```

## 33 Crear producto para comprobar eliminación
POST /api/v1/productos
Authorization: Bearer [OMITIDO]
HTTP 201 | esperado 201 | OK
```json
{
  "id": 3,
  "codigo": "T-317601",
  "nombre": "Producto actualizado",
  "unidadMedida": "caja",
  "stockMinimo": 10,
  "categoriaId": 1,
  "categoria": "Bebidas-317601"
}
```

## 34 Eliminar producto sin referencias
DELETE /api/v1/productos/3
Authorization: Bearer [OMITIDO]
HTTP 204 | esperado 204 | OK
```json
""
```

## 35 Registro eliminado retorna 404
GET /api/v1/productos/3
Authorization: Bearer [OMITIDO]
HTTP 404 | esperado 404 | OK
```json
{
  "timestamp": "2026-10-09T21:31:30.315014",
  "mensaje": "Registro no encontrado: 3",
  "detalles": "NOT_FOUND"
}
```

## 36 Rechazar eliminación de producto con historial
DELETE /api/v1/productos/1
Authorization: Bearer [OMITIDO]
HTTP 409 | esperado 409 | OK
```json
{
  "timestamp": "2026-10-09T21:31:30.340896",
  "mensaje": "La operación contradice una regla de integridad o de negocio",
  "detalles": "CONFLICT"
}
```
