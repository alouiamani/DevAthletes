package Gimify.Pi.entities;

import java.util.Date;

public class Entraineur extends User {
    private String specialite;



    // Constructeur avec ID (pour les mises à jour)
    public Entraineur(int id_User, String nom, String prenom, String email, String password,
                      Date dateNaissance, String imageURL, String specialite) {
        super(id_User, nom, prenom, email, password, "Entraineur", dateNaissance, imageURL);
        this.specialite = specialite;
    }

    // Constructeur sans ID (pour la création d'un nouvel entraîneur)
    public Entraineur(String nom, String prenom, String email, String password,
                      Date dateNaissance, String imageURL, String specialite) {
        super(nom, prenom, email, password, "Entraineur", dateNaissance, imageURL);
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
