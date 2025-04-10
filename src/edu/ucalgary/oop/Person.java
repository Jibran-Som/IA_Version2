/**
 * Person.java
 * Version: 1.0
 * Author: Jibran Somroo
 * Date: April 7, 2025
 */

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
    private int personId;
    private static TranslationManager translationManager = TranslationManager.getInstance();

    /**
     * Constructs a Person object with the given first and last name.
     *
     * @param firstName The first name of the person. Must not be null or empty.
     * @param lastName The last name of the person. Must not be null or empty.
     * @throws IllegalArgumentException if the first or last name is null or empty.
     */
    public Person(String firstName, String lastName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException(translationManager.getTranslation("error.firstNameNullOrEmpty"));
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException(translationManager.getTranslation("error.lastNameNullOrEmpty"));
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.medicalRecords = new ArrayList<>();


    }


    /**
     * Constructs a Person object with the given first name, last name and date of birth.
     *
     * @param firstName The first name of the person. Must not be null or empty.
     * @param lastName The last name of the person. Must not be null or empty.
     * @param dateOfBirth The date of birth of the person.
     * @throws IllegalArgumentException if a value is null/improperly formatted.
     */
    public Person(String firstName, String lastName, String dateOfBirth) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException(translationManager.getTranslation("error.firstNameNullOrEmpty"));
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException(translationManager.getTranslation("error.lastNameNullOrEmpty"));
        }
        this.firstName = firstName;
        this.lastName = lastName;
        if(isValidDateFormat(dateOfBirth)) {
            this.dateOfBirth = dateOfBirth;
        }
        else {
            throw new IllegalArgumentException(translationManager.getTranslation("error.dateOfBirthInvalidFormat"));
        }
        this.medicalRecords = new ArrayList<>();
    }


    /**
     * Retrieves the first name of the person.
     *
     * @return The first name of the person.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Retrieves the last name of the person.
     *
     * @return The last name of the person.
     */
    public String getLastName() {
        return lastName;
    }


    /**
     * Retrieves the date of birth
     *
     * @return The date of birth
     */
    public String getDateOfBirth() {
        return dateOfBirth;
    }


    /**
     * Retrieves the comments
     *
     * @return The comments
     */
    public String getComments() {
        return comments;
    }

    /**
     * Retrieves the phone number
     *
     * @return The phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }


    /**
     * Retrieves the family group
     *
     * @return The family group
     */
    public FamilyGroup getFamilyGroup() {
        return familyGroup;
    }

    /**
     * Retrieves the gender
     *
     * @return The gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Retrieves the medical records
     *
     * @return The medical records
     */
    public ArrayList<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    /**
     * Retrieves the id
     *
     * @return The id
     */
    public int getPersonId() {
        return personId;
    }


    /**
     * Sets the first name of the person.
     *
     * @param firstName The first name to be set.
     * @throws IllegalArgumentException if the provided first name is null or empty.
     */
    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException(translationManager.getTranslation("error.firstNameNullOrEmpty"));
        }
        this.firstName = firstName;
    }

    /**
     * Sets the last name of the person.
     *
     * @param lastName The last name to be set.
     * @throws IllegalArgumentException if the provided last name is null or empty.
     */
    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException(translationManager.getTranslation("error.lastNameNullOrEmpty"));
        }
        this.lastName = lastName;
    }

    /**
     * Sets the date of birth of the person.
     *
     * @param dateOfBirth The date of birth to be set.
     * @throws IllegalArgumentException if the provided date of birth is improperly formatted
     */
    public void setDateOfBirth(String dateOfBirth) {
        if(isValidDateFormat(dateOfBirth)) {
            this.dateOfBirth = dateOfBirth;
        }
        else {
            throw new IllegalArgumentException(translationManager.getTranslation("error.dateOfBirthInvalidFormat"));
        }
    }

    /**
     * Sets the comments of the person.
     *
     * @param comments The comments to be set.
     */
    public void setComments(String comments) {
        this.comments = comments;
    }


    /**
     * Sets the phone number of the person.
     *
     * @param phoneNumber The phone number to be set.
     */
    public void setPhoneNumber(String phoneNumber) {
        if(isValidPhoneNumberSimpleFormat(phoneNumber)){
            this.phoneNumber = phoneNumber;
        }
    }

    /**
     * Sets the family group of the person.
     *
     * @param familyGroup The family group to be set.
     */
    public void setFamilyGroup(FamilyGroup familyGroup) {
        this.familyGroup = familyGroup;

    }

    /**
     * Sets the gender of the person.
     *
     * @param gender The gender to be set.
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Sets the medical records of the person.
     *
     * @param medicalRecords The medical records to be set.
     */
    public void setMedicalRecords(ArrayList<MedicalRecord> medicalRecords) {
        this.medicalRecords = medicalRecords;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }



    /**
     * Validates if the provided date string matches the required format and is a valid date.
     *
     * @param date The date being checked
     * @return true if the date is valid and follows the "yyyy-MM-dd" format; false otherwise.
     */
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


    /**
     * Validates if the given phone number matches a simple format.
     * Checks for two formats:
     * 1. XXX-XXX-XXXX (12 characters)
     * 2. XXX-XXXX (8 characters)
     *
     * @param phone The phone number to be validated.
     * @return true if the phone number is valid according to the specified formats, false otherwise.
     */
    private static boolean isValidPhoneNumberSimpleFormat(String phone) {
        if (phone == null) {
            return false;
        }

        // Check for XXX-XXX-XXXX format (12 characters)
        if (phone.length() == 12) {
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
        // Check for XXX-XXXX format (8 characters)
        else if (phone.length() == 8) {
            if (phone.charAt(3) != '-') {
                return false;
            }

            String part1 = phone.substring(0, 3);
            String part2 = phone.substring(4, 8);

            return part1.matches("\\d{3}") &&
                    part2.matches("\\d{4}");
        }

        return false;
    }




}
