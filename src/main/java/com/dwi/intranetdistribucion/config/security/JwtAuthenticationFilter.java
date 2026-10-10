package com.dwi.intranetdistribucion.config.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider tokens;
    private final CustomUserDetailsService users;
    @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        if (header != null) {
            try {
                if (!header.startsWith("Bearer ") || header.length() <= 7) throw new IllegalArgumentException();
                String email = tokens.obtenerUsernameDelJWT(header.substring(7));
                var user = users.loadUserByUsername(email);
                if (!user.isEnabled() || !user.isAccountNonLocked()) throw new IllegalArgumentException();
                var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JwtException | IllegalArgumentException | UsernameNotFoundException ex) {
                SecurityContextHolder.clearContext();
                SecurityResponses.error(res, 401, "Token inválido, expirado o usuario no habilitado");
                return;
            }
        }
        chain.doFilter(req, res);
    }
}
