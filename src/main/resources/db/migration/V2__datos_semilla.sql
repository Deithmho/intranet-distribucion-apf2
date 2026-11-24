INSERT INTO permisos(nombre,descripcion,modulo) VALUES
('USUARIOS_VER','Consultar usuarios','SEGURIDAD'),('USUARIOS_CREAR','Administrar usuarios','SEGURIDAD'),('ROLES_GESTIONAR','Administrar roles','SEGURIDAD'),('INVENTARIO_VER','Consultar inventario','INVENTARIO'),('INVENTARIO_GESTIONAR','Administrar inventario','INVENTARIO') ON CONFLICT(nombre) DO NOTHING;
INSERT INTO roles(nombre,descripcion) VALUES ('ADMIN','Administración'),('ALMACENERO','Gestión del inventario'),('AUDITOR','Consulta del inventario') ON CONFLICT(nombre) DO NOTHING;
INSERT INTO roles_permisos(rol_id,permiso_id) SELECT r.id,p.id FROM roles r CROSS JOIN permisos p WHERE r.nombre='ADMIN' OR (r.nombre='ALMACENERO' AND p.modulo='INVENTARIO') OR (r.nombre='AUDITOR' AND p.nombre='INVENTARIO_VER') ON CONFLICT DO NOTHING;
-- No credentials or sample users are stored in SQL. Bootstrap reads a private environment.
