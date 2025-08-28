package com.proyecto.restaurante.repository;

import com.proyecto.restaurante.model.Comida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComidaRepository extends JpaRepository<Comida, Long> {
    
    Optional<Comida> findBySlug(String slug);
    
    List<Comida> findByActivoTrue();
    
    List<Comida> findByActivoTrueOrderByNombreAsc();
    
    List<Comida> findByCategoriaAndActivoTrue(String categoria);
    
    @Query("SELECT c FROM Comida c WHERE c.activo = true AND " +
           "(LOWER(c.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :termino, '%')))")
    List<Comida> buscarPorTermino(@Param("termino") String termino);
    
    @Query("SELECT DISTINCT c.categoria FROM Comida c WHERE c.categoria IS NOT NULL AND c.activo = true")
    List<String> findCategorias();
    
    boolean existsBySlug(String slug);
}