package com.dwi.intranetdistribucion.dto.response;
import com.dwi.intranetdistribucion.model.*;
public final class CatalogoResponse {
    private CatalogoResponse() {}
    public record SedeView(Long id,String codigo,String nombre,String direccion) { public static SedeView from(Sede x) { return new SedeView(x.getId(),x.getCodigo(),x.getNombre(),x.getDireccion()); } }
    public record AlmacenView(Long id,String codigo,String nombre,Long sedeId) { public static AlmacenView from(Almacen x) { return new AlmacenView(x.getId(),x.getCodigo(),x.getNombre(),x.getSede().getId()); } }
    public record CategoriaView(Long id,String nombre,String descripcion) { public static CategoriaView from(Categoria x) { return new CategoriaView(x.getId(),x.getNombre(),x.getDescripcion()); } }
    public record ProductoView(Long id,String codigo,String nombre,String unidadMedida,Integer stockMinimo,Long categoriaId,String categoria) { public static ProductoView from(Producto x) { return new ProductoView(x.getId(),x.getCodigo(),x.getNombre(),x.getUnidadMedida(),x.getStockMinimo(),x.getCategoria().getId(),x.getCategoria().getNombre()); } }
    public record ExistenciaView(Long id,Long productoId,String codigo,String producto,Long almacenId,String almacen,Integer cantidad,Integer stockMinimo,boolean critico) { public static ExistenciaView from(Existencia x) { return new ExistenciaView(x.getId(),x.getProducto().getId(),x.getProducto().getCodigo(),x.getProducto().getNombre(),x.getAlmacen().getId(),x.getAlmacen().getNombre(),x.getCantidad(),x.getProducto().getStockMinimo(),x.getCantidad()<=x.getProducto().getStockMinimo()); } }
    public record MovimientoView(Long id,Long productoId,Long origenId,Long destinoId,Integer cantidad,String tipo,String motivo,String responsable,java.time.LocalDateTime fecha) { public static MovimientoView from(Movimiento x) { return new MovimientoView(x.getId(),x.getProducto().getId(),x.getOrigen()==null ? null : x.getOrigen().getId(),x.getDestino().getId(),x.getCantidad(),x.getTipo(),x.getMotivo(),x.getResponsable(),x.getFecha()); } }
}
