package com.proyecto.restaurante.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.proyecto.restaurante.model.Comida;
import com.proyecto.restaurante.repository.ComidaRepository;

@Service
public class ComidaService {

    private final ComidaRepository repo;

    // Inyección de dependencias por constructor
    public ComidaService(ComidaRepository repo) {
        this.repo = repo;
    }

    // Listar solo comidas activas
    public List<Comida> listarActivas() {
        return repo.findAll()
                   .stream()
                   .filter(Comida::getActivo) // solo las que están disponibles
                   .toList();
    }

    // Obtener comida por slug
    public Comida getBySlug(String slug) {
        return repo.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Comida no encontrada"));
    }
}
