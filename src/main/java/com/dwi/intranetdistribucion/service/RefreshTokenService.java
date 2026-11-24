package com.dwi.intranetdistribucion.service;
import com.dwi.intranetdistribucion.model.*;
import com.dwi.intranetdistribucion.repository.*;
import com.dwi.intranetdistribucion.dto.response.JwtAuthResponseDTO;
import com.dwi.intranetdistribucion.config.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.*;
import java.time.Instant;
import java.util.*;
@Service @RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refresh;
    private final UsuarioRepository usuarios;
    private final JwtTokenProvider jwt;
    private final UserDetailsService details;
    @jakarta.persistence.PersistenceContext private jakarta.persistence.EntityManager entityManager;
    private final SecureRandom random = new SecureRandom();
    @Value("${app.refresh.expiration-seconds}") private long lifetime;
    @Value("${app.jwt.expiration-milliseconds}") private long accessLifetime;
    public static String hash(String raw) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8))); }
        catch (NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
    }
    @Transactional
    public JwtAuthResponseDTO emitir(Usuario u, Authentication auth) { return emitir(u,auth,UUID.randomUUID(),Instant.now().plusSeconds(lifetime)); }
    private JwtAuthResponseDTO emitir(Usuario u,Authentication auth,UUID familia,Instant expires) {
        byte[] bytes=new byte[32];random.nextBytes(bytes);
        String raw=Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        var t=new RefreshToken();t.setUsuario(u);t.setFamilia(familia);t.setExpiresAt(expires);t.setTokenHash(hash(raw));refresh.save(t);
        return JwtAuthResponseDTO.builder().accessToken(jwt.generarToken(auth)).refreshToken(raw).expiresIn(accessLifetime/1000).tokenType("Bearer").build();
    }
    private RefreshToken bloquear(String raw) {
        var t=refresh.findByTokenHash(hash(raw)).orElseThrow(() -> new BadCredentialsException("Refresh token no válido"));
        // Serializes rotations and replay detection for the same account.
        var u=usuarios.buscarParaLogin(t.getUsuario().getEmail()).orElseThrow(() -> new BadCredentialsException("Sesión no vigente"));
        entityManager.refresh(u);
        refresh.flush();
        return t;
    }
    @Transactional(noRollbackFor=BadCredentialsException.class)
    public JwtAuthResponseDTO renovar(String raw) {
        var t=bloquear(raw);
        // Refresh state again after acquiring the account lock (concurrent refresh).
        entityManager.refresh(t);
        var u=t.getUsuario();
        if(t.isRevocado() || !t.getExpiresAt().isAfter(Instant.now()) || !Boolean.TRUE.equals(u.getEsActivo()) || Boolean.TRUE.equals(u.getEsBloqueado())) {
            refresh.revocarFamilia(t.getFamilia());throw new BadCredentialsException("Refresh token no vigente");
        }
        var user=details.loadUserByUsername(u.getEmail());
        var auth=new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
        t.setRevocado(true);refresh.saveAndFlush(t);
        return emitir(u,auth,t.getFamilia(),t.getExpiresAt());
    }
    @Transactional
    public void cerrar(String raw) {
        var t=bloquear(raw);refresh.revocarFamilia(t.getFamilia());
    }
}
