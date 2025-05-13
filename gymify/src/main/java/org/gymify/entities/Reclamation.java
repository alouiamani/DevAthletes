package org.gymify.entities;

import java.sql.Timestamp;

public class Reclamation {
    private int id;
    private int user_id;
    private String sujet;
    private String description;
    private String statut;
    private Timestamp dateCreation;
    private User user;

    public Reclamation() {}

    public Reclamation(int id, int user_id, String sujet, String description, String statut, Timestamp dateCreation) {
        this.id = id;
        this.user_id = user_id;
        this.sujet = sujet;
        this.description = description;
        this.statut = statut;
        this.dateCreation = dateCreation;
    }

    public Reclamation(int user_id, String sujet, String description, String statut, User user) {
        this.user_id = user_id;
        this.sujet = sujet;
        this.description = description;
        this.statut = statut;
        this.user = user;
        this.dateCreation = new Timestamp(System.currentTimeMillis());
    }

    public Reclamation(int user_id, String sujet, String description) {
        this.user_id = user_id;
        this.sujet = sujet;
        this.description = description;
        this.dateCreation = new Timestamp(System.currentTimeMillis());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", sujet='" + sujet + '\'' +
                ", description='" + description + '\'' +
                ", statut='" + (statut != null ? statut : "Non défini") + '\'' +
                ", dateCreation=" + dateCreation +
                ", user=" + (user != null ? user.getNom() : "Aucun") +
                '}';
    }
}