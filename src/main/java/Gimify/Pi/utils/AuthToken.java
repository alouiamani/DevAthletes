package Gimify.Pi.utils;

import Gimify.Pi.entities.User;

public class AuthToken {
    public static User currentUser;

    // 🔹 Stocke l'utilisateur actuellement connecté
    public static void setCurrentUser(User user) {
        currentUser = user;
        if (currentUser == null) {
            System.out.println("Erreur : Aucun utilisateur sélectionné !");
            return;
        }
    }

    // 🔹 Récupère l'utilisateur actuellement connecté
    public static User getCurrentUser() {
        return currentUser;
    }

    // 🔹 Déconnecte l'utilisateur
    public static void logout() {
        currentUser = null;
    }
}
