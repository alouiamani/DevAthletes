package org.gymify.services;

import org.gymify.utils.gymifyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StatistiquesService {
    private final Connection con; // ✅ Garder une connexion ouverte

    public StatistiquesService() {
        this.con = gymifyDataBase.getInstance().getConnection();
        try {
            if (con.isClosed()) {
                System.out.println("❌ Connexion fermée avant exécution de la requête !");
            } else {
                System.out.println("✅ Connexion active !");
            }
        } catch (SQLException e) {
            throw new RuntimeException("❌ Erreur lors de la vérification de la connexion : " + e.getMessage());
        }
    }

    public Map<String, Integer> getNombreUtilisateursParRole() {
        Map<String, Integer> stats = new HashMap<>();

        String query = "SELECT IFNULL(role, 'Inconnu') AS role, COUNT(*) as total FROM user GROUP BY role";

        try (PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String role = rs.getString("role").trim();
                if (role.isEmpty()) {
                    role = "Inconnu";  // Remplacer les rôles vides
                }
                int total = rs.getInt("total");
                stats.put(role, total);
                System.out.println("🔹 Rôle: " + role + ", Total: " + total);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL : " + e.getMessage());
        }
        return stats;
    }
}
