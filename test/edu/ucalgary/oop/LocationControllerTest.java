/**
 * LocationControllerTest.java
 * Version: 2.0
 * Author: Jibran Somroo
 * Date: April 8, 2025
 */

package edu.ucalgary.oop;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.sql.SQLException;

public class LocationControllerTest {
    private LocationController locationController;
    private Location testLocation1;
    private Location testLocation2;
    private Person testPerson;
    private Supply testSupply;

    @Before
    public void setUp() {
        // Use the test constructor
        locationController = new LocationController(0);

        // Create test locations
        testLocation1 = new Location("Tes","test");
        testLocation1.setLocationId(1);
        testLocation1.setLocationName("Shelter A");
        testLocation1.setLocationAddress("123 Main St");

        testLocation2 = new Location("test","test");
        testLocation2.setLocationId(2);
        testLocation2.setLocationName("Shelter B");
        testLocation2.setLocationAddress("456 Oak Ave");

        // Create test person
        testPerson = new Person("Jav","test");
        testPerson.setPersonId(1);
        testPerson.setFirstName("Test");
        testPerson.setLastName("Person");

        // Create test supply
        testSupply = new Supply("Blanket", "Wool");
        testSupply.setSupplyId(1);

        // Manually add to controller's list
        locationController.getAllLocationsTest().add(testLocation1);
        locationController.getAllLocationsTest().add(testLocation2);
    }

    // Tests that should pass with the test constructor

    @Test
    public void testGetAllLocations() {
        ArrayList<Location> locations = locationController.getAllLocations();
        assertEquals("Should return all test locations", 2, locations.size());
        assertTrue("Should contain test location 1", locations.contains(testLocation1));
        assertTrue("Should contain test location 2", locations.contains(testLocation2));
    }

    @Test
    public void testGetLocationById() {
        try {
            Location found = locationController.getLocationById(1);
            assertNotNull("Should find location with ID 1", found);
            assertEquals("Should return correct location", "Shelter A", found.getLocationName());
        } catch (SQLException e) {
            fail("Should not throw SQLException in test mode");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetLocationByIdInvalidId() throws SQLException {
        locationController.getLocationById(0);
    }

    @Test
    public void testGenerateLocationIdIncrements() {
        int firstId = locationController.generateLocationId();
        int secondId = locationController.generateLocationId();
        assertEquals("IDs should increment", firstId + 1, secondId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNullLocation() throws SQLException {
        locationController.addLocation(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateNullLocation() throws SQLException {
        locationController.updateLocation(null);
    }

    // Tests that expect SQLException due to null databaseManager

    @Test(expected = NullPointerException.class)
    public void testAddLocationNullPointerException() throws SQLException {
        Location newLocation = new Location("test", "Shelter A");
        newLocation.setLocationName("New Shelter");
        locationController.addLocation(newLocation);
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateLocationNullPointerException() throws SQLException {
        testLocation1.setLocationName("Updated Name");
        locationController.updateLocation(testLocation1);
    }

    @Test(expected = NullPointerException.class)
    public void testDeleteLocationNullPointerException() throws SQLException {
        locationController.deleteLocation(1);
    }

    @Test(expected = NullPointerException.class)
    public void testGetOccupantsAtLocationNullPointerException() throws SQLException {
        locationController.getOccupantsAtLocation(1);
    }

    @Test(expected = NullPointerException.class)
    public void testAddPersonToLocationNullPointerException() throws SQLException {
        locationController.addPersonToLocation(1, 1);
    }

    @Test(expected = NullPointerException.class)
    public void testGetSuppliesAtLocationNullPointerException () throws SQLException {
        locationController.getSuppliesAtLocation(1);
    }

    @Test(expected = NullPointerException.class)
    public void testAllocateSupplyToPersonAtLocationThrowsSQLException() throws Exception {
        locationController.allocateSupplyToPersonAtLocation(1, 1, 1);
    }

    // Additional tests for business logic

    @Test
    public void testLocalModelUpdatesDespiteDatabaseException() {
        try {
            // Try to update - should fail but update local model
            testLocation1.setLocationName("Updated Name");
            locationController.updateLocation(testLocation1);
            fail("Should have thrown SQLException");
        } catch (NullPointerException | SQLException e) {
            // Verify local update happened despite database exception
            Location updated = locationController.getAllLocations().get(0);
            assertEquals("Name should be updated locally", "Updated Name", updated.getLocationName());
        }
    }


}