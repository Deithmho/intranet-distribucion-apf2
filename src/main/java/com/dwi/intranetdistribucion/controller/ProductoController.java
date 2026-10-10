package com.dwi.intranetdistribucion.controller;
import com.dwi.intranetdistribucion.dto.request.CatalogoDTO.ProductoRequest;
import com.dwi.intranetdistribucion.dto.response.CatalogoResponse.ProductoView;
import com.dwi.intranetdistribucion.service.CatalogoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequiredArgsConstructor @RequestMapping("/api/v1/productos")
public class ProductoController {
    private final CatalogoService service;
    @GetMapping @PreAuthorize("hasAuthority('INVENTARIO_VER')")
    public List<ProductoView> listar(@RequestParam(required=false) Long categoriaId, @RequestParam(defaultValue="") String texto) { return service.productos(categoriaId,texto); }
    @GetMapping("/{id}") @PreAuthorize("hasAuthority('INVENTARIO_VER')")
    public ProductoView obtener(@PathVariable Long id) { return service.producto(id); }
    @PostMapping @PreAuthorize("hasAuthority('INVENTARIO_GESTIONAR')")
    public ResponseEntity<ProductoView> crear(@Valid @RequestBody ProductoRequest r) { return ResponseEntity.status(201).body(service.guardarProducto(null,r)); }
    @PutMapping("/{id}") @PreAuthorize("hasAuthority('INVENTARIO_GESTIONAR')")
    public ProductoView editar(@PathVariable Long id,@Valid @RequestBody ProductoRequest r) { return service.guardarProducto(id,r); }
    @DeleteMapping("/{id}") @PreAuthorize("hasAuthority('INVENTARIO_GESTIONAR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { service.eliminarProducto(id);return ResponseEntity.noContent().build(); }
}
