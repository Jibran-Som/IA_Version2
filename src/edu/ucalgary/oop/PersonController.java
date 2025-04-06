package edu.ucalgary.oop;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PersonController {
    private ArrayList<Person> personModels;
    private DatabaseManager databaseManager;

    public PersonController() {
        try {
            this.databaseManager = DatabaseManager.getInstance();
            this.personModels = new ArrayList<>();
            populatePeopleFromDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize PersonController", e);
        }
    }

    private void populatePeopleFromDatabase() throws SQLException {
        try {
            ArrayList<Person> people = (ArrayList<Person>) databaseManager.getAllPeople();
            this.personModels.clear();
            this.personModels.addAll(people);
        } catch (SQLException e) {
            System.err.println("Error loading people from database: " + e.getMessage());
            throw e;
        }
    }

    public List<Person> getAllPeople() {
        return new ArrayList<>(this.personModels);
    }

    public void addPerson(Person person) throws SQLException {
        databaseManager.addPerson(person);
        this.personModels.add(person);
    }

    public void updatePerson(Person person) throws SQLException {
        databaseManager.updatePerson(person);
        // Find and replace the person in the local list
        for (int i = 0; i < personModels.size(); i++) {
            if (personModels.get(i).getPersonId() == person.getPersonId()) {
                personModels.set(i, person);
                break;
            }
        }
    }

    public void deletePerson(int personId) throws SQLException {
        databaseManager.deletePerson(personId);
        // Remove the person from the local list
        personModels.removeIf(p -> p.getPersonId() == personId);
    }

    public Person getPersonById(int personId) throws SQLException {
        // First check local list
        for (Person person : personModels) {
            if (person.getPersonId() == personId) {
                return person;
            }
        }
        // If not found, try to get from database
        return databaseManager.getPersonById(personId);
    }

    public void refresh() throws SQLException {
        populatePeopleFromDatabase();
    }
}