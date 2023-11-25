package com.project.airportsimulator.airplane;
import com.project.airportsimulator.airplane.airexceptions.AirplaneException;
import com.project.airportsimulator.airplane.enums.AirplaneStatus;
import com.project.airportsimulator.airport.Runway;

/**
 * Represents a single airplane in the Airport Simulator system.
 *
 * <p>This class stores details of a plane, including flight number, city of origin, status, and
 * the associated runway.
 *
 * @author Dhairya
 * @since November 6, 2023
 */
public class Airplane {
    // Attributes
    private final String flightNumber;    // The flight number
    public String originCity;           // Origin city of airplane
    private String destinationCity;        // Destination city of airplane
    private AirplaneStatus currentStatus;      // Current status of airplane
    private Runway currentAllocatedRunway; // to implement Runway association


    // methods
    /**
     * Constructs an Airplane object with given flight number and city of origin.
     *
     * @param flightNumber The flight number of the plane to register
     * @param cityOfOrigin The city of origin of the plane to register
     */
    public Airplane(String flightNumber, String cityOfOrigin)
    {
        this.flightNumber = flightNumber;
        this.originCity = cityOfOrigin;
        this.currentStatus = AirplaneStatus.DUE;
        this.currentAllocatedRunway = null; // indicates no runway is allocated during construct of new plane
        this.destinationCity = null; // will be assigned once airplane reach the airport, shows the new destination of flight
    }

    // Getter Methods

    /**
     * Gets the flight number of the airplane.
     *
     * @return The flight number.
     */
    public String getFlightNumber() {
        return this.flightNumber;
    }

    /**
     * Gets the origin city associated with the flight.
     *
     * @return The city of origin.
     */
    public String getOriginCity() {
        return this.originCity;
    }

    /**
     * Gets the destination city associated with the flight.
     *
     * @return The city of destination.
     */
    public String getDestinationCity() {
        return this.destinationCity;
    }

    /**
     * Gets the current status of the airplane.
     *
     * @return The current status.
     */
    public AirplaneStatus getStatus() {
        return this.currentStatus;
    }

    /**
     * Gets the current status alongside description of the airplane
     *
     * @return The current status description.
     */
    public String getStatusDesc(){
        return this.currentStatus.getDescription();
    }

    /**
     * Gets the runway allocated to this airplane or null if no runway is allocated.
     *
     * @return The allocated runway.
     */
    public Runway getRunway() {
        return this.currentAllocatedRunway;
    }

    /**
     * Checks if the airplane is allocated a runway.
     *
     * @return True if the airplane has been allocated a runway; false otherwise.
     */
    public boolean isAllocatedARunway() {
        return this.currentAllocatedRunway != null;
    }


    /**
     * Gets the runway number allocated to this airplane.
     *
     * @return The runway number.
     * @throws AirplaneException If no runway is allocated.
     */
    public int getRunwayNumber() {
        if (!isAllocatedARunway()) {
            throw new AirplaneException("Flight " + flightNumber + " has not been allocated a runway");
        }
        return currentAllocatedRunway.getRunwayNumber();
    }

    /**
     *
     * @param destinationCity
     */
    public void setDestinationCity(String destinationCity){
        this.destinationCity = destinationCity;
    }

    /**
     * Allocates the given runway to the airplane.
     *
     * @param runway The runway to be allocated.
     * @throws AirplaneException If the runway parameter is null or if the runway is already allocated.
     */
    public void allocateRunway(Runway runway) throws AirplaneException {
        if (runway == null) {
            throw new AirplaneException("No runway to allocate");
        }
        if (runway.isAllocated()) {
            throw new AirplaneException("Runway already allocated");
        }
        this.currentAllocatedRunway = runway;
        runway.book();
    }

    /**
     * De-allocates the current runway.
     *
     * @throws AirplaneException If no runway is allocated.
     */
    public void vacateRunway() {
        if (this.currentAllocatedRunway == null) {
            throw new AirplaneException("No runway allocated");
        }
        currentAllocatedRunway.vacate();
    }

    /**
     * Upgrades the status of the airplane.
     */
    public void upgradeStatus() {
        switch (currentStatus) {
            case DUE -> currentStatus = AirplaneStatus.WAITING;
            case WAITING -> currentStatus = AirplaneStatus.LANDED;
            case LANDED -> currentStatus = AirplaneStatus.DEPARTING;
            case DEPARTING -> throw new AirplaneException("Cannot upgrade DEPARTING status");
        }
    }

    // Overrides

    /**
     * Returns a string representation of the airplane.
     *
     * @return A string representation including flight number,origin city, destination city, status, and allocated runway.
     */
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("Number: " + flightNumber + "\tOrigin City: " + originCity + "\tFlight Status: " + currentStatus);
        if (currentAllocatedRunway != null) {
            output.append("\tRunway: ").append(currentAllocatedRunway);
        }
        return output.toString();
    }

    /**
     * Checks whether the airplane is equal to the given object.
     *
     * @param obj The object to compare.
     * @return True if the objects are equal; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Airplane airplane = (Airplane) obj;
        return flightNumber.equals(airplane.flightNumber);
    }

    /**
     * Returns a hashcode value.
     *
     * @return The hashcode based on the flight number.
     */
    @Override
    public int hashCode() {
        return flightNumber.hashCode();
    }
}
