SELECT table_name FROM information_schema.tables
WHERE table_schema='public' ORDER BY table_name;
SELECT p.codigo, p.nombre, a.codigo AS almacen, e.cantidad, p.stock_minimo
FROM existencias e JOIN productos p ON p.id=e.producto_id
JOIN almacenes a ON a.id=e.almacen_id ORDER BY p.codigo, a.codigo;
SELECT id, tipo, cantidad, origen_id, destino_id, responsable
FROM movimientos ORDER BY id;
SELECT email, es_activo, intentos_fallidos, es_bloqueado FROM usuarios ORDER BY id;
