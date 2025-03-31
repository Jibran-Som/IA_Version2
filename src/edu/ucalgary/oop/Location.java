package edu.ucalgary.oop;

import java.util.ArrayList;

public class Location {
    private String locationName;
    private String locationAddress;
    private ArrayList<DisasterVictim> occupants;
    private ArrayList<Supply> inventory;
    private int locationId = createLocationId();
    private static int counter = 100;


    // Constructor
    public Location(String locationName, String locationAddress) {
        if (locationName == null || locationName.trim().isEmpty()) {
            throw new IllegalArgumentException("Location name cannot be null or empty");
        }
        if (locationAddress == null || locationAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Location address cannot be null or empty");
        }

        this.locationName = locationName;
        this.locationAddress = locationAddress;
        this.occupants = new ArrayList<>();
        this.inventory = new ArrayList<>();
    }

    // Getters
    public String getLocationName() {
        return locationName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public int getLocationId() {
        return locationId;
    }

    public ArrayList<DisasterVictim> getOccupants() {
        return occupants;
    }


    // Setters
    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public void setOccupants(ArrayList<DisasterVictim> occupants) {
        this.occupants = occupants;
    }



    // Class Specific Code

    // Controlling DisasterVictims
    public void addOccupant(DisasterVictim occupant) {
        occupants.add(occupant);
    }

    public void removeOccupant(DisasterVictim occupant) {
        if(!(occupants.contains(occupant))) {
            throw new IllegalArgumentException("The given occupant is not in the list");
        }
        occupants.remove(occupant);
    }





    // Controlling Supply
    public void addItem(Supply item) {
        if (item == null) {
            throw new IllegalArgumentException("Supply cannot be null");
        }
        if (item instanceof PersonalBelonging) {
            throw new IllegalArgumentException("Personal belongings cannot be added to a location");
        }


        inventory.add(item);
    }

    public void removeItem(Supply item) {
        inventory.remove(item);
    }


    public void allocateItem(DisasterVictim victim, Supply supply) {
        if (!inventory.contains(supply)) {
            throw new IllegalArgumentException("Supply not available in this location");
        }

        if (supply instanceof PersonalBelonging) {
            throw new IllegalArgumentException("Cannot allocate personal belongings");
        }

        if (supply instanceof Water) {
            inventory.remove(supply);
            return; // Exit early (water is deleted, not given to victim)
        }
        inventory.remove(supply);
        victim.addItem(supply);
    }




    // Private Code for Checking or Initialization

    private int createLocationId() {
        return counter++;
    }


}
