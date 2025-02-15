package tn.esprit.entities;

public class EquipeEvent {
    private int idEquipe;
    private int idEvent;

    public EquipeEvent(int idEquipe, int idEvent) {
        this.idEquipe = idEquipe;
        this.idEvent = idEvent;
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

    @Override
    public String toString() {
        return "EquipeEvent{" + "idEquipe=" + idEquipe + ", idEvent=" + idEvent + '}';
    }
}
