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
    private static int counter = 100;

    // Constructor
    public Inquiry(Person inquirer, DisasterVictim missingPerson, String dateOfInquiry,
                   String infoProvided, Location lastKnownLocation) {
        setInquirer(inquirer);
        setMissingPerson(missingPerson);
        setDateOfInquiry(dateOfInquiry);
        setInfoProvided(infoProvided);
        setLastKnownLocation(lastKnownLocation);
        this.inquiryId = createInquiryID();
    }

    // Overloaded constructor for database loading
    public Inquiry(int inquiryId, Person inquirer, DisasterVictim missingPerson,
                   String dateOfInquiry, String infoProvided, Location lastKnownLocation) {
        this(inquirer, missingPerson, dateOfInquiry, infoProvided, lastKnownLocation);
        setInquiryId(inquiryId);
    }

    // Getters
    public Person getInquirer() {
        return inquirer;
    }

    public DisasterVictim getMissingPerson() {
        return missingPerson;
    }

    public String getDateOfInquiry() {
        return dateOfInquiry;
    }

    public String getInfoProvided() {
        return infoProvided;
    }

    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }

    public int getInquiryId() {
        return inquiryId;
    }

    // Setters with validation
    public void setInquirer(Person inquirer) {
        if (inquirer == null) {
            throw new IllegalArgumentException("Inquirer cannot be null");
        }
        this.inquirer = inquirer;
    }

    public void setMissingPerson(DisasterVictim missingPerson) {
        if (missingPerson == null) {
            throw new IllegalArgumentException("Missing person cannot be null");
        }
        this.missingPerson = missingPerson;
    }

    public void setDateOfInquiry(String dateOfInquiry) {
        if (!isValidDateFormat(dateOfInquiry)) {
            throw new IllegalArgumentException("Date of inquiry is invalid");
        }
        this.dateOfInquiry = dateOfInquiry;
    }

    public void setInfoProvided(String infoProvided) {
        if (infoProvided == null || infoProvided.trim().isEmpty()) {
            throw new IllegalArgumentException("Information provided cannot be null or empty");
        }
        this.infoProvided = infoProvided.trim();
    }

    public void setLastKnownLocation(Location lastKnownLocation) {
        if (lastKnownLocation == null) {
            throw new IllegalArgumentException("Last known location cannot be null");
        }
        this.lastKnownLocation = lastKnownLocation;
    }

    public void setInquiryId(int inquiryId) {
        if (inquiryId < 0) {
            throw new IllegalArgumentException("Inquiry ID cannot be negative");
        }
        this.inquiryId = inquiryId;
        // Update counter if needed to prevent ID conflicts
        if (inquiryId >= counter) {
            counter = inquiryId + 1;
        }
    }

    // Helper methods
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

    public String getFormattedDate() {
        try {
            LocalDate date = LocalDate.parse(dateOfInquiry, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        } catch (DateTimeParseException e) {
            return dateOfInquiry; // Return original if formatting fails
        }
    }

    public String getSummary() {
        return String.format("Inquiry #%d: %s reported %s missing at %s on %s",
                inquiryId,
                inquirer.getFirstName() + " " + inquirer.getLastName(),
                missingPerson.getFirstName() + " " + missingPerson.getLastName(),
                lastKnownLocation.getLocationName(),
                getFormattedDate());
    }

    // Database helper methods
    public String toDatabaseString() {
        return String.format("%d,%d,%d,%d,%s,%s",
                inquiryId,
                inquirer.getPersonId(),
                missingPerson.getPersonId(),
                lastKnownLocation.getLocationId(),
                dateOfInquiry,
                infoProvided.replace(",", "\\,")); // Escape commas in info
    }

    // Equality check based on ID
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Inquiry inquiry = (Inquiry) obj;
        return inquiryId == inquiry.inquiryId;
    }

    @Override
    public int hashCode() {
        return inquiryId;
    }

    @Override
    public String toString() {
        return getSummary();
    }

    private int createInquiryID() {
        return counter++;
    }
}