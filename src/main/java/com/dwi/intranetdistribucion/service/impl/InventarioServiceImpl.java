package com.dwi.intranetdistribucion.service.impl;
import com.dwi.intranetdistribucion.dto.request.CatalogoDTO.*;
import com.dwi.intranetdistribucion.dto.response.CatalogoResponse.*;
import com.dwi.intranetdistribucion.model.*;
import com.dwi.intranetdistribucion.repository.*;
import com.dwi.intranetdistribucion.service.InventarioService;
import com.dwi.intranetdistribucion.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class InventarioServiceImpl implements InventarioService {
    private final ProductoRepository productos;
    private final AlmacenRepository almacenes;
    private final ExistenciaRepository existencias;
    private final MovimientoRepository movimientos;
    public List<ExistenciaView> consultar(Long almacenId,Long categoriaId,boolean critico) { return existencias.buscar(almacenId,categoriaId,critico).stream().map(ExistenciaView::from).toList(); }
    public List<MovimientoView> movimientos() { return movimientos.findAll().stream().map(MovimientoView::from).toList(); }
    @Transactional public MovimientoView entrada(EntradaRequest r,String responsable) {
        Producto producto=bloquear(r.productoId());Almacen destino=almacen(r.almacenId());
        Existencia stock=stock(producto,destino);stock.setCantidad(Math.addExact(stock.getCantidad(),r.cantidad()));existencias.save(stock);
        return registrar(producto,null,destino,r.cantidad(),"ENTRADA",r.motivo(),responsable);
    }
    @Transactional public MovimientoView trasladar(TrasladoRequest r,String responsable) {
        if(r.origenId().equals(r.destinoId())) throw new IllegalArgumentException("Almacenes distintos requeridos");
        // Serializa movimientos de un mismo producto y evita crear existencias duplicadas.
        Producto producto=bloquear(r.productoId());Almacen origen=almacen(r.origenId());
        Existencia salida=stock(producto,origen);
        if(salida.getCantidad()<r.cantidad()) throw new IllegalStateException("Stock insuficiente");
        salida.setCantidad(salida.getCantidad()-r.cantidad());
        existencias.saveAndFlush(salida);
        // Si el destino no existe, la transacción revierte incluso la salida ya enviada a PostgreSQL.
        Almacen destino=almacen(r.destinoId());Existencia llegada=stock(producto,destino);
        llegada.setCantidad(Math.addExact(llegada.getCantidad(),r.cantidad()));existencias.save(llegada);
        return registrar(producto,origen,destino,r.cantidad(),"TRASLADO",r.motivo(),responsable);
    }
    private Producto bloquear(Long id) { return productos.bloquear(id).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado")); }
    private Almacen almacen(Long id) { return almacenes.findById(id).orElseThrow(() -> new ResourceNotFoundException("Almacén no encontrado")); }
    private Existencia stock(Producto p,Almacen a) { return existencias.findByProductoIdAndAlmacenId(p.getId(),a.getId()).orElseGet(() -> {var x=new Existencia();x.setProducto(p);x.setAlmacen(a);return x;}); }
    private MovimientoView registrar(Producto p,Almacen o,Almacen d,int cantidad,String tipo,String motivo,String responsable) {
        var x=new Movimiento();x.setProducto(p);x.setOrigen(o);x.setDestino(d);x.setCantidad(cantidad);x.setTipo(tipo);x.setMotivo(motivo);x.setResponsable(responsable);
        return MovimientoView.from(movimientos.saveAndFlush(x));
    }
}
