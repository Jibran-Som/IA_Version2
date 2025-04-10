/**
 * MedicalRecord.java
 * Version: 4.0
 * Author: Jibran Somroo
 * Date: April 9, 2025
 */

package edu.ucalgary.oop;

import java.sql.SQLException;
import java.util.ArrayList;

public class MedicalRecordController {
    private ArrayList<MedicalRecord> medicalRecordModels;
    private DatabaseManager databaseManager;
    private static int medicalRecordIdCounter;


    /**
     * Initializes the MedicalRecordController, setting up the database manager
     *
     * @throws RuntimeException if there is an error initializing the controller or populating the medical records.
     */
    public MedicalRecordController() {
        try {
            this.databaseManager = DatabaseManager.getInstance();
            this.medicalRecordModels = new ArrayList<>();
            populateMedicalRecordsFromDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize MedicalRecordController", e);
        }
    }


    /**
     * Initializes the MedicalRecordController, meant for MedicalRecordControllerTest
     *
     */
    public MedicalRecordController(int test) {
            this.medicalRecordModels = new ArrayList<>();

    }

    /**
     * Populates the medical record models by loading all medical records from the database.
     *
     * @throws SQLException if an error occurs while fetching the medical records from the database.
     */
    private void populateMedicalRecordsFromDatabase() throws SQLException {
        try {
            ArrayList<MedicalRecord> records = (ArrayList<MedicalRecord>) databaseManager.getAllMedicalRecords();
            this.medicalRecordModels.clear();
            this.medicalRecordModels.addAll(records);
            initializeIdCounter();
        } catch (SQLException e) {
            System.err.println("Error loading medical records from database: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves a copy of the list of all medical records currently in the system.
     *
     * @return A new ArrayList containing all the medical records.
     */
    public ArrayList<MedicalRecord> getAllMedicalRecords() {
        return new ArrayList<>(this.medicalRecordModels);
    }

    /**
     * Retrieves a list of all medical records currently in the system.
     *
     * @return A ArrayList containing all the medical records.
     */
    public ArrayList<MedicalRecord> getAllMedicalRecordsTest() {
        return this.medicalRecordModels;
    }

    /**
     * Adds a new medical record to the database.
     *
     * @param record The medical record to be added.
     * @throws IllegalArgumentException if the provided medical record is null.
     * @throws SQLException if there is an error when interacting with the database while adding the medical record.
     */
    public void addMedicalRecord(MedicalRecord record) throws SQLException {
        if (record == null) {
            throw new IllegalArgumentException("Medical record cannot be null");
        }

        if(record.getMedicalRecordId() <= 0) {
            record.setMedicalRecordId(generateMedicalRecordId());
        }

        try {
            databaseManager.addMedicalRecord(record);
            this.medicalRecordModels.add(record);
        } catch (SQLException e) {
            System.err.println("Error adding medical record: " + e.getMessage());
            medicalRecordIdCounter--;
            throw e;
        }
    }

    /**
     * Updates an existing medical record in the system.
     *
     * @param record The medical record to be updated.
     * @throws IllegalArgumentException if the provided medical record is null.
     * @throws SQLException if there is an error when interacting with the database while updating the medical record.
     */
    public void updateMedicalRecord(MedicalRecord record) throws SQLException {
        if (record == null) {
            throw new IllegalArgumentException("Medical record cannot be null");
        }

        try {
            databaseManager.updateMedicalRecord(record);
            // Find and update in local models
            for (int i = 0; i < medicalRecordModels.size(); i++) {
                if (medicalRecordModels.get(i).getMedicalRecordId() == record.getMedicalRecordId()) {
                    medicalRecordModels.set(i, record);
                    break;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating medical record: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves all medical records associated with a specific person.
     *
     * @param personId The ID of the person for whom the medical records are being retrieved.
     * @return A list of medical records associated with the person.
     * @throws SQLException if there is an error while interacting with the database.
     */
    public ArrayList<MedicalRecord> getMedicalRecordsForPerson(int personId) throws SQLException {
        try {
            return (ArrayList<MedicalRecord>) databaseManager.getMedicalRecordsForPerson(personId);
        } catch (SQLException e) {
            System.err.println("Error getting medical records for person: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves all medical records associated with a specific location.
     *
     * @param locationId The ID of the location for which the medical records are being retrieved.
     * @return A list of medical records associated with the location.
     * @throws SQLException if there is an error while interacting with the database.
     */
    public ArrayList<MedicalRecord> getMedicalRecordsAtLocation(int locationId) throws SQLException {
        try {
            return (ArrayList<MedicalRecord>) databaseManager.getMedicalRecordsAtLocation(locationId);
        } catch (SQLException e) {
            System.err.println("Error getting medical records at location: " + e.getMessage());
            throw e;
        }
    }




    /**
     * Refreshes the list of medical records by reloading data from the database.
     * Note: Mainly used during the early development if this code.
     *
     * @throws SQLException if there is an error while fetching the medical records from the database.
     */
    public void refreshMedicalRecords() throws SQLException {
        populateMedicalRecordsFromDatabase();
    }



    /**
     * Initializes the medical record ID counter by retrieving the largest existing medical record ID from the database.
     *
     * @throws SQLException if there is an error retrieving the largest medical record ID from the database.
     */
    private void initializeIdCounter() throws SQLException {
        int maxId = databaseManager.getLargestMedicalRecordId();
        medicalRecordIdCounter = maxId + 1;
    }

    /**
     * Generates a new medical record ID by adding 1 to medicalRecordIdCounter
     *
     * @return The next available medical record ID.
     */
    public int generateMedicalRecordId() {
        return medicalRecordIdCounter++;
    }
}