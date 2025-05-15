package org.gymify.entities;

import java.sql.Timestamp;

public class Post {
    private int id;
    private int id_User;
    private String title;
    private String content;
    private Timestamp createdAt;
    private String imageUrl; // Nouvel attribut
    private String userImageUrl; // ✅ Ajouter ce champ

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    // Constructeur avec id
    public Post(int id, int id_User, String title, String content, String imageUrl, Timestamp createdAt) {
        this.id = id;
        this.id_User = id_User;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }


    // Constructeur sans id
    public Post(int id_User, String title, String content, String imageUrl, Timestamp createdAt) {
        this.id_User = id_User;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

    // Constructeur par défaut
    public Post() {
        // Initialisation par défaut
    }


    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getId_User() { return id_User; }
    public void setId_User(int id_User) { this.id_User = id_User; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    // Getters/Setters pour imageUrl
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }


    // Méthode toString()
    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", id_User=" + id_User +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }




}
