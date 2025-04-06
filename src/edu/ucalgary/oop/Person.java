package edu.ucalgary.oop;

import java.util.ArrayList;


public class Person {
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String comments;
    private String phoneNumber;
    private FamilyGroup familyGroup;
    private String gender;
    private ArrayList<MedicalRecord> medicalRecords;
    private int personId = createPersonId();
    private static int counter = 10;

    // Constructors
    public Person(String firstName, String lastName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.medicalRecords = new ArrayList<>();


    }


    public Person(String firstName, String lastName, String dateOfBirth) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        this.firstName = firstName;
        this.lastName = lastName;
        if(isValidDateFormat(dateOfBirth)) {
            this.dateOfBirth = dateOfBirth;
        }
        else {
            throw new IllegalArgumentException("Date of birth cannot be null or empty");
        }
        this.medicalRecords = new ArrayList<>();
    }


    // Getters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }


    public String getComments() {
        return comments;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }


    public FamilyGroup getFamilyGroup() {
        return familyGroup;
    }

    public String getGender() {
        return gender;
    }

    public ArrayList<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    public int getPersonId() {
        return personId;
    }


    // Setters
    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        this.lastName = lastName;
    }

    public void setDateOfBirth(String dateOfBirth) {
        if(isValidDateFormat(dateOfBirth)) {
            this.dateOfBirth = dateOfBirth;
        }
        else {
            throw new IllegalArgumentException("Date of birth cannot be null or empty");
        }
    }

    public void setComments(String comments) {
        this.comments = comments;
    }


    public void setPhoneNumber(String phoneNumber) {
        if(isValidPhoneNumberSimpleFormat(phoneNumber)){
            this.phoneNumber = phoneNumber;
        }
        /*else {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }*/
    }

    public void setFamilyGroup(FamilyGroup familyGroup) {
        if (familyGroup == null) {
            throw new IllegalArgumentException("FamilyGroup cannot be null");
        }
        this.familyGroup = familyGroup;

    }

    public void setGender(String gender) {
        /*if (gender == null) {
            throw new IllegalArgumentException("Gender cannot be null");
        }*/
        this.gender = gender;
    }

    public void setMedicalRecords(ArrayList<MedicalRecord> medicalRecords) {
        this.medicalRecords = medicalRecords;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }



    // Class Specific Code

    public void addMedicalRecord(MedicalRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Medical record cannot be null");
        }
        this.medicalRecords.add(record);
    }



    public Inquiry createInquiry(Object missingPerson, String date, String infoProvided, Location lastKnownLocation) {
        // Validate inputs
        if (missingPerson == null) {
            throw new IllegalArgumentException("Missing person cannot be null");
        }
        if (!(missingPerson instanceof DisasterVictim)) {
            throw new IllegalArgumentException("Missing person must be a DisasterVictim");
        }
        DisasterVictim victim = (DisasterVictim) missingPerson;

        if (date == null || !isValidDateFormat(date)) {
            throw new IllegalArgumentException("Invalid date format");
        }
        if (infoProvided == null || infoProvided.trim().isEmpty()) {
            throw new IllegalArgumentException("Info provided cannot be null or empty");
        }
        if (lastKnownLocation == null) {
            throw new IllegalArgumentException("Last known location cannot be null");
        }

        return new Inquiry(this, victim, date, infoProvided, lastKnownLocation);
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

    private static boolean isValidPhoneNumberSimpleFormat(String phone) {
        if (phone == null || phone.length() != 12) {
            return false;
        }

        // Check format XXX-XXX-XXXX
        if (phone.charAt(3) != '-' || phone.charAt(7) != '-') {
            return false;
        }

        String part1 = phone.substring(0, 3);
        String part2 = phone.substring(4, 7);
        String part3 = phone.substring(8, 12);

        return part1.matches("\\d{3}") &&
                part2.matches("\\d{3}") &&
                part3.matches("\\d{4}");
    }


    private int createPersonId() {
        return counter++;
    }


}
