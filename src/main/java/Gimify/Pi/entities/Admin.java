package Gimify.Pi.entities;

public class Admin extends User {
    public Admin(int id_User, String nom, String prenom, String password, String role, String email) {
        super(id_User, nom, prenom, password, "Admin", email);
    }
}
