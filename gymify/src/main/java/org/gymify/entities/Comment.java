package org.gymify.entities;

import java.sql.Timestamp;

public class Comment {
    private int id;
    private int postId;
    private int id_User;
    private String content;
    private Timestamp createdAt;
    private String userImageUrl; // 🔥 Ajout de l'image de profil


    // Constructeur par défaut
    public Comment() {
        // Initialisation par défaut
    }

    // Constructeur avec paramètres avec id
    public Comment(int id, int postId, int id_User, String content, Timestamp createdAt,String userImageUrl) {
        this.id = id;
        this.postId = postId;
        this.id_User = id_User;
        this.content = content;
        this.createdAt = createdAt;
        this.userImageUrl = userImageUrl;
    }


    // Constructeur avec paramètres sans id
    public Comment( int postId, int id_User, String content, Timestamp createdAt,String userImageUrl) {

        this.postId = postId;
        this.id_User = id_User;
        this.content = content;
        this.createdAt = createdAt;
        this.userImageUrl = userImageUrl;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public int getId_User() { return id_User; }
    public void setId_User(int id_User) { this.id_User = id_User; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }


    // 🛠 Getters et Setters
    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    // Méthode toString()
    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", postId=" + postId +
                ", id_User=" + id_User +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    // Constructeur sans imageURL
    public Comment(int postId, int id_User, String content, Timestamp createdAt) {
        this.postId = postId;
        this.id_User = id_User;
        this.content = content;
        this.createdAt = createdAt;
        //this.userImageUrl = null; // Vous pouvez soit laisser ceci à null, soit ne pas l'initialiser ici.
    }


}
