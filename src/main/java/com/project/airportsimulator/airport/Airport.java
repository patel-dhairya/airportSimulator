package com.project.airportsimulator.airport;

import com.project.airportsimulator.airplane.Airplane;
import java.util.Map;
import java.util.Queue;

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
    private Runway []runways;   // Available runways in airport

    // Further methods will be added after testing on my local computer
}
