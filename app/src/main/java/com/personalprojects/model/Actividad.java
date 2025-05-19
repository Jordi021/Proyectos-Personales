package com.personalprojects.model;

public class Actividad {
    private int idActividad;
    private String nombre;
    private String descripcion;
    private String fechaInicio;
    private String fechaFin;
    private String estado;
    private int idProyecto;

    public Actividad(String nombre, String descripcion, String fechaInicio, String fechaFin, String estado, int idProyecto) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.idProyecto = idProyecto;
    }

    public Actividad(int idActividad, String nombre, String descripcion, String fechaInicio, String fechaFin, String estado, int idProyecto) {
        this.idActividad = idActividad;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.idProyecto = idProyecto;
    }

    // Getters y Setters
    public int getIdActividad() { return idActividad; }
    public void setIdActividad(int idActividad) { this.idActividad = idActividad; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }
    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public int getIdProyecto() { return idProyecto; }
    public void setIdProyecto(int idProyecto) { this.idProyecto = idProyecto; }

    @Override
    public String toString() { return nombre + " (" + estado + ")"; }
}