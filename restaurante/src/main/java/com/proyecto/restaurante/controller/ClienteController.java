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

import com.proyecto.restaurante.model.Cliente;
import com.proyecto.restaurante.service.IClienteService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private IClienteService clienteService;

    // Verificar sesión - método auxiliar
    private boolean verificarSesion(HttpSession session, RedirectAttributes ra) {
        Cliente clienteLogueado = (Cliente) session.getAttribute("clienteLogueado");
        if (clienteLogueado == null) {
            ra.addFlashAttribute("error", "Debes iniciar sesión para acceder a esta sección");
            return false;
        }
        return true;
    }

    // Listar todos los clientes
    @GetMapping
    public String listarClientes(HttpSession session, 
                                 Model model, 
                                 RedirectAttributes ra) {
        if (!verificarSesion(session, ra)) {
            return "redirect:/login";
        }

        List<Cliente> clientes = clienteService.listarActivos();
        long totalActivos = clienteService.contarActivos();
        
        model.addAttribute("clientes", clientes);
        model.addAttribute("totalActivos", totalActivos);
        model.addAttribute("titulo", "Gestión de Clientes");
        
        return "clientes/lista";
    }

    // Buscar clientes
    @GetMapping("/buscar")
    public String buscarClientes(@RequestParam(required = false) String termino,
                                 HttpSession session,
                                 Model model,
                                 RedirectAttributes ra) {
        if (!verificarSesion(session, ra)) {
            return "redirect:/login";
        }

        List<Cliente> clientes;
        
        if (termino != null && !termino.trim().isEmpty()) {
            clientes = clienteService.buscarPorTermino(termino);
            model.addAttribute("termino", termino);
            
            if (clientes.isEmpty()) {
                model.addAttribute("info", "No se encontraron clientes con el término: " + termino);
            }
        } else {
            clientes = clienteService.listarActivos();
        }
        
        model.addAttribute("clientes", clientes);
        model.addAttribute("totalActivos", clienteService.contarActivos());
        model.addAttribute("titulo", "Búsqueda de Clientes");
        
        return "clientes/lista";
    }

    // Mostrar formulario para nuevo cliente
    @GetMapping("/nuevo")
    public String nuevoClienteForm(HttpSession session,
                                   Model model,
                                   RedirectAttributes ra) {
        if (!verificarSesion(session, ra)) {
            return "redirect:/login";
        }

        model.addAttribute("cliente", new Cliente());
        model.addAttribute("titulo", "Nuevo Cliente");
        model.addAttribute("accion", "Crear");
        
        return "clientes/form";
    }

    // Guardar nuevo cliente
    @PostMapping("/nuevo")
    public String guardarCliente(@Valid @ModelAttribute Cliente cliente,
                                 BindingResult result,
                                 HttpSession session,
                                 Model model,
                                 RedirectAttributes ra) {
        if (!verificarSesion(session, ra)) {
            return "redirect:/login";
        }

        // Validar email único
        if (clienteService.existePorEmail(cliente.getEmail())) {
            result.rejectValue("email", "error.cliente", 
                             "Ya existe un cliente con ese email");
        }

        if (result.hasErrors()) {
            model.addAttribute("titulo", "Nuevo Cliente");
            model.addAttribute("accion", "Crear");
            return "clientes/form";
        }

        try {
            Cliente nuevoCliente = clienteService.guardar(cliente);
            ra.addFlashAttribute("success", 
                               "Cliente " + nuevoCliente.getNombreCompleto() + " creado exitosamente");
            return "redirect:/clientes";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al crear el cliente: " + e.getMessage());
            return "redirect:/clientes/nuevo";
        }
    }

    // Mostrar formulario para editar cliente
    @GetMapping("/{id}/editar")
    public String editarClienteForm(@PathVariable Long id,
                                    HttpSession session,
                                    Model model,
                                    RedirectAttributes ra) {
        if (!verificarSesion(session, ra)) {
            return "redirect:/login";
        }

        Optional<Cliente> clienteOpt = clienteService.obtenerPorId(id);
        
        if (!clienteOpt.isPresent()) {
            ra.addFlashAttribute("error", "Cliente no encontrado");
            return "redirect:/clientes";
        }

        model.addAttribute("cliente", clienteOpt.get());
        model.addAttribute("titulo", "Editar Cliente");
        model.addAttribute("accion", "Actualizar");
        
        return "clientes/form";
    }

    // Actualizar cliente - Corregida la ruta
    @PostMapping("/{id}/editar")
    public String actualizarCliente(@PathVariable Long id,
                                   @Valid @ModelAttribute Cliente cliente,
                                   BindingResult result,
                                   HttpSession session,
                                   Model model,
                                   RedirectAttributes ra) {
        if (!verificarSesion(session, ra)) {
            return "redirect:/login";
        }

        // Validar email único (excepto el propio)
        if (clienteService.existePorEmailYDiferenteId(cliente.getEmail(), id)) {
            result.rejectValue("email", "error.cliente", 
                             "Ya existe un cliente con ese email");
        }

        if (result.hasErrors()) {
            model.addAttribute("titulo", "Editar Cliente");
            model.addAttribute("accion", "Actualizar");
            return "clientes/form";
        }

        try {
            Cliente clienteActualizado = clienteService.actualizar(id, cliente);
            ra.addFlashAttribute("success", 
                               "Cliente " + clienteActualizado.getNombreCompleto() + " actualizado exitosamente");
            return "redirect:/clientes";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al actualizar el cliente: " + e.getMessage());
            return "redirect:/clientes";
        }
    }

    // Ver detalles de un cliente
    @GetMapping("/ver/{id}")
    public String verCliente(@PathVariable Long id,
                             HttpSession session,
                             Model model,
                             RedirectAttributes ra) {
        if (!verificarSesion(session, ra)) {
            return "redirect:/login";
        }

        Optional<Cliente> clienteOpt = clienteService.obtenerPorId(id);
        
        if (!clienteOpt.isPresent()) {
            ra.addFlashAttribute("error", "Cliente no encontrado");
            return "redirect:/clientes";
        }

        model.addAttribute("cliente", clienteOpt.get());
        model.addAttribute("titulo", "Detalles del Cliente");
        
        return "clientes/perfil";
    }

    // Eliminar cliente (eliminación física)
    @PostMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id,
                                  HttpSession session,
                                  RedirectAttributes ra) {
        if (!verificarSesion(session, ra)) {
            return "redirect:/login";
        }

        try {
            Optional<Cliente> clienteOpt = clienteService.obtenerPorId(id);
            
            if (!clienteOpt.isPresent()) {
                ra.addFlashAttribute("error", "Cliente no encontrado");
                return "redirect:/clientes";
            }

            // Verificar que no se elimine a sí mismo
            Cliente clienteLogueado = (Cliente) session.getAttribute("clienteLogueado");
            if (clienteLogueado.getId().equals(id)) {
                ra.addFlashAttribute("error", "No puedes eliminar tu propia cuenta desde aquí");
                return "redirect:/clientes";
            }

            String nombreCliente = clienteOpt.get().getNombreCompleto();
            clienteService.eliminar(id);
            ra.addFlashAttribute("success", "Cliente " + nombreCliente + " eliminado exitosamente");
            
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al eliminar el cliente: " + e.getMessage());
        }
        
        return "redirect:/clientes";
    }

    // Desactivar cliente (eliminación lógica)
    @PostMapping("/desactivar/{id}")
    public String desactivarCliente(@PathVariable Long id,
                                    HttpSession session,
                                    RedirectAttributes ra) {
        if (!verificarSesion(session, ra)) {
            return "redirect:/login";
        }

        try {
            Optional<Cliente> clienteOpt = clienteService.obtenerPorId(id);
            
            if (!clienteOpt.isPresent()) {
                ra.addFlashAttribute("error", "Cliente no encontrado");
                return "redirect:/clientes";
            }

            // Verificar que no se desactive a sí mismo
            Cliente clienteLogueado = (Cliente) session.getAttribute("clienteLogueado");
            if (clienteLogueado.getId().equals(id)) {
                ra.addFlashAttribute("error", "No puedes desactivar tu propia cuenta desde aquí");
                return "redirect:/clientes";
            }

            String nombreCliente = clienteOpt.get().getNombreCompleto();
            clienteService.desactivar(id);
            ra.addFlashAttribute("success", "Cliente " + nombreCliente + " desactivado exitosamente");
            
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al desactivar el cliente: " + e.getMessage());
        }
        
        return "redirect:/clientes";
    }

    // Activar cliente
    @PostMapping("/activar/{id}")
    public String activarCliente(@PathVariable Long id,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        if (!verificarSesion(session, ra)) {
            return "redirect:/login";
        }

        try {
            Optional<Cliente> clienteOpt = clienteService.obtenerPorId(id);
            
            if (!clienteOpt.isPresent()) {
                ra.addFlashAttribute("error", "Cliente no encontrado");
                return "redirect:/clientes";
            }

            String nombreCliente = clienteOpt.get().getNombreCompleto();
            clienteService.activar(id);
            ra.addFlashAttribute("success", "Cliente " + nombreCliente + " activado exitosamente");
            
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al activar el cliente: " + e.getMessage());
        }
        
        return "redirect:/clientes";
    }

    // Listar clientes inactivos
    @GetMapping("/inactivos")
    public String listarInactivos(HttpSession session,
                                  Model model,
                                  RedirectAttributes ra) {
        if (!verificarSesion(session, ra)) {
            return "redirect:/login";
        }

        List<Cliente> todosClientes = clienteService.listarTodos();
        List<Cliente> clientesInactivos = todosClientes.stream()
                .filter(c -> !c.getActivo())
                .toList();
        
        long totalActivos = clienteService.contarActivos();
        
        model.addAttribute("clientes", clientesInactivos);
        model.addAttribute("totalActivos", totalActivos);
        model.addAttribute("titulo", "Clientes Inactivos");
        model.addAttribute("mostrarInactivos", true);
        
        return "clientes/inactivos";
    }

    // Cambiar contraseña de cliente (solo para administración)
    @GetMapping("/cambiar-password/{id}")
    public String cambiarPasswordForm(@PathVariable Long id,
                                      HttpSession session,
                                      Model model,
                                      RedirectAttributes ra) {
        if (!verificarSesion(session, ra)) {
            return "redirect:/login";
        }

        Optional<Cliente> clienteOpt = clienteService.obtenerPorId(id);
        
        if (!clienteOpt.isPresent()) {
            ra.addFlashAttribute("error", "Cliente no encontrado");
            return "redirect:/clientes";
        }

        model.addAttribute("cliente", clienteOpt.get());
        model.addAttribute("titulo", "Cambiar Contraseña");
        
        return "clientes/cambiar-password";
    }

    @PostMapping("/cambiar-password/{id}")
    public String cambiarPassword(@PathVariable Long id,
                                  @RequestParam String password,
                                  @RequestParam String password2,
                                  HttpSession session,
                                  RedirectAttributes ra) {
        if (!verificarSesion(session, ra)) {
            return "redirect:/login";
        }

        // Validar que las contraseñas coincidan
        if (!password.equals(password2)) {
            ra.addFlashAttribute("error", "Las contraseñas no coinciden");
            return "redirect:/clientes/cambiar-password/" + id;
        }

        // Validar longitud mínima
        if (password.length() < 8) {
            ra.addFlashAttribute("error", "La contraseña debe tener al menos 8 caracteres");
            return "redirect:/clientes/cambiar-password/" + id;
        }

        try {
            Optional<Cliente> clienteOpt = clienteService.obtenerPorId(id);
            
            if (!clienteOpt.isPresent()) {
                ra.addFlashAttribute("error", "Cliente no encontrado");
                return "redirect:/clientes";
            }

            Cliente cliente = clienteOpt.get();
            cliente.setPassword(password);
            clienteService.actualizar(id, cliente);
            
            ra.addFlashAttribute("success", 
                               "Contraseña actualizada exitosamente para " + cliente.getNombreCompleto());
            return "redirect:/clientes";
            
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al cambiar la contraseña: " + e.getMessage());
            return "redirect:/clientes";
        }
    }
}