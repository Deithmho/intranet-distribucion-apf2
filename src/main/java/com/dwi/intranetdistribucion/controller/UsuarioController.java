package com.dwi.intranetdistribucion.controller;

import com.dwi.intranetdistribucion.dto.request.UsuarioRequestDTO;
import com.dwi.intranetdistribucion.dto.response.UsuarioResponseDTO;
import com.dwi.intranetdistribucion.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PreAuthorize("hasAuthority('USUARIOS_VER')")
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    @PreAuthorize("hasAuthority('USUARIOS_VER')")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PreAuthorize("hasAuthority('USUARIOS_CREAR')")
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        return new ResponseEntity<>(usuarioService.crear(usuarioRequestDTO), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('USUARIOS_CREAR')")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        return ResponseEntity.ok(usuarioService.actualizar(id, usuarioRequestDTO));
    }

    @PreAuthorize("hasAuthority('USUARIOS_CREAR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
