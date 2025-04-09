package edu.ucalgary.oop;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordController {
    private ArrayList<MedicalRecord> medicalRecordModels;
    private DatabaseManager databaseManager;
    private static int medicalRecordIdCounter;


    // Constructor
    public MedicalRecordController() {
        try {
            this.databaseManager = DatabaseManager.getInstance();
            this.medicalRecordModels = new ArrayList<>();
            populateMedicalRecordsFromDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize MedicalRecordController", e);
        }
    }

    // Constructor with dependency injection for testing
    public MedicalRecordController(DatabaseManager databaseManager) throws SQLException {
        if (databaseManager == null) {
            throw new IllegalArgumentException("DatabaseManager cannot be null");
        }
        this.databaseManager = databaseManager;
        this.medicalRecordModels = new ArrayList<>();
        populateMedicalRecordsFromDatabase();
    }

    // Populate models from database
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

    // Get all medical records
    public ArrayList<MedicalRecord> getAllMedicalRecords() {
        return new ArrayList<>(this.medicalRecordModels);
    }

    // Add new medical record
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

    // Update existing medical record
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

    // Delete medical record
    public void deleteMedicalRecord(int medicalRecordId) throws SQLException {
        try {
            databaseManager.deleteMedicalRecord(medicalRecordId);
            medicalRecordModels.removeIf(r -> r.getMedicalRecordId() == medicalRecordId);
        } catch (SQLException e) {
            System.err.println("Error deleting medical record: " + e.getMessage());
            throw e;
        }
    }

    // Get medical records for a specific person
    public ArrayList<MedicalRecord> getMedicalRecordsForPerson(int personId) throws SQLException {
        try {
            return (ArrayList<MedicalRecord>) databaseManager.getMedicalRecordsForPerson(personId);
        } catch (SQLException e) {
            System.err.println("Error getting medical records for person: " + e.getMessage());
            throw e;
        }
    }

    // Get medical records at a specific location
    public ArrayList<MedicalRecord> getMedicalRecordsAtLocation(int locationId) throws SQLException {
        try {
            return (ArrayList<MedicalRecord>) databaseManager.getMedicalRecordsAtLocation(locationId);
        } catch (SQLException e) {
            System.err.println("Error getting medical records at location: " + e.getMessage());
            throw e;
        }
    }




    // Refresh records from database
    public void refreshMedicalRecords() throws SQLException {
        populateMedicalRecordsFromDatabase();
    }



    private void initializeIdCounter() throws SQLException {
        int maxId = databaseManager.getLargestMedicalRecordId();
        medicalRecordIdCounter = maxId + 1;
    }

    public int generateMedicalRecordId() {
        return medicalRecordIdCounter++;
    }
}