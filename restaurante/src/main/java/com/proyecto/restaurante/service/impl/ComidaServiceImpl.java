package com.proyecto.restaurante.service.impl;

import com.proyecto.restaurante.model.Comida;
import com.proyecto.restaurante.repository.ComidaRepository;
import com.proyecto.restaurante.service.IComidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ComidaServiceImpl implements IComidaService {
    
    @Autowired
    private ComidaRepository comidaRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<Comida> listarTodas() {
        return comidaRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Comida> listarActivas() {
        return comidaRepository.findByActivoTrueOrderByNombreAsc();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Comida> obtenerPorId(Long id) {
        return comidaRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Comida> obtenerPorSlug(String slug) {
        return comidaRepository.findBySlug(slug);
    }
    
    @Override
    public Comida guardar(Comida comida) {
        // Generar slug si no existe
        if (comida.getSlug() == null || comida.getSlug().trim().isEmpty()) {
            comida.setSlug(generarSlug(comida.getNombre()));
        }
        return comidaRepository.save(comida);
    }
    
    @Override
    public Comida actualizar(Long id, Comida comida) {
        Optional<Comida> existente = comidaRepository.findById(id);
        if (existente.isPresent()) {
            Comida comidaExistente = existente.get();
            comidaExistente.setNombre(comida.getNombre());
            comidaExistente.setDescripcion(comida.getDescripcion());
            comidaExistente.setPrecio(comida.getPrecio());
            comidaExistente.setImagenUrl(comida.getImagenUrl());
            comidaExistente.setCategoria(comida.getCategoria());
            comidaExistente.setActivo(comida.getActivo());
            
            // Actualizar slug si cambió el nombre
            if (!comida.getSlug().equals(comidaExistente.getSlug())) {
                comidaExistente.setSlug(comida.getSlug());
            }
            
            return comidaRepository.save(comidaExistente);
        }
        throw new RuntimeException("Comida no encontrada con ID: " + id);
    }
    
    @Override
    public void eliminar(Long id) {
        comidaRepository.deleteById(id);
    }
    
    @Override
    public void activar(Long id) {
        Optional<Comida> comida = comidaRepository.findById(id);
        if (comida.isPresent()) {
            comida.get().setActivo(true);
            comidaRepository.save(comida.get());
        }
    }
    
    @Override
    public void desactivar(Long id) {
        Optional<Comida> comida = comidaRepository.findById(id);
        if (comida.isPresent()) {
            comida.get().setActivo(false);
            comidaRepository.save(comida.get());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Comida> buscarPorCategoria(String categoria) {
        return comidaRepository.findByCategoriaAndActivoTrue(categoria);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Comida> buscarPorTermino(String termino) {
        return comidaRepository.buscarPorTermino(termino);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> obtenerCategorias() {
        return comidaRepository.findCategorias();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existePorSlug(String slug) {
        return comidaRepository.existsBySlug(slug);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existePorSlugYDiferenteId(String slug, Long id) {
        Optional<Comida> comida = comidaRepository.findBySlug(slug);
        return comida.isPresent() && !comida.get().getId().equals(id);
    }
    
    private String generarSlug(String nombre) {
        return nombre.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-")
                .trim();
    }
}