package com.sena.springpoo.models;

public class Comunicados {

    private long idComunicado;
    private String titulo;
    private String contenido;
    private Usuario idUsuario;

    public Comunicados(String contenido, long id_comunicado, Usuario idUsuario, String titulo) {
        this.contenido = contenido;
        this.idComunicado = id_comunicado;
        this.idUsuario = idUsuario;
        this.titulo = titulo;
    }

    public Comunicados() {
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public long getId_comunicado() {
        return idComunicado;
    }

    public void setId_comunicado(long id_comunicado) {
        this.idComunicado = id_comunicado;
    }

    public Usuario getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Usuario idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
