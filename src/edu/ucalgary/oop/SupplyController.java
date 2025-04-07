package edu.ucalgary.oop;

import java.util.ArrayList;
import java.sql.SQLException;

public class SupplyController {
    private ArrayList<Supply> supplyModels;
    private DatabaseManager databaseManager;

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

        try {
            databaseManager.addSupply(supply);
            this.supplyModels.add(supply); // Add to local model
        } catch (SQLException e) {
            System.err.println("Error adding supply: " + e.getMessage());
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


    public void allocateSupply(int supplyId, Integer personId, Integer locationId)
            throws SQLException, IllegalArgumentException {

        // Validate that we're allocating to either a person or location, but not both
        if (personId == null && locationId == null) {
            throw new IllegalArgumentException("Must specify either personId or locationId");
        }
        if (personId != null && locationId != null) {
            throw new IllegalArgumentException("Cannot allocate to both person and location simultaneously");
        }

        // Check if supply is already allocated
        if (databaseManager.isSupplyAllocated(supplyId)) {
            throw new IllegalArgumentException("Supply is already allocated and cannot be reallocated");
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

        // Special handling for PersonalBelonging - can't be allocated to locations
        if (supplyToAllocate instanceof PersonalBelonging && locationId != null) {
            throw new IllegalArgumentException("Personal belongings cannot be allocated to locations");
        }

        // Special handling for Water - needs allocation date
        if (supplyToAllocate instanceof Water) {
            Water water = (Water) supplyToAllocate;
            if (water.getAllocationDate() == null || water.getAllocationDate().isEmpty()) {
                throw new IllegalArgumentException("Water allocation requires a valid allocation date");
            }
        }

        try {
            // Allocate in the database
            databaseManager.allocateSupply(supplyId, personId, locationId);

            // Update local models

            refreshSupplies();

        } catch (SQLException e) {
            System.err.println("Error allocating supply: " + e.getMessage());
            throw e;
        }
    }












}