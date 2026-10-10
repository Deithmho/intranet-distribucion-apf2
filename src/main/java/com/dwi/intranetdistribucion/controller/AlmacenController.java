package com.dwi.intranetdistribucion.controller;
import com.dwi.intranetdistribucion.dto.request.CatalogoDTO.AlmacenRequest;
import com.dwi.intranetdistribucion.dto.response.CatalogoResponse.AlmacenView;
import com.dwi.intranetdistribucion.service.CatalogoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequiredArgsConstructor @RequestMapping("/api/v1/almacenes")
public class AlmacenController {
    private final CatalogoService service;
    @GetMapping @PreAuthorize("hasAuthority('INVENTARIO_VER')")
    public List<AlmacenView> listar() { return service.almacenes(); }
    @GetMapping("/{id}") @PreAuthorize("hasAuthority('INVENTARIO_VER')")
    public AlmacenView obtener(@PathVariable Long id) { return service.almacen(id); }
    @PostMapping @PreAuthorize("hasAuthority('INVENTARIO_GESTIONAR')")
    public ResponseEntity<AlmacenView> crear(@Valid @RequestBody AlmacenRequest r) { return ResponseEntity.status(201).body(service.guardarAlmacen(null,r)); }
    @PutMapping("/{id}") @PreAuthorize("hasAuthority('INVENTARIO_GESTIONAR')")
    public AlmacenView editar(@PathVariable Long id,@Valid @RequestBody AlmacenRequest r) { return service.guardarAlmacen(id,r); }
    @DeleteMapping("/{id}") @PreAuthorize("hasAuthority('INVENTARIO_GESTIONAR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { service.eliminarAlmacen(id);return ResponseEntity.noContent().build(); }
}
