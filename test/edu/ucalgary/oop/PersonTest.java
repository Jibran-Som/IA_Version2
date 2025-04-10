/**
 * PersonTest.java
 * Version: 2.0
 * Author: Jibran Somroo
 * Date: March 30, 2025
 */

package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class PersonTest {

    private Person personOne;
    private Person personTwo;
    private Person personThree;
    private Person personFour;
    private FamilyGroup familyGroupOne;
    private FamilyGroup familyGroupTwo;



    @Before
    public void setUp(){
        personOne = new Person("Jay", "Luck");
        personTwo = new Person("Emily", "Fall");

        ArrayList<Person> familyMembers = new ArrayList<>();

        familyMembers.add(personOne);
        familyMembers.add(personTwo);

        familyGroupOne = new FamilyGroup(familyMembers);

        personThree = new Person("Lionel", "Messi");
        personFour = new Person("Sergio", "Ramos");

        ArrayList<Person> familyMembers2 = new ArrayList<>();

        familyMembers2.add(personThree);
        familyMembers2.add(personFour);

        familyGroupTwo = new FamilyGroup(familyMembers2);


    }


    @Test
    public void testPersonConstructor() {
        // Arrange
        String expectedFirstName = "Alex";
        String expectedLastName = "Doe";

        // Act
        Person person = new Person(expectedFirstName, expectedLastName);

        // Assert
        assertNotNull("Constructor did not create an instance", person);
        assertEquals("Constructor set first name incorrectly",expectedFirstName, person.getFirstName());
        assertEquals("Constructor set last name incorrectly", expectedLastName, person.getLastName());
    }


    @Test
    public void testPersonConstructorWithDateOfBirth() {
        String expectedFirstName = "John";
        String expectedLastName = "Smith";
        String expectedDateOfBirth = "2000-01-01";

        Person person = new Person(expectedFirstName, expectedLastName, expectedDateOfBirth);

        assertNotNull("Constructor did not create an instance", person);
        assertEquals("Constructor set first name incorrectly",expectedFirstName, person.getFirstName());
        assertEquals("Constructor set last name incorrectly", expectedLastName, person.getLastName());
        assertEquals("Constructor set date of birth incorrectly", expectedDateOfBirth, person.getDateOfBirth());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testPersonConstructorWithInvalidBirthdate() {
        String firstName = "Henry";
        String lastName = "Smith";
        String invalidDateOfBirth = "2030-01-01";

        Person person = new Person(firstName, lastName, invalidDateOfBirth);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testPersonConstructorWithInvalidFormatBirthdate() {
        String firstName = "Henry";
        String lastName = "Smith";
        String invalidDateOfBirth = "20300101";

        Person person = new Person(firstName, lastName, invalidDateOfBirth);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testPersonConstructorWithEmptyFirstName() {
        new Person("", "Doe");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPersonConstructorWithEmptyLastName() {
        new Person("John", "");
    }

    @Test
    public void testSetFirstName() {
        String expectedFirstName = "John";
        String originalFirstName = "Alex";

        Person person = new Person(originalFirstName, "Smith");
        person.setFirstName(expectedFirstName);
        assertEquals("Setter for first name is incorrect", expectedFirstName, person.getFirstName());

    }


    @Test
    public void testSetLastName() {
        String expectedLastName = "Smith";
        String originalLastName = "Doe";
        Person person = new Person("Alex", originalLastName);
        //Act
        person.setLastName(expectedLastName);
        //Assert
        assertEquals("Setter for last name is incorrect", expectedLastName, person.getLastName());
    }


    @Test
    public void testSetDateOfBirth() {
        String expectedDateOfBirth = "2000-01-01";

        Person person = new Person("Alex", "Smith", "2005-01-01");

        person.setDateOfBirth(expectedDateOfBirth);
        assertEquals("Setter for birth date is incorrect", expectedDateOfBirth, person.getDateOfBirth());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetInvalidDateOfBirth() {
        String invalidDateOfBirth = "2030-01-01";
        Person person = new Person("Jacob", "Bach", "2005-01-01");

        person.setDateOfBirth(invalidDateOfBirth);
    }

    @Test
    public void testSetComments(){
        String expectedComments = "This is a comment";
        Person person = new Person("Alex", "Smith", "2005-01-01");

        person.setComments(expectedComments);

        assertEquals("Setter for comment doesn't work", expectedComments, person.getComments());


    }


    @Test
    public void testSetPhoneNumber(){
        String expectedPhoneNumber = "012-345-6789";
        Person person = new Person("Henry", "Smith", "2006-01-01");

        person.setPhoneNumber(expectedPhoneNumber);

        assertEquals("Setter for phone number is incorrect", expectedPhoneNumber, person.getPhoneNumber());

    }


    @Test
    public void testGetFamilyGroup(){
        assertEquals("Getter for family group is incorrect", personOne.getFamilyGroup(), personTwo.getFamilyGroup());

    }

    @Test
    public void testSetFamilyGroup(){
        personOne.setFamilyGroup(familyGroupTwo);
        // Need to make sure personOne is in ArrayList when doing tests for familyGroup
        assertEquals("Setter for family group is incorrect", personOne.getFamilyGroup(), familyGroupTwo);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testSetPersonToMultipleFamilyGroups() {
        Person person = new Person("John", "Doe");

        ArrayList<Person> groupOneMembers = new ArrayList<>();
        FamilyGroup groupOne = new FamilyGroup(groupOneMembers);

        ArrayList<Person> groupTwoMembers = new ArrayList<>();
        FamilyGroup groupTwo = new FamilyGroup(groupTwoMembers);

        person.setFamilyGroup(groupOne);
        assertEquals("First family group assignment failed", groupOne, person.getFamilyGroup());

        person.setFamilyGroup(groupTwo);
        assertNotEquals("Person shouldn't belong to a second family group", groupTwo, person.getFamilyGroup());
    }


    @Test
    public void testSetGender(){
        personOne.setGender("Man");
        assertEquals("Getter for gender in incorrect", "Man", personOne.getGender());
    }




    @Test
    public void testSetMedicalRecord() {
        MedicalRecord record = new MedicalRecord(personOne, new Location("Ward", "555 St"), "Blood Transfusion", "2023-01-01");
        ArrayList<MedicalRecord> medicalRecords = new ArrayList<>();
        medicalRecords.add(record);
        personOne.setMedicalRecords(medicalRecords);
        assertEquals("Medical records set using setter", medicalRecords, personOne.getMedicalRecords());

    }




}