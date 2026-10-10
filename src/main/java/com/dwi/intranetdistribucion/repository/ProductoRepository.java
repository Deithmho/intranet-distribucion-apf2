package com.dwi.intranetdistribucion.repository;
import com.dwi.intranetdistribucion.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductoRepository extends JpaRepository<Producto,Long> {
    boolean existsByCategoriaId(Long id);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select p from Producto p where p.id = :id")
    java.util.Optional<Producto> bloquear(@org.springframework.data.repository.query.Param("id") Long id);
    @org.springframework.data.jpa.repository.Query("select p from Producto p join fetch p.categoria c where (:categoriaId is null or c.id = :categoriaId) and (lower(p.nombre) like lower(concat('%', :texto, '%')) or lower(p.codigo) like lower(concat('%', :texto, '%'))) order by p.codigo")
    java.util.List<Producto> buscar(@org.springframework.data.repository.query.Param("categoriaId") Long categoriaId, @org.springframework.data.repository.query.Param("texto") String texto);
}
