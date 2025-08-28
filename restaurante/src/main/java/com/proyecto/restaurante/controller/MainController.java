package com.proyecto.restaurante.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.proyecto.restaurante.model.Comida;
import com.proyecto.restaurante.service.IAdicionalService;
import com.proyecto.restaurante.service.IComidaService;

@Controller
public class MainController {

    @Autowired
    private IComidaService comidaService;

    @Autowired
    private IAdicionalService adicionalService;

    @GetMapping("/")
    public String index(Model model) {
        // Platos destacados para el slider del index
        List<Comida> platosDestacados = comidaService.listarActivas()
                .stream()
                .limit(6)
                .toList();
        model.addAttribute("platosDestacados", platosDestacados);
        return "index";
    }

    // Vista CLIENTES - Tarjetas 
    @GetMapping("/menu")
    public String menu(Model model) {
        List<Comida> comidas = comidaService.listarActivas();
        model.addAttribute("items", comidas);
        return "menu"; // ← Tu template actual de tarjetas
    }

    // Detalle de producto individual (compatibilidad con tu URL actual)
    @GetMapping("/producto/{slug}")
    public String detalleProducto(@PathVariable String slug, Model model, RedirectAttributes ra) {
        Optional<Comida> comida = comidaService.obtenerPorSlug(slug);
        if (comida.isEmpty()) {
            ra.addFlashAttribute("error", "Producto no encontrado");
            return "redirect:/menu";
        }

        model.addAttribute("item", comida.get());
        model.addAttribute("adicionales", adicionalService.obtenerPorComidaId(comida.get().getId()));

        // Productos relacionados (misma categoría)
        List<Comida> relacionados = List.of();
        if (comida.get().getCategoria() != null) {
            relacionados = comidaService.buscarPorCategoria(comida.get().getCategoria())
                    .stream()
                    .filter(c -> !c.getId().equals(comida.get().getId()))
                    .limit(3)
                    .toList();
        }
        model.addAttribute("productosRelacionados", relacionados);

        return "producto"; 
    }
}