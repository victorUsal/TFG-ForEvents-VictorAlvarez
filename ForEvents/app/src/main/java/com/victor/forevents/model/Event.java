package com.victor.forevents.model;

public class Event {
    private String postid;
    private String postimage;
    private String nombre;
    private String descripcion;
    private String fechaInicio;
    private String horaInicio;
    private String fechaFin;
    private String horaFin;
    private String ubicacion;
    private String tematica;
    private String aforo;
    private String tipo;
    private String publisher;


    public Event(String postid, String postimage, String nombre, String descripcion, String fechaInicio, String horaInicio, String fechaFin, String horaFin, String ubicacion, String tematica, String aforo, String tipo, String publisher) {
        this.postid = postid;
        this.postimage = postimage;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.horaInicio = horaInicio;
        this.fechaFin = fechaFin;
        this.horaFin = horaFin;
        this.ubicacion = ubicacion;
        this.tematica = tematica;
        this.aforo = aforo;
        this.tipo = tipo;
        this.publisher = publisher;
    }

    public Event (){

    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getTematica() {
        return tematica;
    }

    public void setTematica(String tematica) {
        this.tematica = tematica;
    }

    public String getAforo() {
        return aforo;
    }

    public void setAforo(String aforo) {
        this.aforo = aforo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
