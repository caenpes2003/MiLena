package com.proyecto.restaurante.service;

import java.util.List;
import java.util.Optional;

import com.proyecto.restaurante.model.Cliente;

public interface IClienteService {
    
    // CRUD básico
    List<Cliente> listarTodos();
    List<Cliente> listarActivos();
    Optional<Cliente> obtenerPorId(Long id);
    Optional<Cliente> obtenerPorEmail(String email);
    Cliente guardar(Cliente cliente);
    Cliente actualizar(Long id, Cliente cliente);
    void eliminar(Long id);
    void activar(Long id);
    void desactivar(Long id);
    
    // Búsquedas específicas
    List<Cliente> buscarPorTermino(String termino);
    
    // Validaciones
    boolean existePorEmail(String email);
    boolean existePorEmailYDiferenteId(String email, Long id);
    
    // Autenticación
    Optional<Cliente> autenticar(String email, String password);
    Cliente registrar(Cliente cliente);
    
    // Estadísticas
    long contarActivos();
} 
    

