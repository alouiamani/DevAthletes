package org.gymify.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Planning {
    private int id;
    private Date dateDebut;
    private Date dateFin;
    private String description;
    private String title;
    List<Cours> courses;
    public Planning() {}
    public Planning(Date dateDebut, String description, String title, Date dateFin) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.description = description;
        this.title=title;
        this.courses=new ArrayList<>();

    }
    public Planning(String description, String title, int id) {
        this.id=id;
        this.description=description;
        this.title=title;
    }
    public Planning(int id){
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public Date getdateDebut() {
        return dateDebut;
    }
    public void setdateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }
    public Date getdateFin() {
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
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public List<Cours> getCourses() {
        return courses;
    }
    public void setCourses(List<Cours> courses) {
        this.courses = courses;
    }

    public String toString(){
        return "Planning:id="+id+", Date Debut="+dateDebut+", Date Fin="+dateFin+", Description="+description+", Title="+title;
    }


}
