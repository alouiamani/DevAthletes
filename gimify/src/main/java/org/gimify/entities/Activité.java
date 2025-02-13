package org.gimify.entities;

public class Activité {
    private int id;
    private String nom;
    private String type;
    private String description;
    private String url;
    public Activité(){}
    public Activité(String nom, String type, String description, String url) {
        this.nom = nom;
        this.type = type;
        this.description = description;
        this.url = url;
    }
    public Activité(int id){
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;

    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Actvité: id=" + id + ", nom=" + nom + ", type=" + type + ", description=" + description + ", url=" + url;
    }
}
