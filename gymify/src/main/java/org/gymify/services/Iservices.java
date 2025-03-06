package org.gymify.services;

import java.sql.SQLException;
import java.util.List;

public interface Iservices<T> {
    default int ajouter(T t) throws SQLException {
        return 0;
    }
    default void ajouter(T t, int j) throws SQLException {}
    void modifier(T t) throws SQLException;
     void supprimer(int id) throws SQLException;
     List<T> afficher() throws SQLException;


}
