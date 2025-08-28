package com.proyecto.restaurante.config;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.proyecto.restaurante.model.Adicional;
import com.proyecto.restaurante.model.Cliente;
import com.proyecto.restaurante.model.Comida;
import com.proyecto.restaurante.service.IAdicionalService;
import com.proyecto.restaurante.service.IClienteService;
import com.proyecto.restaurante.service.IComidaService;

@Component
public class DataLoader implements CommandLineRunner {

        @Autowired
        private IComidaService comidaService;

        @Autowired
        private IClienteService clienteService;

        @Autowired
        private IAdicionalService adicionalService;

        @Override
        public void run(String... args) throws Exception {
                cargarComidas();
                cargarClientes();
                cargarAdicionales();
        }

        private void cargarComidas() {
                List<Comida> comidas = Arrays.asList(
                                new Comida(null, "churrasco-argentino", "Churrasco Argentino",
                                                "Jugosa carne, corte importado al punto perfecto", 25000,
                                                "/assets/menu1.png", true, "res", null),

                                new Comida(null, "chorizo-santandereano", "Chorizo Santandereano",
                                                "Con arepa de maíz pelao, tradición antioqueña", 18000,
                                                "/assets/menu2.png", true, "res", null),

                                new Comida(null, "mazorcada-gratinada", "Mazorcada Gratinada",
                                                "Maíz tierno gratinado con queso y especias", 15000,
                                                "/assets/menu3.png", true, "otros", null),

                                new Comida(null, "pechuga-parrilla", "Pechuga a la Parrilla",
                                                "Marinada en especias, cocción a la leña y ensalada fresca", 22000,
                                                "/assets/menu4.png", true, "pollo", null),

                                new Comida(null, "costillas-bbq", "Costillas BBQ a la Leña",
                                                "Glaseado casero y humo suave, se desprenden del hueso", 28000,
                                                "/assets/menu5.png", true, "res", null),

                                new Comida(null, "ajiaco-santafereno", "Ajiaco Santafereño",
                                                "Tres papas, pollo y crema; el clásico que reconforta", 20000,
                                                "/assets/menu6.png", true, "pollo", null),

                                new Comida(null, "bandeja-mi-lena", "Bandeja Mi Leña",
                                                "Selección mixta de la casa: res, cerdo y pollo para compartir", 35000,
                                                "/assets/menu7.png", true, "res", null),

                                new Comida(null, "chorizo-arepa", "Chorizo con Arepa",
                                                "Santarrosano, a la brasa, con arepa de maíz y guacamole", 16000,
                                                "/assets/menu8.png", true, "res", null),

                                new Comida(null, "refajo-clasico", "Refajo Clásico",
                                                "Cerveza con colombiana, la tradición de siempre", 8000,
                                                "/assets/menu10.png", true, "bebidas", null),

                                new Comida(null, "limonada-natural", "Limonada Natural",
                                                "Limón fresco con hielo y azúcar al gusto", 6000,
                                                "/assets/menu9.png", true, "bebidas", null));

                for (Comida comida : comidas) {
                        try {
                                comidaService.guardar(comida);
                                System.out.println("Comida cargada: " + comida.getNombre());
                        } catch (Exception e) {
                                System.err.println(
                                                "Error cargando comida " + comida.getNombre() + ": " + e.getMessage());
                        }
                }
        }

        private void cargarClientes() {
                List<Cliente> clientes = Arrays.asList(
                                new Cliente(null, "Juan", "Pérez", "juan.perez@email.com", "12345678",
                                                "3001234567", "Calle 123 #45-67", LocalDateTime.now(), true),

                                new Cliente(null, "María", "González", "maria.gonzalez@email.com", "87654321",
                                                "3007654321", "Carrera 45 #23-12", LocalDateTime.now(), true),

                                new Cliente(null, "Carlos", "Rodríguez", "carlos.rodriguez@email.com", "password123",
                                                "3009876543", "Avenida 68 #34-56", LocalDateTime.now(), true),

                                new Cliente(null, "Ana", "Martínez", "ana.martinez@email.com", "mipassword",
                                                "3002345678", "Calle 85 #12-34", LocalDateTime.now(), true),

                                new Cliente(null, "Luis", "López", "luis.lopez@email.com", "clave123",
                                                "3005432167", "Carrera 15 #78-90", LocalDateTime.now(), true));

                for (Cliente cliente : clientes) {
                        try {
                                clienteService.guardar(cliente);
                                System.out.println("Cliente cargado: " + cliente.getNombreCompleto());
                        } catch (Exception e) {
                                System.err.println("Error cargando cliente " + cliente.getNombreCompleto() + ": "
                                                + e.getMessage());
                        }
                }
        }

        private void cargarAdicionales() {
                // Esperar a que las comidas se carguen
                List<Comida> todasLasComidas = comidaService.listarActivas();

                if (todasLasComidas.isEmpty()) {
                        System.err.println("No hay comidas cargadas, no se pueden crear adicionales");
                        return;
                }

                // Buscar comidas específicas para asignar adicionales
                Comida churrasco = comidaService.obtenerPorSlug("churrasco-argentino").orElse(null);
                Comida pechuga = comidaService.obtenerPorSlug("pechuga-parrilla").orElse(null);
                Comida bandeja = comidaService.obtenerPorSlug("bandeja-mi-lena").orElse(null);

                List<Adicional> adicionales = Arrays.asList(
                                // Adicionales para Churrasco
                                new Adicional(null, "Papas a la Francesa", 5000,
                                                "Papas doradas y crujientes", true, churrasco),
                                new Adicional(null, "Chimichurri Extra", 2000,
                                                "Porción adicional de nuestra salsa especial", true, churrasco),
                                new Adicional(null, "Arepa con Queso", 4000,
                                                "Arepa de maíz con queso costeño", true, churrasco),

                                // Adicionales para Pechuga
                                new Adicional(null, "Ensalada César", 6000,
                                                "Lechuga romana, crutones y aderezo césar", true, pechuga),
                                new Adicional(null, "Salsa BBQ", 1500,
                                                "Salsa barbacoa casera", true, pechuga),
                                new Adicional(null, "Vegetales Grillados", 5500,
                                                "Mix de vegetales a la parrilla", true, pechuga),

                                // Adicionales para Bandeja (para compartir)
                                new Adicional(null, "Porción Extra de Carne", 8000,
                                                "Más carne para compartir", true, bandeja),
                                new Adicional(null, "Arroz Blanco", 3000,
                                                "Arroz blanco tradicional", true, bandeja),
                                new Adicional(null, "Yuca Frita", 4500,
                                                "Yuca dorada y crujiente", true, bandeja),

                                // Adicionales generales (sin comida específica - pueden usarse para todas)
                                new Adicional(null, "Aguacate", 3500,
                                                "Palta fresca en tajadas", true, null),
                                new Adicional(null, "Queso Gratinado", 4000,
                                                "Queso mozzarella gratinado", true, null),
                                new Adicional(null, "Porción de Limón", 0,
                                                "Limones frescos - Gratis", true, null));

                for (Adicional adicional : adicionales) {
                        try {
                                adicionalService.guardar(adicional);
                                String comidaNombre = adicional.getComida() != null ? adicional.getComida().getNombre()
                                                : "General";
                                System.out.println("Adicional cargado: " + adicional.getNombre() +
                                                " (para: " + comidaNombre + ")");
                        } catch (Exception e) {
                                System.err.println("Error cargando adicional " + adicional.getNombre() +
                                                ": " + e.getMessage());
                        }
                }
        }
}