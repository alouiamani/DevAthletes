package org.gymify.entities;

import java.util.Date;

public class Sportif extends User {
    public Sportif(int id, String nom, String prenom, String email, String password, Date date_naissance, String image_url) {
        super(String.valueOf(id), nom, prenom, email, password, date_naissance, image_url);
    }

    public Sportif(String nom, String prenom, String email, String password, Date date_naissance, String image_url) {
        super(nom, prenom, email, password, "Sportif");
    }

}
