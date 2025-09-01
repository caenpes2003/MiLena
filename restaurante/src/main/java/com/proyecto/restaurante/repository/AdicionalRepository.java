package com.proyecto.restaurante.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proyecto.restaurante.model.Adicional;
import com.proyecto.restaurante.model.Comida;

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
    
    // Obtener adicionales que NO están asignados a ninguna comida
    List<Adicional> findByComidaIsNullAndActivoTrue();
    
    // Obtener todos los adicionales sin comida (incluye inactivos para administración)
    List<Adicional> findByComidaIsNull();
}