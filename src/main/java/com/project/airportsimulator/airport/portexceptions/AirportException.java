package com.project.airportsimulator.airport.portexceptions;

/**
 * Custom exception for the Airport section of the Airport Simulator application.
 *
 * <p>This exception is thrown to indicate issues related to the Airport component.
 *
 * @author Dhairya
 * @since November 23, 2023
 */
public class AirportException extends RuntimeException {

    /**
     * Constructs a new AirportException with a generic error message.
     */
    public AirportException() {
        this("Error: Airport Simulator Runway error");
    }

    /**
     * Constructs a new AirportException with a specific error message.
     *
     * @param message The error message to be associated with this exception.
     */
    public AirportException(String message) {
        super(message);
    }
}
