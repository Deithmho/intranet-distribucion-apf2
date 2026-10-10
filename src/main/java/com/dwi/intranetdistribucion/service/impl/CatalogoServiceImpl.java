package com.dwi.intranetdistribucion.service.impl;
import com.dwi.intranetdistribucion.dto.request.CatalogoDTO.*;
import com.dwi.intranetdistribucion.dto.response.CatalogoResponse.*;
import com.dwi.intranetdistribucion.model.*;
import com.dwi.intranetdistribucion.repository.*;
import com.dwi.intranetdistribucion.service.CatalogoService;
import com.dwi.intranetdistribucion.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class CatalogoServiceImpl implements CatalogoService {
    private final SedeRepository sedes;
    private final AlmacenRepository almacenes;
    private final CategoriaRepository categorias;
    private final ProductoRepository productos;
    private final ExistenciaRepository existencias;
    private final MovimientoRepository movimientos;
    private <T> T obtener(JpaRepository<T,Long> repo,Long id) { return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Registro no encontrado: "+id)); }
    public List<SedeView> sedes() { return sedes.findAll().stream().map(SedeView::from).toList(); }
    public SedeView sede(Long id) { return SedeView.from(obtener(sedes,id)); }
    @Transactional public SedeView guardarSede(Long id,SedeRequest r) { var x=id==null ? new Sede() : obtener(sedes,id);x.setCodigo(r.codigo().trim().toUpperCase());x.setNombre(r.nombre().trim());x.setDireccion(r.direccion().trim());return SedeView.from(sedes.saveAndFlush(x)); }
    @Transactional public void eliminarSede(Long id) { var x=obtener(sedes,id);if(almacenes.existsBySedeId(id))throw new IllegalStateException("Sede con almacenes");sedes.delete(x); }
    public List<AlmacenView> almacenes() { return almacenes.findAll().stream().map(AlmacenView::from).toList(); }
    public AlmacenView almacen(Long id) { return AlmacenView.from(obtener(almacenes,id)); }
    @Transactional public AlmacenView guardarAlmacen(Long id,AlmacenRequest r) { var x=id==null ? new Almacen() : obtener(almacenes,id);x.setCodigo(r.codigo().trim().toUpperCase());x.setNombre(r.nombre().trim());x.setSede(obtener(sedes,r.sedeId()));return AlmacenView.from(almacenes.saveAndFlush(x)); }
    @Transactional public void eliminarAlmacen(Long id) { var x=obtener(almacenes,id);if(existencias.existsByAlmacenId(id)||movimientos.existsByOrigenIdOrDestinoId(id,id))throw new IllegalStateException("Almacén con inventario o historial");almacenes.delete(x); }
    public List<CategoriaView> categorias() { return categorias.findAll().stream().map(CategoriaView::from).toList(); }
    public CategoriaView categoria(Long id) { return CategoriaView.from(obtener(categorias,id)); }
    @Transactional public CategoriaView guardarCategoria(Long id,CategoriaRequest r) { var x=id==null ? new Categoria() : obtener(categorias,id);x.setNombre(r.nombre().trim());x.setDescripcion(r.descripcion());return CategoriaView.from(categorias.saveAndFlush(x)); }
    @Transactional public void eliminarCategoria(Long id) { var x=obtener(categorias,id);if(productos.existsByCategoriaId(id))throw new IllegalStateException("Categoría con productos");categorias.delete(x); }
    public List<ProductoView> productos(Long categoriaId,String texto) { return productos.buscar(categoriaId,texto).stream().map(ProductoView::from).toList(); }
    public ProductoView producto(Long id) { return ProductoView.from(obtener(productos,id)); }
    @Transactional public ProductoView guardarProducto(Long id,ProductoRequest r) { var x=id==null ? new Producto() : obtener(productos,id);x.setCodigo(r.codigo().trim().toUpperCase());x.setNombre(r.nombre().trim());x.setUnidadMedida(r.unidadMedida().trim());x.setStockMinimo(r.stockMinimo());x.setCategoria(obtener(categorias,r.categoriaId()));return ProductoView.from(productos.saveAndFlush(x)); }
    @Transactional public void eliminarProducto(Long id) { var x=obtener(productos,id);if(existencias.existsByProductoId(id)||movimientos.existsByProductoId(id))throw new IllegalStateException("Producto con inventario o historial");productos.delete(x); }
}
