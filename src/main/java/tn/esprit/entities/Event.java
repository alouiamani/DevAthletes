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
    private EventType type;
    private String description;
    private String imageUrl;
    private String reward;
    private Set<Equipe> equipes = new HashSet<>();

    // Constructeur sans argument
    public Event() {}

    // ✅ Constructeur
    public Event(int id, String nom, String lieu, LocalDate date, LocalTime heureDebut, LocalTime heureFin,
                 EventType type, String description, String imageUrl, String reward) {
        this.id = id;
        this.nom = nom;
        this.lieu = lieu;
        this.date = date;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.type = type;
        this.description = description;
        this.imageUrl = imageUrl;
        this.reward = reward != null && !reward.trim().isEmpty() ? reward : "Default Reward";
    }

    // ✅ Constructeur sans ID (pour la création d'événements)
    public Event(String nom, String lieu, LocalDate date, LocalTime heureDebut, LocalTime heureFin,
                 EventType type, String description, String imageUrl, String reward) {
        this.nom = nom;
        this.lieu = lieu;
        this.date = date;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.type = type;
        this.description = description;
        this.imageUrl = imageUrl;
        this.reward = reward != null && !reward.trim().isEmpty() ? reward : "Default Reward";
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

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
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
        this.reward = reward != null && !reward.trim().isEmpty() ? reward : "Default Reward";
    }

    public Set<Equipe> getEquipes() {
        return equipes;
    }

    public void setEquipes(Set<Equipe> equipes) {
        this.equipes = equipes;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", lieu='" + lieu + '\'' +
                ", date=" + date +
                ", heureDebut=" + heureDebut +
                ", heureFin=" + heureFin +
                ", type=" + type +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", reward='" + reward + '\'' +
                '}';
    }
}






