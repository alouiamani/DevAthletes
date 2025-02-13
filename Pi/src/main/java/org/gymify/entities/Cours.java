package org.gymify.entities;

import java.util.Date;

public class Cours {
    private int id;
    private String title;
    private Date dateDebut;
    private Date dateFin;
    private String description;
    private Activité activité;
    private Planning planning;
    public Cours() {}
    public Cours(int id){
        this.id = id;
    }
    public Cours(String title, Date dateDebut, Date dateFin,String description,Activité activité,Planning planning) {
        this.title = title;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.description = description;
        this.activité=activité;
        this.planning = planning;
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

    public String toString(){
        return "Cours: id:" +id+", title:"+title+ ", dateDebut"+dateDebut+", dateFin"+dateFin+", Description:"+ description+", Ativité_id:"+activité.getId()+", Planning_id:"+planning.getId();
    }
}
