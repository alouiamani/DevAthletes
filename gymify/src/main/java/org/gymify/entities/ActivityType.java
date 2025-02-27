package org.gymify.entities;

public enum ActivityType {
    PERSONAL_TRAINING("Personal Training"),
    GROUP_ACTIVITY("Group Activity"),
    FITNESS_CONSULTATION("Fitness Consultation");

    private final String label;

    ActivityType(String label) {
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
