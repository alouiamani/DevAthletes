package org.gymify.entities;

public class Reaction {
    private int id;
    private int postId;
    private int id_User;
    private String type; // "like", "love", "haha", etc.

    public Reaction(int postId, int id_user, String type) {
        this.postId = postId;
        this.id_User = id_User;
        this.type = type;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public int getId_User() { return id_User; }
    public void setId_User(int id_user) { this.id_User = id_User; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
