package Gimify.Pi.entities;

public class Entraineur extends  User{
    private String Specialite;


    public Entraineur(int id_User,String nom, String prenom, String password, String role, String email, String Specialite) {
        super(nom, prenom, password, "Entraineur", email);
        this.Specialite = Specialite;
    }

    public Entraineur( String nom, String prenom, String email, String password, String role, String Specialite) {
        super (nom, prenom, password, "Entraineur", email);
        this.Specialite = Specialite;
    }

    public Entraineur(int id_User, String nom, String prenom, String email, String password, String Specialite) {
        super (nom, prenom, password, "Entraineur", email);
        this.Specialite = Specialite;
    }


    public String getSpecialite() {
        return Specialite;
    }

    public void setSpecialite(String specialite) {
        Specialite = specialite;
    }

}
