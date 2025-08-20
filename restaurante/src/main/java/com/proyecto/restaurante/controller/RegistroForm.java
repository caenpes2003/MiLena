package com.proyecto.restaurante.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistroForm {
    @NotBlank
    private String nombre;
    @NotBlank
    private String apellido;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String telefono;
    @NotBlank
    private String direccion;
    @NotBlank
    @Size(min = 8)
    private String password;
    @NotBlank
    @Size(min = 8)
    private String password2;
    private boolean terms;
    private boolean marketing;

    // getters/setters públicos
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String v) {
        this.nombre = v;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String v) {
        this.apellido = v;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String v) {
        this.email = v;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String v) {
        this.telefono = v;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String v) {
        this.direccion = v;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String v) {
        this.password = v;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String v) {
        this.password2 = v;
    }

    public boolean isTerms() {
        return terms;
    }

    public void setTerms(boolean v) {
        this.terms = v;
    }

    public boolean isMarketing() {
        return marketing;
    }

    public void setMarketing(boolean v) {
        this.marketing = v;
    }
}
