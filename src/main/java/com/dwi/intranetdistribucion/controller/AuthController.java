package com.dwi.intranetdistribucion.controller;

import com.dwi.intranetdistribucion.dto.request.LoginRequestDTO;
import com.dwi.intranetdistribucion.dto.request.UsuarioRequestDTO;
import com.dwi.intranetdistribucion.dto.response.JwtAuthResponseDTO;
import com.dwi.intranetdistribucion.dto.response.UsuarioResponseDTO;
import com.dwi.intranetdistribucion.service.AuthService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        JwtAuthResponseDTO response = authService.login(loginRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('USUARIOS_CREAR')")
    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDTO> registrar(@Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        UsuarioResponseDTO response = authService.registrar(usuarioRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
