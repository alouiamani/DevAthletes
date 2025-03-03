package org.gymify.entities;

public enum EventType {
    COMPETITION,
    ENTRAINEMENT,
    RANDONEE;

    public enum Niveau {
        Debutant,
        Intermediaire,
        Avance,
        Proffessionnel;
    }

    public enum Reward {
        MEDAILLE,
        CERTIFICAT,
        TROUPHEE;
    }
}
