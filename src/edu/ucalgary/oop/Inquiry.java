package edu.ucalgary.oop;

public class Inquiry {
    private Person inquirer;
    private DisasterVictim missingPerson;
    private String dateOfInquiry;
    private String infoProvided;
    private Location lastKnownLocation;
    private int inquiryId = createInquiryID();
    private static int counter = 100;


    // Constructor
    public Inquiry(Person inquirer, DisasterVictim missingPerson, String dateOfInquiry, String infoProvided, Location lastKnownLocation) {
        this.inquirer = inquirer;
        this.missingPerson = missingPerson;
        if(!(isValidDateFormat(dateOfInquiry))) {
            throw new IllegalArgumentException("Date of inquiry is invalid");
        }
        this.dateOfInquiry = dateOfInquiry;
        this.infoProvided = infoProvided;
        this.lastKnownLocation = lastKnownLocation;
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


    // Setters
    public void setDateOfInquiry(String dateOfInquiry) {
        if(!(isValidDateFormat(dateOfInquiry))) {
            throw new IllegalArgumentException("Date of inquiry is invalid");
        }
        this.dateOfInquiry = dateOfInquiry;
    }

    public void setInfoProvided(String infoProvided) {
        if (infoProvided == null || infoProvided.trim().isEmpty()) {
            throw new IllegalArgumentException("Supply name cannot be null or empty");
        }
        this.infoProvided = infoProvided;
    }

    public void setLastKnownLocation(Location lastKnownLocation) {
        if (lastKnownLocation == null) {
            throw new IllegalArgumentException("Supply name cannot be null or empty");
        }
        this.lastKnownLocation = lastKnownLocation;
    }

    public void setInquiryId(int inquiryId) {
        this.inquiryId = inquiryId;
    }








    // Private Code for Checking or Initialization
    private static boolean isValidDateFormat(String date) {
        if (date == null || date.length() != 10) {
            return false;
        }

        if (date.charAt(4) != '-' || date.charAt(7) != '-') {
            return false;
        }

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

        if(year < 1900 || year > 2026) {
            return false;
        }

        return true;

    }

    private int createInquiryID() {
        return counter++;
    }


}
