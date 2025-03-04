package tn.esprit.entities;

import org.gymify.entities.Event;

public class EquipeEvent {
    private int idEquipe;
    private int idEvent;
    private Event event;  // Champ pour l'événement associé

    // Constructeur avec les deux IDs
    public EquipeEvent(int idEquipe, int idEvent) {
        this.idEquipe = idEquipe;
        this.idEvent = idEvent;
    }

    // Constructeur avec l'événement complet
    public EquipeEvent(int idEquipe, int idEvent, Event event) {
        this.idEquipe = idEquipe;
        this.idEvent = idEvent;
        this.event = event;
    }

    public int getIdEquipe() {
        return idEquipe;
    }

    public void setIdEquipe(int idEquipe) {
        this.idEquipe = idEquipe;
    }

    public int getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(int idEvent) {
        this.idEvent = idEvent;
    }

    // Getter et setter pour l'événement
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "EquipeEvent{" +
                "idEquipe=" + idEquipe +
                ", idEvent=" + idEvent +
                ", event=" + event +  // Affichage des détails de l'événement
                '}';
    }
}
