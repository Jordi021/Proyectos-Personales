package com.personalprojects.model;

public class Actividad {

    // Constantes para los estados de la actividad
    public static final String ESTADO_PENDIENTE = "Pendiente";
    public static final String ESTADO_EN_PROGRESO = "En Progreso";
    public static final String ESTADO_REALIZADO = "Realizado";
    // Si usas "Planificado" como estado inicial, puedes añadirlo:
    // public static final String ESTADO_PLANIFICADO = "Planificado";


    private int idActividad;
    private int idProyecto;
    private String nombre;
    private String descripcion;
    private String fechaInicio;
    private String fechaFin;      // Esta será tu fecha final/límite
    private String estado;

    // Constructor vacío
    public Actividad() {
    }

    // Constructor para crear una nueva actividad (sin ID, se genera en BD)
    public Actividad(int idProyecto, String nombre, String descripcion, String fechaInicio, String fechaFin, String estado) {
        this.idProyecto = idProyecto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
    }

    // --- Getters y Setters ---

    public int getIdActividad() { return idActividad; }
    public void setIdActividad(int idActividad) { this.idActividad = idActividad; }

    public int getIdProyecto() { return idProyecto; }
    public void setIdProyecto(int idProyecto) { this.idProyecto = idProyecto; }

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


    @Override
    public String toString() {
        return "Actividad{" +
                "idActividad=" + idActividad +
                ", idProyecto=" + idProyecto +
                ", nombre='" + nombre + '\'' +
                ", fechaInicio='" + fechaInicio + '\'' +
                ", fechaFin='" + fechaFin + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}