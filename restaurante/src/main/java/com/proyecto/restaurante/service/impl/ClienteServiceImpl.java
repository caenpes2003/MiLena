package com.proyecto.restaurante.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.restaurante.model.Cliente;
import com.proyecto.restaurante.repository.ClienteRepository;
import com.proyecto.restaurante.service.IClienteService;

@Service
@Transactional
public class ClienteServiceImpl implements IClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    // ==========
    // Utilidades
    // ==========
    private String norm(String s) {
        return s == null ? null : s.trim();
    }

    private String normEmail(String s) {
        return s == null ? null : s.trim().toLowerCase();
    }

    // ==========
    // Lecturas
    // ==========
    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarActivos() {
        return clienteRepository.findByActivoTrueOrderByNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> obtenerPorId(Long id) {
        return clienteRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> obtenerPorEmail(String email) {
        return clienteRepository.findByEmail(normEmail(email));
    }

    // ==========
    // Escrituras
    // ==========
    @Override
    public Cliente guardar(Cliente cliente) {
        // Normaliza email y demás campos
        cliente.setEmail(normEmail(cliente.getEmail()));
        cliente.setNombre(norm(cliente.getNombre()));
        cliente.setApellido(norm(cliente.getApellido()));
        cliente.setTelefono(norm(cliente.getTelefono()));
        cliente.setDireccion(norm(cliente.getDireccion()));
        // activo: por si viene null desde el form/model
        if (cliente.getActivo() == null)
            cliente.setActivo(Boolean.TRUE);

        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente actualizar(Long id, Cliente cliente) {
        Optional<Cliente> existenteOpt = clienteRepository.findById(id);
        if (existenteOpt.isEmpty()) {
            throw new RuntimeException("Cliente no encontrado con ID: " + id);
        }

        Cliente existente = existenteOpt.get();

        // Actualización segura (no pisar con null/empty)
        String nombre = norm(cliente.getNombre());
        if (nombre != null)
            existente.setNombre(nombre);

        String apellido = norm(cliente.getApellido());
        if (apellido != null)
            existente.setApellido(apellido);

        String email = normEmail(cliente.getEmail());
        if (email != null)
            existente.setEmail(email);

        String telefono = norm(cliente.getTelefono());
        if (telefono != null)
            existente.setTelefono(telefono);

        String direccion = norm(cliente.getDireccion());
        if (direccion != null)
            existente.setDireccion(direccion);

        if (cliente.getActivo() != null)
            existente.setActivo(cliente.getActivo());

        // Solo cambiar password si llega no vacía
        String nuevaPass = cliente.getPassword();
        if (nuevaPass != null && !nuevaPass.trim().isEmpty()) {
            existente.setPassword(nuevaPass);
        }

        return clienteRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        clienteRepository.deleteById(id);
    }

    @Override
    public void activar(Long id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        if (cliente.isPresent()) {
            cliente.get().setActivo(true);
            clienteRepository.save(cliente.get());
        } else {
            throw new RuntimeException("Cliente no encontrado con ID: " + id);
        }
    }

    @Override
    public void desactivar(Long id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        if (cliente.isPresent()) {
            cliente.get().setActivo(false);
            clienteRepository.save(cliente.get());
        } else {
            throw new RuntimeException("Cliente no encontrado con ID: " + id);
        }
    }

    // ==========
    // Búsquedas
    // ==========
    @Override
    @Transactional(readOnly = true)
    public List<Cliente> buscarPorTermino(String termino) {
        return clienteRepository.buscarPorTermino(termino);
    }

    // ==========
    // Validaciones
    // ==========
    @Override
    @Transactional(readOnly = true)
    public boolean existePorEmail(String email) {
        String e = normEmail(email);
        return e != null && clienteRepository.existsByEmail(e);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorEmailYDiferenteId(String email, Long id) {
        String e = normEmail(email);
        Optional<Cliente> c = clienteRepository.findByEmail(e);
        return c.isPresent() && !Objects.equals(c.get().getId(), id);
    }

    // ==========
    // Auth
    // ==========
    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> autenticar(String email, String password) {
        String e = normEmail(email);
        Optional<Cliente> cliente = clienteRepository.findByEmail(e);
        if (cliente.isPresent() && Boolean.TRUE.equals(cliente.get().getActivo())) {
            if (cliente.get().getPassword() != null && cliente.get().getPassword().equals(password)) {
                return cliente;
            }
        }
        return Optional.empty();
    }

    @Override
    public Cliente registrar(Cliente cliente) {
        if (existePorEmail(cliente.getEmail())) {
            throw new RuntimeException("Ya existe un cliente con ese email");
        }
        return guardar(cliente);
    }

    // ==========
    // Stats
    // ==========
    @Override
    @Transactional(readOnly = true)
    public long contarActivos() {
        return clienteRepository.countByActivoTrue();
    }
}
