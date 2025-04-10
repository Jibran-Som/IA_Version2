/**
 * Cot.java
 * Version: 2.0
 * Author: Jibran Somroo
 * Date: April 4, 2025
 */

package edu.ucalgary.oop;

public class Cot extends Supply {
    private String roomLocation;
    private String gridLocation;


    /**
     * Constructor for creating a Cot object.
     * Initializes the supply name and supply type by calling the parent class constructor.
     * Validates and sets the room and grid locations for the cot.
     * Throws an IllegalArgumentException if either location is null or empty.
     *
     * @param supplyName The name of the cot supply.
     * @param supplyType The type or category of the cot supply.
     * @param roomLocation The room location where the cot is placed.
     * @param gridLocation The grid location where the cot is placed.
     * @throws IllegalArgumentException If roomLocation or gridLocation is null or empty.
     */
    public Cot(String supplyName, String supplyType, String roomLocation, String gridLocation) {
        super(supplyName, supplyType); // Call parent constructor
        if (roomLocation == null || roomLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("Room location cannot be null or empty");
        }
        if (gridLocation == null || gridLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("Grid location cannot be null or empty");
        }
        this.roomLocation = roomLocation;
        this.gridLocation = gridLocation;
    }


    /**
     * Getter for the cot's room location.
     * Retrieves the current room location where the cot is placed.
     *
     * @return The room location of the cot.
     */
    public String getRoomLocation() {
        return roomLocation;
    }


    /**
     * Getter for the cot's grid location.
     * Retrieves the current grid location where the cot is placed.
     *
     * @return The grid location of the cot.
     */
    public String getGridLocation() {
        return gridLocation;
    }


    /**
     * Setter for the cot's room location.
     * Sets the room location where the cot will be placed.
     * Throws an IllegalArgumentException if the provided room location is null or empty.
     *
     * @param roomLocation The desired room location for the cot.
     * @throws IllegalArgumentException If roomLocation is null or empty.
     */

    public void setRoomLocation(String roomLocation) {
        if (roomLocation == null || roomLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("Room location cannot be null or empty");
        }
        this.roomLocation = roomLocation;
    }

    /**
     * Setter for the cot's grid location.
     * Sets the grid location where the cot will be placed.
     * Throws an IllegalArgumentException if the provided grid location is null or empty.
     *
     * @param gridLocation The desired grid location for the cot.
     * @throws IllegalArgumentException If gridLocation is null or empty.
     */
    public void setGridLocation(String gridLocation) {
        if (gridLocation == null || gridLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("Grid location cannot be null or empty");
        }
        this.gridLocation = gridLocation;
    }


}