package com.project.airportsimulator.airport;

import com.project.airportsimulator.airport.portexceptions.RunwayException;

import java.io.Serializable;

/**
 * Represents a single runway at the airport.
 *
 * <p>This class stores details of a runway, including its number and allocation status.
 *
 * @author Dhairya
 * @since November 5, 2023
 */
public class Runway implements Serializable {

    // Attributes
    private int runwayNumber;       // Runway number
    private boolean isAllocated;    // Check if runway is allocated for plane

    /**
     * Constructs a Runway with the given runway number.
     *
     * @param runwayNumber The number to set for the runway.
     * @throws RunwayException if the runway number is less than 1.
     */
    public Runway(int runwayNumber) {
        validateRunwayNumber(runwayNumber);
        this.runwayNumber = runwayNumber;
        this.isAllocated = false; // Runway is vacant initially
    }

    /**
     * Returns the runway number.
     *
     * @return The runway number.
     */
    public int getRunwayNumber() {
        return runwayNumber;
    }

    /**
     * Checks if the runway has been allocated.
     *
     * @return True if the runway has been allocated; false otherwise.
     */
    public boolean isAllocated() {
        return isAllocated;
    }

    /**
     * Marks the runway as booked.
     */
    public void book() {
        isAllocated = true;
    }

    /**
     * Marks the runway as vacant.
     */
    public void vacate() {
        isAllocated = false;
    }

    private void validateRunwayNumber(int runwayNumber) {
        if (runwayNumber < 1) {
            throw new RunwayException("Invalid runway number: " + runwayNumber);
        }
    }
}
