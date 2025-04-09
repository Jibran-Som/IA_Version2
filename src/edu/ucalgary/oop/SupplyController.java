package edu.ucalgary.oop;

import java.util.ArrayList;
import java.sql.SQLException;
import java.util.Date;
import java.time.LocalDate;


public class SupplyController {
    private ArrayList<Supply> supplyModels;
    private static DatabaseManager databaseManager;
    private static int supplyIdCounter;


    // Constructor
    public SupplyController() {
        try {
            this.databaseManager = DatabaseManager.getInstance();
            this.supplyModels = new ArrayList<>();
            populateSuppliesFromDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize SupplyController", e);
        }
    }

    // Constructor with dependency injection for testing purposes
    public SupplyController(DatabaseManager databaseManager) throws SQLException {
        if (databaseManager == null) {
            throw new IllegalArgumentException("DatabaseManager cannot be null");
        }
        this.databaseManager = databaseManager;
        this.supplyModels = new ArrayList<>();
        populateSuppliesFromDatabase();
    }




    // Populate supplyModels from database
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

    // Getter
    public ArrayList<Supply> getAllSupplies() {
        return new ArrayList<>(this.supplyModels); // Return copy to maintain encapsulation
    }

    // Add new supply
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


    // Update existing supply
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



    // Delete supply
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





    // Get supplies allocated to a specific person or location
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



    // Refresh supplies from database
    public void refreshSupplies() throws SQLException {
        populateSuppliesFromDatabase();
    }


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


    public boolean isSupplyAllocated(int supplyId) throws SQLException {
        if (databaseManager.isSupplyAllocated(supplyId)) return true;
        else return false;
    }



    /**
     * Gets the largest supply ID currently in use in the database
     *
     * @return The largest supply ID
     * @throws SQLException If there's a database error
     */
    public static int getLargestSupplyId() throws SQLException {
        try {
            return databaseManager.getLargestSupplyId();
        } catch (SQLException e) {
            System.err.println("Error getting largest supply ID: " + e.getMessage());
            throw e;
        }
    }



    /**
     * Checks if a supply ID exists in the database
     *
     * @param supplyId The ID to check
     * @return true if the ID exists, false otherwise
     * @throws SQLException If there's a database error
     */
    public boolean supplyIdExists(int supplyId) throws SQLException {
        try {
            for (Supply supply : supplyModels) {
                if (supply.getSupplyId() == supplyId) {
                    return true;
                }
            }
            return databaseManager.getLargestSupplyId() >= supplyId;
        } catch (SQLException e) {
            System.err.println("Error checking supply ID existence: " + e.getMessage());
            throw e;
        }
    }


    private void initializeIdCounter() throws SQLException {
        int maxId = databaseManager.getLargestSupplyId();
        supplyIdCounter = maxId + 1;
    }

    public int generateSupplyId() {
        return supplyIdCounter++;
    }


}