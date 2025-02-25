package org.gymify.entities;

import java.util.Date;

public class Responsable_Salle extends User {
    public Responsable_Salle(int id_User, String nom, String prenom, String email, String password, Date dateNaissance, String imageURL) {
        super();
    }

    public Responsable_Salle(String nom, String prenom, String email, String password,
                      Date dateNaissance, String imageURL) {
        super(nom, prenom, email, password, "Responsable", dateNaissance, imageURL);}

    @Override
    public String toString() {
        return "Responsable_Salle{" + super.toString() + "}";
    }
}
