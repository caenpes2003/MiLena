package com.proyecto.restaurante.service;

import com.proyecto.restaurante.model.Comida;
import java.util.List;
import java.util.Optional;

public interface IComidaService {
    
    // CRUD básico
    List<Comida> listarTodas();
    List<Comida> listarActivas();
    Optional<Comida> obtenerPorId(Long id);
    Optional<Comida> obtenerPorSlug(String slug);
    Comida guardar(Comida comida);
    Comida actualizar(Long id, Comida comida);
    void eliminar(Long id);
    void activar(Long id);
    void desactivar(Long id);
    
    // Búsquedas específicas
    List<Comida> buscarPorCategoria(String categoria);
    List<Comida> buscarPorTermino(String termino);
    List<String> obtenerCategorias();
    
    // Validaciones
    boolean existePorSlug(String slug);
    boolean existePorSlugYDiferenteId(String slug, Long id);
}