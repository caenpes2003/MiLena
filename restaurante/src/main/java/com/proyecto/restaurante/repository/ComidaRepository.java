package com.proyecto.restaurante.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.proyecto.restaurante.model.Comida;

import jakarta.annotation.PostConstruct;

@Repository
public class ComidaRepository {

    private final List<Comida> data = new ArrayList<>();

    @PostConstruct
    public void init() {
        data.add(new Comida(1L, "arepa-rellena", "Churrasco Argentino",
                "Jugosa carne, corte importado.", 12000,
                "/assets/menu1.png", true));

        data.add(new Comida(2L, "chorizo-sant", "Chorizo santandereano",
                "Con arepa de maíz pelao.", 15000,
                "/assets/menu2.png", true));

        data.add(new Comida(3L, "mazorcada", "Mazorcada",
                "Maíz tierno gratinado.", 18000,
                "/assets/menu3.png", true));
    }

    public List<Comida> findAll() {
        return data;
    }

    public Optional<Comida> findBySlug(String slug) {
        return data.stream()
                .filter(c -> c.getSlug().equals(slug))
                .findFirst();
    }
}
