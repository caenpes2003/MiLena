package com.proyecto.restaurante.service;

import com.proyecto.restaurante.model.Adicional;
import com.proyecto.restaurante.model.Comida;
import java.util.List;
import java.util.Optional;

public interface IAdicionalService {
    
    // CRUD básico
    List<Adicional> listarTodos();
    List<Adicional> listarActivos();
    List<Adicional> listarInactivos();
    Optional<Adicional> obtenerPorId(Long id);
    Adicional guardar(Adicional adicional);
    Adicional actualizar(Long id, Adicional adicional);
    void eliminar(Long id);
    void activar(Long id);
    void desactivar(Long id);
    
    // Búsquedas específicas
    List<Adicional> obtenerPorComida(Comida comida);
    List<Adicional> obtenerPorComidaId(Long comidaId);
    List<Adicional> buscarPorTermino(String termino);
    
    // Estadísticas
    long contarPorComida(Long comidaId);
}