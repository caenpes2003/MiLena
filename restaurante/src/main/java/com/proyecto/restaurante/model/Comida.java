package com.proyecto.restaurante.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comida {
    private Long id;
    private String slug;
    private String nombre;
    private String descripcion;
    private Integer precio;
    private String imagenUrl;
    private Boolean activo;
}
