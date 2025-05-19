package com.personalprojects.model;
public class Usuario {
    private int idUsuario;
    private String nombreUsuario;
    private String contrasena;
    private String email;

    public Usuario(String nombreUsuario, String contrasena, String email) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.email = email;
    }

    public Usuario(int idUsuario, String nombreUsuario, String contrasena, String email) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.email = email;
    }

    // Getters y Setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
