/**
 * Inquiry.java
 * Version: 1.0
 * Author: Jibran Somroo
 * Date: April 7, 2025
 */

package edu.ucalgary.oop;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Inquiry {
    private Person inquirer;
    private DisasterVictim missingPerson;
    private String dateOfInquiry;
    private String infoProvided;
    private Location lastKnownLocation;
    private int inquiryId;
    private static TranslationManager translationManager = TranslationManager.getInstance();

    /**
     * Constructor to create a new Inquiry instance.
     *
     * This constructor initializes an Inquiry with the provided details, including the inquirer,
     * missing person, date of inquiry, information provided, and last known location. The inquiry
     * ID is automatically generated using the `createInquiryID` method to ensure a unique identifier
     * for each inquiry.
     *
     * @param inquirer The person making the inquiry.
     * @param missingPerson The person who is missing (disaster victim).
     * @param dateOfInquiry The date when the inquiry was made.
     * @param infoProvided Information provided about the missing person.
     * @param lastKnownLocation The last known location of the missing person.
     */
    public Inquiry(Person inquirer, DisasterVictim missingPerson, String dateOfInquiry,
                   String infoProvided, Location lastKnownLocation) {
        setInquirer(inquirer);
        setMissingPerson(missingPerson);
        setDateOfInquiry(dateOfInquiry);
        setInfoProvided(infoProvided);
        setLastKnownLocation(lastKnownLocation);
    }

    /**
     * Constructor to create a new Inquiry instance with a specified inquiry ID.
     *
     * @param inquiryId The unique identifier for the inquiry.
     * @param inquirer The person making the inquiry.
     * @param missingPerson The person who is missing (disaster victim).
     * @param dateOfInquiry The date when the inquiry was made.
     * @param infoProvided Information provided about the missing person.
     * @param lastKnownLocation The last known location of the missing person.
     */
    public Inquiry(int inquiryId, Person inquirer, DisasterVictim missingPerson,
                   String dateOfInquiry, String infoProvided, Location lastKnownLocation) {
        this(inquirer, missingPerson, dateOfInquiry, infoProvided, lastKnownLocation);
        setInquiryId(inquiryId);
    }

    /**
     * Retrieves the inquirer
     *
     * @return The inquirer.
     */
    public Person getInquirer() {
        return inquirer;
    }


    /**
     * Retrieves the missing person
     *
     * @return The DisasterVictim
     */
    public DisasterVictim getMissingPerson() {
        return missingPerson;
    }

    /**
     * Retrieves the date of the inquiry.
     *
     * @return The date of the inquiry.
     */
    public String getDateOfInquiry() {
        return dateOfInquiry;
    }

    /**
     * Retrieves the information provided in the inquiry.
     *
     * @return The information provided.
     */
    public String getInfoProvided() {
        return infoProvided;
    }

    /**
     * Retrieves the last known location of the missing person.
     *
     * @return The last known location
     */
    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }


    /**
     * Retrieves the ID of the inquiry.
     *
     * @return The inquiry ID.
     */
    public int getInquiryId() {
        return inquiryId;
    }


    /**
     * Sets the inquirer
     *
     * @param inquirer The Person object representing the inquirer.
     * @throws IllegalArgumentException if the inquirer is null.
     */
    public void setInquirer(Person inquirer) {
        if (inquirer == null) {
            throw new IllegalArgumentException(translationManager.getTranslation("error.inquirySetNullInquirer"));
        }
        this.inquirer = inquirer;
    }


    /**
     * Sets the missing person
     *
     * @param missingPerson The (DisasterVictim) missing person.
     * @throws IllegalArgumentException if the missingPerson is null.
     */
    public void setMissingPerson(DisasterVictim missingPerson) {
        if (missingPerson == null) {
            throw new IllegalArgumentException(translationManager.getTranslation("error.inquirySetNullMissingPerson"));
        }
        this.missingPerson = missingPerson;
    }


    /**
     * Sets the date of the inquiry.
     * Note: Must be in proper format (YYYY-MM-DD)
     *
     * @param dateOfInquiry A String representing the date of the inquiry.
     * @throws IllegalArgumentException if the dateOfInquiry does not have a valid format.
     */
    public void setDateOfInquiry(String dateOfInquiry) {
        if (!isValidDateFormat(dateOfInquiry)) {
            throw new IllegalArgumentException(translationManager.getTranslation("error.inquirySetInvalidDate"));
        }
        this.dateOfInquiry = dateOfInquiry;
    }

    /**
     * Sets the information provided in the inquiry.
     *
     * @param infoProvided A String containing the information provided in the inquiry.
     * @throws IllegalArgumentException if the infoProvided is null or empty.
     */
    public void setInfoProvided(String infoProvided) {
        if (infoProvided == null || infoProvided.trim().isEmpty()) {
            throw new IllegalArgumentException(translationManager.getTranslation("error.inquirySetInfoNullOrEmpty"));
        }
        this.infoProvided = infoProvided.trim();
    }

    /**
     * Sets the last known location of the person.
     *
     * @param lastKnownLocation The Location object representing the last known location.
     * @throws IllegalArgumentException if the lastKnownLocation is null.
     */
    public void setLastKnownLocation(Location lastKnownLocation) {
        if (lastKnownLocation == null) {
            throw new IllegalArgumentException(translationManager.getTranslation("error.inquirySetLocationNull"));
        }
        this.lastKnownLocation = lastKnownLocation;
    }


    /**
     * Sets the ID for the inquiry.
     *
     * @param inquiryId The integer representing the inquiry ID.
     */
    public void setInquiryId(int inquiryId) {
        this.inquiryId = inquiryId;

    }

    /**
     * Validates if the provided date string matches the required format and is a valid date.
     *
     * @param date The date being checked
     * @return true if the date is valid and follows the "yyyy-MM-dd" format; false otherwise.
     */
    public static boolean isValidDateFormat(String date) {
        if (date == null || date.length() != 10) {
            return false;
        }

        if (date.charAt(4) != '-' || date.charAt(7) != '-') {
            return false;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate parsedDate = LocalDate.parse(date, formatter);
            LocalDate currentDate = LocalDate.now();

            // Validate year range (1900-2100)
            if (parsedDate.getYear() < 1900 || parsedDate.getYear() > 2100) {
                return false;
            }

            // Date shouldn't be in the future
            if (parsedDate.isAfter(currentDate)) {
                return false;
            }

            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Returns the date of inquiry in a formatted string.
     * Personal Note: Made for the inquiry report.
     *
     * @return A string representing the formatted date of inquiry.
     */
    public String getFormattedDate() {
        try {
            LocalDate date = LocalDate.parse(dateOfInquiry, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        } catch (DateTimeParseException e) {
            return dateOfInquiry; // Return original if formatting fails
        }
    }


    /**
     * Generates a summary string for the inquiry
     *
     * @return A string summarizing the inquiry
     */
    public String getSummary() {
        return String.format("Inquiry #%d: %s reported %s missing at %s on %s",
                inquiryId,
                inquirer.getFirstName() + " " + inquirer.getLastName(),
                missingPerson.getFirstName() + " " + missingPerson.getLastName(),
                lastKnownLocation.getLocationName(),
                getFormattedDate());
    }


    /**
     * Converts the Inquiry object to a string format.
     * Note: Was initially created for the database storage but now is just
     * used as a checking function.
     *
     * @return A string representation of the Inquiry object formatted for database storage.
     */
    public String toDatabaseString() {
        return String.format("%d,%d,%d,%d,%s,%s",
                inquiryId,
                inquirer.getPersonId(),
                missingPerson.getPersonId(),
                lastKnownLocation.getLocationId(),
                dateOfInquiry,
                infoProvided.replace(",", "\\,"));
    }


    /**
     * Compares this Inquiry object with another object for equality.
     *
     * @param obj The object to compare this Inquiry object with.
     * @return true if the two objects are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Inquiry inquiry = (Inquiry) obj;
        return inquiryId == inquiry.inquiryId;
    }


    /**
     * Returns a hash code value for the Inquiry object.
     *
     * @return The hash code value for this Inquiry object.
     */
    @Override
    public int hashCode() {
        return inquiryId;
    }

    /**
     * Returns a string representation of the Inquiry object.
     * Note: Originally created for testing purposes
     *
     * @return A string representing the summary of the Inquiry object.
     */
    @Override
    public String toString() {
        return getSummary();
    }

}