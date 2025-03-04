package org.gymify.entities;

import jdk.jfr.Event;
import org.gymify.entities.EventType;

import java.util.HashSet;
import java.util.Set;

public class Equipe {
    private int id;
    private String nom;
    private String imageUrl;
    private EventType.Niveau niveau;
    private int nombreMembres;
    private Set<Event> events = new HashSet<>();

    public Equipe() {}

    public Equipe(int id, String nom, String imageUrl, EventType.Niveau niveau, int nombreMembres) {
        this.id = id;
        this.nom = nom;
        this.imageUrl = imageUrl;
        this.niveau = niveau;
        this.nombreMembres = nombreMembres;
    }

    public Equipe(String nom, String imageUrl, EventType.Niveau niveau, int nombreMembres) {
        this.nom = nom;
        this.imageUrl = imageUrl;
        this.niveau = niveau;
        this.nombreMembres = nombreMembres;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public EventType.Niveau getNiveau() { return niveau; }
    public void setNiveau(EventType.Niveau niveau) { this.niveau = niveau; }

    public int getNombreMembres() { return nombreMembres; }
    public void setNombreMembres(int nombreMembres) { this.nombreMembres = nombreMembres; }

    public Set<Event> getEvents() { return events; }
    public void setEvents(Set<Event> events) { this.events = events; }

    @Override
    public String toString() {
        return "Equipe{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", niveau=" + niveau +
                ", nombreMembres=" + nombreMembres +
                '}';
    }
}