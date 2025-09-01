# Diagrama Entidad-Relación (ER) COMPLETO
## Sistema de Gestión - Restaurante "Mi Leña"

### 🎯 **MODELO COMPLETO DEL PROYECTO**

**Entidades Implementadas (3):**
- ✅ **COMIDA** - Menú del restaurante
- ✅ **ADICIONAL** - Complementos opcionales  
- ✅ **CLIENTE** - Usuarios del sistema

**Relaciones Activas (1):**
- ✅ **COMIDA ↔ ADICIONAL** (One-to-Many)

---

## 📊 **Entidades del Sistema**

### 🍽️ **COMIDA**
**Tabla:** `comidas`

| Campo | Tipo | Restricciones | Descripción |
|-------|------|---------------|-------------|
| **id** | `Long` | `PK, AUTO_INCREMENT` | Identificador único |
| **slug** | `String(100)` | `UNIQUE, NOT NULL` | URL amigable para SEO |
| **nombre** | `String(100)` | `NOT NULL` | Nombre del plato |
| **descripcion** | `String(500)` | `NOT NULL` | Descripción detallada |
| **precio** | `Integer` | `NOT NULL, > 0` | Precio en pesos colombianos |
| **imagen_url** | `String(500)` | `NULLABLE` | URL de la imagen |
| **activo** | `Boolean` | `NOT NULL, DEFAULT TRUE` | Estado activo/inactivo |
| **categoria** | `String(50)` | `NULLABLE` | Categoría (res, pollo, pescado, etc.) |

### ➕ **ADICIONAL**
**Tabla:** `adicionales`

| Campo | Tipo | Restricciones | Descripción |
|-------|------|---------------|-------------|
| **id** | `Long` | `PK, AUTO_INCREMENT` | Identificador único |
| **nombre** | `String(100)` | `NOT NULL` | Nombre del adicional |
| **precio** | `Integer` | `NOT NULL, >= 0` | Precio (puede ser 0 = gratis) |
| **descripcion** | `String(200)` | `NULLABLE` | Descripción del adicional |
| **activo** | `Boolean` | `NOT NULL, DEFAULT TRUE` | Estado activo/inactivo |
| **comida_id** | `Long` | `FK, NULLABLE` | Referencia a comida asignada |

### 👤 **CLIENTE**
**Tabla:** `clientes`

| Campo | Tipo | Restricciones | Descripción |
|-------|------|---------------|-------------|
| **id** | `Long` | `PK, AUTO_INCREMENT` | Identificador único |
| **nombre** | `String(50)` | `NOT NULL` | Nombre del cliente |
| **apellido** | `String(50)` | `NOT NULL` | Apellido del cliente |
| **email** | `String(100)` | `UNIQUE, NOT NULL` | Correo electrónico |
| **password** | `String` | `NOT NULL, MIN 8` | Contraseña cifrada |
| **telefono** | `String(15)` | `NOT NULL` | Teléfono de contacto |
| **direccion** | `String(200)` | `NOT NULL` | Dirección de entrega |
| **fecha_registro** | `LocalDateTime` | `NOT NULL, AUTO` | Fecha de registro |
| **activo** | `Boolean` | `NOT NULL, DEFAULT TRUE` | Estado activo/inactivo |

---

## 🔗 **Relaciones**

### **1️⃣ COMIDA ↔ ADICIONAL (Uno a Muchos)**
- **Cardinalidad:** 1:N (Una comida puede tener muchos adicionales)
- **Tipo:** Opcional (adicionales pueden existir sin comida asignada)
- **FK:** `adicionales.comida_id` → `comidas.id`
- **Comportamiento:** ON DELETE SET NULL (preserva adicionales)

```java
// En Comida.java
@OneToMany(mappedBy = "comida", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private List<Adicional> adicionales;

// En Adicional.java  
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "comida_id")
private Comida comida;
```

### **2️⃣ CLIENTE (Entidad Independiente)**
- **Sin relaciones directas** con otras entidades en la versión actual
- Preparado para futuras relaciones (pedidos, favoritos, etc.)
- Gestión de autenticación y perfil de usuario


## 🗄️ **Esquema de Base de Datos (DDL)**

```sql
-- Tabla comidas
CREATE TABLE comidas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    slug VARCHAR(100) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    precio INTEGER NOT NULL CHECK (precio > 0),
    imagen_url VARCHAR(500),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    categoria VARCHAR(50)
);

-- Tabla clientes  
CREATE TABLE clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(15) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabla adicionales
CREATE TABLE adicionales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio INTEGER NOT NULL CHECK (precio >= 0),
    descripcion VARCHAR(200),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    comida_id BIGINT,
    FOREIGN KEY (comida_id) REFERENCES comidas(id) ON DELETE SET NULL
);
```

---

## 📈 **Estadísticas del Modelo**

| Métrica | Valor | Descripción |
|---------|--------|-------------|
| **Entidades Principales** | 3 | Comida, Adicional, Cliente |
| **Relaciones Activas** | 1 | Comida-Adicional (1:N) |
| **Campos Totales** | 22 | Con validaciones JPA completas |
| **Índices Únicos** | 2 | comidas.slug, clientes.email |
| **Claves Foráneas** | 1 | adicionales.comida_id |

---

## 🔧 **Arquitectura Técnica**

### **Stack Tecnológico:**
- **Spring Boot 3.2.0** - Framework principal
- **Spring Data JPA** - Persistencia de datos
- **Hibernate 6.3.1** - ORM implementation
- **H2 Database** - Base de datos de desarrollo
- **MySQL** - Base de datos de producción (configurada)
- **Jakarta Bean Validation** - Validaciones
- **Thymeleaf** - Motor de templates

### **Patrones Implementados:**
- **Repository Pattern** con Spring Data JPA
- **Service Layer Pattern** con interfaces
- **MVC Pattern** para controllers y vistas
- **Validation Pattern** con Bean Validation

### **Consultas Principales:**
```java
// Adicionales por comida
findByComidaIdAndActivoTrue(comidaId)

// Adicionales disponibles para asignación
findByComidaIsNullAndActivoTrue()

// Comidas por categoría
findByCategoriaAndActivoTrue(categoria)

// Búsqueda por término
buscarPorTermino(termino)

// Obtener comida por slug
findBySlug(slug)
```

---

## 🔮 **Expansiones Futuras Previstas**

El modelo actual está diseñado para soportar las siguientes expansiones:

| Entidad | Relación | Propósito |
|---------|-----------|-----------|
| **PEDIDO** | Cliente → Pedidos | Sistema de órdenes |
| **PEDIDO_ITEM** | Pedido ↔ Comida (N:M) | Items por pedido |
| **CATEGORIA** | Normalización | Gestión de categorías |
| **USUARIO_ADMIN** | Roles | Administración del sistema |
| **FAVORITO** | Cliente ↔ Comida (N:M) | Comidas favoritas |
| **COMENTARIO** | Cliente → Comida | Reviews y ratings |

---

**📅 Creado:** Sprint 4 - Sistema Mi Leña  
**🔧 Tecnología:** Spring Boot + JPA + H2/MySQL  
**📊 Estado:** ✅ Implementado y Funcional  
**🎯 Cobertura:** 100% del Modelo Actual del Proyecto