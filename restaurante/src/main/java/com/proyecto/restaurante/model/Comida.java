package com.proyecto.restaurante.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "comidas")
public class Comida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El slug es obligatorio")
    @Size(min = 2, max = 100, message = "El slug debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
    @Column(nullable = false, length = 500)
    private String descripcion;

    @Positive(message = "El precio debe ser positivo")
    @Column(nullable = false)
    private Integer precio;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(nullable = false)
    private Boolean activo = true;

    @Size(max = 50, message = "La categoría no puede exceder 50 caracteres")
    @Column(length = 50)
    private String categoria;

    // Relación con Adicionales (Uno a Muchos)
    @OneToMany(mappedBy = "comida", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Adicional> adicionales;

    // Constructores
    public Comida() {
    }

    public Comida(Long id, String slug, String nombre, String descripcion, Integer precio,
            String imagenUrl, Boolean activo, String categoria, List<Adicional> adicionales) {
        this.id = id;
        this.slug = slug;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagenUrl = imagenUrl;
        this.activo = activo;
        this.categoria = categoria;
        this.adicionales = adicionales;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getPrecio() {
        return precio;
    }

    public void setPrecio(Integer precio) {
        this.precio = precio;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public List<Adicional> getAdicionales() {
        return adicionales;
    }

    public void setAdicionales(List<Adicional> adicionales) {
        this.adicionales = adicionales;
    }

    // Métodos de conveniencia
    public String getPrecioFormateado() {
        return String.format("$%,d", precio);
    }

    public boolean tieneAdicionales() {
        return adicionales != null && !adicionales.isEmpty();
    }

    public List<Adicional> getAdicionalesActivos() {
        if (adicionales == null) {
            return List.of();
        }
        return adicionales.stream()
                .filter(a -> Boolean.TRUE.equals(a.getActivo()))
                .toList();
    }
}