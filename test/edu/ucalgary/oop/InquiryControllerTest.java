/**
 * InquiryControllerTest.java
 * Version: 2.0
 * Author: Jibran Somroo
 * Date: April 7, 2025
 */

package edu.ucalgary.oop;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.sql.SQLException;

public class InquiryControllerTest {
    private InquiryController inquiryController;
    private Inquiry testInquiry1;
    private Inquiry testInquiry2;
    private Person testInquirer;
    private Person testMissingPerson;
    private Location testLocation;

    @Before
    public void setUp() {
        // Use the test constructor
        inquiryController = new InquiryController(0);

        // Create test persons and location
        testInquirer = new Person("John", "Doe");
        testInquirer.setPersonId(1);


        testMissingPerson = new DisasterVictim("Jane", "Doe");
        testMissingPerson.setPersonId(2);


        testLocation = new Location("Somewhere", "Somewhere Else");
        testLocation.setLocationId(1);

        // Create test inquiries
        testInquiry1 = new Inquiry(testInquirer, (DisasterVictim) testMissingPerson, "2024-05-05", "Lost", testLocation);
        testInquiry1.setInquiryId(1);
        testInquiry1.setInquirer(testInquirer);
        testInquiry1.setMissingPerson((DisasterVictim) testMissingPerson);
        testInquiry1.setLastKnownLocation(testLocation);
        testInquiry1.setInfoProvided("First inquiry");

        testInquiry2 = new Inquiry(testInquirer, (DisasterVictim) testMissingPerson, "2025-01-01", "Second inquiry", testLocation);
        testInquiry2.setInquiryId(2);

        // Manually add to controller's list
        inquiryController.getAllInquiriesTest().add(testInquiry1);
        inquiryController.getAllInquiriesTest().add(testInquiry2);
    }


    @Test
    public void testGetAllInquiries() {
        ArrayList<Inquiry> inquiries = inquiryController.getAllInquiries();
        assertEquals("Should return all test inquiries", 2, inquiries.size());
        assertTrue("Should contain test inquiry 1", inquiries.contains(testInquiry1));
        assertTrue("Should contain test inquiry 2", inquiries.contains(testInquiry2));
    }

    @Test
    public void testGetInquiryById() {
        try {
            Inquiry found = inquiryController.getInquiryById(1);
            assertNotNull("Should find inquiry with ID 1", found);
            assertEquals("Should return correct inquiry", "First inquiry", found.getInfoProvided());
        } catch (SQLException e) {
            fail("Should not throw SQLException in test mode");
        }
    }

    @Test
    public void testGetInquiriesByInquirer() {
        ArrayList<Inquiry> results = inquiryController.getInquiriesByInquirer(1);
        assertEquals("Should find 2 inquiries for inquirer", 2, results.size());
        assertTrue("Should contain both test inquiries",
                results.contains(testInquiry1) && results.contains(testInquiry2));
    }

    @Test
    public void testGetInquiriesByMissingPerson() {
        ArrayList<Inquiry> results = inquiryController.getInquiriesByMissingPerson(2);
        assertEquals("Should find 2 inquiries for missing person", 2, results.size());
        assertTrue("Should contain both test inquiries",
                results.contains(testInquiry1) && results.contains(testInquiry2));
    }

    @Test
    public void testGenerateInquiryIdIncrements() {
        int firstId = inquiryController.generateInquiryId();
        int secondId = inquiryController.generateInquiryId();
        assertEquals("IDs should increment", firstId + 1, secondId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNullInquiry() throws SQLException {
        inquiryController.addInquiry(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateNullInquiry() throws SQLException {
        inquiryController.updateInquiry(null);
    }

    // Tests that expect SQLException due to null databaseManager

    @Test(expected = NullPointerException.class)
    public void testAddInquiryNullPointerException() throws SQLException {
        Inquiry newInquiry = new Inquiry(testInquirer, (DisasterVictim) testMissingPerson, "2023-02-02", "Bleeding", testLocation);
        newInquiry.setInfoProvided("New inquiry");
        inquiryController.addInquiry(newInquiry);
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateInquiryNullPointerException() throws SQLException {
        testInquiry1.setInfoProvided("Updated inquiry");
        inquiryController.updateInquiry(testInquiry1);
    }

    @Test(expected = NullPointerException.class)
    public void testDeleteInquiryNullPointerException() throws SQLException {
        inquiryController.deleteInquiry(1);
    }

    @Test(expected = NullPointerException.class)
    public void testRefreshInquiriesNullPointerException() throws SQLException {
        inquiryController.refreshInquiries();
    }



    @Test
    public void testLocalModelUpdatesDespiteDatabaseException() {
        try {
            // Try to update - should fail but update local model
            testInquiry1.setInfoProvided("Updated inquiry");
            inquiryController.updateInquiry(testInquiry1);
            fail("Should have thrown SQLException");
        } catch (NullPointerException | SQLException e) {
            // Verify local update happened despite database exception
            Inquiry updated = inquiryController.getAllInquiries().get(0);
            assertEquals("Comments should be updated locally",
                    "Updated inquiry", updated.getInfoProvided());
        }
    }

    @Test
    public void testGetAllInquiriesReturnsCopy() {
        ArrayList<Inquiry> inquiries = inquiryController.getAllInquiries();
        inquiries.clear();
        assertEquals("Original list should remain unchanged",
                2, inquiryController.getAllInquiries().size());
    }
}