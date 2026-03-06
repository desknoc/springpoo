package com.sena.springpoo.models;

public class Formularios {

    private long idFormulario;
    private String nombreFormulario;
    private String descripcion;
    private Usuario idUsuario;

    public Formularios(String descripcion, long idFormulario, Usuario idUsuario, String nombreFormulario) {
        this.descripcion = descripcion;
        this.idFormulario = idFormulario;
        this.idUsuario = idUsuario;
        this.nombreFormulario = nombreFormulario;
    }

    public Formularios() {
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public long getIdFormulario() {
        return idFormulario;
    }

    public void setIdFormulario(long idFormulario) {
        this.idFormulario = idFormulario;
    }

    public Usuario getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Usuario idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreFormulario() {
        return nombreFormulario;
    }

    public void setNombreFormulario(String nombreFormulario) {
        this.nombreFormulario = nombreFormulario;
    }
}
