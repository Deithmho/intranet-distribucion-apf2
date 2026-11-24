package com.dwi.intranetdistribucion.repository;
import com.dwi.intranetdistribucion.model.RefreshToken;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    @Modifying @Query("update RefreshToken t set t.revocado=true where t.familia=:familia")
    int revocarFamilia(@Param("familia") UUID familia);
}
