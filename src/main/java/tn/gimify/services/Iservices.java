package tn.gimify.services;

import java.sql.SQLException;
import java.util.List;

public interface Iservices<T> {
    default void ajouter(T t) throws SQLException {}
    default void ajouter(T t, int j) throws SQLException {}
    void modifier(T t) throws SQLException;
     void supprimer(int id) throws SQLException;
     List<T> afficher() throws SQLException;


}
