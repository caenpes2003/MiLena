package com.proyecto.restaurante.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.proyecto.restaurante.model.Cliente;
import com.proyecto.restaurante.service.IClienteService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private IClienteService clienteService;

    // Mostrar formulario de login
    @GetMapping("/login")
    public String loginForm(Model model) {
        return "auth/login";
    }

    // Procesar login
    @PostMapping("/login")
    public String login(@RequestParam String username,
                       @RequestParam String password,
                       HttpSession session,
                       RedirectAttributes ra) {
        
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            ra.addFlashAttribute("loginError", "Credenciales inválidas");
            return "redirect:/login";
        }

        // Solución: Verificar que el Optional no sea nulo antes de llamar a isPresent()
        Optional<Cliente> clienteOpt = clienteService.autenticar(username, password);
        
        if (clienteOpt != null && clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            // Login exitoso
            session.setAttribute("clienteLogueado", cliente);
            session.setAttribute("clienteId", cliente.getId());
            session.setAttribute("clienteNombre", cliente.getNombreCompleto());
            
            ra.addFlashAttribute("success", "¡Bienvenido " + cliente.getNombre() + "!");
            return "redirect:/";
        } else {
            ra.addFlashAttribute("loginError", "Email o contraseña incorrectos");
            return "redirect:/login";
        }
    }

    // Mostrar formulario de registro
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "auth/register";
    }

    // Procesar registro
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute Cliente cliente,
                          BindingResult result,
                          @RequestParam String password2,
                          Model model,
                          RedirectAttributes ra) {
        
        // Validar que las contraseñas coincidan
        if (!cliente.getPassword().equals(password2)) {
            result.rejectValue("password", "error.cliente", "Las contraseñas no coinciden");
        }
        
        // Validar que el email no exista
        if (clienteService.existePorEmail(cliente.getEmail())) {
            result.rejectValue("email", "error.cliente", "Ya existe una cuenta con ese email");
        }
        
        if (result.hasErrors()) {
            return "auth/register";
        }
        
        try {
            // Solución: Usar el cliente registrado correctamente
            Cliente clienteRegistrado = clienteService.registrar(cliente);
            if (clienteRegistrado != null) {
                ra.addFlashAttribute("registerOk", "¡Cuenta creada exitosamente! Ahora puedes iniciar sesión.");
                return "redirect:/login";
            } else {
                ra.addFlashAttribute("registerError", "Error al crear la cuenta");
                return "redirect:/register";
            }
        } catch (Exception e) {
            ra.addFlashAttribute("registerError", "Error al crear la cuenta: " + e.getMessage());
            return "redirect:/register";
        }
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes ra) {
        session.invalidate();
        ra.addFlashAttribute("success", "Sesión cerrada correctamente");
        return "redirect:/";
    }
    
    // Perfil del usuario logueado
    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model, RedirectAttributes ra) {
        Cliente clienteLogueado = (Cliente) session.getAttribute("clienteLogueado");
        if (clienteLogueado == null) {
            ra.addFlashAttribute("error", "Debes iniciar sesión");
            return "redirect:/login";
        }
        
        // Obtener datos actualizados
        Optional<Cliente> clienteOpt = clienteService.obtenerPorId(clienteLogueado.getId());
        if (clienteOpt != null && clienteOpt.isPresent()) {
            model.addAttribute("cliente", clienteOpt.get());
            return "auth/perfil";
        } else {
            session.invalidate();
            ra.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/login";
        }
    }
    
    // Actualizar perfil
    @PostMapping("/perfil")
    public String actualizarPerfil(@Valid @ModelAttribute Cliente cliente,
                                  BindingResult result,
                                  HttpSession session,
                                  Model model,
                                  RedirectAttributes ra) {
        
        Cliente clienteLogueado = (Cliente) session.getAttribute("clienteLogueado");
        if (clienteLogueado == null) {
            return "redirect:/login";
        }
        
        // Validar email único (excepto el propio)
        if (clienteService.existePorEmailYDiferenteId(cliente.getEmail(), clienteLogueado.getId())) {
            result.rejectValue("email", "error.cliente", "Ya existe una cuenta con ese email");
        }
        
        if (result.hasErrors()) {
            return "auth/perfil";
        }
        
        try {
            Cliente clienteActualizado = clienteService.actualizar(clienteLogueado.getId(), cliente);
            if (clienteActualizado != null) {
                session.setAttribute("clienteLogueado", clienteActualizado);
                session.setAttribute("clienteNombre", clienteActualizado.getNombreCompleto());
                
                ra.addFlashAttribute("success", "Perfil actualizado correctamente");
                return "redirect:/perfil";
            } else {
                ra.addFlashAttribute("error", "Error al actualizar perfil");
                return "redirect:/perfil";
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al actualizar perfil: " + e.getMessage());
            return "redirect:/perfil";
        }
    }
}