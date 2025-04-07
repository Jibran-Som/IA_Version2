package edu.ucalgary.oop;

import java.util.ArrayList;
import java.sql.SQLException;

public class LocationController {
    private ArrayList<Location> locationModels;
    private DatabaseManager databaseManager;

    // Constructor
    public LocationController() {
        try {
            this.databaseManager = DatabaseManager.getInstance();
            this.locationModels = new ArrayList<>();
            populateLocationsFromDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize LocationController", e);
        }
    }

    // Constructor with dependency injection for testing purposes
    public LocationController(DatabaseManager databaseManager) throws SQLException {
        if (databaseManager == null) {
            throw new IllegalArgumentException("DatabaseManager cannot be null");
        }
        this.databaseManager = databaseManager;
        this.locationModels = new ArrayList<>();
        populateLocationsFromDatabase();
    }

    // Populate locationModels from database
    private void populateLocationsFromDatabase() throws SQLException {
        try {
            ArrayList<Location> locations = (ArrayList<Location>) databaseManager.getAllLocations();
            this.locationModels.clear();
            this.locationModels.addAll(locations);
        } catch (SQLException e) {
            System.err.println("Error loading locations from database: " + e.getMessage());
            throw e;
        }
    }

    // Getter
    public ArrayList<Location> getAllLocations() {
        return new ArrayList<>(this.locationModels); // Return copy to maintain encapsulation
    }

    // Add new location
    public void addLocation(Location location) throws SQLException {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }

        try {
            databaseManager.addLocation(location);
            this.locationModels.add(location); // Add to local model
        } catch (SQLException e) {
            System.err.println("Error adding location: " + e.getMessage());
            throw e;
        }
    }

    // Update existing location
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

    // Delete location
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

    // Get occupants at a specific location
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

    // Add person to location
    public void addPersonToLocation(int personId, int locationId) throws SQLException {
        try {
            databaseManager.addPersonToLocation(personId, locationId);
        } catch (SQLException e) {
            System.err.println("Error adding person to location: " + e.getMessage());
            throw e;
        }
    }

    // Remove person from location
    public void removePersonFromLocation(int personId, int locationId) throws SQLException {
        try {
            databaseManager.removePersonFromLocation(personId, locationId);
        } catch (SQLException e) {
            System.err.println("Error removing person from location: " + e.getMessage());
            throw e;
        }
    }

    // Get supplies allocated to a location
    public ArrayList<Supply> getSuppliesAtLocation(int locationId) throws SQLException {
        try {
            return (ArrayList<Supply>) databaseManager.getSuppliesAllocatedTo(null, locationId);
        } catch (SQLException e) {
            System.err.println("Error getting supplies at location: " + e.getMessage());
            throw e;
        }
    }

    // Refresh locations from database
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

}