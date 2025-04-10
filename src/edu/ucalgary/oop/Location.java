/**
 * Location.java
 * Version: 1.0
 * Author: Jibran Somroo
 * Date: April 7, 2025
 */

package edu.ucalgary.oop;

import java.util.ArrayList;

public class Location {
    private String locationName;
    private String locationAddress;
    private ArrayList<Person> occupants;
    private ArrayList<Supply> inventory;
    private int locationId;
    private static TranslationManager translationManager;



    /**
     * Constructs a new Location object with the specified name and address.
     * The constructor validates the inputs to ensure neither the location name nor address is null or empty..
     *
     * @param locationName The name of the location. Can't be null or empty.
     * @param locationAddress The address of the location.
     * @throws IllegalArgumentException if either the location name or address is null or empty.
     */
    public Location(String locationName, String locationAddress) {
        translationManager = TranslationManager.getInstance();
        if (locationName == null || locationName.trim().isEmpty()) {
            throw new IllegalArgumentException(translationManager.getTranslation("error.locationNameEmpty"));
        }
        if (locationAddress == null || locationAddress.trim().isEmpty()) {
            throw new IllegalArgumentException(translationManager.getTranslation("error.locationAddressEmpty"));
        }

        this.locationName = locationName;
        this.locationAddress = locationAddress;
        this.occupants = new ArrayList<>();
        this.inventory = new ArrayList<>();
    }


    /**
     * Retrieves the name of the location.
     *
     * @return The name of the location.
     */
    public String getLocationName() {
        return locationName;
    }

    /**
     * Retrieves the address of the location.
     *
     * @return The address of the location.
     */
    public String getLocationAddress() {
        return locationAddress;
    }

    /**
     * Retrieves the id of the location.
     *
     * @return The id of the location.
     */
    public int getLocationId() {
        return locationId;
    }

    /**
     * Retrieves the occupants of the location.
     *
     * @return The arraylist of occupants at the location.
     */
    public ArrayList<Person> getOccupants() {
        return occupants;
    }

    /**
     * Retrieves the supplies of the location.
     *
     * @return The arraylist of supplies at the location.
     */
    public ArrayList<Supply> getLocationInventory() {
        return inventory;
    }


    /**
     * Sets the ID of the location.
     *
     * @param locationId The ID to be set for the location.
     */
    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    /**
     * Sets the name of the location.
     *
     * @param locationName The name to be set for the location.
     */
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    /**
     * Sets the address of the location.
     *
     * @param locationAddress The address to be set for the location.
     */
    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    /**
     * Sets the occupants of the location.
     *
     * @param occupants The arraylist of occupants to be set for the location.
     */
    public void setOccupants(ArrayList<Person> occupants) {
        this.occupants = occupants;
    }




    /**
     * Adds a new occupant to the location's list of occupants.
     *
     * @param occupant The Person to be added as an occupant of the location.
     */
    public void addOccupant(Person occupant) {
        occupants.add(occupant);
    }


    /**
     * Removes an occupant (Person) from the location's list of occupants.
     *
     * @param occupant The Person to be removed from the list of occupants.
     * @throws IllegalArgumentException if the given occupant is not in the list.
     */
    public void removeOccupant(Person occupant) {
        if(!(occupants.contains(occupant))) {
            throw new IllegalArgumentException("The given occupant is not in the list");
        }
        occupants.remove(occupant);
    }





    /**
     * Adds a new item (Supply) to the location's inventory.
     *
     * @param item The Supply object to be added to the inventory.
     * @throws IllegalArgumentException if the item is null or if the item is an instance of PersonalBelonging.
     */
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


    /**
     * Allocates a supply item to a disaster victim from location.
     * Note: Was created when controllers weren't the main things that
     * control the models and database.
     *
     * @param victim The DisasterVictim to whom the supply is allocated.
     * @param supply The Supply item to be allocated to the victim.
     * @throws IllegalArgumentException if the supply is not available in the inventory or if the supply is a personal belonging.
     */
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








}
