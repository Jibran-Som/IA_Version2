package edu.ucalgary.oop;

public class Cot extends Supply {
    private String roomLocation;
    private String gridLocation;


    // Constructor
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


    // Getters
    public String getRoomLocation() {
        return roomLocation;
    }

    public String getGridLocation() {
        return gridLocation;
    }


    // Setters
    public void setRoomLocation(String roomLocation) {
        if (roomLocation == null || roomLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("Room location cannot be null or empty");
        }
        this.roomLocation = roomLocation;
    }


    public void setGridLocation(String gridLocation) {
        if (gridLocation == null || gridLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("Grid location cannot be null or empty");
        }
        this.gridLocation = gridLocation;
    }
}