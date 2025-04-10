package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class InquiryTest {

    private Person inquirer;
    private DisasterVictim missingPerson;
    private Location lastKnownLocation;
    private Inquiry inquiry;

    @Before
    public void setUp() {
        inquirer = new Person("John", "Doe");
        missingPerson = new DisasterVictim("Jane", "Doe");
        lastKnownLocation = new Location("Park", "456 St");
        inquiry = new Inquiry(inquirer, missingPerson, "2023-01-01", "Info provided", lastKnownLocation);
    }


    @Test
    public void testInquiryConstructor() {

        Inquiry inquiryTwo = new Inquiry(inquirer, missingPerson, "2023-01-05", "Lost", lastKnownLocation);
        assertNotNull("Inquiry constructor failed", inquiryTwo);
        assertEquals("Inquirer not set correctly", inquirer, inquiryTwo.getInquirer());
        assertEquals("Missing person not set correctly", missingPerson, inquiryTwo.getMissingPerson());
        assertEquals("Last known location not set correctly", lastKnownLocation, inquiryTwo.getLastKnownLocation());
    }

    @Test
    public void testGetDateOfInquiry() {
        assertEquals("Date of inquiry not set correctly", "2023-01-01", inquiry.getDateOfInquiry());
    }

    @Test
    public void testGetInfoProvided() {
        assertEquals("Info provided not set correctly", "Info provided", inquiry.getInfoProvided());
    }

    @Test
    public void testSetDateOfInquiry() {
        String newDate = "2023-02-01";
        inquiry.setDateOfInquiry(newDate);
        assertEquals("Date of inquiry not updated correctly", newDate, inquiry.getDateOfInquiry());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDateOfInquiryWithInvalidDate() {
        inquiry.setDateOfInquiry("2050-02-01");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDateOfInquiryWithNull() {
        inquiry.setDateOfInquiry(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDateOfInquiryWithEmptyString() {
        inquiry.setDateOfInquiry("");
    }

    @Test
    public void testSetInfoProvided() {
        //Arrange
        String newInfo = "New info provided";
        //Act
        inquiry.setInfoProvided(newInfo);
        //Assert
        assertEquals("Info provided not updated correctly", newInfo, inquiry.getInfoProvided());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetInfoProvidedWithNull() {
        inquiry.setInfoProvided(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetInfoProvidedWithEmptyString() {
        inquiry.setInfoProvided("");
    }


    @Test
    public void testSetLastKnownLocation() {
        //Arrange
        Location newLastKnownLocation = new Location("Mall", "123 St");
        //Act
        inquiry.setLastKnownLocation(newLastKnownLocation);
        //Assert
        assertEquals("Last known location not updated correctly", newLastKnownLocation, inquiry.getLastKnownLocation());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetLastKnownLocationWithNull() {
        inquiry.setLastKnownLocation(null);
    }






}