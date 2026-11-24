package com.dwi.intranetdistribucion.repository;
import com.dwi.intranetdistribucion.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MovimientoRepository extends JpaRepository<Movimiento,Long> {
    interface Historico {
        Long getId(); Long getProductoId(); Long getOrigenId(); Long getDestinoId(); Integer getCantidad();
        String getTipo(); String getMotivo(); String getResponsable(); java.time.LocalDateTime getFecha();
    }
    @org.springframework.data.jpa.repository.Query(value="select id, producto_id as productoId, origen_id as origenId, destino_id as destinoId, cantidad, tipo, motivo, responsable, fecha from movimientos order by id", nativeQuery=true)
    java.util.List<Historico> historico();
    boolean existsByProductoId(Long id);
    boolean existsByOrigenIdOrDestinoId(Long origenId, Long destinoId);
}
