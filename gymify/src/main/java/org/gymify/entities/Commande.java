package org.gymify.entities;

import java.util.Date;

public class Commande {
    private int id_c;
    private double total_c;
    private Date date_c;
    private String statut_c;
    private Integer user_id;
    private String phone_number;

    public Commande() {}

    public Commande(int id_c, double total_c, Date date_c, String statut_c, Integer user_id, String phone_number) {
        this.id_c = id_c;
        this.total_c = total_c;
        this.date_c = date_c;
        this.statut_c = statut_c;
        this.user_id = user_id;
        this.phone_number = phone_number;
    }

    public int getId_c() { return id_c; }
    public void setId_c(int id_c) { this.id_c = id_c; }

    public double getTotal_c() { return total_c; }
    public void setTotal_c(double total_c) { this.total_c = total_c; }

    public Date getDate_c() { return date_c; }
    public void setDate_c(Date date_c) { this.date_c = date_c; }

    public String getStatut_c() { return statut_c; }
    public void setStatut_c(String statut_c) { this.statut_c = statut_c; }

    public Integer getUser_id() { return user_id; }
    public void setUser_id(Integer user_id) { this.user_id = user_id; }

    public String getPhone_number() { return phone_number; }
    public void setPhone_number(String phone_number) { this.phone_number = phone_number; }
} 