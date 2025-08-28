package com.proyecto.restaurante.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.proyecto.restaurante.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    Optional<Cliente> findByEmail(String email);
    
    List<Cliente> findByActivoTrue();
    
    List<Cliente> findByActivoTrueOrderByNombreAsc();
    
    @Query("SELECT c FROM Cliente c WHERE c.activo = true AND " +
           "(LOWER(c.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(c.apellido) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :termino, '%')))")
    List<Cliente> buscarPorTermino(@Param("termino") String termino);
    
    boolean existsByEmail(String email);
    
    long countByActivoTrue();
}