package org.gymify.entities;

public class Activité {
    private int id;
    private String nom;
    private ActivityType type;
    private String description;
    private String url;
    public Activité(){}
    public Activité(String nom, ActivityType type, String description, String url) {
        this.nom = nom;
        this.type = type;
        this.description = description;
        this.url = url;
    }
    public Activité(int id, String nom, ActivityType type, String description, String url) {
        this.id = id;
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
    public ActivityType getType() {
        return type;
    }
    public void setType(ActivityType type) {
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
