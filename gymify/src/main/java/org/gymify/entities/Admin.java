package org.gymify.entities;

import java.util.Date;

public class Admin extends User {
    public Admin(int id, String nom, String prenom, String email, String password, Date date_naissance, String image_url) {
        super(id,nom, prenom, email, password, "Admin");
    }

    public Admin(String nom, String prenom, String email, String password, Date date_naissance, String image_url) {
        super(nom, prenom, email, password, "Admin");
    }

    @Override
    public String toString() {
        return "Admin{" + super.toString() + "}";
    }
}
