package com.proyecto.restaurante.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "adicionales")
public class Adicional {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre del adicional es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @PositiveOrZero(message = "El precio debe ser positivo o cero")
    @Column(nullable = false)
    private Integer precio;
    
    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    @Column(length = 200)
    private String descripcion;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    // Relación con Comida (Muchos a Uno)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comida_id")
    private Comida comida;

    // Constructores
    public Adicional() {}
    
    public Adicional(Long id, String nombre, Integer precio, String descripcion, Boolean activo, Comida comida) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.activo = activo;
        this.comida = comida;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public Integer getPrecio() { return precio; }
    public void setPrecio(Integer precio) { this.precio = precio; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public Comida getComida() { return comida; }
    public void setComida(Comida comida) { this.comida = comida; }
    
    // Método de conveniencia
    public String getPrecioFormateado() {
        if (precio == 0) {
            return "Gratis";
        }
        return String.format("$%,d", precio);
    }
}