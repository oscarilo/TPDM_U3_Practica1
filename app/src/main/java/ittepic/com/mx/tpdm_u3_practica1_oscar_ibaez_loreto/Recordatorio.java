package ittepic.com.mx.tpdm_u3_practica1_oscar_ibaez_loreto;

public class Recordatorio {
    private String titulo, descripcion, creacion, prioridad, caducidad;

    public Recordatorio(String clave, String titulo, String descripcion, String creacion, String prioridad, String caducidad) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.creacion = creacion;
        this.prioridad = prioridad;
        this.caducidad = caducidad;

    }// constructor

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCreacion() {
        return creacion;
    }

    public void setCreacion(String creacion) {
        this.creacion = creacion;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getCaducidad() {
        return caducidad;
    }

    public void setCaducidad(String caducidad) {
        this.caducidad = caducidad;
    }
}// class
