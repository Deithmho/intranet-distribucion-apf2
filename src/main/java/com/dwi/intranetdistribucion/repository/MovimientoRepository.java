package com.dwi.intranetdistribucion.repository;
import com.dwi.intranetdistribucion.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MovimientoRepository extends JpaRepository<Movimiento,Long> {
    boolean existsByProductoId(Long id);
    boolean existsByOrigenIdOrDestinoId(Long origenId, Long destinoId);
}
