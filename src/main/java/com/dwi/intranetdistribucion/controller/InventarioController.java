package com.dwi.intranetdistribucion.controller;
import com.dwi.intranetdistribucion.dto.request.CatalogoDTO.*;
import com.dwi.intranetdistribucion.dto.response.CatalogoResponse.*;
import com.dwi.intranetdistribucion.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
@RestController @RequiredArgsConstructor @RequestMapping("/api/v1/inventario")
public class InventarioController {
    private final InventarioService service;
    @GetMapping @PreAuthorize("hasAuthority('INVENTARIO_VER')")
    public List<ExistenciaView> stock(@RequestParam(required=false) Long almacenId,@RequestParam(required=false) Long categoriaId,@RequestParam(defaultValue="false") boolean critico) { return service.consultar(almacenId,categoriaId,critico); }
    @GetMapping("/movimientos") @PreAuthorize("hasAuthority('INVENTARIO_VER')")
    public List<MovimientoView> movimientos() { return service.movimientos(); }
    @PostMapping("/entradas") @PreAuthorize("hasAuthority('INVENTARIO_GESTIONAR')")
    public ResponseEntity<MovimientoView> entrada(@Valid @RequestBody EntradaRequest r,Principal p) { return ResponseEntity.status(201).body(service.entrada(r,p.getName())); }
    @PostMapping("/traslados") @PreAuthorize("hasAuthority('INVENTARIO_GESTIONAR')")
    public ResponseEntity<MovimientoView> traslado(@Valid @RequestBody TrasladoRequest r,Principal p) { return ResponseEntity.status(201).body(service.trasladar(r,p.getName())); }
}
