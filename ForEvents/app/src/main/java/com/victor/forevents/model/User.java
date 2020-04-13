package com.victor.forevents.model;

public class User {
    private String id;
    private String email;
    private String username;
    private String name;
    private String imagen;
    private String bio;

    public User(String id, String email, String username, String name, String imagen, String bio) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.name = name;
        this.imagen = imagen;
        this.bio = bio;
    }

    public User (){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
