package services;

import java.sql.SQLException;
import java.util.List;


public interface IService<T> {
    // Méthode pour ajouter un élément
    public void ajouter(T t) throws SQLException;

    // Méthode pour modifier un élément
    public void modifier(T t) throws SQLException;

    // Méthode pour supprimer un élément par son identifiant
    public void supprimer(int id) throws SQLException;

    // Méthode pour afficher tous les éléments
    public List<T> afficher() throws SQLException;
}


