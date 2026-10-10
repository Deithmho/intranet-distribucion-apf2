package com.dwi.intranetdistribucion.service.impl;
import com.dwi.intranetdistribucion.config.security.JwtTokenProvider;
import com.dwi.intranetdistribucion.dto.request.*;
import com.dwi.intranetdistribucion.dto.response.*;
import com.dwi.intranetdistribucion.repository.UsuarioRepository;
import com.dwi.intranetdistribucion.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service @RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager manager;
    private final UsuarioRepository usuarios;
    private final JwtTokenProvider tokens;
    private final UsuarioService userService;
    @Transactional(noRollbackFor={BadCredentialsException.class, LockedException.class})
    public JwtAuthResponseDTO login(LoginRequestDTO r) {
        String email=r.getEmail().trim().toLowerCase(java.util.Locale.ROOT);
        var u=usuarios.buscarParaLogin(email).orElseThrow(() -> new BadCredentialsException("Credenciales incorrectas"));
        if (Boolean.TRUE.equals(u.getEsBloqueado())) throw new LockedException("Cuenta bloqueada");
        try {
            var auth=manager.authenticate(new UsernamePasswordAuthenticationToken(email,r.getPassword()));
            u.setIntentosFallidos(0);
            return JwtAuthResponseDTO.builder().accessToken(tokens.generarToken(auth)).tokenType("Bearer").build();
        } catch (BadCredentialsException ex) {
            int count=u.getIntentosFallidos()+1;u.setIntentosFallidos(count);
            if(count>=5) {u.setEsBloqueado(true);throw new LockedException("Cuenta bloqueada tras cinco intentos fallidos");}
            throw new BadCredentialsException("Credenciales incorrectas");
        }
    }
    @Transactional public UsuarioResponseDTO registrar(UsuarioRequestDTO r) { return userService.crear(r); }
}
