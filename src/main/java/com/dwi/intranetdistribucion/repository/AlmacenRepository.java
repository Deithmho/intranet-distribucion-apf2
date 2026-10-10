package com.dwi.intranetdistribucion.repository;
import com.dwi.intranetdistribucion.model.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AlmacenRepository extends JpaRepository<Almacen,Long> {
    boolean existsBySedeId(Long id);
}
