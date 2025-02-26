package org.gymify.entities;

import java.util.ArrayList;
import java.util.List;
import org.gymify.entities.Abonnement;
public class Salle {
    private int id_Salle;
    private String nom;
    private String adresse;
    private String details;
    private String num_tel;
    private String email;
private String url_photo;
    private List<Abonnement> abonnements;

    public Salle(int idSalle, String nom, String adresse, String numTel, String email,String url_photo) {}

    public Salle(int id_Salle , String nom, String adresse, String details, String num_tel, String email,String url_photo) {
        this.id_Salle = id_Salle;
        this.nom = nom;
        this.adresse = adresse;
        this.details = details;
        this.num_tel = num_tel;
        this.email = email;
        this.url_photo = url_photo;
        this.abonnements = new ArrayList<>();


    }

    public Salle(String nom, String adresse, String details, String num_tel, String email,String url_photo) {
        this.nom = nom;
        this.adresse = adresse;
        this.details = details;
        this.num_tel = num_tel;
        this.email = email;
        this.url_photo = url_photo;
        this.abonnements = new ArrayList<>();
    }

    public Salle() {

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
    public List<Abonnement> getAbonnements() { return abonnements; }
    public void addAbonnement(Abonnement abonnement) { abonnements.add(abonnement); abonnement.setSalle(this); }

    @Override
    public String toString() {
     return "l'id de salle est : " + id_Salle + " le nom de salle est : " + nom + " l'addresse de salle est : " + adresse
     + " les details de salle sont : " + details + " l'numero de tel de salle est : " + num_tel + " l'email de salle est : " + email;
    }

}
