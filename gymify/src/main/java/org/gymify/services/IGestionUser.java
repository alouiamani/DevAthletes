package org.gymify.services;

import java.sql.SQLException;
import java.util.List;

public interface IGestionUser<T> {
    void ajouter(T t) throws SQLException;

    void modifier(T t) throws SQLException;

    void supprimer(int t) throws SQLException;

    List<T> afficher() throws SQLException;

}
