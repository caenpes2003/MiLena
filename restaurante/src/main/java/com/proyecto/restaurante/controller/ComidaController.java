package com.proyecto.restaurante.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.proyecto.restaurante.model.Comida;
import com.proyecto.restaurante.service.ComidaService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ComidaController {

    private final ComidaService service;

    public ComidaController(ComidaService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String loginPost(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes ra) {

        if (username.isBlank() || password.isBlank()) {
            ra.addFlashAttribute("loginError", "Credenciales inválidas.");
            return "redirect:/login";
        }

        // Simula “usuario logueado”
        session.setAttribute("userEmail", username);

        return "redirect:/menu";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerPost(
            @Valid @ModelAttribute RegistroForm form,
            BindingResult br,
            RedirectAttributes ra) {
        // Validaciones simples
        if (br.hasErrors()) {
            ra.addFlashAttribute("registerError", "Por favor corrige los campos del formulario.");
            return "redirect:/register";
        }
        if (!form.getPassword().equals(form.getPassword2())) {
            ra.addFlashAttribute("registerError", "Las contraseñas no coinciden.");
            return "redirect:/register";
        }

        // TODO: aquí iría la lógica de guardado (service + repository)

        ra.addFlashAttribute("registerOk", "¡Cuenta creada! Ahora inicia sesión.");
        return "redirect:/login"; // o "redirect:/menu" si quieres loguearlo
    }

    // Mostrar comidas en formato de tarjetas
    @GetMapping("/menu")
    public String menuTarjetas(Model model) {
        List<Comida> comidas = service.listarActivas();
        model.addAttribute("items", comidas);
        return "menu"; // Thymeleaf buscará templates/menu.html
    }

    // Mostrar comidas en formato de tabla
    @GetMapping("/menu/table")
    public String menuTabla(Model model) {
        List<Comida> comidas = service.listarActivas();
        model.addAttribute("items", comidas);
        return "menu-table"; // Thymeleaf buscará templates/menu-table.html
    }

    // Mostrar detalle de una comida
    @GetMapping("/producto/{slug}")
    public String detalleProducto(@PathVariable String slug, Model model) {
        Comida comida = service.getBySlug(slug);
        model.addAttribute("item", comida);
        return "producto"; // Thymeleaf buscará templates/producto.html
    }
}
