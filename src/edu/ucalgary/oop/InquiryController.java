/**
 * InquiryController.java
 * Version: 3.0
 * Author: Jibran Somroo
 * Date: April 9, 2025
 */

package edu.ucalgary.oop;

import java.util.ArrayList;
import java.sql.SQLException;

public class InquiryController {
    private ArrayList<Inquiry> inquiryModels;
    private DatabaseManager databaseManager;
    private static int inquiryIdCounter;


    /**
     * Constructs an InquiryController object and initializes its dependencies
     *
     * @throws RuntimeException if there is a failure in initializing the InquiryController,
     */
    public InquiryController() {
        try {
            this.databaseManager = DatabaseManager.getInstance();

            this.inquiryModels = new ArrayList<>();
            populateInquiriesFromDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize InquiryController", e);
        }
    }


    /**
     * Constructs an InquiryController for InquiryControllerTest
     *
     */
    public InquiryController(int test) {
            this.inquiryModels = new ArrayList<>();

    }



    /**
     * Populates the inquiryModels list by loading inquiries from the database.
     *
     * @throws SQLException if there is an error accessing the database while retrieving the inquiries.
     */
    private void populateInquiriesFromDatabase() throws SQLException {
        try {
            ArrayList<Inquiry> inquiries = (ArrayList<Inquiry>) databaseManager.getAllInquiries();
            this.inquiryModels.clear();
            this.inquiryModels.addAll(inquiries);
            initializeIdCounter();
        } catch (SQLException e) {
            System.err.println("Error loading inquiries from database: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves a copy of the list of all inquiries.
     *
     * @return A new ArrayList containing all Inquiry objects.
     */
    public ArrayList<Inquiry> getAllInquiries() {
        return new ArrayList<>(this.inquiryModels);
    }

    /**
     * Retrieves a list of all inquiries.
     *
     * @return A ArrayList containing all Inquiry objects.
     */
    public ArrayList<Inquiry> getAllInquiriesTest() {
        return this.inquiryModels;
    }

    /**
     * Adds a new inquiry to the system and stores it in the database.
     *
     * @param inquiry The Inquiry object to be added.
     * @throws IllegalArgumentException if the provided inquiry is null.
     * @throws SQLException if there is an error adding the inquiry to the database.
     */
    public void addInquiry(Inquiry inquiry) throws SQLException {
        if (inquiry == null) {
            throw new IllegalArgumentException("Inquiry cannot be null");
        }

        if(inquiryIdCounter <= 0) {
            inquiry.setInquiryId(generateInquiryId());
        }

        try {
            databaseManager.addInquiry(inquiry);
            this.inquiryModels.add(inquiry);
        } catch (SQLException e) {
            System.err.println("Error adding inquiry: " + e.getMessage());
            inquiryIdCounter--;
            throw e;
        }
    }


    /**
     * Updates an existing inquiry in the system and the database.
     *
     * @param inquiry The Inquiry object to be updated.
     * @throws IllegalArgumentException if the provided inquiry is null.
     * @throws SQLException if there is an error updating the inquiry in the database.
     */
    public void updateInquiry(Inquiry inquiry) throws SQLException {
        if (inquiry == null) {
            throw new IllegalArgumentException("Inquiry cannot be null");
        }

        try {
            databaseManager.updateInquiry(inquiry);
            // Update local model
            for (int i = 0; i < inquiryModels.size(); i++) {
                if (inquiryModels.get(i).getInquiryId() == inquiry.getInquiryId()) {
                    inquiryModels.set(i, inquiry);
                    break;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating inquiry: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Deletes an inquiry from the system and the database.
     *
     * @param inquiryId The ID of the inquiry to be deleted.
     * @throws SQLException if there is an error deleting the inquiry from the database.
     */
    public void deleteInquiry(int inquiryId) throws SQLException {
        try {
            databaseManager.deleteInquiry(inquiryId);
            inquiryModels.removeIf(i -> i.getInquiryId() == inquiryId);
        } catch (SQLException e) {
            System.err.println("Error deleting inquiry: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves an inquiry by its ID.
     *
     * @param inquiryId The ID of the inquiry to be retrieved.
     * @return The Inquiry object corresponding to the given inquiry ID.
     * @throws SQLException if there is an error retrieving the inquiry from the database.
     */
    public Inquiry getInquiryById(int inquiryId) throws SQLException {
        for (Inquiry inquiry : inquiryModels) {
            if (inquiry.getInquiryId() == inquiryId) {
                return inquiry;
            }
        }

        try {
            return databaseManager.getInquiryById(inquiryId);
        } catch (SQLException e) {
            System.err.println("Error getting inquiry by ID: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves a list of inquiries associated with a specific inquirer.
     * Note: Created to make sure that 1 person can make more than 1 inquiry.
     *
     * @param personId The ID of the person (inquirer) whose inquiries are to be retrieved.
     * @return An ArrayList of Inquiry objects associated with the given inquirer.
     */
    public ArrayList<Inquiry> getInquiriesByInquirer(int personId){
        ArrayList<Inquiry> results = new ArrayList<>();
        for (Inquiry inquiry : inquiryModels) {
            if (inquiry.getInquirer().getPersonId() == personId) {
                results.add(inquiry);
            }
        }
        return results;
    }

    /**
     * Retrieves a list of inquiries associated with a specific disaster victim.
     * Note: This was more of a quality check to ensure that DisasterVictim could
     * have multiple inquires about them
     *
     * @param personId The ID of the missing person whose associated inquiries are to be retrieved.
     * @return An ArrayList of Inquiry objects associated with the given missing person.
     */
    public ArrayList<Inquiry> getInquiriesByMissingPerson(int personId){
        ArrayList<Inquiry> results = new ArrayList<>();
        for (Inquiry inquiry : inquiryModels) {
            if (inquiry.getMissingPerson().getPersonId() == personId) {
                results.add(inquiry);
            }
        }
        return results;
    }

    /**
     * Refreshes the list of inquiries by reloading them from the database.
     * Note: Exists due to various updating errors in early development.
     *
     * @throws SQLException if there is an error accessing the database while reloading the inquiries.
     */
    public void refreshInquiries() throws SQLException {
        populateInquiriesFromDatabase();
    }



    /**
     * Initializes the inquiry ID counter by retrieving the largest existing inquiry ID from the database.
     *
     * @throws SQLException if there is an error retrieving the largest inquiry ID from the database.
     */
    private void initializeIdCounter() throws SQLException {
        int maxId = databaseManager.getLargestInquiryId();
        inquiryIdCounter = maxId + 1;
    }

    /**
     * Generates a new inquiry ID by adding 1 to inquiryIdCounter
     *
     * @return The next available inquiry ID.
     */
    public int generateInquiryId() {
        return inquiryIdCounter++;
    }



}