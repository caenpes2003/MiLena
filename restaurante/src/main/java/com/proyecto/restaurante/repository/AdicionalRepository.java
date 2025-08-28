package com.proyecto.restaurante.repository;

import com.proyecto.restaurante.model.Adicional;
import com.proyecto.restaurante.model.Comida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdicionalRepository extends JpaRepository<Adicional, Long> {
    
    List<Adicional> findByActivoTrue();
    
    List<Adicional> findByActivoTrueOrderByNombreAsc();
    
    List<Adicional> findByActivoFalseOrderByNombreAsc();
    
    List<Adicional> findByComidaAndActivoTrue(Comida comida);
    
    List<Adicional> findByComidaIdAndActivoTrue(Long comidaId);
    
    @Query("SELECT a FROM Adicional a WHERE a.activo = true AND " +
           "LOWER(a.nombre) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Adicional> buscarPorTermino(@Param("termino") String termino);
    
    long countByComidaIdAndActivoTrue(Long comidaId);
}