package com.dwi.intranetdistribucion.service;
import com.dwi.intranetdistribucion.dto.request.CatalogoDTO.*;
import com.dwi.intranetdistribucion.dto.response.CatalogoResponse.*;
import java.util.List;
public interface CatalogoService {
    List<SedeView> sedes(); SedeView sede(Long id); SedeView guardarSede(Long id,SedeRequest r); void eliminarSede(Long id);
    List<AlmacenView> almacenes(); AlmacenView almacen(Long id); AlmacenView guardarAlmacen(Long id,AlmacenRequest r); void eliminarAlmacen(Long id);
    List<CategoriaView> categorias(); CategoriaView categoria(Long id); CategoriaView guardarCategoria(Long id,CategoriaRequest r); void eliminarCategoria(Long id);
    List<ProductoView> productos(Long categoriaId,String texto); ProductoView producto(Long id); ProductoView guardarProducto(Long id,ProductoRequest r); void eliminarProducto(Long id);
}
