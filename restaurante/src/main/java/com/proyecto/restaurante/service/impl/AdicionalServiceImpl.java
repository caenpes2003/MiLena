// ===============================
// AdicionalServiceImpl.java
// ===============================
package com.proyecto.restaurante.service.impl;

import com.proyecto.restaurante.model.Adicional;
import com.proyecto.restaurante.model.Comida;
import com.proyecto.restaurante.repository.AdicionalRepository;
import com.proyecto.restaurante.service.IAdicionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdicionalServiceImpl implements IAdicionalService {
    
    @Autowired
    private AdicionalRepository adicionalRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<Adicional> listarTodos() {
        return adicionalRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Adicional> listarActivos() {
        return adicionalRepository.findByActivoTrueOrderByNombreAsc();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Adicional> listarInactivos() {
        return adicionalRepository.findByActivoFalseOrderByNombreAsc();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Adicional> obtenerPorId(Long id) {
        return adicionalRepository.findById(id);
    }
    
    @Override
    public Adicional guardar(Adicional adicional) {
        return adicionalRepository.save(adicional);
    }
    
    @Override
    public Adicional actualizar(Long id, Adicional adicional) {
        Optional<Adicional> existente = adicionalRepository.findById(id);
        if (existente.isPresent()) {
            Adicional adicionalExistente = existente.get();
            adicionalExistente.setNombre(adicional.getNombre());
            adicionalExistente.setPrecio(adicional.getPrecio());
            adicionalExistente.setDescripcion(adicional.getDescripcion());
            adicionalExistente.setActivo(adicional.getActivo());
            adicionalExistente.setComida(adicional.getComida());
            
            return adicionalRepository.save(adicionalExistente);
        }
        throw new RuntimeException("Adicional no encontrado con ID: " + id);
    }
    
    @Override
    public void eliminar(Long id) {
        adicionalRepository.deleteById(id);
    }
    
    @Override
    public void activar(Long id) {
        Optional<Adicional> adicional = adicionalRepository.findById(id);
        if (adicional.isPresent()) {
            adicional.get().setActivo(true);
            adicionalRepository.save(adicional.get());
        }
    }
    
    @Override
    public void desactivar(Long id) {
        Optional<Adicional> adicional = adicionalRepository.findById(id);
        if (adicional.isPresent()) {
            adicional.get().setActivo(false);
            adicionalRepository.save(adicional.get());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Adicional> obtenerPorComida(Comida comida) {
        return adicionalRepository.findByComidaAndActivoTrue(comida);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Adicional> obtenerPorComidaId(Long comidaId) {
        return adicionalRepository.findByComidaIdAndActivoTrue(comidaId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Adicional> buscarPorTermino(String termino) {
        return adicionalRepository.buscarPorTermino(termino);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long contarPorComida(Long comidaId) {
        return adicionalRepository.countByComidaIdAndActivoTrue(comidaId);
    }
}