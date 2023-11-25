package com.project.airportsimulator.airport;

import com.project.airportsimulator.airplane.Airplane;
import com.project.airportsimulator.airplane.enums.AirplaneStatus;
import com.project.airportsimulator.airport.portexceptions.AirportException;

import java.io.*;
import java.util.*;

/**
 * Class that implements functionalities of airport system
 *
 * @author Dhairya
 * @since November 9, 2023
 */
public class Airport {
    // Attributes
    private Map<String, Airplane> airplanes;    //added planes
    private Queue<Airplane> waitingQueue;     // list of circling planes waiting to land
    private Runway[] runways;   // Available runways in airport

    // Methods

    /**
     * This Constructor creates an empty collection of planes and allocates number of runways
     *
     * @param numberOfRunways The number of runway
     * @throws AirportException if negative number used for runway
     */
    public Airport(int numberOfRunways) {
        try {
            // init
            runways = new Runway[numberOfRunways];
            for (int i = 0; i < numberOfRunways; i++) {
                runways[i] = new Runway(i + 1);
            }
            airplanes = new HashMap<>();
            waitingQueue = new PriorityQueue<>();
        } catch (Exception e) {
            throw new AirportException("Invalid Runway Number set");
        }
    }

    /**
     * This constructor is used to load airport data from a local stored file
     *
     * @param fileName The name of locally stored file
     * @throws IOException            if any problem with opening and loading given file
     * @throws ClassNotFoundException if type of object differs from the right type required
     */
    public Airport(String fileName) throws IOException, ClassNotFoundException {
        load(fileName);
    }

    /**
     * Loads airport data from file
     *
     * @param fileName The name of locally stored file
     * @throws IOException            if any problem with opening and loading given file
     * @throws ClassNotFoundException if type of object differs from the right type required
     */
    public void load(String fileName) throws IOException, ClassNotFoundException {
        // File closes safely
        // Assumes that objects are stored in right format to load the data successfully
        try (FileInputStream input = new FileInputStream(fileName);
             ObjectInputStream objectInput = new ObjectInputStream(input)) {
            airplanes = (Map<String, Airplane>) objectInput.readObject();
            waitingQueue = (Queue<Airplane>) objectInput.readObject();
            runways = (Runway[]) objectInput.readObject();
        }
    }

    /**
     *
     * @param fileName
     * @throws IOException
     */
    public void save(String fileName) throws IOException{
        // File closes safely
        try (FileOutputStream output = new FileOutputStream(fileName);
             ObjectOutputStream objectOutput = new ObjectOutputStream(output)) {
            objectOutput.writeObject(airplanes);
            objectOutput.writeObject(waitingQueue);
            objectOutput.writeObject(runways);
        }
    }

    /**
     * Add or register an incoming plane with airport
     *
     * @param flightNumber The flight number of incoming flight
     * @param cityOfOrigin The city of origin of flight (from location the flight is coming)
     * @throws AirportException If flight is already registered with airplane
     */
    public void flightRegister(String flightNumber, String cityOfOrigin) {
        if (airplanes.containsKey(flightNumber)) {
            throw new AirportException("flight-" + flightNumber + " already registered with airport.");
        }
        Airplane newAirPlane = new Airplane(flightNumber, cityOfOrigin);
        airplanes.put(flightNumber, newAirPlane);
    }

    /**
     *
     * @param flightNumber
     * @return
     */
    public int airplaneAssignedRunway(String flightNumber) {
        Runway emptyRunway = null;
        // Find the next free runway
        for (Runway nextRunway : runways) {
            System.out.println(nextRunway.getRunwayNumber());
            System.out.println(nextRunway.isAllocated());
            if (!nextRunway.isAllocated()) {
                emptyRunway = nextRunway;
                break;
            }
        }
        if (emptyRunway == null) {
            addToWaitingQueue(flightNumber);    // Add airplane to waiting queue as no runway is available for land
            return 0;
        } else {
            assignRunway(flightNumber, emptyRunway);    // Assign runway to airplane
            return emptyRunway.getRunwayNumber();   // Return the assigned empty runway number
        }
    }

    /**
     *
     * @param flightNumber
     * @return
     * @throws AirportException
     */
    private Airplane findAirplane(String flightNumber)
    {
        if (!airplanes.containsKey(flightNumber))
        {
            throw new AirportException ("Flight-"+flightNumber+" has not been registered yet.");
        }
        return airplanes.get(flightNumber);
    }

    /**
     *
     * @param flightNumber
     * @param emptyRunway
     * @throws AirportException
     */
    private void assignRunway(String flightNumber, Runway emptyRunway){
        Airplane airPlane = findAirplane(flightNumber);     //throws exception if flight not been registered with airport yet
        if (airPlane.getStatus().compareTo(AirplaneStatus.WAITING) > 0){
            throw new AirportException(("Flight-" +flightNumber+ " already at airport with current status "+airPlane.getStatus()));
        }
        if (airPlane.isAllocatedARunway()){
            throw new AirportException("Flight-" +flightNumber+ "has already been allocated runway number=" + airPlane.getRunway());
        }
        airPlane.allocateRunway(emptyRunway);
        if (airPlane.getStatus() == AirplaneStatus.DUE) airPlane.upgradeStatus();
    }

    /**
     *
     * @param flightNumber
     */
    private void addToWaitingQueue(String flightNumber){
        Airplane airPlane = findAirplane(flightNumber); //throws exception if flight not been registered with airport yet
        if (airPlane.getStatus() != AirplaneStatus.DUE){
            throw new AirportException("Flight-" +flightNumber+ "has either already arrived at airport or already been assigned runway.");
        }
        airPlane.upgradeStatus();
        waitingQueue.add(airPlane);
    }

    /**
     *
     * @param flightNumber
     * @param runwayNumber
     */
    public void readyToLand(String flightNumber, int runwayNumber){
        Airplane airPlane = findAirplane(flightNumber); //throws exception if flight not been registered with airport yet
        if (airPlane.getRunwayNumber()!=runwayNumber){
            throw new AirportException("Flight-" +flightNumber+ "is assigned runway number=" +airPlane.getRunway()+ "not runway=" +runwayNumber);
        }
        if (airPlane.getStatus()==AirplaneStatus.DUE){
            throw new AirportException("Flight-" +flightNumber+ "has not been assigned runway yet.");
        }
        if (airPlane.getStatus().compareTo(AirplaneStatus.WAITING) > 0) {   // Check if status of flight is neither landed nor departing
            throw new AirportException("Flight-" +flightNumber+ "has already landed");
        }
        airPlane.upgradeStatus();
    }

    /**
     *
     * @param flightNumber
     * @param destination
     */
    public void readyToBoard(String flightNumber, String destination){
        Airplane airPlane = findAirplane(flightNumber); //throws exception if flight not been registered with airport yet
        // Check if flight landed
        if (airPlane.getStatus().compareTo(AirplaneStatus.LANDED) < 0){
            throw new AirportException("Flight-" +flightNumber+ " hasn't landed yet.");
        }
        // Check if flight already departed
        if (airPlane.getStatus()==AirplaneStatus.DEPARTING)
        {
            throw new AirportException ("Flight-"+flightNumber+" already registered for depart");
        }
        airPlane.upgradeStatus();
        // Assign destination city
        airPlane.setDestinationCity(destination);
    }

    /**
     *
     * @param flightNumber
     */
    private void readyToLeave(String flightNumber){
        Airplane airPlane = findAirplane(flightNumber); //throws exception if flight not been registered with airport yet
        // check if plane is ready to leave
        if (airPlane.getStatus().compareTo(AirplaneStatus.LANDED) < 0){
            throw new AirportException("Flight-" +flightNumber+ "has not landed yet");
        }
        if (airPlane.getStatus()==AirplaneStatus.LANDED){
            throw new AirportException("Flight-" +flightNumber+ "has not started boarding yet.");
        }
        // Empty runway to assign another flight
        airPlane.vacateRunway();
        airplanes.remove(flightNumber);
    }

    /**
     *
     * @param flightNumber
     * @return
     */
    public Airplane readyToTakeOff(String flightNumber){
        readyToLeave(flightNumber);

        // Check if there is a flight waiting for land
        Airplane nextAirplane = getNextAvailableFlight();
        if (nextAirplane!=null){
            Runway emptyRunway = getNextFreeRunway();
            assignRunway(nextAirplane.getFlightNumber(), emptyRunway);
            return nextAirplane;
        }
        // No airplane waiting to land
        else{
            return null;
        }
    }

    /**
     *
     * @return
     */
    private Runway getNextFreeRunway() {
        for (Runway runwayEmpty : runways)
        {
            if (!runwayEmpty.isAllocated())
            {
                return runwayEmpty;
            }
        }
        return null;
    }

    /**
     *
     * @return
     */
    private Airplane getNextAvailableFlight() {
        if (!waitingQueue.isEmpty()){
            Airplane nextPlaneToLand = waitingQueue.poll();
            return nextPlaneToLand;
        }
        else{
            return null;
        }
    }

    public int getNumberOfRunways(){return runways.length;}

    /**
     * Returns all the planes due for arrival
     *
     * @return
     */
    public Set<Airplane> getAllArrivals(){
        Set<Airplane> airplaneSet = new HashSet<>();
        Set<String> airplaneNumberSet = airplanes.keySet();
        for (String airplaneNumber: airplaneNumberSet){
            Airplane airPlane = airplanes.get(airplaneNumber);
            if (airPlane.getStatus()!=AirplaneStatus.DEPARTING){
                airplaneSet.add(airPlane);
            }
        }
        return airplaneSet;
    }


    /**
     * Returns all the planes due for departure
     *
     * @return
     */
    public Set<Airplane> getAllDepartures(){
        Set<Airplane> airplaneSet = new HashSet<>();
        Set<String> airplaneNumberSet = airplanes.keySet();
        for (String airplaneNumber: airplaneNumberSet){
            Airplane airPlane = airplanes.get(airplaneNumber);
            if (airPlane.getStatus()==AirplaneStatus.DEPARTING){
                airplaneSet.add(airPlane);
            }
        }
        return airplaneSet;
    }

}
