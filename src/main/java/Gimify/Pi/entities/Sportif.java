package Gimify.Pi.entities;

import java.util.Date;

public class Sportif extends User {
    public Sportif(int id_User, String nom, String prenom, String email, String password, Date dateNaissance, String image) {
        super(id_User, nom, prenom, email, password, "Sportif", dateNaissance, image);
    }

    public Sportif(String nom, String prenom, String email, String password, Date dateNaissance, String image) {
        super(nom, prenom, email, password, "Sportif");
    }

    @Override
    public String toString() {
        return "Sportif{" + super.toString() + "}";
    }
}
