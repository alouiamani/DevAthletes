package entities;

import java.util.Date;

public class User {
    private int id_User;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String role;
    private Date dateNaissance;
    private String imageURL;

    public User(int id_User, String nom, String prenom, String email, String password, String role) {
        this.id_User = id_User;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(String nom, String prenom, String email, String password, String role, Date dateNaissance, String imageURL) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.role = role;
        this.dateNaissance = dateNaissance;
        this.imageURL = imageURL;
    }

    // Getters and setters
    public int getId_User() { return id_User; }
    public void setId_User(int id_User) { this.id_User = id_User; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Date getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(Date dateNaissance) { this.dateNaissance = dateNaissance; }
    public String getImageURL() { return imageURL; }
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }
} 