package com.dwi.intranetdistribucion.controller;
import com.dwi.intranetdistribucion.dto.request.CatalogoDTO.CategoriaRequest;
import com.dwi.intranetdistribucion.dto.response.CatalogoResponse.CategoriaView;
import com.dwi.intranetdistribucion.service.CatalogoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequiredArgsConstructor @RequestMapping("/api/v1/categorias")
public class CategoriaController {
    private final CatalogoService service;
    @GetMapping @PreAuthorize("hasAuthority('INVENTARIO_VER')")
    public List<CategoriaView> listar() { return service.categorias(); }
    @GetMapping("/{id}") @PreAuthorize("hasAuthority('INVENTARIO_VER')")
    public CategoriaView obtener(@PathVariable Long id) { return service.categoria(id); }
    @PostMapping @PreAuthorize("hasAuthority('INVENTARIO_GESTIONAR')")
    public ResponseEntity<CategoriaView> crear(@Valid @RequestBody CategoriaRequest r) { return ResponseEntity.status(201).body(service.guardarCategoria(null,r)); }
    @PutMapping("/{id}") @PreAuthorize("hasAuthority('INVENTARIO_GESTIONAR')")
    public CategoriaView editar(@PathVariable Long id,@Valid @RequestBody CategoriaRequest r) { return service.guardarCategoria(id,r); }
    @DeleteMapping("/{id}") @PreAuthorize("hasAuthority('INVENTARIO_GESTIONAR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { service.eliminarCategoria(id);return ResponseEntity.noContent().build(); }
}
