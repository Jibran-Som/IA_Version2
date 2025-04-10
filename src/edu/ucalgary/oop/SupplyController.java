/**
 * SupplyController.java
 * Version: 2.0
 * Author: Jibran Somroo
 * Date: April 8, 2025
 */

package edu.ucalgary.oop;

import java.util.ArrayList;
import java.sql.SQLException;
import java.time.LocalDate;


public class SupplyController {
    private ArrayList<Supply> supplyModels;
    private static DatabaseManager databaseManager;
    private static int supplyIdCounter;


    /**
     * Constructs a new SupplyController
     *
     * @throws RuntimeException if the initialization of the SupplyController fails
     */
    public SupplyController() {
        try {
            this.databaseManager = DatabaseManager.getInstance();
            this.supplyModels = new ArrayList<>();
            populateSuppliesFromDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize SupplyController", e);
        }
    }




    /**
     * Constructs a new SupplyController for SupplyControllerTest
     * Arbitrary constructor, has no real purpose
     */
    public SupplyController(int test) {
        this.supplyModels = new ArrayList<>();
    }



    /**
     * Populates the local list of supplies models from the database.
     *
     * @throws SQLException if an error occurs while fetching the supplies data from the database.
     */
    private void populateSuppliesFromDatabase() throws SQLException {
        try {
            ArrayList<Supply> supplies = (ArrayList<Supply>) databaseManager.getAllSupplies();
            this.supplyModels.clear();
            this.supplyModels.addAll(supplies);
            initializeIdCounter();
        } catch (SQLException e) {
            System.err.println("Error loading supplies from database: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves all supplies stored in the supplyModels list.
     *
     * @return A new ArrayList containing all the supplies.
     */
    public ArrayList<Supply> getAllSupplies() {
        return new ArrayList<>(this.supplyModels);
    }


    /**
     * Retrieves all supplies stored in the supplyModels list for testing purposes
     *
     * @return The ArrayList containing all the supplies.
     */
    public ArrayList<Supply> getAllSuppliesTest() {
        return this.supplyModels;
    }



    /**
     * Adds a new supply to the system by first setting its ID, then adding it to the database
     * and the local list of supplies.
     *
     * @param supply The supply to be added. This can't be null.
     * @throws SQLException If there is an error during the database operation.
     * @throws IllegalArgumentException If the provided supply is null.
     */
    public void addSupply(Supply supply) throws SQLException {
        if (supply == null) {
            throw new IllegalArgumentException("Supply cannot be null");
        }

        // Set the ID before adding to database
        if (supply.getSupplyId() <= 0) { // Assuming 0 or negative means unset
            supply.setSupplyId(generateSupplyId());
        }

        try {
            databaseManager.addSupply(supply);
            this.supplyModels.add(supply);
        } catch (SQLException e) {
            // If add fails, decrement counter to reuse the ID
            supplyIdCounter--;
            throw e;
        }
    }


    /**
     * Updates the details of an existing supply in the system.
     *
     * @param supply The supply object with updated information.
     * @throws SQLException If there is an error during the database operation.
     * @throws IllegalArgumentException If the provided supply is null.
     */
    public void updateSupply(Supply supply) throws SQLException {
        if (supply == null) {
            throw new IllegalArgumentException("Supply cannot be null");
        }

        try {
            databaseManager.updateSupply(supply);
            // Find and update the supply in local models
            for (int i = 0; i < supplyModels.size(); i++) {
                if (supplyModels.get(i).getSupplyId() == supply.getSupplyId()) {
                    supplyModels.set(i, supply);
                    break;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating supply: " + e.getMessage());
            throw e;
        }
    }



    /**
     * Deletes a supply from the system based on its unique ID.
     * Note: Used at the code development stage to remove some 'improper'
     * supplies.
     *
     * @param supplyId The ID of the supply to be deleted. This cannot be zero or negative.
     * @throws SQLException If there is an error during the database operation.
     */
    public void deleteSupply(int supplyId) throws SQLException {
        try {
            databaseManager.deleteSupply(supplyId);
            // Remove from local models
            supplyModels.removeIf(s -> s.getSupplyId() == supplyId);
        } catch (SQLException e) {
            System.err.println("Error deleting supply: " + e.getMessage());
            throw e;
        }
    }





    /**
     * Retrieves the list of supplies that have been allocated either to a person or to a location.
     *
     * @param personId The ID of the person to whom the supplies are allocated.
     * @param locationId The ID of the location where the supplies are allocated.
     * @return A list of supplies allocated to the specified person or location.
     * @throws SQLException If there is an error during the database operation.
     */
    public ArrayList<Supply> getSuppliesAllocatedTo(Integer personId, Integer locationId) throws SQLException {
        if (personId == null && locationId == null) {
            throw new IllegalArgumentException("Must specify either personId or locationId");
        }

        try {
            return (ArrayList<Supply>) databaseManager.getSuppliesAllocatedTo(personId, locationId);
        } catch (SQLException e) {
            System.err.println("Error getting allocated supplies: " + e.getMessage());
            throw e;
        }
    }



    /**
     * Refreshes the list of supplies by reloading them from the database.
     *
     * @throws SQLException If there is an error during the database operation.
     */
    public void refreshSupplies() throws SQLException {
        populateSuppliesFromDatabase();
    }


    /**
     * Allocates a supply to either a person or a location.
     *
     * @param supplyId The ID of the supply to be allocated.
     * @param personId The ID of the person to allocate the supply to. Can be null if allocating to a location.
     * @param locationId The ID of the location to allocate the supply to. Can be null if allocating to a person.
     * @param fromLocationId The ID of the location to move the supply from (if applicable).
     * @throws SQLException If there is an error during database operations.
     * @throws IllegalArgumentException If the allocation parameters are invalid.
     */
    public void allocateSupply(int supplyId, Integer personId, Integer locationId, Integer fromLocationId)
            throws SQLException, IllegalArgumentException {

        // Validate allocation parameters
        if (personId == null && locationId == null) {
            throw new IllegalArgumentException("Must specify either personId or locationId as allocation target");
        }
        if (personId != null && locationId != null) {
            throw new IllegalArgumentException("Cannot allocate to both person and location simultaneously");
        }
        if (fromLocationId != null && locationId != null) {
            throw new IllegalArgumentException("Cannot allocate from and to locations simultaneously");
        }

        // Find the supply in our local models
        Supply supplyToAllocate = null;
        for (Supply supply : supplyModels) {
            if (supply.getSupplyId() == supplyId) {
                supplyToAllocate = supply;
                break;
            }
        }

        if (supplyToAllocate == null) {
            throw new IllegalArgumentException("No supply found with ID: " + supplyId);
        }

        // Special handling for PersonalBelonging
        if (supplyToAllocate instanceof PersonalBelonging && locationId != null) {
            throw new IllegalArgumentException("Personal belongings cannot be allocated to locations");
        }

        // Special handling for Water
        if (supplyToAllocate instanceof Water) {
            Water water = (Water) supplyToAllocate;
            if (water.getAllocationDate() == null || water.getAllocationDate().isEmpty()) {
                String currentDate = LocalDate.now().toString();
                water.setAllocationDate(currentDate);
            }
        }

        try {
            // Check if we're moving from a location
            if (fromLocationId != null) {
                // Verify supply is actually at the source location
                if (!databaseManager.isSupplyAtLocation(supplyId, fromLocationId)) {
                    throw new IllegalArgumentException("Supply is not at the specified source location");
                }

                // Remove from location first
                databaseManager.removeSupplyFromLocation(supplyId, fromLocationId);
            }

            // Perform the allocation
            databaseManager.allocateSupply(supplyId, personId, locationId);

            // Update local models
            refreshSupplies();

        } catch (SQLException e) {
            System.err.println("Error allocating supply: " + e.getMessage());
            throw e;
        }
    }


    /**
     * Checks if a supply is allocated in the database.
     *
     * @param supplyId The ID of the supply to check.
     * @return True if the supply is allocated, otherwise false.
     * @throws SQLException If there is an error during the database query.
     */
    public boolean isSupplyAllocated(int supplyId) throws SQLException {
        if (databaseManager.isSupplyAllocated(supplyId)) return true;
        else return false;
    }


    /**
     * Initializes the supply ID counter by retrieving the largest existing supply ID from the database.
     *
     * @throws SQLException if there is an error retrieving the largest supply ID from the database.
     */
    private void initializeIdCounter() throws SQLException {
        int maxId = databaseManager.getLargestSupplyId();
        supplyIdCounter = maxId + 1;
    }


    /**
     * Generates a new supply ID by adding 1 to supplyIdCounter
     *
     * @return The next available supply ID.
     */
    public int generateSupplyId() {
        return supplyIdCounter++;
    }


}