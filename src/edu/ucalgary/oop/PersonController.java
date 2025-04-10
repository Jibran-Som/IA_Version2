/**
 * PersonController.java
 * Version: 4.0
 * Author: Jibran Somroo
 * Date: April 10, 2025
 */

package edu.ucalgary.oop;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonController {
    private ArrayList<Person> personModels;
    private DatabaseManager databaseManager;
    private static int personIdCounter;


    /**
     * Constructs a new PersonController
     *
     * @throws RuntimeException if the initialization of the PersonController fails
     */
    public PersonController() {
        try {
            this.databaseManager = DatabaseManager.getInstance();
            this.personModels = new ArrayList<>();
            populatePeopleFromDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize PersonController", e);
        }
    }

    /**
     * Constructs a new PersonController for PersonControllerTest
     */
    public PersonController(int test) {
        this.personModels = new ArrayList<>();

    }


    /**
     * Populates the local list of people models from the database.
     *
     * @throws SQLException if an error occurs while fetching the people data from the database.
     */
    private void populatePeopleFromDatabase() throws SQLException {
        try {
            ArrayList<Person> people = (ArrayList<Person>) databaseManager.getAllPeople();
            this.personModels.clear();
            this.personModels.addAll(people);
            initializeIdCounter();
        } catch (SQLException e) {
            System.err.println("Error loading people from database: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves a copy of the list of all people
     *
     * @return A new ArrayList containing all people.
     */
    public ArrayList<Person> getAllPeople() {
        return new ArrayList<>(this.personModels);
    }


    /**
     * Retrieves a copy of the list of all people for test purposes
     *
     * @return A ArrayList containing all people.
     */
    public ArrayList<Person> getAllPeopleTest() {
        return this.personModels;
    }



    public void addPerson(Person person) throws SQLException {
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }

        // Set the ID before adding to database
        if (person.getPersonId() <= 0) { // Assuming 0 or negative means unset
            person.setPersonId(generatePersonId());
        }

        try {
            databaseManager.addPerson(person);
            this.personModels.add(person);
        } catch (SQLException e) {
            personIdCounter--;
            throw e;
        }
    }

    /**
     * Updates the information of a person in both the database and the local model.
     *
     * @param person The person object containing the updated details.
     * @throws SQLException If there is an error while updating the person in the database.
     */
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


    /**
     * Deletes a person from both the database and the local model.
     *
     * @param personId The ID of the person to be deleted.
     * @throws SQLException If there is an error while deleting the person from the database.
     */
    public void deletePerson(int personId) throws SQLException {
        databaseManager.deletePerson(personId);
        personModels.removeIf(p -> p.getPersonId() == personId);
    }

    /**
     * Retrieves a person by their ID.
     *
     * @param personId The ID of the person to be retrieved.
     * @return The Person object corresponding to the given ID.
     * @throws SQLException If an error occurs while retrieving the person from the database.
     */
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


    /**
     * Converts a Person to a DisasterVictim by copying over relevant properties and transferring
     * related entities like medical records and allocated supplies.
     *
     * @param personId The ID of the person to be converted.
     * @throws SQLException If an error occurs during database interaction.
     */
    public void convertToDisasterVictim(int personId) throws SQLException {
        Person person = getPersonById(personId);
        if (person == null) {
            throw new IllegalArgumentException("Person not found with ID: " + personId);
        }

        if (person instanceof DisasterVictim) {
            throw new IllegalArgumentException("Person is already a DisasterVictim");
        }

        // Create new DisasterVictim with same properties
        DisasterVictim victim = new DisasterVictim(person.getFirstName(), person.getLastName());
        if (person.getDateOfBirth() != null) {
            victim.setDateOfBirth(person.getDateOfBirth());
        }
        victim.setGender(person.getGender());
        victim.setComments(person.getComments());
        victim.setPhoneNumber(person.getPhoneNumber());
        victim.setFamilyGroup(person.getFamilyGroup());
        victim.setMedicalRecords(person.getMedicalRecords());

        // Delete original person and add new victim
        deletePerson(personId);
        addPerson(victim);

        // Transfer any allocated supplies
        List<Supply> allocatedSupplies = databaseManager.getSuppliesAllocatedTo(personId, null);
        for (Supply supply : allocatedSupplies) {
            databaseManager.allocateSupply(supply.getSupplyId(), victim.getPersonId(), null);
        }
    }

















    // Add these methods to your PersonController class

    /**
     * Creates a new family group with the specified members
     * @param members List of persons to include in the family group
     * @return The created FamilyGroup
     * @throws SQLException If there's a database error
     * @throws IllegalArgumentException If members list is empty or null
     */
    public FamilyGroup createFamilyGroup(ArrayList<Person> members) throws SQLException {
        if (members == null || members.isEmpty()) {
            throw new IllegalArgumentException("Family group must have at least one member");
        }

        // Create new family group
        FamilyGroup familyGroup = new FamilyGroup(members);

        // Update all members to belong to this family group
        for (Person member : members) {
            member.setFamilyGroup(familyGroup);
            databaseManager.updatePerson(member);
        }

        return familyGroup;
    }

    /**
     * Adds a person to an existing family group
     * @param personId ID of person to add
     * @param familyGroupId ID of family group to add to
     * @throws SQLException If there's a database error
     * @throws IllegalArgumentException If person or family group not found
     */
    public void addPersonToFamilyGroup(int personId, int familyGroupId) throws SQLException {
        Person person = getPersonById(personId);
        if (person == null) {
            throw new IllegalArgumentException("Person not found with ID: " + personId);
        }

        // Find the family group by checking all persons
        FamilyGroup familyGroup = findFamilyGroupById(familyGroupId);
        if (familyGroup == null) {
            throw new IllegalArgumentException("Family group not found with ID: " + familyGroupId);
        }

        // Remove from current family if any
        if (person.getFamilyGroup() != null) {
            person.getFamilyGroup().removeMember(person);
        }

        // Add to new family
        familyGroup.addMember(person);
        person.setFamilyGroup(familyGroup);
        databaseManager.updatePerson(person);
    }

    /**
     * Removes a person from their current family group
     * @param personId ID of person to remove
     * @throws SQLException If there's a database error
     * @throws IllegalArgumentException If person not found or not in a family group
     */
    public void removePersonFromFamilyGroup(int personId) throws SQLException {
        Person person = getPersonById(personId);
        if (person == null) {
            throw new IllegalArgumentException("Person not found with ID: " + personId);
        }

        if (person.getFamilyGroup() == null) {
            throw new IllegalArgumentException("Person is not in a family group");
        }

        person.getFamilyGroup().removeMember(person);
        person.setFamilyGroup(null);
        databaseManager.updatePerson(person);
    }

    /**
     * Gets all existing family groups
     * @return List of all family groups
     * @throws SQLException If there's a database error
     */
    public ArrayList<FamilyGroup> getAllFamilyGroups() throws SQLException {
        Map<Integer, FamilyGroup> familyGroups = new HashMap<>();

        for (Person person : personModels) {
            if (person.getFamilyGroup() != null) {
                FamilyGroup family = person.getFamilyGroup();
                familyGroups.putIfAbsent(family.getFamilyGroupId(), family);
            }
        }

        return new ArrayList<>(familyGroups.values());
    }

    /**
     * Finds a family group by ID
     * @param familyGroupId ID of family group to find
     * @return The FamilyGroup or null if not found
     */
    public FamilyGroup findFamilyGroupById(int familyGroupId) {
        for (Person person : personModels) {
            if (person.getFamilyGroup() != null &&
                    person.getFamilyGroup().getFamilyGroupId() == familyGroupId) {
                return person.getFamilyGroup();
            }
        }
        return null;
    }

    /**
     * Gets all members of a specific family group
     * @param familyGroupId ID of the family group
     * @return List of members
     * @throws SQLException If there's a database error
     */
    public ArrayList<Person> getFamilyGroupMembers(int familyGroupId) throws SQLException {
        FamilyGroup familyGroup = findFamilyGroupById(familyGroupId);
        if (familyGroup == null) {
            throw new IllegalArgumentException("Family group not found with ID: " + familyGroupId);
        }

        return new ArrayList<>(familyGroup.getMembers());
    }

    /**
     * Deletes a family group and removes all members from it
     *
     * @param familyGroupId ID of family group to delete
     * @throws SQLException If there's a database error
     */
    public void deleteFamilyGroup(int familyGroupId) throws SQLException {
        FamilyGroup familyGroup = findFamilyGroupById(familyGroupId);
        if (familyGroup == null) {
            throw new IllegalArgumentException("Family group not found with ID: " + familyGroupId);
        }

        for (Person member : new ArrayList<>(familyGroup.getMembers())) {
            member.setFamilyGroup(null);
            databaseManager.updatePerson(member);
        }
    }


    /**
     * Initializes the person ID counter by retrieving the largest existing person ID from the database.
     *
     * @throws SQLException if there is an error retrieving the largest person ID from the database.
     */
    private void initializeIdCounter() throws SQLException {
        int maxId = databaseManager.getLargestPersonId();
        personIdCounter = maxId + 1;
    }

    /**
     * Generates a new person ID by adding 1 to medicalRecordIdCounter
     *
     * @return The next available person ID.
     */
    public int generatePersonId() {
        return personIdCounter++;
    }




}