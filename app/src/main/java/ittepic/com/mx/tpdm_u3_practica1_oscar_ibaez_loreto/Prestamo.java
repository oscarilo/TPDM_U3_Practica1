package ittepic.com.mx.tpdm_u3_practica1_oscar_ibaez_loreto;

public class Prestamo {
    private String objeto, descripcion, fechaentrega, fechaprestamo, persona;

    public Prestamo(String clave, String objeto, String descripcion, String fechaentrega, String fechaprestamo, String persona) {
        this.objeto = objeto;
        this.descripcion = descripcion;
        this.fechaentrega = fechaentrega;
        this.fechaprestamo = fechaprestamo;
        this.persona = persona;

    }// constructor

    public String getObjeto() {
        return objeto;
    }

    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaentrega() {
        return fechaentrega;
    }

    public void setFechaentrega(String fechaentrega) {
        this.fechaentrega = fechaentrega;
    }

    public String getFechaprestamo() {
        return fechaprestamo;
    }

    public void setFechaprestamo(String fechaprestamo) {
        this.fechaprestamo = fechaprestamo;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }
}// class
