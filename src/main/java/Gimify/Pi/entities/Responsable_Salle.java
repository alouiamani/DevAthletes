package Gimify.Pi.entities;

public class Responsable_Salle extends User {
    public Responsable_Salle(int id_User, String nom,String prenom, String email, String password,  String Role) {
        super (id_User, nom,  prenom, password ,"Responsable_Salle",email);
    }
}
