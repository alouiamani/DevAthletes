package org.gymify.entities;

import java.util.ArrayList;
import java.util.List;

public class Salle {
    private int id_Salle;
    private String nom;
    private String adresse;
    private String details;
    private String num_tel;
    private String email;
    private String url_photo;
    private List<Abonnement> abonnements;
    private List<Event> events;
    private int idResponsable;

    public Salle() {
        this.abonnements = new ArrayList<>();
        this.events = new ArrayList<>();
    }

    public Salle(int id_Salle, String nom, String adresse, String details, String num_tel, String email, String url_photo, int idResponsable) {
        this.id_Salle = id_Salle;
        this.nom = nom;
        this.adresse = adresse;
        this.details = details;
        this.num_tel = num_tel;
        this.email = email;
        this.url_photo = url_photo;
        this.abonnements = new ArrayList<>();
        this.events = new ArrayList<>();
        this.idResponsable = idResponsable;
    }

    public Salle(String nom, String adresse, String details, String num_tel, String email, String url_photo, int idResponsable) {
        this.nom = nom;
        this.adresse = adresse;
        this.details = details;
        this.num_tel = num_tel;
        this.email = email;
        this.url_photo = url_photo;
        this.abonnements = new ArrayList<>();
        this.events = new ArrayList<>();
        this.idResponsable = idResponsable;
    }

    public Salle(int idSalle, String nom, String adresse, String details, String numTel, String email, String urlPhoto) {
        this.id_Salle = idSalle;
        this.nom = nom;
        this.adresse = adresse;
        this.details = details;
        this.num_tel = numTel;
        this.email = email;
        this.url_photo = urlPhoto;
        this.abonnements = new ArrayList<>();
        this.events = new ArrayList<>();
        this.idResponsable = 0; // Default value, should be set later if needed
    }

    public int getIdResponsable() {
        return this.idResponsable;
    }

    public void setIdResponsable(int idResponsable) {
        this.idResponsable = idResponsable;
    }

    public int getId_Salle() {
        return id_Salle;
    }

    public void setId_Salle(int id_Salle) {
        this.id_Salle = id_Salle;
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

    public List<Abonnement> getAbonnements() {
        return abonnements;
    }

    public void addAbonnement(Abonnement abonnement) {
        abonnements.add(abonnement);
        abonnement.setSalle(this);
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = (events != null) ? events : new ArrayList<>();
    }

    public void addEvent(Event event) {
        if (event != null) {
            this.events.add(event);
            event.setIdSalle(this.id_Salle);
        }
    }

    public void removeEvent(Event event) {
        if (event != null) {
            this.events.remove(event);
            event.setIdSalle(0);
        }
    }

    @Override
    public String toString() {
        return "Salle{" +
                "id_Salle=" + id_Salle +
                ", nom='" + nom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", details='" + details + '\'' +
                ", num_tel='" + num_tel + '\'' +
                ", email='" + email + '\'' +
                ", url_photo='" + url_photo + '\'' +
                ", idResponsable=" + idResponsable +
                ", events=" + events.size() +
                '}';
    }
}