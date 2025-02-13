package tn.gimify.services;
import tn.gimify.entities.Salle;
import tn.gimify.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public  class SalleService implements Iservices<Salle> {
        Connection connection;
        public SalleService(){
            connection= MyDatabase.getInstance().getConnection();

        }
        @Override
        public void ajouter(Salle salle) throws SQLException {

            String req = "INSERT INTO salle (nom, adresse, details, num_tel, email) VALUES ('"
                    + salle.getNom() + "', '"
                    + salle.getAdresse() + "', '"
                    + salle.getDetails() + "', '"
                    + salle.getNum_tel() + "', '"
                    + salle.getEmail() + "')";

            Statement statement = connection.createStatement();
            statement.executeUpdate(req);
            System.out.println("Salle ajoutée avec succès !");


            }

        @Override
        public void modifier(Salle salle) throws SQLException {
            String req="UPDATE `salle` SET nom=?,adresse=?,details=?,num_tel=?,email=? WHERE id_Salle=?";
            PreparedStatement preparedStatement= connection.prepareStatement(req);
            preparedStatement.setString(1,salle.getNom());
            preparedStatement.setString(2,salle.getAdresse());
            preparedStatement.setString(3,salle.getDetails());
            preparedStatement.setString(4,salle.getNum_tel());
            preparedStatement.setString(5,salle.getEmail());
            preparedStatement.setInt(6,salle.getId_Salle());
            preparedStatement.executeUpdate();
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println(" Modification réussie !");
            } else {
                System.out.println("Aucune ligne mise à jour ID incorrect ?");
            }


        }

        @Override
        public void supprimer(int id) throws SQLException {
            String req = "DELETE FROM salle WHERE id_Salle=?";
            PreparedStatement preparedStatement= connection.prepareStatement(req);

               preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
                System.out.println(" Événement supprimé avec succès !");

            }



        @Override
        public List<Salle> afficher() throws SQLException {
            List<Salle> salles= new ArrayList<>();
            String req="SELECT * FROM salle";
            Statement statement= connection.createStatement();

            ResultSet rs= statement.executeQuery(req);
            while (rs.next()){
                Salle salle= new Salle();
                salle.setId_Salle(rs.getInt("id_Salle"));
                salle.setNom(rs.getString("nom"));
                salle.setAdresse(rs.getString("adresse"));
                salle.setDetails(rs.getString("details"));
                salle.setNum_tel(rs.getString("num_tel"));
                salle.setEmail(rs.getString("email"));


                salles.add(salle);
            }


            return salles;
        }
    }


