package edu.ucalgary.oop;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.sql.SQLException;

public class MedicalRecordControllerTest {
    private MedicalRecordController medicalRecordController;
    private MedicalRecord testRecord1;
    private MedicalRecord testRecord2;
    private Person testPerson;
    private Location testLocation;

    @Before
    public void setUp() {
        // Use the test constructor
        medicalRecordController = new MedicalRecordController(0);

        // Create test person and location
        testPerson = new Person("John", "Doe");
        testPerson.setPersonId(1);

        testLocation = new Location("San Francisco", "CA");
        testLocation.setLocationId(1);

        // Create test medical records
        testRecord1 = new MedicalRecord(testPerson, testLocation, "Cold", "2024-02-02");
        testRecord1.setMedicalRecordId(1);
        testRecord1.setPerson(testPerson);
        testRecord1.setLocation(testLocation);
        testRecord1.setTreatmentDetails("Broken arm");

        testRecord2 = new MedicalRecord(testPerson, testLocation, "Cold", "2024-02-03");
        testRecord2.setMedicalRecordId(2);
        testRecord2.setPerson(testPerson);
        testRecord2.setLocation(testLocation);
        testRecord2.setTreatmentDetails("Sprained ankle");

        // Manually add to controller's list
        medicalRecordController.getAllMedicalRecordsTest().add(testRecord1);
        medicalRecordController.getAllMedicalRecordsTest().add(testRecord2);
    }

    // Tests that should pass with test constructor

    @Test
    public void testGetAllMedicalRecords() {
        ArrayList<MedicalRecord> records = medicalRecordController.getAllMedicalRecords();
        assertEquals("Should return all test records", 2, records.size());
        assertTrue("Should contain test record 1", records.contains(testRecord1));
        assertTrue("Should contain test record 2", records.contains(testRecord2));
    }

    @Test
    public void testGenerateMedicalRecordIdIncrements() {
        int firstId = medicalRecordController.generateMedicalRecordId();
        int secondId = medicalRecordController.generateMedicalRecordId();
        assertEquals("IDs should increment", firstId + 1, secondId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNullMedicalRecord() throws SQLException {
        medicalRecordController.addMedicalRecord(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateNullMedicalRecord() throws SQLException {
        medicalRecordController.updateMedicalRecord(null);
    }

    // Tests that expect SQLException due to null databaseManager

    @Test(expected = NullPointerException.class)
    public void testAddMedicalRecordNullPointerException() throws SQLException {
        Person testPerson3 = new Person("Ale", "Doe");
        testPerson3.setPersonId(3);

        Location testLocation3 = new Location("New", "CA");
        testLocation3.setLocationId(3);

        MedicalRecord newRecord = new MedicalRecord(testPerson3, testLocation3, "Fever", "2024-05-03");
        newRecord.setTreatmentDetails("New treatment");
        medicalRecordController.addMedicalRecord(newRecord);
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateMedicalRecordNullPointerException() throws SQLException {
        testRecord1.setTreatmentDetails("Updated treatment");
        medicalRecordController.updateMedicalRecord(testRecord1);
    }

    @Test(expected = NullPointerException.class)
    public void testGetMedicalRecordsForPersonNullPointerException() throws SQLException {
        medicalRecordController.getMedicalRecordsForPerson(1);
    }

    @Test(expected = NullPointerException.class)
    public void testGetMedicalRecordsAtLocationNullPointerException() throws SQLException {
        medicalRecordController.getMedicalRecordsAtLocation(1);
    }

    @Test(expected = NullPointerException.class)
    public void testRefreshMedicalRecordsNullPointerException() throws SQLException {
        medicalRecordController.refreshMedicalRecords();
    }


    // Additional tests for business logic



    @Test
    public void testLocalModelUpdatesDespiteDatabaseException() {
        try {
            // Try to update - should fail but update local model
            testRecord1.setTreatmentDetails("Updated treatment");
            medicalRecordController.updateMedicalRecord(testRecord1);
            fail("Should have thrown SQLException");
        } catch (NullPointerException | SQLException e) {
            // Verify local update happened despite database exception
            MedicalRecord updated = medicalRecordController.getAllMedicalRecords().get(0);
            assertEquals("Treatment should be updated locally",
                    "Updated treatment", updated.getTreatmentDetails());
        }
    }

    @Test
    public void testGetAllMedicalRecordsReturnsCopy() {
        ArrayList<MedicalRecord> records = medicalRecordController.getAllMedicalRecords();
        records.clear();
        assertEquals("Original list should remain unchanged",
                2, medicalRecordController.getAllMedicalRecords().size());
    }
}