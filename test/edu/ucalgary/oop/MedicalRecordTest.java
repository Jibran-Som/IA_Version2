package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MedicalRecordTest {

    private Location locationOne;
    private Location locationTwo;
    private Person personOne;
    private MedicalRecord medicalRecord;



    @Before
    public void setUp(){

        locationOne = new Location("UofC", "University Drive");
        locationTwo = new Location("UBC", "University Ave");

        personOne = new Person("Luka","Modric");

        medicalRecord = new MedicalRecord(personOne, locationOne, "Treatment details", "2023-01-01");

    }

    @Test
    public void testValidConstructor(){
        String expectedTreatmentDetails = "Painkillers";
        String expectedDateOfTreatment = "2020-05-05";

        MedicalRecord medicalRecord = new MedicalRecord(personOne, locationOne, expectedTreatmentDetails, expectedDateOfTreatment);

        assertNotNull("Constructor didn't create an instance of MedicalRecord", medicalRecord);
        assertEquals("Location should match the one set in setup", locationOne, medicalRecord.getLocation());
        assertEquals("Person should match the one set in setup", personOne, medicalRecord.getPerson());
        assertEquals("Constructor set treatment details incorrectly", expectedTreatmentDetails, medicalRecord.getTreatmentDetails());
        assertEquals("Constructor set date of treatment incorrectly", expectedDateOfTreatment, medicalRecord.getDateOfTreatment());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithInvalidFormattedDateOfTreatment(){
        String invalidDateOfTreatment = "205005-05";
        MedicalRecord medicalRecord = new MedicalRecord(personOne, locationOne, "Painkillers", invalidDateOfTreatment);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyTreatmentDetails(){
        String emptyTreatmentDetails = "";
        MedicalRecord medicalRecord = new MedicalRecord(personOne, locationOne, emptyTreatmentDetails, "2020-05-05");
    }


    @Test
    public void testSetLocation() {
        Location newLocation = new Location("Clinic", "456 St");
        medicalRecord.setLocation(newLocation);
        assertEquals("Location not updated correctly", newLocation, medicalRecord.getLocation());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNullLocation() {
        medicalRecord.setLocation(null);
    }


    @Test
    public void testSetTreatmentDetails() {
        medicalRecord.setTreatmentDetails("New treatment details");
        assertEquals("Treatment details not updated correctly", "New treatment details", medicalRecord.getTreatmentDetails());
    }

    @Test
    public void testSetDateOfTreatment() {
        medicalRecord.setDateOfTreatment("2023-02-01");
        assertEquals("Date of treatment not updated correctly", "2023-02-01", medicalRecord.getDateOfTreatment());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testSetInvalidFormatDateOfTreatment() {
        medicalRecord.setDateOfTreatment("20230212");
    }



}

