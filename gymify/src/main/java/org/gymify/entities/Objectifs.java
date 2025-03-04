package org.gymify.entities;

public enum Objectifs {
    PERTE_PROIDS("perte de poids"),
    PRISE_DE_MASSE("prise de masse"),
    ENDURANCE("endurance"),
    RELAXATION ("relaxation");
    private final String label;

    Objectifs(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;

    }
}
