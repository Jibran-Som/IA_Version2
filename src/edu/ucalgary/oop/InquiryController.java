package edu.ucalgary.oop;

import java.util.ArrayList;
import java.sql.SQLException;

public class InquiryController {
    private ArrayList<Inquiry> inquiryModels;
    private DatabaseManager databaseManager;
    private PersonController personController;
    private LocationController locationController;

    // Constructor
    public InquiryController() {
        try {
            this.databaseManager = DatabaseManager.getInstance();
            this.personController = new PersonController();
            this.locationController = new LocationController();
            this.inquiryModels = new ArrayList<>();
            populateInquiriesFromDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize InquiryController", e);
        }
    }

    // Constructor with dependency injection for testing
    public InquiryController(DatabaseManager databaseManager,
                             PersonController personController,
                             LocationController locationController) throws SQLException {
        if (databaseManager == null || personController == null || locationController == null) {
            throw new IllegalArgumentException("Dependencies cannot be null");
        }
        this.databaseManager = databaseManager;
        this.personController = personController;
        this.locationController = locationController;
        this.inquiryModels = new ArrayList<>();
        populateInquiriesFromDatabase();
    }

    // Populate inquiryModels from database
    private void populateInquiriesFromDatabase() throws SQLException {
        try {
            ArrayList<Inquiry> inquiries = (ArrayList<Inquiry>) databaseManager.getAllInquiries();
            this.inquiryModels.clear();
            this.inquiryModels.addAll(inquiries);
        } catch (SQLException e) {
            System.err.println("Error loading inquiries from database: " + e.getMessage());
            throw e;
        }
    }

    // Getter for all inquiries
    public ArrayList<Inquiry> getAllInquiries() {
        return new ArrayList<>(this.inquiryModels);
    }

    // Add new inquiry
    public void addInquiry(Inquiry inquiry) throws SQLException {
        if (inquiry == null) {
            throw new IllegalArgumentException("Inquiry cannot be null");
        }

        try {
            databaseManager.addInquiry(inquiry);
            this.inquiryModels.add(inquiry);
        } catch (SQLException e) {
            System.err.println("Error adding inquiry: " + e.getMessage());
            throw e;
        }
    }

    // Update existing inquiry
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

    // Delete inquiry
    public void deleteInquiry(int inquiryId) throws SQLException {
        try {
            databaseManager.deleteInquiry(inquiryId);
            inquiryModels.removeIf(i -> i.getInquiryId() == inquiryId);
        } catch (SQLException e) {
            System.err.println("Error deleting inquiry: " + e.getMessage());
            throw e;
        }
    }

    // Get inquiry by ID
    public Inquiry getInquiryById(int inquiryId) throws SQLException {
        // Check local models first
        for (Inquiry inquiry : inquiryModels) {
            if (inquiry.getInquiryId() == inquiryId) {
                return inquiry;
            }
        }

        // If not found locally, try database
        try {
            return databaseManager.getInquiryById(inquiryId);
        } catch (SQLException e) {
            System.err.println("Error getting inquiry by ID: " + e.getMessage());
            throw e;
        }
    }

    // Get inquiries by inquirer
    public ArrayList<Inquiry> getInquiriesByInquirer(int personId) throws SQLException {
        ArrayList<Inquiry> results = new ArrayList<>();
        for (Inquiry inquiry : inquiryModels) {
            if (inquiry.getInquirer().getPersonId() == personId) {
                results.add(inquiry);
            }
        }
        return results;
    }

    // Get inquiries by missing person
    public ArrayList<Inquiry> getInquiriesByMissingPerson(int personId) throws SQLException {
        ArrayList<Inquiry> results = new ArrayList<>();
        for (Inquiry inquiry : inquiryModels) {
            if (inquiry.getMissingPerson().getPersonId() == personId) {
                results.add(inquiry);
            }
        }
        return results;
    }

    // Get inquiries by location
    public ArrayList<Inquiry> getInquiriesByLocation(int locationId) throws SQLException {
        ArrayList<Inquiry> results = new ArrayList<>();
        for (Inquiry inquiry : inquiryModels) {
            if (inquiry.getLastKnownLocation().getLocationId() == locationId) {
                results.add(inquiry);
            }
        }
        return results;
    }

    // Refresh from database
    public void refreshInquiries() throws SQLException {
        populateInquiriesFromDatabase();
    }

    // Helper method to create a new inquiry
    public Inquiry createInquiry(int inquirerId, int missingPersonId,
                                 String date, String info, int locationId)
            throws SQLException, IllegalArgumentException {

        Person inquirer = personController.getPersonById(inquirerId);
        if (inquirer == null) {
            throw new IllegalArgumentException("Inquirer not found");
        }

        Person person = personController.getPersonById(missingPersonId);
        if (!(person instanceof DisasterVictim)) {
            throw new IllegalArgumentException("Missing person must be a DisasterVictim");
        }
        DisasterVictim missingPerson = (DisasterVictim) person;

        Location location = locationController.getLocationById(locationId);
        if (location == null) {
            throw new IllegalArgumentException("Location not found");
        }

        return new Inquiry(inquirer, missingPerson, date, info, location);
    }
}