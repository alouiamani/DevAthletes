package org.gymify.entities;

import java.util.Date;

public class Sportif extends User {
    public Sportif(int id_User, String nom, String prenom, String email, String password, Date dateNaissance, String imageURL) {
        super(String.valueOf(id_User), nom, prenom, email, password, dateNaissance, imageURL);
    }

    public Sportif(String nom, String prenom, String email, String password, Date dateNaissance, String imageURL) {
        super(nom, prenom, email, password, "Sportif");
    }

}
