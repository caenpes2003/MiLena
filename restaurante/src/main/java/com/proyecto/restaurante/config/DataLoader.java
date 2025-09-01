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
                        // CARNES - ESPECIALIDAD DE LA CASA
                        new Comida(null, "churrasco-argentino", "Churrasco Argentino",
                                "Jugosa carne, corte importado al punto perfecto", 25000,
                                "/assets/menu1.png", true, "res", null),
                        
                        new Comida(null, "costillas-bbq", "Costillas BBQ a la Leña",
                                "Glaseado casero y humo suave, se desprenden del hueso", 28000,
                                "/assets/menu5.png", true, "res", null),
                        
                        new Comida(null, "bandeja-mi-lena", "Bandeja Mi Leña",
                                "Selección mixta de la casa: res, cerdo y pollo para compartir", 35000,
                                "/assets/menu7.png", true, "res", null),
                        
                        new Comida(null, "chorizo-santandereano", "Chorizo Santandereano",
                                "Con arepa de maíz pelao, tradición antioqueña", 18000,
                                "/assets/menu2.png", true, "res", null),
                        
                        new Comida(null, "chorizo-arepa", "Chorizo con Arepa",
                                "Santarrosano, a la brasa, con arepa de maíz y guacamole", 16000,
                                "/assets/menu8.png", true, "res", null),
                        
                        new Comida(null, "lomo-al-trapo", "Lomo al Trapo",
                                "Técnica tradicional envuelto en sal y trapo", 32000,
                                "/assets/menu1.png", true, "res", null),
                        
                        new Comida(null, "bife-angosto", "Bife de Chorizo",
                                "Corte premium argentino con chimichurri", 27000,
                                "/assets/menu1.png", true, "res", null),
                        
                        new Comida(null, "entraña-marinada", "Entraña Marinada",
                                "24 horas en marinado especial de la casa", 24000,
                                "/assets/menu1.png", true, "res", null),
                        
                        new Comida(null, "sobrebarriga-lena", "Sobrebarriga a la Leña",
                                "Cocción lenta con guiso criollo", 22000,
                                "/assets/menu1.png", true, "res", null),
                        
                        new Comida(null, "carne-desmechada", "Carne Desmechada",
                                "Guiso tradicional con yuca y arepa", 19000,
                                "/assets/menu1.png", true, "res", null),

                        // POLLOS
                        new Comida(null, "pechuga-parrilla", "Pechuga a la Parrilla",
                                "Marinada en especias, cocción a la leña y ensalada fresca", 22000,
                                "/assets/menu4.png", true, "pollo", null),
                        
                        new Comida(null, "pollo-entero-lena", "Pollo Entero a la Leña",
                                "Para 4 personas, marinado 12 horas", 45000,
                                "/assets/menu4.png", true, "pollo", null),
                        
                        new Comida(null, "alitas-picantes", "Alitas Picantes",
                                "12 unidades con salsa especial", 18000,
                                "/assets/menu4.png", true, "pollo", null),
                        
                        new Comida(null, "muslos-hierbas", "Muslos en Hierbas",
                                "Cocción a fuego lento con romero y tomillo", 20000,
                                "/assets/menu4.png", true, "pollo", null),
                        
                        new Comida(null, "ajiaco-santafereno", "Ajiaco Santafereño",
                                "Tres papas, pollo y crema; el clásico que reconforta", 20000,
                                "/assets/menu6.png", true, "pollo", null),

                        // PESCADOS
                        new Comida(null, "trucha-lena", "Trucha a la Leña",
                                "Pescado fresco con vegetales grillados", 26000,
                                "/assets/menu3.png", true, "pescado", null),
                        
                        new Comida(null, "salmon-parrilla", "Salmón a la Parrilla",
                                "Con salsa de eneldo y limón", 29000,
                                "/assets/menu3.png", true, "pescado", null),
                        
                        new Comida(null, "mojarra-frita", "Mojarra Frita",
                                "Entera con patacones y ensalada", 23000,
                                "/assets/menu3.png", true, "pescado", null),
                        
                        new Comida(null, "filete-bagre", "Filete de Bagre",
                                "A la plancha con arroz de coco", 21000,
                                "/assets/menu3.png", true, "pescado", null),

                        // CERDO
                        new Comida(null, "costillas-cerdo", "Costillas de Cerdo",
                                "Marinadas en cerveza y miel", 25000,
                                "/assets/menu5.png", true, "cerdo", null),
                        
                        new Comida(null, "lechona-tolimense", "Lechona Tolimense",
                                "Porción generosa con arepa", 17000,
                                "/assets/menu5.png", true, "cerdo", null),
                        
                        new Comida(null, "chicharron-carnudo", "Chicharrón Carnudo",
                                "Con yuca cocida y hogao", 16000,
                                "/assets/menu5.png", true, "cerdo", null),
                        
                        new Comida(null, "lomo-cerdo-ahumado", "Lomo de Cerdo Ahumado",
                                "Proceso de 6 horas a baja temperatura", 24000,
                                "/assets/menu5.png", true, "cerdo", null),

                        // VEGETARIANOS
                        new Comida(null, "mazorcada-gratinada", "Mazorcada Gratinada",
                                "Maíz tierno gratinado con queso y especias", 15000,
                                "/assets/menu3.png", true, "vegetariano", null),
                        
                        new Comida(null, "arepa-rellena", "Arepa Rellena",
                                "Con queso, frijoles y aguacate", 12000,
                                "/assets/menu3.png", true, "vegetariano", null),
                        
                        new Comida(null, "quesadilla-campesina", "Quesadilla Campesina",
                                "Tortilla artesanal con queso fundido", 14000,
                                "/assets/menu3.png", true, "vegetariano", null),
                        
                        new Comida(null, "ensalada-grillada", "Ensalada de Vegetales Grillados",
                                "Mix de verduras asadas con vinagreta", 13000,
                                "/assets/menu3.png", true, "vegetariano", null),

                        // SOPAS
                        new Comida(null, "sancocho-gallina", "Sancocho de Gallina",
                                "Tradicional con yuca, ñame y plátano", 18000,
                                "/assets/menu6.png", true, "sopa", null),
                        
                        new Comida(null, "mondongo-boyacense", "Mondongo Boyacense",
                                "Receta de la abuela con papa criolla", 16000,
                                "/assets/menu6.png", true, "sopa", null),
                        
                        new Comida(null, "caldo-costilla", "Caldo de Costilla",
                                "Para curar el guayabo, con arepa", 15000,
                                "/assets/menu6.png", true, "sopa", null),

                        // BEBIDAS ALCOHÓLICAS
                        new Comida(null, "refajo-clasico", "Refajo Clásico",
                                "Cerveza con colombiana, la tradición de siempre", 8000,
                                "/assets/menu10.png", true, "bebidas", null),
                        
                        new Comida(null, "cerveza-artesanal", "Cerveza Artesanal",
                                "Producción local, varios estilos", 12000,
                                "/assets/menu10.png", true, "bebidas", null),
                        
                        new Comida(null, "aguardiente-antioqueño", "Aguardiente Antioqueño",
                                "Botella compartida con limón", 45000,
                                "/assets/menu10.png", true, "bebidas", null),
                        
                        new Comida(null, "ron-viejo-caldas", "Ron Viejo de Caldas",
                                "Copa doble con hielo", 15000,
                                "/assets/menu10.png", true, "bebidas", null),

                        // BEBIDAS SIN ALCOHOL
                        new Comida(null, "limonada-natural", "Limonada Natural",
                                "Limón fresco con hielo y azúcar al gusto", 6000,
                                "/assets/menu9.png", true, "bebidas", null),
                        
                        new Comida(null, "jugo-lulo", "Jugo de Lulo",
                                "Fruta fresca, endulzado naturalmente", 7000,
                                "/assets/menu9.png", true, "bebidas", null),
                        
                        new Comida(null, "chicha-maiz", "Chicha de Maíz",
                                "Bebida tradicional fermentada", 5000,
                                "/assets/menu9.png", true, "bebidas", null),
                        
                        new Comida(null, "agua-panela-limon", "Agua de Panela con Limón",
                                "Caliente o fría, perfecta para el clima", 4000,
                                "/assets/menu9.png", true, "bebidas", null),

                        // POSTRES
                        new Comida(null, "tres-leches", "Torta Tres Leches",
                                "Esponjosa y cremosa, receta de la casa", 8000,
                                "/assets/menu3.png", true, "postre", null),
                        
                        new Comida(null, "flan-caramelo", "Flan de Caramelo",
                                "Tradicional con salsa de arequipe", 7000,
                                "/assets/menu3.png", true, "postre", null),
                        
                        new Comida(null, "helado-mamoncillo", "Helado de Mamoncillo",
                                "Sabor tropical artesanal", 6000,
                                "/assets/menu3.png", true, "postre", null),

                        // PARRILLADAS ESPECIALES
                        new Comida(null, "parrillada-familiar", "Parrillada Familiar",
                                "Para 6 personas: res, cerdo, pollo y chorizos", 120000,
                                "/assets/menu7.png", true, "parrillada", null),
                        
                        new Comida(null, "parrillada-mar-tierra", "Parrillada Mar y Tierra",
                                "Carnes mixtas con trucha y camarones", 85000,
                                "/assets/menu7.png", true, "parrillada", null));

                for (Comida comida : comidas) {
                        try {
                                comidaService.guardar(comida);
                                System.out.println("✅ Comida cargada: " + comida.getNombre());
                        } catch (Exception e) {
                                System.err.println("❌ Error cargando comida " + comida.getNombre() + ": " + e.getMessage());
                        }
                }
                System.out.println("📊 TOTAL COMIDAS CARGADAS: " + comidas.size());
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
                                System.out.println("✅ Cliente cargado: " + cliente.getNombreCompleto());
                        } catch (Exception e) {
                                System.err.println("❌ Error cargando cliente " + cliente.getNombreCompleto() + ": "
                                                + e.getMessage());
                        }
                }
        }

        private void cargarAdicionales() {
                // Crear 20+ adicionales SIN asignar a comida específica
                // Según instrucción del profesor: se crean independientes y luego se asignan desde comidas
                List<Adicional> adicionales = Arrays.asList(
                        // GUARNICIONES
                        new Adicional(null, "Papas a la Francesa", 5000,
                                        "Papas doradas y crujientes", true, null),
                        
                        new Adicional(null, "Yuca Frita", 4500,
                                        "Yuca dorada y crujiente", true, null),
                        
                        new Adicional(null, "Patacones", 5500,
                                        "Plátano verde aplastado y frito", true, null),
                        
                        new Adicional(null, "Arroz Blanco", 3000,
                                        "Arroz blanco tradicional", true, null),
                        
                        new Adicional(null, "Frijoles Rojos", 4000,
                                        "Frijoles rojos con garra", true, null),
                        
                        new Adicional(null, "Arepa de Maíz", 2500,
                                        "Arepa tradicional antioqueña", true, null),
                        
                        new Adicional(null, "Papa Criolla", 6000,
                                        "Papitas criollas salteadas", true, null),

                        // SALSAS Y ADEREZOS
                        new Adicional(null, "Chimichurri Extra", 2000,
                                        "Porción adicional de nuestra salsa especial", true, null),
                        
                        new Adicional(null, "Salsa BBQ", 1500,
                                        "Salsa barbacoa casera", true, null),
                        
                        new Adicional(null, "Hogao Casero", 2500,
                                        "Salsa criolla con tomate y cebolla", true, null),
                        
                        new Adicional(null, "Ají Picante", 1000,
                                        "Ají de la casa, nivel picante", true, null),
                        
                        new Adicional(null, "Guacamole", 4500,
                                        "Aguacate fresco con especias", true, null),

                        // PROTEÍNAS ADICIONALES
                        new Adicional(null, "Porción Extra de Carne", 8000,
                                        "Más carne para compartir", true, null),
                        
                        new Adicional(null, "Chorizo Parrillero", 6000,
                                        "Chorizo argentino a la parrilla", true, null),
                        
                        new Adicional(null, "Morcilla Antioqueña", 5500,
                                        "Morcilla tradicional con arroz", true, null),
                        
                        new Adicional(null, "Chicharrón Prensado", 5000,
                                        "Chicharrón crujiente", true, null),

                        // ENSALADAS
                        new Adicional(null, "Ensalada César", 6000,
                                        "Lechuga romana, crutones y aderezo césar", true, null),
                        
                        new Adicional(null, "Ensalada Mixta", 5000,
                                        "Lechuga, tomate, cebolla y zanahoria", true, null),
                        
                        new Adicional(null, "Vegetales Grillados", 5500,
                                        "Mix de vegetales a la parrilla", true, null),

                        // EXTRAS ESPECIALES
                        new Adicional(null, "Aguacate", 3500,
                                        "Palta fresca en tajadas", true, null),
                        
                        new Adicional(null, "Queso Gratinado", 4000,
                                        "Queso mozzarella gratinado", true, null),
                        
                        new Adicional(null, "Huevo Frito", 2500,
                                        "Huevo campesino frito", true, null),
                        
                        new Adicional(null, "Tocineta Crocante", 4500,
                                        "Tocineta ahumada crujiente", true, null),
                        
                        new Adicional(null, "Porción de Limón", 0,
                                        "Limones frescos - Gratis", true, null));

                for (Adicional adicional : adicionales) {
                        try {
                                adicionalService.guardar(adicional);
                                System.out.println("✅ Adicional cargado: " + adicional.getNombre() + 
                                        " - " + adicional.getPrecioFormateado());
                        } catch (Exception e) {
                                System.err.println("❌ Error cargando adicional " + adicional.getNombre() +
                                                ": " + e.getMessage());
                        }
                }
                System.out.println("📊 TOTAL ADICIONALES CARGADOS: " + adicionales.size());
        }
}