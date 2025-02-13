package tn.esprit.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
public class Event {
    private int id;
    private String nom;
    private String lieu;
    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String type;
    private int capacite;
    private String description;
    private String imageUrl;
    private String reward;
    private Set<Equipe> equipes = new HashSet<>();

    // Constructeur sans argument
    public Event() {}

    // ✅ Constructeur
    public Event(int id, String nom, String lieu, LocalDate date, LocalTime heureDebut, LocalTime heureFin,
                 String type, int capacite, String description, String imageUrl, String reward) {
        this.id = id;
        this.nom = nom;
        this.lieu = lieu;
        this.date = date;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.type = type;
        this.capacite = capacite;
        this.description = description;
        this.imageUrl = imageUrl;
        this.reward = reward;
    }

    // ✅ Constructeur sans ID (pour la création d'événements)
    public Event(String nom, String lieu, LocalDate date, LocalTime heureDebut, LocalTime heureFin,
                 String type, int capacite, String description, String imageUrl, String reward) {
        this.nom = nom;
        this.lieu = lieu;
        this.date = date;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.type = type;
        this.capacite = capacite;
        this.description = description;
        this.imageUrl = imageUrl;
        this.reward = reward;
    }

    // ✅ Getters et Setters
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

    public String getLieu() {
        return lieu;
    }
    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getHeureDebut() {
        return heureDebut;
    }
    public void setHeureDebut(LocalTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public LocalTime getHeureFin() {
        return heureFin;
    }
    public void setHeureFin(LocalTime heureFin) {
        this.heureFin = heureFin;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public int getCapacite() {
        return capacite;
    }
    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getReward() {
        return reward;
    }
    public void setReward(String reward) {
        this.reward = reward;
    }

    public Set<Equipe> getEquipes() {
        return equipes;
    }
    public void setEquipes(Set<Equipe> equipes) {
        this.equipes = equipes;
    }

    // ✅ toString() pour affichage dans la console
    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", lieu='" + lieu + '\'' +
                ", date=" + date +
                ", heureDebut=" + heureDebut +
                ", heureFin=" + heureFin +
                ", type='" + type + '\'' +
                ", capacite=" + capacite +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", reward=" + reward +
                '}';
    }
}






