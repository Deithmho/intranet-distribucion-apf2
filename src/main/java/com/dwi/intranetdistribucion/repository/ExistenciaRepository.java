package com.dwi.intranetdistribucion.repository;
import com.dwi.intranetdistribucion.model.Existencia;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ExistenciaRepository extends JpaRepository<Existencia,Long> {
    boolean existsByProductoId(Long id);
    boolean existsByAlmacenId(Long id);
    java.util.Optional<Existencia> findByProductoIdAndAlmacenId(Long productoId, Long almacenId);
    @org.springframework.data.jpa.repository.Query("select e from Existencia e join fetch e.producto p join fetch p.categoria c join fetch e.almacen a where (:almacenId is null or a.id = :almacenId) and (:categoriaId is null or c.id = :categoriaId) and (:critico = false or e.cantidad <= p.stockMinimo) order by a.codigo, p.codigo")
    java.util.List<Existencia> buscar(@org.springframework.data.repository.query.Param("almacenId") Long almacenId, @org.springframework.data.repository.query.Param("categoriaId") Long categoriaId, @org.springframework.data.repository.query.Param("critico") boolean critico);
}
