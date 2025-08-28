// ===============================
// AdicionalController.java
// ===============================
package com.proyecto.restaurante.controller;

import com.proyecto.restaurante.model.Adicional;
import com.proyecto.restaurante.model.Comida;
import com.proyecto.restaurante.service.IAdicionalService;
import com.proyecto.restaurante.service.IComidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/adicionales")
public class AdicionalController {

    @Autowired
    private IAdicionalService adicionalService;

    @Autowired
    private IComidaService comidaService;

    // Listar todos los adicionales
    @GetMapping
    public String listarAdicionales(Model model) {
        List<Adicional> adicionales = adicionalService.listarActivos();
        model.addAttribute("adicionales", adicionales);
        return "adicionales/lista";
    }

    // Listar adicionales inactivos
    @GetMapping("/inactivos")
    public String listarAdicionalesInactivos(Model model) {
        List<Adicional> adicionalesInactivos = adicionalService.listarInactivos();
        model.addAttribute("adicionales", adicionalesInactivos);
        return "adicionales/inactivos";
    }

    // Ver adicional específico
    @GetMapping("/{id}")
    public String verAdicional(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<Adicional> adicional = adicionalService.obtenerPorId(id);
        if (adicional.isEmpty()) {
            ra.addFlashAttribute("error", "Adicional no encontrado");
            return "redirect:/adicionales";
        }

        model.addAttribute("adicional", adicional.get());
        return "adicionales/perfil";
    }

    // Adicionales por comida
    @GetMapping("/comida/{comidaId}")
    public String adicionalesPorComida(@PathVariable Long comidaId, Model model, RedirectAttributes ra) {
        Optional<Comida> comida = comidaService.obtenerPorId(comidaId);
        if (comida.isEmpty()) {
            ra.addFlashAttribute("error", "Comida no encontrada");
            return "redirect:/comidas";
        }

        List<Adicional> adicionales = adicionalService.obtenerPorComidaId(comidaId);
        model.addAttribute("comida", comida.get());
        model.addAttribute("adicionales", adicionales);
        return "adicionales/por-comida";
    }

    // Buscar adicionales
    @GetMapping("/buscar")
    public String buscarAdicionales(@RequestParam(required = false) String termino,
            Model model) {
        List<Adicional> adicionales;

        if (termino != null && !termino.trim().isEmpty()) {
            adicionales = adicionalService.buscarPorTermino(termino);
        } else {
            adicionales = adicionalService.listarActivos();
        }

        model.addAttribute("adicionales", adicionales);
        model.addAttribute("termino", termino);
        return "adicionales/lista";
    }

    // ===============================
    // CRUD para administradores
    // ===============================

    // Crear nuevo adicional - Formulario
    @GetMapping("/nuevo")
    public String nuevoAdicionalForm(Model model) {
        model.addAttribute("adicional", new Adicional());
        model.addAttribute("comidas", comidaService.listarActivas());
        return "adicionales/form";
    }

    // Crear nuevo adicional - Procesar
    @PostMapping("/nuevo")
    public String crearAdicional(@Valid @ModelAttribute Adicional adicional,
            BindingResult result,
            @RequestParam(required = false) Long comidaId,
            Model model,
            RedirectAttributes ra) {

        if (result.hasErrors()) {
            model.addAttribute("comidas", comidaService.listarActivas());
            return "adicionales/form";
        }

        try {
            // Setear la comida si se seleccionó una
            if (comidaId != null && comidaId > 0) {
                Optional<Comida> comida = comidaService.obtenerPorId(comidaId);
                if (comida.isPresent()) {
                    adicional.setComida(comida.get());
                }
            } else {
                adicional.setComida(null);
            }

            Adicional nuevoAdicional = adicionalService.guardar(adicional);
            ra.addFlashAttribute("success", "Adicional creado exitosamente");
            return "redirect:/adicionales/" + nuevoAdicional.getId();
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al crear el adicional: " + e.getMessage());
            return "redirect:/adicionales/nuevo";
        }
    }

    // Editar adicional - Formulario
    @GetMapping("/{id}/editar")
    public String editarAdicionalForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<Adicional> adicional = adicionalService.obtenerPorId(id);
        if (adicional.isEmpty()) {
            ra.addFlashAttribute("error", "Adicional no encontrado");
            return "redirect:/adicionales";
        }

        model.addAttribute("adicional", adicional.get());
        model.addAttribute("comidas", comidaService.listarActivas());
        return "adicionales/form";
    }

    // Editar adicional - Procesar
    @PostMapping("/{id}/editar")
    public String actualizarAdicional(@PathVariable Long id,
            @Valid @ModelAttribute Adicional adicional,
            BindingResult result,
            @RequestParam(required = false) Long comidaId,
            Model model,
            RedirectAttributes ra) {

        if (result.hasErrors()) {
            model.addAttribute("comidas", comidaService.listarActivas());
            return "adicionales/form";
        }

        try {
            // Setear la comida si se seleccionó una
            if (comidaId != null && comidaId > 0) {
                Optional<Comida> comida = comidaService.obtenerPorId(comidaId);
                if (comida.isPresent()) {
                    adicional.setComida(comida.get());
                }
            } else {
                adicional.setComida(null);
            }

            adicionalService.actualizar(id, adicional);
            ra.addFlashAttribute("success", "Adicional actualizado exitosamente");
            return "redirect:/adicionales/" + id;
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al actualizar el adicional: " + e.getMessage());
            return "redirect:/adicionales/" + id + "/editar";
        }
    }

    // Activar/Desactivar adicional
    @PostMapping("/{id}/toggle")
    public String toggleActivo(@PathVariable Long id, RedirectAttributes ra) {
        try {
            Optional<Adicional> adicional = adicionalService.obtenerPorId(id);
            if (adicional.isPresent()) {
                if (adicional.get().getActivo()) {
                    adicionalService.desactivar(id);
                    ra.addFlashAttribute("success", "Adicional desactivado");
                } else {
                    adicionalService.activar(id);
                    ra.addFlashAttribute("success", "Adicional activado");
                }
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al cambiar estado: " + e.getMessage());
        }
        return "redirect:/adicionales";
    }

    // Eliminar adicional
    @PostMapping("/{id}/eliminar")
    public String eliminarAdicional(@PathVariable Long id, RedirectAttributes ra) {
        try {
            adicionalService.eliminar(id);
            ra.addFlashAttribute("success", "Adicional eliminado exitosamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al eliminar el adicional: " + e.getMessage());
        }
        return "redirect:/adicionales";
    }
}