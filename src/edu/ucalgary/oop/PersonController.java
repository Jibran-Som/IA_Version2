package edu.ucalgary.oop;

import java.util.ArrayList;
import java.sql.SQLException;

public class PersonController {
    private ArrayList<Person> personModels;
    private DatabaseManager databaseManager;

    // Constructor
    public PersonController() {
        try {
            this.databaseManager = DatabaseManager.getInstance();
            this.personModels = new ArrayList<>();
            populatePersonsFromDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize PersonController", e);
        }
    }

    // Constructor with dependency injection for testing purposes
    public PersonController(DatabaseManager databaseManager) throws SQLException {
        if (databaseManager == null) {
            throw new IllegalArgumentException("DatabaseManager cannot be null");
        }
        this.databaseManager = databaseManager;
        this.personModels = new ArrayList<>();
        populatePersonsFromDatabase();
    }

    // Populate personModels from database
    private void populatePersonsFromDatabase() throws SQLException {
        try {
            ArrayList<Person> persons = (ArrayList<Person>) databaseManager.getAllPersons();
            this.personModels.clear();
            this.personModels.addAll(persons);
        } catch (SQLException e) {
            System.err.println("Error loading persons from database: " + e.getMessage());
            throw e;
        }
    }

    // Getter for all persons
    public ArrayList<Person> getAllPersons() {
        return new ArrayList<>(this.personModels); // Return copy to maintain encapsulation
    }

    // Getter for disaster victims only
    public ArrayList<DisasterVictim> getDisasterVictims() throws SQLException {
        try {
            return (ArrayList<DisasterVictim>) databaseManager.getDisasterVictims();
        } catch (SQLException e) {
            System.err.println("Error getting disaster victims: " + e.getMessage());
            throw e;
        }
    }

    // Add new person
    public void addPerson(Person person) throws SQLException {
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }

        try {
            int personId = databaseManager.addPerson(person);
            person.setPersonId(personId);
            this.personModels.add(person); // Add to local model
        } catch (SQLException e) {
            System.err.println("Error adding person: " + e.getMessage());
            throw e;
        }
    }

    // Update existing person
    public void updatePerson(Person person) throws SQLException {
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }

        try {
            databaseManager.updatePerson(person);
            // Find and update the person in local models
            for (int i = 0; i < personModels.size(); i++) {
                if (personModels.get(i).getPersonId() == person.getPersonId()) {
                    personModels.set(i, person);
                    break;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating person: " + e.getMessage());
            throw e;
        }
    }

    // Delete person
    public void deletePerson(int personId) throws SQLException {
        try {
            databaseManager.deletePerson(personId);
            // Remove from local models
            personModels.removeIf(p -> p.getPersonId() == personId);
        } catch (SQLException e) {
            System.err.println("Error deleting person: " + e.getMessage());
            throw e;
        }
    }

    // Get person by ID
    public Person getPersonById(int personId) throws SQLException {
        try {
            return databaseManager.getPersonById(personId);
        } catch (SQLException e) {
            System.err.println("Error getting person by ID: " + e.getMessage());
            throw e;
        }
    }

    // Add medical record for a person
    public void addMedicalRecord(int personId, MedicalRecord record) throws SQLException {
        if (record == null) {
            throw new IllegalArgumentException("MedicalRecord cannot be null");
        }

        try {
            databaseManager.addMedicalRecord(personId, record);
            // Update the local model if needed
            Person person = getPersonById(personId);
            if (person != null) {
                person.addMedicalRecord(record);
            }
        } catch (SQLException e) {
            System.err.println("Error adding medical record: " + e.getMessage());
            throw e;
        }
    }

    // Get medical records for a person
    public ArrayList<MedicalRecord> getMedicalRecords(int personId) throws SQLException {
        try {
            return (ArrayList<MedicalRecord>) databaseManager.getMedicalRecords(personId);
        } catch (SQLException e) {
            System.err.println("Error getting medical records: " + e.getMessage());
            throw e;
        }
    }

    // Refresh persons from database
    public void refreshPersons() throws SQLException {
        populatePersonsFromDatabase();
    }
}