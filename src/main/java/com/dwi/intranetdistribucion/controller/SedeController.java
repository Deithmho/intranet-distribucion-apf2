package com.dwi.intranetdistribucion.controller;
import com.dwi.intranetdistribucion.dto.request.CatalogoDTO.SedeRequest;
import com.dwi.intranetdistribucion.dto.response.CatalogoResponse.SedeView;
import com.dwi.intranetdistribucion.service.CatalogoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequiredArgsConstructor @RequestMapping("/api/v1/sedes")
public class SedeController {
    private final CatalogoService service;
    @GetMapping @PreAuthorize("hasAuthority('INVENTARIO_VER')")
    public List<SedeView> listar() { return service.sedes(); }
    @GetMapping("/{id}") @PreAuthorize("hasAuthority('INVENTARIO_VER')")
    public SedeView obtener(@PathVariable Long id) { return service.sede(id); }
    @PostMapping @PreAuthorize("hasAuthority('INVENTARIO_GESTIONAR')")
    public ResponseEntity<SedeView> crear(@Valid @RequestBody SedeRequest r) { return ResponseEntity.status(201).body(service.guardarSede(null,r)); }
    @PutMapping("/{id}") @PreAuthorize("hasAuthority('INVENTARIO_GESTIONAR')")
    public SedeView editar(@PathVariable Long id,@Valid @RequestBody SedeRequest r) { return service.guardarSede(id,r); }
    @DeleteMapping("/{id}") @PreAuthorize("hasAuthority('INVENTARIO_GESTIONAR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { service.eliminarSede(id);return ResponseEntity.noContent().build(); }
}
