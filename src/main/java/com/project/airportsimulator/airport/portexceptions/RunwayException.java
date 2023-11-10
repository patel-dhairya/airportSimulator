package com.project.airportsimulator.airport.portexceptions;

    /**
     * Custom exception for the Runway section of the Airport Simulator application.
     *
     * <p>This exception is thrown to indicate issues related to the Runway component.
     *
     * @author Dhairya
     * @since November 5, 2023
     */
public class RunwayException extends RuntimeException {

     /**
     * Constructs a new RunwayException with a generic error message.
     */
     public RunwayException() {
            this("Error: Airport Simulator Runway error");
     }

     /**
        * Constructs a new RunwayException with a specific error message.
        *
        * @param message The error message to be associated with this exception.
      */
     public RunwayException(String message) {
            super(message);
        }
    }
