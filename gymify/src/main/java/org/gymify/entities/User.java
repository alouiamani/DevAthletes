package org.gymify.entities;

import org.gymify.utils.gymifyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {
    private int id; // Changed from id_User
    private String nom;
    private String prenom;
    private String password;
    private String email;
    private String role;
    private Date date_naissance; // Changed from dateNaissance
    private String image_url; // Changed from imageURL
    private List<Reclamation> reclamations;
    private int id_Salle;
    private List<Abonnement> abonnements;
    private Salle salle;

    // 🔹 Constructeurs
    public User() {
        this.reclamations = new ArrayList<>();
    }

    public User(int id, String nom, String prenom, String password, String email, String role, Date date_naissance, String image_url, int id_Salle) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
        this.email = email;
        this.role = role;
        this.date_naissance = date_naissance;
        this.image_url = image_url;
        this.reclamations = new ArrayList<>();
        this.id_Salle = id_Salle;
    }

    public User(int id, String nom, String prenom, String email, String password, String role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
        this.email = email;
        this.role = role;
        this.reclamations = new ArrayList<>();
    }

    public User(String nom, String prenom, String email, String password, String role, Date date_naissance, String image_url) {
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
        this.email = email;
        this.role = role;
        this.date_naissance = date_naissance;
        this.image_url = image_url;
        this.reclamations = new ArrayList<>();
    }

    public User(int id, String nom, String prenom, String email, String password, String role, int id_Salle) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
        this.email = email;
        this.role = role;
        this.id_Salle = id_Salle;
        this.reclamations = new ArrayList<>();
    }

    public User(String nom, String prenom, String password, String email, String role) {
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
        this.email = email;
        this.role = "sportif"; // Aligned with ENUM (assuming 'sportif' is a valid value)
        this.reclamations = new ArrayList<>();
    }

    public User(String nom, String prenom, String email, String role, Date date_naissance, String image_url) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
        this.date_naissance = date_naissance;
        this.image_url = image_url;
        this.reclamations = new ArrayList<>();
    }

    // 🔹 Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Date getDate_naissance() {
        return date_naissance;
    }

    public void setDate_naissance(Date date_naissance) {
        this.date_naissance = date_naissance;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public List<Reclamation> getReclamations() {
        return reclamations;
    }

    public void setReclamations(List<Reclamation> reclamations) {
        this.reclamations = reclamations;
    }

    public List<Abonnement> getAbonnements() {
        return abonnements;
    }

    public void setAbonnements(List<Abonnement> abonnements) {
        this.abonnements = abonnements;
    }

    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

    public int getId_Salle() {
        return id_Salle;
    }

    public void setId_Salle(int id_Salle) {
        this.id_Salle = id_Salle;
    }

    public String getId_equipe() {
        return "hdhd"; // Placeholder; should ideally retrieve equipe_id from DB
    }

    public void setId_equipe(int equipeId) {
        // Placeholder; no field exists, so no action taken
    }

    public class AuthToken {
        private static User currentUser;

        public static void setCurrentUser(User user) {
            currentUser = user;
        }

        public static User getCurrentUser() {
            return currentUser;
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", date_naissance=" + date_naissance +
                ", image_url='" + image_url + '\'' +
                ", reclamations=" + reclamations +
                ", id_Salle=" + id_Salle +
                '}';
    }

    public int getSalleIdByUserId(int userId) throws SQLException {
        String query = "SELECT id_Salle FROM user WHERE id = ?";
        try (Connection conn = gymifyDataBase.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_Salle");
            }
        }
        return -1;
    }
    //methode ajoutée ranym
    public String getUsername() {
        return nom + " " + prenom; // 🔥 Retourne le nom complet de l'utilisateur
    }
}