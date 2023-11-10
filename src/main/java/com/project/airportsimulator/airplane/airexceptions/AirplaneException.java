package com.project.airportsimulator.airplane.airexceptions;

/**
 * Custom exception for the Airplane class in the Airport Simulator system.
 *
 * <p>This exception is thrown to indicate issues related to the Airplane component.
 *
 * @author Dhairya
 * @since November 5, 2023
 */
public class AirplaneException extends RuntimeException {

    /**
     * Constructs a new AirplaneException with a generic error message.
     */
    public AirplaneException() {
        this("Error: Airport Simulator Airplane error");
    }

    /**
     * Constructs a new AirplaneException with a specific error message.
     *
     * @param message The error message to be associated with this exception.
     */
    public AirplaneException(String message) {
        super(message);
    }
}
