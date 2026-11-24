package com.dwi.intranetdistribucion.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="refresh_tokens") @Getter @Setter @NoArgsConstructor
public class RefreshToken {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="token_hash",nullable=false,unique=true,length=64) private String tokenHash;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="usuario_id",nullable=false) private Usuario usuario;
    @Column(nullable=false) private UUID familia;
    @Column(name="expires_at",nullable=false) private Instant expiresAt;
    @Column(nullable=false) private boolean revocado;
}
