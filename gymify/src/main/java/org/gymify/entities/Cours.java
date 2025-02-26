package org.gymify.entities;

import java.time.LocalTime;
import java.util.Date;

public class Cours {
    private int id;
    private String title;
    private Date dateDebut;
    private Date dateFin;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String description;
    private Activité activité;
    private Planning planning;
    private User coach;
    public Cours() {}
    public Cours(int id){
        this.id = id;
    }
    public Cours(String title, Date dateDebut, Date dateFin, LocalTime heureDebut, LocalTime heureFin, String description, Activité activité, Planning planning, User coach) {
        this.title = title;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.description = description;
        this.activité=activité;
        this.planning = planning;
        this.coach = coach;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Date getDateDebut() {
        return dateDebut;
    }
    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }
    public Date getDateFin() {
        return dateFin;
    }
    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }
    public String getDescription() {
        return description;

    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Activité getActivité(){
        return activité;
    }

    public void setActivité(Activité activité) {
        this.activité=activité;
    }

    public Planning getPlanning() {
        return planning;
    }
    public void setPlanning(Planning planning) {
        this.planning = planning;
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
    public User getUser() {
        return coach;
    }
    public void setUser(User coach) {
        this.coach = coach;
    }

    public String toString(){
        return "Cours: id:" +id+", title:"+title+ ", dateDebut"+dateDebut+", dateFin"+dateFin+", heureDebut:"+heureDebut+", heureFin:"+heureFin+", Description:"+ description+", Ativité_id:"+activité.getId()+", Planning_id:"+planning.getId()+", Entraineur_id:"+coach;
    }

}
