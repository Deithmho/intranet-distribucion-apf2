package com.dwi.intranetdistribucion.service.impl;
import com.dwi.intranetdistribucion.dto.request.UsuarioRequestDTO;
import com.dwi.intranetdistribucion.dto.response.UsuarioResponseDTO;
import com.dwi.intranetdistribucion.exception.ResourceNotFoundException;
import com.dwi.intranetdistribucion.model.Usuario;
import com.dwi.intranetdistribucion.repository.*;
import com.dwi.intranetdistribucion.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarios;
    private final RolRepository roles;
    private final PasswordEncoder encoder;
    public List<UsuarioResponseDTO> obtenerTodos() { return usuarios.findAll().stream().map(this::dto).toList(); }
    public UsuarioResponseDTO obtenerPorId(Long id) { return dto(buscar(id)); }
    @Transactional public UsuarioResponseDTO crear(UsuarioRequestDTO r) {
        var user = new Usuario(); aplicar(user,r); return dto(usuarios.save(user));
    }
    @Transactional public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO r) {
        var user=buscar(id); aplicar(user,r); return dto(usuarios.save(user));
    }
    @Transactional public void eliminar(Long id) { usuarios.delete(buscar(id)); }
    private Usuario buscar(Long id) { return usuarios.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado")); }
    private void aplicar(Usuario u, UsuarioRequestDTO r) {
        String email=r.getEmail().trim().toLowerCase(java.util.Locale.ROOT);
        usuarios.findByEmail(email).ifPresent(v -> { if (!v.getId().equals(u.getId())) throw new IllegalStateException("Correo duplicado"); });
        u.setNombre(r.getNombre().trim());u.setApellido(r.getApellido().trim());u.setEmail(email);
        u.setPassword(encoder.encode(r.getPassword()));
        u.setRol(roles.findById(r.getRolId()).orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado")));
        u.setEsActivo(r.getEsActivo()==null || r.getEsActivo());
    }
    private UsuarioResponseDTO dto(Usuario u) {
        return UsuarioResponseDTO.builder().id(u.getId()).nombre(u.getNombre()).apellido(u.getApellido()).email(u.getEmail())
            .esActivo(u.getEsActivo()).fechaCreacion(u.getFechaCreacion()).rolId(u.getRol().getId()).rolNombre(u.getRol().getNombre()).build();
    }
}
