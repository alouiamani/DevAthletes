package org.gymify.entities;

public class infoSportif {
    private int id;
    private float poids;
    private float taille;
    private int age;
    private String sexe;
    private Objectifs objectifs;
    private User user;
    public infoSportif() {}
    public infoSportif(float poids, float taille, int age, String sexe, Objectifs objectifs, User user) {
        this.poids = poids;
        this.taille = taille;
        this.age = age;
        this.sexe = sexe;
        this.objectifs = objectifs;
        this.user = user;

    }
    public infoSportif(int id){
        this.id = id;

    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public float getPoids() {
        return poids;

    }
    public void setPoids(float poids) {
        this.poids = poids;
    }
    public float getTaille() {
        return taille;
    }
    public void setTaille(float taille) {
        this.taille = taille;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public String getSexe() {
        return sexe;
    }
    public void setSexe(String sexe) {
        this.sexe = sexe;
    }
    public Objectifs getObjectifs() {
        return objectifs;
    }
    public void setObjectifs(Objectifs objectifs) {
        this.objectifs = objectifs;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }



}
