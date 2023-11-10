package com.project.airportsimulator.airplane.enums;

/**
 * Enumerated type representing different statuses of an airplane.
 *
 * @author Dhairya
 * @since November 5, 2023
 */
public enum AirplaneStatus {
    DUE("Due for arrival or departure"),
    WAITING("Waiting on the ground"),
    LANDED("Successfully landed"),
    DEPARTING("Preparing for departure");

    private final String description;

    /**
     * Constructor for AirplaneStatus enum.
     *
     * @param description A human-readable description of the status.
     */
    AirplaneStatus(String description) {
        this.description = description;
    }

    /**
     * Get a description of the airplane status.
     *
     * @return A description of the status.
     */
    public String getDescription() {
        return description;
    }
}