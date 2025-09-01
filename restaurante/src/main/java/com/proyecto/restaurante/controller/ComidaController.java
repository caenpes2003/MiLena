package com.proyecto.restaurante.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.proyecto.restaurante.model.Adicional;
import com.proyecto.restaurante.model.Comida;
import com.proyecto.restaurante.service.IAdicionalService;
import com.proyecto.restaurante.service.IComidaService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/comidas") // Solo rutas de administración
public class ComidaController {

    @Autowired
    private IComidaService comidaService;

    @Autowired
    private IAdicionalService adicionalService;

    // ADMIN: Lista tabla con CRUD
    @GetMapping
    public String listarComidas(Model model) {
        List<Comida> comidas = comidaService.listarTodas(); // Todas (activas e inactivas)
        model.addAttribute("items", comidas);
        model.addAttribute("categorias", comidaService.obtenerCategorias());
        return "comidas/tabla"; // ← Nueva vista administrativa
    }

    // ADMIN: Buscar con filtros
    @GetMapping("/buscar")
    public String buscarComidas(@RequestParam(required = false) String termino,
            @RequestParam(required = false) String categoria,
            Model model) {
        List<Comida> comidas;

        if (termino != null && !termino.trim().isEmpty()) {
            comidas = comidaService.buscarPorTermino(termino);
        } else if (categoria != null && !categoria.trim().isEmpty()) {
            comidas = comidaService.buscarPorCategoria(categoria);
        } else {
            comidas = comidaService.listarTodas();
        }

        model.addAttribute("items", comidas);
        model.addAttribute("termino", termino);
        model.addAttribute("categoria", categoria);
        model.addAttribute("categorias", comidaService.obtenerCategorias());
        return "comidas/tabla";
    }

    // ADMIN: Ver detalle individual
    @GetMapping("/{id}")
    public String verComida(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<Comida> comida = comidaService.obtenerPorId(id);
        if (comida.isEmpty()) {
            ra.addFlashAttribute("error", "Comida no encontrada");
            return "redirect:/comidas";
        }

        model.addAttribute("item", comida.get());
        model.addAttribute("adicionales", adicionalService.obtenerPorComidaId(id));

        // Usar template de detalle (puedes crear uno específico para admin si quieres)
        return "producto"; // O crear "comidas/detalle.html"
    }

    // ADMIN: Crear nueva comida - Formulario
    @GetMapping("/nueva")
    public String nuevaComidaForm(Model model) {
        model.addAttribute("comida", new Comida());
        model.addAttribute("categorias", List.of("res", "pollo", "pescado", "bebidas", "otros"));
        model.addAttribute("adicionales", adicionalService.obtenerSinAsignar()); // Solo adicionales sin asignar
        return "comidas/form";
    }

    // ADMIN: Crear nueva comida - Procesar
    @PostMapping("/nueva")
    public String crearComida(@Valid @ModelAttribute Comida comida,
            @RequestParam(value = "adicionalesSeleccionados", required = false) List<String> adicionalesSeleccionados,
            BindingResult result,
            Model model,
            RedirectAttributes ra) {

        if (comidaService.existePorSlug(comida.getSlug())) {
            result.rejectValue("slug", "error.comida", "Ya existe una comida con ese slug");
        }

        if (result.hasErrors()) {
            model.addAttribute("categorias", List.of("res", "pollo", "pescado", "bebidas", "otros"));
            model.addAttribute("adicionales", adicionalService.obtenerSinAsignar()); // Solo adicionales sin asignar
            return "comidas/form";
        }

        try {
            Comida nuevaComida = comidaService.guardar(comida);
            
            // Asignar adicionales seleccionados
            if (adicionalesSeleccionados != null && !adicionalesSeleccionados.isEmpty()) {
                for (String adicionalIdStr : adicionalesSeleccionados) {
                    try {
                        Long adicionalId = Long.valueOf(adicionalIdStr);
                        Optional<Adicional> adicional = adicionalService.obtenerPorId(adicionalId);
                        if (adicional.isPresent()) {
                            Adicional adicionalActualizado = adicional.get();
                            adicionalActualizado.setComida(nuevaComida);
                            adicionalService.actualizar(adicionalId, adicionalActualizado);
                        }
                    } catch (NumberFormatException e) {
                        // Ignorar IDs inválidos
                    }
                }
            }
            
            ra.addFlashAttribute("success", "Comida creada exitosamente");
            return "redirect:/comidas/" + nuevaComida.getId();
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al crear la comida: " + e.getMessage());
            return "redirect:/comidas/nueva";
        }
    }

    // ADMIN: Editar comida - Formulario
    @GetMapping("/{id}/editar")
    public String editarComidaForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<Comida> comida = comidaService.obtenerPorId(id);
        if (comida.isEmpty()) {
            ra.addFlashAttribute("error", "Comida no encontrada");
            return "redirect:/comidas";
        }

        model.addAttribute("comida", comida.get());
        model.addAttribute("categorias", List.of("res", "pollo", "pescado", "bebidas", "otros"));
        model.addAttribute("adicionales", adicionalService.listarTodos()); // Todos los adicionales disponibles
        model.addAttribute("adicionalesAsignados", adicionalService.obtenerPorComidaId(id)); // Adicionales ya asignados
        return "comidas/form";
    }

    // ADMIN: Editar comida - Procesar
    @PostMapping("/{id}/editar")
    public String actualizarComida(@PathVariable Long id,
            @Valid @ModelAttribute Comida comida,
            @RequestParam(value = "adicionalesSeleccionados", required = false) List<String> adicionalesSeleccionados,
            BindingResult result,
            Model model,
            RedirectAttributes ra) {

        if (comidaService.existePorSlugYDiferenteId(comida.getSlug(), id)) {
            result.rejectValue("slug", "error.comida", "Ya existe una comida con ese slug");
        }

        if (result.hasErrors()) {
            model.addAttribute("categorias", List.of("res", "pollo", "pescado", "bebidas", "otros"));
            model.addAttribute("adicionales", adicionalService.listarTodos());
            model.addAttribute("adicionalesAsignados", adicionalService.obtenerPorComidaId(id));
            return "comidas/form";
        }

        try {
            comidaService.actualizar(id, comida);
            
            // Actualizar adicionales - Primero desasignar todos los anteriores
            List<Adicional> adicionalesAnteriores = adicionalService.obtenerPorComidaId(id);
            for (Adicional adicional : adicionalesAnteriores) {
                adicional.setComida(null);
                adicionalService.actualizar(adicional.getId(), adicional);
            }
            
            // Luego asignar los nuevos seleccionados
            if (adicionalesSeleccionados != null && !adicionalesSeleccionados.isEmpty()) {
                Optional<Comida> comidaActualizada = comidaService.obtenerPorId(id);
                if (comidaActualizada.isPresent()) {
                    for (String adicionalIdStr : adicionalesSeleccionados) {
                        try {
                            Long adicionalId = Long.valueOf(adicionalIdStr);
                            Optional<Adicional> adicional = adicionalService.obtenerPorId(adicionalId);
                            if (adicional.isPresent()) {
                                Adicional adicionalActualizado = adicional.get();
                                adicionalActualizado.setComida(comidaActualizada.get());
                                adicionalService.actualizar(adicionalId, adicionalActualizado);
                            }
                        } catch (NumberFormatException e) {
                            // Ignorar IDs inválidos
                        }
                    }
                }
            }
            
            ra.addFlashAttribute("success", "Comida actualizada exitosamente");
            return "redirect:/comidas/" + id;
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al actualizar la comida: " + e.getMessage());
            return "redirect:/comidas/" + id + "/editar";
        }
    }

    // ADMIN: Activar/Desactivar comida
    @PostMapping("/{id}/toggle")
    public String toggleActivo(@PathVariable Long id, RedirectAttributes ra) {
        try {
            Optional<Comida> comida = comidaService.obtenerPorId(id);
            if (comida.isPresent()) {
                if (comida.get().getActivo()) {
                    comidaService.desactivar(id);
                    ra.addFlashAttribute("success", "Comida desactivada");
                } else {
                    comidaService.activar(id);
                    ra.addFlashAttribute("success", "Comida activada");
                }
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al cambiar estado: " + e.getMessage());
        }
        return "redirect:/comidas";
    }

    // ADMIN: Eliminar comida
    @PostMapping("/{id}/eliminar")
    public String eliminarComida(@PathVariable Long id, RedirectAttributes ra) {
        try {
            comidaService.eliminar(id);
            ra.addFlashAttribute("success", "Comida eliminada exitosamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al eliminar la comida: " + e.getMessage());
        }
        return "redirect:/comidas";
    }

    // ==================== GESTIÓN DE ADICIONALES ====================

    // ADMIN: Gestionar adicionales de una comida
    @GetMapping("/{id}/adicionales")
    public String gestionarAdicionales(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<Comida> comida = comidaService.obtenerPorId(id);
        if (comida.isEmpty()) {
            ra.addFlashAttribute("error", "Comida no encontrada");
            return "redirect:/comidas";
        }

        model.addAttribute("comida", comida.get());
        model.addAttribute("adicionalesAsignados", adicionalService.obtenerPorComidaId(id));
        model.addAttribute("adicionalesDisponibles", adicionalService.obtenerSinAsignar());
        
        return "comidas/adicionales";
    }

    // ADMIN: Asignar adicional a comida
    @PostMapping("/{comidaId}/adicionales/{adicionalId}/asignar")
    public String asignarAdicional(@PathVariable Long comidaId, 
            @PathVariable Long adicionalId, 
            RedirectAttributes ra) {
        try {
            Optional<Comida> comida = comidaService.obtenerPorId(comidaId);
            Optional<Adicional> adicional = adicionalService.obtenerPorId(adicionalId);
            
            if (comida.isEmpty() || adicional.isEmpty()) {
                ra.addFlashAttribute("error", "Comida o adicional no encontrado");
                return "redirect:/comidas/" + comidaId + "/adicionales";
            }

            // Asignar adicional a la comida
            Adicional adicionalActualizado = adicional.get();
            adicionalActualizado.setComida(comida.get());
            adicionalService.actualizar(adicionalId, adicionalActualizado);
            
            ra.addFlashAttribute("success", "Adicional '" + adicional.get().getNombre() + "' asignado exitosamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al asignar adicional: " + e.getMessage());
        }
        return "redirect:/comidas/" + comidaId + "/adicionales";
    }

    // ADMIN: Desasignar adicional de comida
    @PostMapping("/{comidaId}/adicionales/{adicionalId}/desasignar")
    public String desasignarAdicional(@PathVariable Long comidaId, 
            @PathVariable Long adicionalId, 
            RedirectAttributes ra) {
        try {
            Optional<Adicional> adicional = adicionalService.obtenerPorId(adicionalId);
            
            if (adicional.isEmpty()) {
                ra.addFlashAttribute("error", "Adicional no encontrado");
                return "redirect:/comidas/" + comidaId + "/adicionales";
            }

            // Desasignar adicional (vuelve a estar disponible para otras comidas)
            Adicional adicionalActualizado = adicional.get();
            adicionalActualizado.setComida(null);
            adicionalService.actualizar(adicionalId, adicionalActualizado);
            
            ra.addFlashAttribute("success", "Adicional '" + adicional.get().getNombre() + "' desasignado exitosamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al desasignar adicional: " + e.getMessage());
        }
        return "redirect:/comidas/" + comidaId + "/adicionales";
    }
}