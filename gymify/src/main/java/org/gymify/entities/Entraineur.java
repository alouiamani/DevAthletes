package org.gymify.entities;

import java.util.Date;

public class Entraineur extends User {
    private String specialite;



    // Constructeur avec ID (pour les mises à jour)
    public Entraineur(int id, String nom, String prenom, String email, String password,
                      Date date_naissance, String image_url, String specialite) {
        super(String.valueOf(id), nom, prenom, email, password, date_naissance, image_url);
        this.specialite = specialite;
    }

    // Constructeur sans ID (pour la création d'un nouvel entraîneur)
    public Entraineur(String nom, String prenom, String email, String password,
                      Date date_naissance, String image_url, String specialite) {
        super(nom, prenom, email, password, "Entraineur", date_naissance, image_url);
        this.specialite = specialite;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    @Override
    public String toString() {
        return "Entraineur{" + super.toString() + ", specialite='" + specialite + "'}";
    }
}
