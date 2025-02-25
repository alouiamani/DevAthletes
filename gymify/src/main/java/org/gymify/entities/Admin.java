package org.gymify.entities;

import java.util.Date;

public class Admin extends User {
    public Admin(int id_User, String nom, String prenom, String email, String password, Date dateNaissance, String image) {
        super();
    }

    public Admin(String nom, String prenom, String email, String password, Date dateNaissance, String image) {
        super(nom, prenom, email, password, "Admin");
    }

    @Override
    public String toString() {
        return "Admin{" + super.toString() + "}";
    }
}
