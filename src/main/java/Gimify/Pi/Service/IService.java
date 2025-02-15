package Gimify.Pi.Service;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {
    void ajouter(T t) throws SQLException;

    void modifier(T t) throws SQLException;

    void supprimer(int t) throws SQLException;

    List<T> afficher() throws SQLException;
}
