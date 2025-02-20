package tn.esprit.entities;
import jdk.jfr.Event;

import java.util.HashSet;
import java.util.Set;
public class Equipe {
    private int id;
    private String nom;
    private Set<Event> events = new HashSet<>();


    public Equipe() {}

    public Equipe(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }
    public Equipe(String nom) {
        this.nom = nom;
    }

    // Getters et Setters
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
    public Set<Event> getEvents() { return events; }
    public void setEvents(Set<Event> events) { this.events = events; }

    @Override
    public String toString() {
        return "Equipe{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                '}';
    }


}
