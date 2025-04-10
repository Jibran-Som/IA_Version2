/**
 * MedicalRecord.java
 * Version: 1.0
 * Author: Jibran Somroo
 * Date: April 7, 2025
 */

package edu.ucalgary.oop;

public class MedicalRecord {
    private Person person;
    private Location location;
    private String treatmentDetails;
    private String dateOfTreatment;
    private int medicalRecordId;


    /**
     * Constructs a new MedicalRecord with the specified person, location, treatment details, and treatment date.
     *
     * @param person           The person associated with the medical record.
     * @param location         The location where treatment was administered.
     * @param treatmentDetails A description of the treatment provided.
     * @param dateOfTreatment  The date the treatment occurred
     * @throws IllegalArgumentException if the date format is invalid.
     */
    public MedicalRecord(Person person, Location location, String treatmentDetails, String dateOfTreatment) {
        this.person = person;
        this.location = location;

        // Convert database timestamp to date string if needed
        if (dateOfTreatment != null && dateOfTreatment.contains(" ")) {
            dateOfTreatment = dateOfTreatment.split(" ")[0]; // Take just the date part
        }

        if (!isValidDateFormat(dateOfTreatment)) {
            throw new IllegalArgumentException("Invalid date format: " + dateOfTreatment);
        }
        if (treatmentDetails == null) {
            throw new IllegalArgumentException("Treatment details cannot be null");
        }
        if (treatmentDetails.trim().isEmpty()) {
            throw new IllegalArgumentException("Treatment details cannot be empty");
        }
        this.treatmentDetails = treatmentDetails;
        this.dateOfTreatment = dateOfTreatment;
    }

    /**
     * Retrieves the person associated with the medical record.
     *
     * @return The person associated with this medical record.
     */
    public Person getPerson() {
        return person;
    }

    /**
     * Retrieves the location associated with the medical record.
     *
     * @return The location associated with this medical record.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Retrieves the treatment details associated with the medical record.
     *
     * @return The treatment details associated with this medical record.
     */
    public String getTreatmentDetails() {
        return treatmentDetails;
    }

    /**
     * Retrieves the date of treatment associated with the medical record.
     *
     * @return The date of treatment associated with this medical record.
     */
    public String getDateOfTreatment() {
        return dateOfTreatment;
    }

    /**
     * Retrieves the id associated with the medical record.
     *
     * @return The id associated with this medical record.
     */
    public int getMedicalRecordId() {
        return medicalRecordId;
    }


    /**
     * Sets the person associated with the medical record.
     *
     * @param person The new person to be associated with the medical record.
     * @throws IllegalArgumentException if the provided location is null
     */
    public void setPerson(Person person) {
        if(person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }
        this.person = person;

    }



    /**
     * Sets the location associated with the medical record.
     *
     * @param newLocation The new location to be associated with the medical record.
     * @throws IllegalArgumentException if the provided location is null
     */
    public void setLocation(Location newLocation) {
        if(newLocation == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        this.location = newLocation;

    }

    /**
     * Sets the date of treatment associated with the medical record.
     *
     * @param dateOfTreatment The new date of treatment to be associated with the medical record.
     * @throws IllegalArgumentException if the provided is null
     */
    public void setDateOfTreatment(String dateOfTreatment) {
        if(!isValidDateFormat(dateOfTreatment)) {
            throw new IllegalArgumentException("Invalid date format");
        }
        this.dateOfTreatment = dateOfTreatment;
    }


    /**
     * Sets the treatment details for the medical record.
     *
     * @param treatmentDetails A description of the treatment provided.
     * @throws IllegalArgumentException if the treatment details are null
     */
    public void setTreatmentDetails(String treatmentDetails) {
        if(treatmentDetails == null) {
            throw new IllegalArgumentException("Treatment details cannot be null");
        }
        if(treatmentDetails.trim().isEmpty()) {
            throw new IllegalArgumentException("Treatment details cannot be empty");
        }
        this.treatmentDetails = treatmentDetails;
    }

    /**
     * Sets the ID for the medical record.
     *
     * @param medicalRecordId The ID to be assigned to the medical record.
     */
    public void setMedicalRecordId(int medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }


    /**
     * Validates whether the provided date string is in the correct format (YYYY-MM-DD).
     *
     * @param date The date string to be validated.
     * @return true if the date is in a valid format
     */
    private static boolean isValidDateFormat(String date) {
        if (date == null) {
            return false;
        }

        // Handle database timestamp format (YYYY-MM-DD HH:MM:SS)
        if (date.contains(" ")) {
            date = date.split(" ")[0];
        }

        if (date.length() != 10) {
            return false;
        }

        if (date.charAt(4) != '-' || date.charAt(7) != '-') {
            return false;
        }

        try {
            String yearStr = date.substring(0, 4);
            String monthStr = date.substring(5, 7);
            String dayStr = date.substring(8, 10);

            int year = Integer.parseInt(yearStr);
            int month = Integer.parseInt(monthStr);
            int day = Integer.parseInt(dayStr);

            if (month < 1 || month > 12) {
                return false;
            }

            if (day < 1 || day > 31) {
                return false;
            }

            return year >= 1900 && year <= 2100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    


}
