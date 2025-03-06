package org.gymify.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

public class Event {
    private int id;
    private String nom;
    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String description;
    private String imageUrl;
    private EventType.Reward reward;
    private double latitude;
    private double longitude;
    private String lieu;
    private int id_salle;
    private Salle salle;
    private EventType type;
    private Set<Equipe> equipes = new HashSet<>();

    public Event() {}

    public Event(int id, String nom, LocalDate date, LocalTime heureDebut, LocalTime heureFin,
                 String description, String imageUrl, EventType.Reward reward, double latitude,
                 double longitude, String lieu, int id_salle, Salle salle, EventType type) {
        this.id = id;
        this.nom = nom;
        this.date = date;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.description = description;
        this.imageUrl = imageUrl;
        this.reward = reward;
        this.latitude = latitude;
        this.longitude = longitude;
        this.lieu = lieu;
        this.id_salle = id_salle;
        this.salle = salle;
        this.type = type;
    }

    public Event(String nom, LocalDate date, LocalTime heureDebut, LocalTime heureFin,
                 String description, String imageUrl, EventType.Reward reward, double latitude,
                 double longitude, String lieu, int id_salle, Salle salle, EventType type) {
        this.nom = nom;
        this.date = date;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.description = description;
        this.imageUrl = imageUrl;
        this.reward = reward;
        this.latitude = latitude;
        this.longitude = longitude;
        this.lieu = lieu;
        this.id_salle = id_salle;
        this.salle = salle;
        this.type = type;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }

    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public EventType.Reward getReward() { return reward; }
    public void setReward(EventType.Reward reward) { this.reward = reward; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public int getIdSalle() { return id_salle; }
    public void setIdSalle(int id_salle) {
        this.id_salle = id_salle;
    }

    public Salle getSalle() { return salle; }
    public void setSalle(Salle salle) {
        this.salle = salle;
        if (salle != null) {
            this.id_salle = salle.getId_Salle();
            salle.addEvent(this);
        } else {
            this.id_salle = 0;
        }
    }

    public EventType getType() { return type; }
    public void setType(EventType type) { this.type = type; }

    public Set<Equipe> getEquipes() { return equipes; }
    public void setEquipes(Set<Equipe> equipes) { this.equipes = equipes; }

    public void addEquipe(Equipe equipe) {
        this.equipes.add(equipe);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", date=" + date +
                ", heureDebut=" + heureDebut +
                ", heureFin=" + heureFin +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", reward=" + reward +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", lieu='" + lieu + '\'' +
                ", id_salle=" + id_salle +
                ", type=" + type +
                '}';
    }
}