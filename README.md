# 🍽️ Proyecto Restaurante — Sprint Actual

Este sprint consiste en:
- Generar **mockups de todas las páginas**, cada uno asociado a una URL.
- Diagramar la **navegación entre páginas** (cómo se llega de una a otra).
- Preparar la migración del trabajo previo hacia **Spring Boot (MVC + capas)**.

---

## 📋 Mockups y URLs

Los siguientes mockups fueron diseñados en **Figma Maker**.  
Cada mockup se exportó como imagen y está asociado a su respectiva ruta en la aplicación:

- [Landing Page](/)  
  ![Landing](components/figma/mockups/landing.png)

- [Login](/login)  
  ![Login](components/figma/mockups/login.png)

- [Register](/register)  
  ![Register](components/figma/mockups/register.png)

- [Menú (Tarjetas)](/menu)  
  ![Menu](components/figma/mockups/menu.png)

- [Menú (Tabla)](/menu/table)  
  ![Menu Tabla](components/figma/mockups/menu-table.png)

- [Detalle de producto](/producto/arepa-rellena)  
  ![Detalle](components/figma/mockups/producto-arepa-rellena.png)

Las imágenes están ubicadas en la carpeta:  
`components/figma/mockups/`

---

## 🔀 Diagrama de Navegación

## 🔀 Diagrama de Navegación

```mermaid
flowchart LR
  L[Landing] --> M[Menú (tarjetas)]
  L --> T[Menú (tabla)]
  L --> LG[Login]
  L --> SG[Register]
  L --- SM[Sección menú] & SR[Sección reservas] & SMM[Sección momentos] & SC[Sección contacto]

  M --> D[Detalle producto]
  T --> D
  LG --> SR


