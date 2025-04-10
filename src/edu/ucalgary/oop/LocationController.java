/**
 * LocationController.java
 * Version: 3.0
 * Author: Jibran Somroo
 * Date: April 9, 2025
 */

package edu.ucalgary.oop;

import java.util.ArrayList;
import java.sql.SQLException;

public class LocationController {
    private ArrayList<Location> locationModels;
    private DatabaseManager databaseManager;
    private static int locationIdCounter;



    /**
     * Constructs a new LocationController object and initializes the database connection.
     *
     * @throws RuntimeException if there is an error initializing the LocationController or retrieving data from the database.
     */
    public LocationController() {
        try {
            this.databaseManager = DatabaseManager.getInstance();
            this.locationModels = new ArrayList<>();
            populateLocationsFromDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize LocationController", e);
        }
    }

    /**
     * Constructs a new LocationController object
     * Created for LocationControllerTest
     */
    public LocationController(int test) {
            this.locationModels = new ArrayList<>();
    }


    /**
     * Populates the locationModels list by retrieving all location data from the database.
     *
     * @throws SQLException if there is an error retrieving location data from the database.
     */
    private void populateLocationsFromDatabase() throws SQLException {
        try {
            ArrayList<Location> locations = (ArrayList<Location>) databaseManager.getAllLocations();
            this.locationModels.clear();
            this.locationModels.addAll(locations);
            initializeIdCounter();
        } catch (SQLException e) {
            System.err.println("Error loading locations from database: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves a copy of the list of all locations.
     *
     * @return A new ArrayList containing all Location objects in the locationModels list.
     */
    public ArrayList<Location> getAllLocations() {
        return new ArrayList<>(this.locationModels);
    }

    /**
     * Retrieves all locations.
     *  Note: Created for LocationControllerTest
     * @return A new ArrayList containing all Location objects
     */
    public ArrayList<Location> getAllLocationsTest() {
        return this.locationModels;
    }


    /**
     * Adds a new location to the system and stores it in the database.
     *
     * @param location The Location object to be added.
     * @throws IllegalArgumentException if the provided location is null.
     * @throws SQLException if there is an error adding the location to the database.
     */
    public void addLocation(Location location) throws SQLException {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }

        if(location.getLocationId() <= 0){
            location.setLocationId(generateLocationId());
        }

        try {
            databaseManager.addLocation(location);
            this.locationModels.add(location); // Add to local model
        } catch (SQLException e) {
            System.err.println("Error adding location: " + e.getMessage());
            locationIdCounter--;
            throw e;
        }
    }

    /**
     * Updates an existing location in the system and in the database.
     *
     * @param location The Location object containing updated data.
     * @throws IllegalArgumentException if the provided location is null.
     * @throws SQLException if there is an error updating the location in the database.
     */
    public void updateLocation(Location location) throws SQLException {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }

        try {
            databaseManager.updateLocation(location);
            // Find and update the location in local models
            for (int i = 0; i < locationModels.size(); i++) {
                if (locationModels.get(i).getLocationId() == location.getLocationId()) {
                    locationModels.set(i, location);
                    break;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating location: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Deletes a location from the system and the database using the specified location ID.
     * The method removes the location from both the database and the local locationModels list.
     * Note: Method isn't necessary nor is it perfect. As it doesn't consider situations where
     * the location has various things allocated to it.
     *
     * @param locationId The ID of the location to be deleted.
     * @throws SQLException if there is an error deleting the location from the database.
     */
    public void deleteLocation(int locationId) throws SQLException {
        try {
            databaseManager.deleteLocation(locationId);
            // Remove from local models
            locationModels.removeIf(l -> l.getLocationId() == locationId);
        } catch (SQLException e) {
            System.err.println("Error deleting location: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves a list of occupants at a specific location
     *
     * @param locationId The ID of the location whose occupants are to be retrieved.
     * @return An ArrayList of occupants
     * @throws SQLException if there is an error retrieving occupants from the database.
     */
    public ArrayList<Person> getOccupantsAtLocation(int locationId) throws SQLException {
        try {
            if(getAllLocations().isEmpty()) {
                return null;
            }
            return (ArrayList<Person>) databaseManager.getOccupantsAtLocation(locationId);
        } catch (SQLException e) {
            System.err.println("Error getting occupants: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Adds a person to a specific location in the database using their respective IDs.
     *
     * @param personId   The ID of the person to be added to the location.
     * @param locationId The ID of the location to which the person will be added.
     * @throws SQLException if there is an error updating the database.
     */
    public void addPersonToLocation(int personId, int locationId) throws SQLException {
        try {
            databaseManager.addPersonToLocation(personId, locationId);
        } catch (SQLException e) {
            System.err.println("Error adding person to location: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Removes a person from a specific location in the database using their respective IDs.
     *
     * @param personId   The ID of the person to be removed from the location.
     * @param locationId The ID of the location from which the person will be removed.
     * @throws SQLException if there is an error updating the database.
     */
    public void removePersonFromLocation(int personId, int locationId) throws SQLException {
        try {
            databaseManager.removePersonFromLocation(personId, locationId);
        } catch (SQLException e) {
            System.err.println("Error removing person from location: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves a list of supplies currently allocated to a specific location.
     *
     * @param locationId The ID of the location whose supplies are to be retrieved.
     * @return An ArrayList of Supply objects allocated to the specified location.
     * @throws SQLException if there is an error retrieving the supplies from the database.
     */
    public ArrayList<Supply> getSuppliesAtLocation(int locationId) throws SQLException {
        try {
            return (ArrayList<Supply>) databaseManager.getSuppliesAllocatedTo(null, locationId);
        } catch (SQLException e) {
            System.err.println("Error getting supplies at location: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Refreshes the list of locations by reloading them from the database.
     * This method clears the current list of location models and repopulates it with the latest data.
     *
     * @throws SQLException if there is an error accessing the database during the refresh operation.
     */
    public void refreshLocations() throws SQLException {
        populateLocationsFromDatabase();
    }


    public Location getLocationById(int locationId) throws SQLException, IllegalArgumentException {
        // Validate input
        if (locationId <= 0) {
            throw new IllegalArgumentException("Location ID must be positive");
        }

        // First check local models
        for (Location location : locationModels) {
            if (location.getLocationId() == locationId) {
                return location;
            }
        }

        return null;
    }


    /**
     * Allocates a supply to a person if they are currently at this location
     *
     * @param supplyId The ID of the supply to allocate
     * @param personId The ID of the person to allocate to
     * @throws SQLException If there's a database error
     * @throws IllegalArgumentException If the person is not at this location or if the supply is already allocated
     */
    public void allocateSupplyToPersonAtLocation(int supplyId, int personId, int locationId)
            throws SQLException, IllegalArgumentException {

        // Verify the person is at this location
        ArrayList<Person> occupants = getOccupantsAtLocation(locationId);
        boolean personFound = false;

        for (Person person : occupants) {
            if (person.getPersonId() == personId) {
                personFound = true;
                break;
            }
        }

        if (!personFound) {
            throw new IllegalArgumentException("Person with ID " + personId +
                    " is not currently at location " + locationId);
        }

        // Check if supply is already allocated
        if (databaseManager.isSupplyAllocatedToPerson(supplyId)) {
            throw new IllegalArgumentException("Supply with ID " + supplyId + " is already allocated");
        }

        // Get the supply to verify it exists
        Supply supplyToAllocate = null;
        for (Supply supply : databaseManager.getAllSupplies()) {
            if (supply.getSupplyId() == supplyId) {
                supplyToAllocate = supply;
                break;
            }
        }

        if (supplyToAllocate == null) {
            throw new IllegalArgumentException("No supply found with ID: " + supplyId);
        }

        // Special handling for Water - needs allocation date
        if (supplyToAllocate instanceof Water) {
            // Beta
        }

        // Allocate the supply
        databaseManager.allocateSupply(supplyId, personId, null);

        // If allocated to a DisasterVictim, add to their inventory
        Person person = databaseManager.getPersonById(personId);
        if (person instanceof DisasterVictim) {
            ((DisasterVictim)person).addItem(supplyToAllocate);
        }


        refreshLocations();
    }





    /**
     * Initializes the location ID counter by retrieving the highest existing location ID from the database.
     *
     * @throws SQLException if there is an error accessing the database to retrieve the maximum location ID.
     */
    private void initializeIdCounter() throws SQLException {
        int maxId = databaseManager.getLargestLocationId();
        locationIdCounter = maxId + 1;
    }

    /**
     * Calculates the location id by incrementing the locationIdCounter by 1
     *
     * @return locationId
     */
    public int generateLocationId() {
        return locationIdCounter++;
    }
}