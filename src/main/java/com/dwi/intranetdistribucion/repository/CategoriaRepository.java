package com.dwi.intranetdistribucion.repository;
import com.dwi.intranetdistribucion.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CategoriaRepository extends JpaRepository<Categoria,Long> {
    
}
