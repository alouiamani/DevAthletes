package entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {
    private int id_User;
    private String nom;
    private String prenom;
    private String password;
    private String email;
    private String role;
    private Date dateNaissance;
    private String imageURL;
    private List<Reclamation> reclamations;

    // 🔹 Constructeurs


    public User() {
        this.id_User = id_User;
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
        this.email = email;
        this.role = role;
        this.dateNaissance = dateNaissance;
        this.imageURL = imageURL;
        this.reclamations = new ArrayList<>();
    }
    public User(int id_User, String nom, String prenom, String email, String password, String role) {
        this.id_User = id_User;
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
        this.email = email;
        this.role = role;

    }

    public User(String nom, String prenom, String email, String password,  String role,Date dateNaissance, String imageURL) {
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
        this.email = email;
        this.role = role;
        this.dateNaissance = dateNaissance;
        this.imageURL = imageURL;
        this.reclamations = new ArrayList<>();
    }



    public User(String nom, String prenom, String password, String email, String role) {
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
        this.email = email;
        this.role = "Sportif";

    }
    public User(String nom, String prenom,  String email, String role,Date dateNaissance, String imageURL) {

        this.nom = nom;
        this.prenom = prenom;

        this.email = email;
        this.role = role;
        this.dateNaissance = dateNaissance;
        this.imageURL = imageURL;
    }

    // 🔹 Getters et Setters
    public int getId_User() {
        return id_User;
    }

    public void setId_User(int id_User) {
        this.id_User = id_User;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getDateNaissance() { // ✅ Correction ici
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) { // ✅ Correction ici
        this.dateNaissance = dateNaissance;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public List<Reclamation> getReclamations() {
        return reclamations;
    }

    public void setReclamations(List<Reclamation> reclamations) {
        this.reclamations = reclamations;
    }

    @Override
    public String toString() {
        return "User{" +
                "id_User=" + id_User +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", dateNaissance=" + dateNaissance +
                ", imageURL='" + imageURL + '\'' +
                ", reclamations=" + reclamations +
                '}';
    }
    //methode ajoutée ranym
    public String getUsername() {
        return nom + " " + prenom; // 🔥 Retourne le nom complet de l'utilisateur
    }
}
