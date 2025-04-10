package edu.ucalgary.oop;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.sql.SQLException;

public class PersonControllerTest {
    private PersonController personController;
    private Person testPerson1;
    private Person testPerson2;
    private DisasterVictim testVictim;
    private FamilyGroup testFamily;

    @Before
    public void setUp() {
        // Use the test constructor
        personController = new PersonController(0);

        // Create test persons
        testPerson1 = new Person("As","sa");
        testPerson1.setPersonId(1);
        testPerson1.setFirstName("John");
        testPerson1.setLastName("Doe");

        testPerson2 = new Person("as","asd");
        testPerson2.setPersonId(2);
        testPerson2.setFirstName("Jane");
        testPerson2.setLastName("Doe");

        // Create test victim
        testVictim = new DisasterVictim("Alice", "Smith");
        testVictim.setPersonId(3);

        // Create test family
        ArrayList<Person> familyMembers = new ArrayList<>();
        familyMembers.add(testPerson1);
        familyMembers.add(testPerson2);
        testFamily = new FamilyGroup(familyMembers);
        testFamily.setFamilyGroupId(1);

        // Manually add to controller's list
        personController.getAllPeopleTest().add(testPerson1);
        personController.getAllPeopleTest().add(testPerson2);
        personController.getAllPeopleTest().add(testVictim);
    }

    // Tests that should pass with test constructor

    @Test
    public void testGetAllPeople() {
        ArrayList<Person> people = personController.getAllPeople();
        assertEquals("Should return all test people", 3, people.size());
        assertTrue("Should contain test person 1", people.contains(testPerson1));
        assertTrue("Should contain test victim", people.contains(testVictim));
    }

    @Test
    public void testGetPersonById() {
        try {
            Person found = personController.getPersonById(1);
            assertNotNull("Should find person with ID 1", found);
            assertEquals("Should return correct person", "John", found.getFirstName());
        } catch (SQLException e) {
            fail("Should not throw SQLException in test mode");
        }
    }

    @Test
    public void testGeneratePersonIdIncrements() {
        int firstId = personController.generatePersonId();
        int secondId = personController.generatePersonId();
        assertEquals("IDs should increment", firstId + 1, secondId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNullPerson() throws SQLException {
        personController.addPerson(null);
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateNullPerson() throws SQLException {
        personController.updatePerson(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertToDisasterVictimAlreadyVictim() throws SQLException {
        personController.convertToDisasterVictim(3); // testVictim's ID
    }



    @Test(expected = IllegalArgumentException.class)
    public void testCreateFamilyGroupEmptyList() throws SQLException {
        personController.createFamilyGroup(new ArrayList<>());
    }

    @Test
    public void testFindFamilyGroupById() {
        testPerson1.setFamilyGroup(testFamily);
        testPerson2.setFamilyGroup(testFamily);

        FamilyGroup found = personController.findFamilyGroupById(1);
        assertNotNull("Should find family group", found);
        assertEquals("Should return correct family", testFamily, found);
    }


    @Test(expected = NullPointerException.class)
    public void testAddPersonNullPointerException() throws SQLException {
        Person newPerson = new Person("test", "test2");
        newPerson.setFirstName("New");
        personController.addPerson(newPerson);
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateNullPointerException() throws SQLException {
        testPerson1.setFirstName("Updated");
        personController.updatePerson(testPerson1);
    }

    @Test(expected = NullPointerException.class)
    public void testDeletePersonNullPointerException() throws SQLException {
        personController.deletePerson(1);
    }



    @Test
    public void testLocalModelUpdatesDespiteDatabaseException() {
        try {
            // Try to update - should fail but update local model
            testPerson1.setFirstName("Updated");
            personController.updatePerson(testPerson1);
            fail("Should have thrown SQLException");
        } catch (NullPointerException | SQLException e) {
            // Verify local update happened despite database exception
            Person updated = personController.getAllPeople().get(0);
            assertEquals("Name should be updated locally", "Updated", updated.getFirstName());
        }
    }
}