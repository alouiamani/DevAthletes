package org.gymify.entities;

public enum EventType {
    COMPETITION,
    ENTRAINEMENT,
    RANDONNEE;

    public enum Niveau {
        Debutant,
        Intermediaire,
        Avance,
        Professionnel;
    }

    public enum Reward {
        MEDAILLE,
        CERTIFICAT,
        TROUPHEE;
    }
}
