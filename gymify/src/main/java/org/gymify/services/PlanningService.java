package org.gymify.services;

import org.gymify.entities.Activité;
import org.gymify.entities.Planning;
import org.gymify.entities.User;
import org.gymify.utils.gymifyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlanningService implements IService<Planning> {
    private Connection con;
    public PlanningService() {
        con = gymifyDataBase.getInstance().getConnection();
    }

    @Override
    public void Add(Planning planning) {
        try{
            PreparedStatement req=con.prepareStatement ("INSERT INTO `planning`(`date_debut`, `description`, `title`, `date_fin`,`entaineur_id`) VALUES (?,?,?,?,?)");
            req.setDate(1,new Date(planning.getdateDebut().getTime()));
            req.setString(2,planning.getDescription());
            req.setString(3,planning.getTitle());
            req.setDate(4,new Date(planning.getdateFin().getTime()));
            req.setInt(5,planning.getUser().getId());
            req.executeUpdate();
            System.out.println("Planning ajouté avec succès.");

        }catch (SQLException e){
            System.out.println("Erreur lors de l'ajout du planning : " + e.getMessage());

        }


    }

    @Override
    public void Update(Planning planning) {
        try{
            PreparedStatement req=con.prepareStatement ("UPDATE `planning` SET description=?,title=? WHERE id=?");
            req.setString(1,planning.getDescription());
            req.setString(2,planning.getTitle());
            req.setInt(3,planning.getId());
            req.executeUpdate();
            System.out.println("Planning mis à jour avec succès.");

        }catch (SQLException e){
            System.out.println("Erreur lors de la mise à jour du planning : " + e.getMessage());

        }

    }

    @Override
    public void Delete(Planning planning) {
        try{
            PreparedStatement req= con.prepareStatement("DELETE FROM planning WHERE id=?");
            req.setInt(1,planning.getId());
            req.executeUpdate();
            System.out.println("Planning supprimé avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du planning : " + e.getMessage());

        }

    }

    @Override
    public List<Planning> Display() {
        List<Planning> plannings = new ArrayList<>();
        try {
            String sql = "SELECT * FROM planning";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                Planning planning = new Planning();
                planning.setId(rs.getInt("id"));
                planning.setdateDebut(rs.getDate("date_debut"));
                planning.setDescription(rs.getString("description"));
                planning.setTitle(rs.getString("title"));
                planning.setDateFin(rs.getDate("date_fin"));
                int entraineurId = rs.getInt("entaineur_id");
                if (entraineurId != 0) {
                   User user=new User(); // Créez une instance de l'activité
                    user.setId(entraineurId); // Vous pouvez récupérer d'autres informations si nécessaire
                    planning.setUser(user);
                }
                plannings.add(planning);
            }


        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage des plannings : " + e.getMessage());

        }
        return plannings;
    }
    public List<Planning> DisplayById(int userId) {
        List<Planning> plannings = new ArrayList<>();
        try {
            // Exemple avec une base de données SQL
            String query = "SELECT * FROM Planning WHERE `entaineur_id` = ?";

            // Assurez-vous d'avoir une classe DatabaseConnection pour gérer la connexion
            PreparedStatement statement = con.prepareStatement(query);

            statement.setInt(1, userId); // Définir l'ID de l'utilisateur dans la requête

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // Créer un objet Planning à partir des résultats de la requête
                    Planning planning = new Planning();
                    planning.setId(resultSet.getInt("id"));
                    planning.setTitle(resultSet.getString("title"));
                    planning.setDescription(resultSet.getString("description"));
                    planning.setdateDebut(resultSet.getDate("date_debut"));
                    planning.setDateFin(resultSet.getDate("date_fin"));
                    int entraineurId = resultSet.getInt("entaineur_id");
                    if (entraineurId != 0) {
                        User user = new User(); // Créez une instance de l'activité
                        user.setId(entraineurId); // Vous pouvez récupérer d'autres informations si nécessaire
                        planning.setUser(user);
                    }

                    plannings.add(planning);
                }
            } }catch (SQLException e) {
                System.out.println("Erreur lors de l'affichage des plannings : " + e.getMessage());

            }

            return plannings;
        }

}
