package Gimify.Pi.entities;

public class Sportif extends User {
    public Sportif(int id_User, String nom, String prenom, String password, String role, String email) {
        super(id_User, nom, prenom, password, "Sportif", email);
   }
}
