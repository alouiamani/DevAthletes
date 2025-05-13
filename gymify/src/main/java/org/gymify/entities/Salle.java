
        package org.gymify.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Salle {
    private int id;
    private String nom;
    private String adresse;
    private String details;
    private String num_tel;
    private String email;
    private String url_photo;
    private int responsable_id; // New field
    private List<Abonnement> abonnements;

    public Salle() {
        this.abonnements = new ArrayList<>();
    }

    // Constructor with responsable_id
    public Salle(int id, String nom, String adresse, String details, String num_tel, String email, String url_photo, int responsable_id) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.details = details;
        this.num_tel = num_tel;
        this.email = email;
        this.url_photo = url_photo;
        this.responsable_id = responsable_id;
        this.abonnements = new ArrayList<>();
    }

    // Constructor without id for new salle
    public Salle(String nom, String adresse, String details, String num_tel, String email, String url_photo, int responsable_id) {
        this.nom = nom;
        this.adresse = adresse;
        this.details = details;
        this.num_tel = num_tel;
        this.email = email;
        this.url_photo = url_photo;
        this.responsable_id = responsable_id;
        this.abonnements = new ArrayList<>();
    }

    // Minimal constructor
    public Salle(int id, String nom) {
        this.id = id;
        this.nom = nom;
        this.abonnements = new ArrayList<>();
    }

    // Getters and Setters
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

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getNum_tel() {
        return num_tel;
    }

    public void setNum_tel(String num_tel) {
        this.num_tel = num_tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl_photo() {
        return url_photo;
    }

    public void setUrl_photo(String url_photo) {
        this.url_photo = url_photo;
    }

    public int getResponsable_id() {
        return responsable_id;
    }

    public void setResponsable_id(int responsable_id) {
        this.responsable_id = responsable_id;
    }

    public List<Abonnement> getAbonnements() {
        return abonnements;
    }

    public void addAbonnement(Abonnement abonnement) {
        abonnements.add(abonnement);
        abonnement.setSalle(this);
    }

    @Override
    public String toString() {
        return "Salle{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", details='" + details + '\'' +
                ", num_tel='" + num_tel + '\'' +
                ", email='" + email + '\'' +
                ", url_photo='" + url_photo + '\'' +
                ", responsable_id=" + responsable_id +
                '}';
    }

    public void addEvent(Event event) {
        // Implement if needed
    }

    public Collection<Object> getEvents() {
        return List.of();
    }
}