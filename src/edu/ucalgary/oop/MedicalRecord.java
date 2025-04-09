package edu.ucalgary.oop;

public class MedicalRecord {
    private Person person;
    private Location location;
    private String treatmentDetails;
    private String dateOfTreatment;
    private int medicalRecordId;


    // Constructor
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

    // Getters
    public Person getPerson() {
        return person;
    }

    public Location getLocation() {
        return location;
    }

    public String getTreatmentDetails() {
        return treatmentDetails;
    }

    public String getDateOfTreatment() {
        return dateOfTreatment;
    }

    public int getMedicalRecordId() {
        return medicalRecordId;
    }



    // Setters
    public void setLocation(Location newLocation) {
        if(newLocation == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        this.location = newLocation;

    }

    public void setDateOfTreatment(String dateOfTreatment) {
        if(!isValidDateFormat(dateOfTreatment)) {
            throw new IllegalArgumentException("Invalid date format");
        }
        this.dateOfTreatment = dateOfTreatment;
    }

    public void setTreatmentDetails(String treatmentDetails) {
        if(treatmentDetails == null) {
            throw new IllegalArgumentException("Treatment details cannot be null");
        }
        if(treatmentDetails.trim().isEmpty()) {
            throw new IllegalArgumentException("Treatment details cannot be empty");
        }
        this.treatmentDetails = treatmentDetails;
    }

    public void setMedicalRecordId(int medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }











    // Private Code for Checking or Initialization
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
