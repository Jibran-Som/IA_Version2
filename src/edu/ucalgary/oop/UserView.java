package edu.ucalgary.oop;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;


public class UserView {

    public static TranslationManager translationManager;
    public static LocationController locationController;
    public static SupplyController supplyController;
    public static MedicalRecordController medicalRecordController;
    public static PersonController personController;




    // Constructor
    public UserView() {
        Scanner scanner = new Scanner(System.in);
        translationManager = TranslationManager.getInstance();

        try {
            System.out.print("Enter language code (or press Enter for default 'en-CA'): ");
            String languageCode = scanner.nextLine().trim();

            // Default if empty
            if (languageCode.isEmpty()) {
                languageCode = "en-CA";
            }

            // Try loading user-specified translation
            translationManager.loadTranslations("data/" + languageCode + ".xml");

        } catch (Exception e) {
            System.err.println("Error loading translations: " + e.getMessage());
            System.err.println("Defaulting to en-CA...");

            try {
                // Fallback to default
                translationManager.loadTranslations("data/en-CA.xml");
            } catch (Exception defaultException) {
                System.err.println("Fatal error: Could not load default translations!");
                throw new RuntimeException("Failed to load any translations", defaultException);
            }
        }

        supplyController = new SupplyController();
        locationController = new LocationController();
        medicalRecordController = new MedicalRecordController();
        personController = new PersonController();


    }




    // Menu
    public static void displayMenuOptions() {
        System.out.println("\nDisaster Victim Information System\n");
        System.out.println("1. Location Details");
        System.out.println("2. Person Details");
        System.out.println("3. Supply Details");
        System.out.println("4. Inquiry Details");
        System.out.println("5. Medical Details");
        System.out.println("0. Exit");
        System.out.print("\nEnter your choice: ");
    }






    // Supply
    public static void displaySupplyDetails() {
        Scanner scanner = new Scanner(System.in);
        boolean stayInMenu = true;

        while (stayInMenu) {
            // Display menu options
            System.out.println("\nSupply Details");
            System.out.println("1. View All Supplies");
            System.out.println("2. Add New Supply");
            System.out.println("3. Update Supply");
            System.out.println("4. Allocate Supply");
            System.out.println("0. Back to Main Menu");
            System.out.print("\nEnter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        viewAllSupplies();
                        break;
                    case 2:
                        addNewSupply();
                        break;
                    case 3:
                        updateSupply();
                        break;
                    case 4:
                        // allocateSupply();
                        break;
                    case 0:
                        stayInMenu = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 0-4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                e.printStackTrace(); // Useful for debugging
            }
        }
    }

    public static void viewAllSupplies() {
        System.out.println("\n--------------------------------------------------");
        System.out.printf("%-8s %-15s %-20s%n", "ID", "TYPE", "COMMENTS");
        System.out.println("--------------------------------------------------");

        // Print each supply with consistent formatting
        for (Supply supply : supplyController.getAllSupplies()) {
            System.out.printf("%-8s %-15s %-20s%n",
                    supply.getSupplyId(),
                    supply.getSupplyType(),
                    supply.getSupplyName());
        }

        System.out.println("--------------------------------------------------");
    }

    public static void addNewSupply() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nAdd New Supply");
        System.out.println("----------------");

        try {
            // Select supply type
            System.out.println("Select supply type:");
            System.out.println("1. Blanket");
            System.out.println("2. Cot");
            System.out.println("3. Personal Belonging");
            System.out.println("4. Water");
            System.out.println("5. Other");
            System.out.println("0. Exit");
            System.out.print("Enter your choice (1-5): ");
            int typeChoice = Integer.parseInt(scanner.nextLine());

            if (typeChoice == 0) {
                System.out.println("Returning to main menu...");
                return;  // This exits the method and returns to the calling menu
            }

            System.out.print("Enter supply name: ");
            String name = scanner.nextLine();

            System.out.print("Enter supply type (general category): ");
            String supplyType = scanner.nextLine();

            Supply newSupply;

            switch (typeChoice) {
                case 1: // Blanket
                    newSupply = new Blanket(name, supplyType);
                    break;

                case 2: // Cot
                    System.out.print("Enter room location: ");
                    String roomLocation = scanner.nextLine();

                    System.out.print("Enter grid location: ");
                    String gridLocation = scanner.nextLine();

                    newSupply = new Cot(name, supplyType, roomLocation, gridLocation);
                    break;

                case 3: // Personal Belonging
                    System.out.print("Enter item description: ");
                    String description = scanner.nextLine();

                    newSupply = new PersonalBelonging(name, supplyType, description);
                    break;

                case 4: // Water
                    newSupply = new Water(name, supplyType);

                    System.out.print("Enter allocation date (YYYY-MM-DD): ");
                    String allocationDate = scanner.nextLine();
                    ((Water)newSupply).setAllocationDate(allocationDate);
                    break;

                default:
                    newSupply = new Supply(name, supplyType);
                    break;

            }

            // Add the supply through the controller
            supplyController.addSupply(newSupply);

            System.out.println("\nSupply added successfully!");
            System.out.println("Type: " + newSupply.getSupplyType());
            System.out.println("Name: " + newSupply.getSupplyName());

            // Display additional info based on type
            if (newSupply instanceof Cot) {
                Cot cot = (Cot)newSupply;
                System.out.println("Room Location: " + cot.getRoomLocation());
                System.out.println("Grid Location: " + cot.getGridLocation());
            }
            else if (newSupply instanceof PersonalBelonging) {
                PersonalBelonging pb = (PersonalBelonging)newSupply;
                System.out.println("Description: " + pb.getItemDescription());
            }
            else if (newSupply instanceof Water) {
                Water water = (Water)newSupply;
                System.out.println("Allocation Date: " + water.getAllocationDate());
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid numeric input. Please enter a valid number.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }


    public static void updateSupply() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nUpdate Supply");
        System.out.println("----------------");

        try {
            // First show all available supplies
            viewAllSupplies();

            System.out.print("\nEnter the ID of the supply to update: ");
            int supplyId = Integer.parseInt(scanner.nextLine());

            // Find the supply to update
            Supply supplyToUpdate = null;
            for (Supply supply : supplyController.getAllSupplies()) {
                if (supply.getSupplyId() == supplyId) {
                    supplyToUpdate = supply;
                    break;
                }
            }

            if (supplyToUpdate == null) {
                System.out.println("No supply found with ID: " + supplyId);
                return;
            }

            // Display current details
            System.out.println("\nCurrent Supply Details:");
            System.out.println("Type: " + supplyToUpdate.getSupplyType());
            System.out.println("Comments: " + supplyToUpdate.getSupplyName());

            // Display type-specific details
            if (supplyToUpdate instanceof Cot) {
                Cot cot = (Cot)supplyToUpdate;
                System.out.println("Room Location: " + cot.getRoomLocation());
                System.out.println("Grid Location: " + cot.getGridLocation());
            }
            else if (supplyToUpdate instanceof PersonalBelonging) {
                PersonalBelonging pb = (PersonalBelonging)supplyToUpdate;
                System.out.println("Description: " + pb.getItemDescription());
            }
            else if (supplyToUpdate instanceof Water) {
                Water water = (Water)supplyToUpdate;
                System.out.println("Allocation Date: " + water.getAllocationDate());
            }

            // Get updated information
            System.out.println("\nEnter new details (leave blank to keep current value):");

            System.out.print("New name [" + supplyToUpdate.getSupplyName() + "]: ");
            String newName = scanner.nextLine();
            if (!newName.isEmpty()) {
                supplyToUpdate.setSupplyName(newName);
            }

            System.out.print("New type [" + supplyToUpdate.getSupplyType() + "]: ");
            String newType = scanner.nextLine();
            if (!newType.isEmpty()) {
                supplyToUpdate.setSupplyType(newType);
            }

            // Handle type-specific updates
            if (supplyToUpdate instanceof Cot) {
                Cot cot = (Cot) supplyToUpdate;

                System.out.print("New room location [" + cot.getRoomLocation() + "]: ");
                String newRoomLocation = scanner.nextLine();
                if (!newRoomLocation.isEmpty()) {
                    cot.setRoomLocation(newRoomLocation);
                }

                System.out.print("New grid location [" + cot.getGridLocation() + "]: ");
                String newGridLocation = scanner.nextLine();
                if (!newGridLocation.isEmpty()) {
                    cot.setGridLocation(newGridLocation);
                }
            }
            else if (supplyToUpdate instanceof PersonalBelonging) {
                PersonalBelonging pb = (PersonalBelonging)supplyToUpdate;

                System.out.print("New description [" + pb.getItemDescription() + "]: ");
                String newDescription = scanner.nextLine();
                if (!newDescription.isEmpty()) {
                    pb.setItemDescription(newDescription);
                }
            }
            else if (supplyToUpdate instanceof Water) {
                Water water = (Water) supplyToUpdate;

                System.out.print("New allocation date [" + water.getAllocationDate() + "]: ");
                String newAllocationDate = scanner.nextLine();
                if (!newAllocationDate.isEmpty()) {
                    water.setAllocationDate(newAllocationDate);
                }
            }

            // Update the supply through the controller
            supplyController.updateSupply(supplyToUpdate);

            System.out.println("\nSupply updated successfully!");

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number for the supply ID.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred while updating the supply: " + e.getMessage());
        }
    }













    // Person













    // Location
    // Location menu functions
    public static void displayLocationDetails() {
        Scanner scanner = new Scanner(System.in);
        boolean stayInMenu = true;

        while (stayInMenu) {
            System.out.println("\nLocation Management");
            System.out.println("1. View All Locations");
            System.out.println("2. Add New Location");
            System.out.println("3. Update Location");
            System.out.println("4. View Location Occupants");
            System.out.println("5. Add Occupant to Location");
            System.out.println("6. Remove Occupant from Location");
            System.out.println("0. Back to Main Menu");
            System.out.print("\nEnter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        viewAllLocations();
                        break;
                    case 2:
                        addNewLocation();
                        break;
                    case 3:
                        updateLocation();
                        break;
                    case 4:
                        viewLocationOccupants();
                        break;
                    case 5:
                        addOccupantToLocation();
                        break;
                    case 6:
                        removeOccupantFromLocation();
                        break;
                    case 0:
                        stayInMenu = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 0-6.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void viewAllLocations() {
        System.out.println("\n--------------------------------------------------");
        System.out.printf("%-8s %-20s %-30s%n", "ID", "NAME", "ADDRESS");
        System.out.println("--------------------------------------------------");

        for (Location location : locationController.getAllLocations()) {
            System.out.printf("%-8s %-20s %-30s%n",
                    location.getLocationId(),
                    location.getLocationName(),
                    location.getLocationAddress());
        }

        System.out.println("--------------------------------------------------");
    }

    public static void addNewLocation() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nAdd New Location");
        System.out.println("----------------");

        try {
            System.out.print("Enter location name: ");
            String name = scanner.nextLine();

            System.out.print("Enter location address: ");
            String address = scanner.nextLine();

            Location newLocation = new Location(name, address);
            locationController.addLocation(newLocation);

            System.out.println("\nLocation added successfully!");
            System.out.println("ID: " + newLocation.getLocationId());
            System.out.println("Name: " + newLocation.getLocationName());
            System.out.println("Address: " + newLocation.getLocationAddress());

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public static void updateLocation() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nUpdate Location");
        System.out.println("----------------");

        try {
            viewAllLocations();

            System.out.print("\nEnter the ID of the location to update: ");
            int locationId = Integer.parseInt(scanner.nextLine());

            // Find the location to update
            Location locationToUpdate = null;
            for (Location location : locationController.getAllLocations()) {
                if (location.getLocationId() == locationId) {
                    locationToUpdate = location;
                    break;
                }
            }

            if (locationToUpdate == null) {
                System.out.println("No location found with ID: " + locationId);
                return;
            }

            System.out.println("\nCurrent Location Details:");
            System.out.println("Name: " + locationToUpdate.getLocationName());
            System.out.println("Address: " + locationToUpdate.getLocationAddress());

            System.out.println("\nEnter new details (leave blank to keep current value):");

            System.out.print("New name [" + locationToUpdate.getLocationName() + "]: ");
            String newName = scanner.nextLine();
            if (!newName.isEmpty()) {
                locationToUpdate.setLocationName(newName);
            }

            System.out.print("New address [" + locationToUpdate.getLocationAddress() + "]: ");
            String newAddress = scanner.nextLine();
            if (!newAddress.isEmpty()) {
                locationToUpdate.setLocationAddress(newAddress);
            }

            locationController.updateLocation(locationToUpdate);
            System.out.println("\nLocation updated successfully!");

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number for the location ID.");
        } catch (Exception e) {
            System.out.println("An error occurred while updating the location: " + e.getMessage());
        }
    }

    public static void viewLocationOccupants() {
        Scanner scanner = new Scanner(System.in);

        try {
            viewAllLocations();
            System.out.print("\nEnter the ID of the location to view occupants: ");
            int locationId = Integer.parseInt(scanner.nextLine());

            ArrayList<Person> occupants = locationController.getOccupantsAtLocation(locationId);

            System.out.println("\nOccupants at Location ID " + locationId + ":");
            System.out.println("--------------------------------------------------");
            System.out.printf("%-8s %-15s %-15s%n", "ID", "FIRST NAME", "LAST NAME");
            System.out.println("--------------------------------------------------");

            for (Person person : occupants) {
                System.out.printf("%-8s %-15s %-15s%n",
                        person.getPersonId(),
                        person.getFirstName(),
                        person.getLastName());
            }

            System.out.println("--------------------------------------------------");

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public static void addOccupantToLocation() {
        Scanner scanner = new Scanner(System.in);

        try {
            viewAllLocations();
            System.out.print("\nEnter the ID of the location: ");
            int locationId = Integer.parseInt(scanner.nextLine());

            // In a real application, you would list available people here
            System.out.print("Enter the ID of the person to add: ");
            int personId = Integer.parseInt(scanner.nextLine());

            locationController.addPersonToLocation(personId, locationId);
            System.out.println("Person added to location successfully!");

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter valid numbers.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public static <List> void removeOccupantFromLocation() {
        Scanner scanner = new Scanner(System.in);

        try {
            viewAllLocations();
            System.out.print("\nEnter the ID of the location: ");
            int locationId = Integer.parseInt(scanner.nextLine());

            // First show current occupants
            ArrayList<Person> occupants = locationController.getOccupantsAtLocation(locationId);
            System.out.println("\nCurrent Occupants:");
            for (Person person : occupants) {
                System.out.println(person.getPersonId() + ": " +
                        person.getFirstName() + " " + person.getLastName());
            }

            System.out.print("Enter the ID of the person to remove: ");
            int personId = Integer.parseInt(scanner.nextLine());

            locationController.removePersonFromLocation(personId, locationId);
            System.out.println("Person removed from location successfully!");

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter valid numbers.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }






    // Medical Record
    // Medical Record menu functions
    public static void displayMedicalDetails() {
        Scanner scanner = new Scanner(System.in);
        boolean stayInMenu = true;

        while (stayInMenu) {
            System.out.println("\nMedical Records Management");
            System.out.println("1. View All Medical Records");
            System.out.println("2. Add New Medical Record");
            System.out.println("3. Update Medical Record");
            System.out.println("4. View Records for Person");
            System.out.println("5. View Records at Location");
            System.out.println("0. Back to Main Menu");
            System.out.print("\nEnter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        viewAllMedicalRecords();
                        break;
                    case 2:
                        addNewMedicalRecord();
                        break;
                    case 3:
                        updateMedicalRecord();
                        break;
                    case 4:
                        viewMedicalRecordsForPerson();
                        break;
                    case 5:
                        viewMedicalRecordsAtLocation();
                        break;
                    case 0:
                        stayInMenu = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 0-5.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void viewAllMedicalRecords() {
        System.out.println("\n--------------------------------------------------");
        System.out.printf("%-8s %-20s %-20s %-15s %-30s%n",
                "ID", "PATIENT", "LOCATION", "DATE", "TREATMENT DETAILS");
        System.out.println("--------------------------------------------------");

        for (MedicalRecord record : medicalRecordController.getAllMedicalRecords()) {
            String patientName = record.getPerson().getFirstName() + " " + record.getPerson().getLastName();
            System.out.printf("%-8s %-20s %-20s %-15s %-30s%n",
                    record.getMedicalRecordId(),
                    patientName,
                    record.getLocation().getLocationName(),
                    record.getDateOfTreatment(),
                    record.getTreatmentDetails());
        }

        System.out.println("--------------------------------------------------");
    }

    // In UserView.java - update addNewMedicalRecord()
    public static void addNewMedicalRecord() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nAdd New Medical Record");
        System.out.println("----------------------");

        try {
            // List available people
            System.out.println("\nAvailable People:");
            // You'll need to implement getAllPeople() in your PersonController
            ArrayList<Person> people = (ArrayList<Person>) personController.getAllPeople();
            for (Person person : people) {
                System.out.println(person.getPersonId() + ": " +
                        person.getFirstName() + " " + person.getLastName());
            }

            System.out.print("\nEnter the ID of the patient: ");
            int personId = Integer.parseInt(scanner.nextLine());

            // Find selected person
            Person selectedPerson = people.stream()
                    .filter(p -> p.getPersonId() == personId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid person ID"));

            // List available locations
            System.out.println("\nAvailable Locations:");
            ArrayList<Location> locations = locationController.getAllLocations();
            for (Location location : locations) {
                System.out.println(location.getLocationId() + ": " +
                        location.getLocationName() + " - " + location.getLocationAddress());
            }

            System.out.print("\nEnter the ID of the location: ");
            int locationId = Integer.parseInt(scanner.nextLine());

            // Find selected location
            Location selectedLocation = locations.stream()
                    .filter(l -> l.getLocationId() == locationId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid location ID"));

            // Get treatment details
            System.out.print("\nEnter treatment details: ");
            String treatmentDetails = scanner.nextLine();

            // Get date
            System.out.print("Enter date of treatment (YYYY-MM-DD): ");
            String dateOfTreatment = scanner.nextLine();

            MedicalRecord newRecord = new MedicalRecord(
                    selectedPerson,
                    selectedLocation,
                    treatmentDetails,
                    dateOfTreatment
            );

            medicalRecordController.addMedicalRecord(newRecord);
            System.out.println("\nMedical record added successfully!");
            System.out.println("Record ID: " + newRecord.getMedicalRecordId());

        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter valid numbers for IDs");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public static void updateMedicalRecord() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nUpdate Medical Record");
        System.out.println("---------------------");

        try {
            // First show all available records
            viewAllMedicalRecords();

            System.out.print("\nEnter the ID of the medical record to update: ");
            int recordId = Integer.parseInt(scanner.nextLine());

            // Find the record to update
            MedicalRecord recordToUpdate = null;
            for (MedicalRecord record : medicalRecordController.getAllMedicalRecords()) {
                if (record.getMedicalRecordId() == recordId) {
                    recordToUpdate = record;
                    break;
                }
            }

            if (recordToUpdate == null) {
                System.out.println("No medical record found with ID: " + recordId);
                return;
            }

            // Display current details
            System.out.println("\nCurrent Medical Record Details:");
            System.out.println("Patient: " + recordToUpdate.getPerson().getFirstName() + " " +
                    recordToUpdate.getPerson().getLastName());
            System.out.println("Location: " + recordToUpdate.getLocation().getLocationName());
            System.out.println("Date: " + recordToUpdate.getDateOfTreatment());
            System.out.println("Treatment Details: " + recordToUpdate.getTreatmentDetails());

            // Get updated information
            System.out.println("\nEnter new details (leave blank to keep current value):");

            // Update patient
            System.out.print("New first name [" + recordToUpdate.getPerson().getFirstName() + "]: ");
            String newFirstName = scanner.nextLine();
            if (!newFirstName.isEmpty()) {
                recordToUpdate.getPerson().setFirstName(newFirstName);
            }

            System.out.print("New last name [" + recordToUpdate.getPerson().getLastName() + "]: ");
            String newLastName = scanner.nextLine();
            if (!newLastName.isEmpty()) {
                recordToUpdate.getPerson().setLastName(newLastName);
            }

            // Update location
            System.out.print("New location name [" + recordToUpdate.getLocation().getLocationName() + "]: ");
            String newLocationName = scanner.nextLine();
            if (!newLocationName.isEmpty()) {
                recordToUpdate.getLocation().setLocationName(newLocationName);
            }

            // Update date
            System.out.print("New treatment date [" + recordToUpdate.getDateOfTreatment() + "]: ");
            String newDate = scanner.nextLine();
            if (!newDate.isEmpty()) {
                recordToUpdate.setDateOfTreatment(newDate);
            }

            // Update treatment details
            System.out.print("New treatment details [" + recordToUpdate.getTreatmentDetails() + "]: ");
            String newDetails = scanner.nextLine();
            if (!newDetails.isEmpty()) {
                recordToUpdate.setTreatmentDetails(newDetails);
            }

            // Update the record through the controller
            medicalRecordController.updateMedicalRecord(recordToUpdate);

            System.out.println("\nMedical record updated successfully!");

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number for the record ID.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred while updating the medical record: " + e.getMessage());
        }
    }

    public static void viewMedicalRecordsForPerson() {
        Scanner scanner = new Scanner(System.in);

        try {
            // In a real app, you would list available people here
            System.out.print("\nEnter the ID of the person: ");
            int personId = Integer.parseInt(scanner.nextLine());

            ArrayList<MedicalRecord> records = medicalRecordController.getMedicalRecordsForPerson(personId);

            System.out.println("\nMedical Records for Person ID " + personId + ":");
            System.out.println("--------------------------------------------------");
            System.out.printf("%-8s %-20s %-15s %-30s%n",
                    "ID", "LOCATION", "DATE", "TREATMENT DETAILS");
            System.out.println("--------------------------------------------------");

            for (MedicalRecord record : records) {
                System.out.printf("%-8s %-20s %-15s %-30s%n",
                        record.getMedicalRecordId(),
                        record.getLocation().getLocationName(),
                        record.getDateOfTreatment(),
                        record.getTreatmentDetails());
            }

            System.out.println("--------------------------------------------------");

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public static void viewMedicalRecordsAtLocation() {
        Scanner scanner = new Scanner(System.in);

        try {
            // First show available locations
            System.out.println("\nAvailable Locations:");
            for (Location location : locationController.getAllLocations()) {
                System.out.println(location.getLocationId() + ": " + location.getLocationName());
            }

            System.out.print("\nEnter the ID of the location: ");
            int locationId = Integer.parseInt(scanner.nextLine());

            ArrayList<MedicalRecord> records = medicalRecordController.getMedicalRecordsAtLocation(locationId);

            System.out.println("\nMedical Records at Location ID " + locationId + ":");
            System.out.println("--------------------------------------------------");
            System.out.printf("%-8s %-20s %-15s %-30s%n",
                    "ID", "PATIENT", "DATE", "TREATMENT DETAILS");
            System.out.println("--------------------------------------------------");

            for (MedicalRecord record : records) {
                String patientName = record.getPerson().getFirstName() + " " + record.getPerson().getLastName();
                System.out.printf("%-8s %-20s %-15s %-30s%n",
                        record.getMedicalRecordId(),
                        patientName,
                        record.getDateOfTreatment(),
                        record.getTreatmentDetails());
            }

            System.out.println("--------------------------------------------------");

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }










    // Person
    // In UserView.java

    // Person Management
    public static void displayPersonDetails() {
        Scanner scanner = new Scanner(System.in);
        boolean stayInMenu = true;

        while (stayInMenu) {
            System.out.println("\nPerson Management");
            System.out.println("1. View All Persons");
            System.out.println("2. Add New Person");
            System.out.println("3. Update Person");
            System.out.println("4. View Disaster Victims");
            System.out.println("5. Add Disaster Victim");
            System.out.println("0. Back to Main Menu");
            System.out.print("\nEnter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        viewAllPersons();
                        break;
                    case 2:
                        addNewPerson();
                        break;
                    case 3:
                        updatePerson();
                        break;
                    case 4:
                        viewDisasterVictims();
                        break;
                    case 5:
                        addDisasterVictim();
                        break;
                    case 0:
                        stayInMenu = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 0-5.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void viewAllPersons() {
        System.out.println("\n--------------------------------------------------");
        System.out.printf("%-8s %-15s %-15s %-12s %-10s %-15s %-10s%n",
                "ID", "FIRST NAME", "LAST NAME", "DOB", "GENDER", "PHONE", "FAMILY GROUP");
        System.out.println("--------------------------------------------------");

        for (Person person : personController.getAllPeople()) {
            System.out.printf("%-8s %-15s %-15s %-12s %-10s %-15s %-10s%n",
                    person.getPersonId(),
                    person.getFirstName(),
                    person.getLastName(),
                    person.getDateOfBirth() != null ? person.getDateOfBirth() : "N/A",
                    person.getGender() != null ? person.getGender() : "N/A",
                    person.getPhoneNumber() != null ? person.getPhoneNumber() : "N/A",
                    person.getFamilyGroup() != null ? person.getFamilyGroup().getFamilyGroupId() : "N/A");
        }

        System.out.println("--------------------------------------------------");
    }

    public static void addNewPerson() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nAdd New Person");
        System.out.println("--------------");

        try {
            System.out.print("First Name: ");
            String firstName = scanner.nextLine();

            System.out.print("Last Name: ");
            String lastName = scanner.nextLine();

            System.out.print("Date of Birth (YYYY-MM-DD, optional): ");
            String dob = scanner.nextLine();

            System.out.print("Gender (optional): ");
            String gender = scanner.nextLine();

            System.out.print("Phone Number (XXX-XXX-XXXX, optional): ");
            String phone = scanner.nextLine();

            System.out.print("Comments (optional): ");
            String comments = scanner.nextLine();

            Person person;
            if (!dob.isEmpty()) {
                person = new Person(firstName, lastName, dob);
            } else {
                person = new Person(firstName, lastName);
            }

            if (!gender.isEmpty()) person.setGender(gender);
            if (!phone.isEmpty()) person.setPhoneNumber(phone);
            if (!comments.isEmpty()) person.setComments(comments);

            personController.addPerson(person);
            System.out.println("\nPerson added successfully! ID: " + person.getPersonId());

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public static void updatePerson() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nUpdate Person");
        System.out.println("-------------");

        try {
            viewAllPersons();
            System.out.print("\nEnter ID of person to update: ");
            int personId = Integer.parseInt(scanner.nextLine());

            Person person = personController.getPersonById(personId);
            if (person == null) {
                System.out.println("No person found with ID: " + personId);
                return;
            }

            System.out.println("\nCurrent Details:");
            System.out.println("1. First Name: " + person.getFirstName());
            System.out.println("2. Last Name: " + person.getLastName());
            System.out.println("3. Date of Birth: " + (person.getDateOfBirth() != null ? person.getDateOfBirth() : "N/A"));
            System.out.println("4. Gender: " + (person.getGender() != null ? person.getGender() : "N/A"));
            System.out.println("5. Phone Number: " + (person.getPhoneNumber() != null ? person.getPhoneNumber() : "N/A"));
            System.out.println("6. Comments: " + (person.getComments() != null ? person.getComments() : "N/A"));

            System.out.print("\nEnter field number to update (1-6) or 0 to cancel: ");
            int field = Integer.parseInt(scanner.nextLine());

            if (field == 0) return;

            System.out.print("Enter new value: ");
            String newValue = scanner.nextLine();

            switch (field) {
                case 1:
                    person.setFirstName(newValue);
                    break;
                case 2:
                    person.setLastName(newValue);
                    break;
                case 3:
                    person.setDateOfBirth(newValue);
                    break;
                case 4:
                    person.setGender(newValue);
                    break;
                case 5:
                    person.setPhoneNumber(newValue);
                    break;
                case 6:
                    person.setComments(newValue);
                    break;
                default:
                    System.out.println("Invalid field number");
                    return;
            }

            personController.updatePerson(person);
            System.out.println("Person updated successfully!");

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    // Disaster Victim specific methods
    public static void viewDisasterVictims() {
        System.out.println("\n--------------------------------------------------");
        System.out.printf("%-8s %-15s %-15s %-12s %-10s %-15s %-10s%n",
                "ID", "FIRST NAME", "LAST NAME", "DOB", "GENDER", "PHONE", "INVENTORY");
        System.out.println("--------------------------------------------------");

        for (Person person : personController.getAllPeople()) {
            if (person instanceof DisasterVictim) {
                DisasterVictim victim = (DisasterVictim) person;
                System.out.printf("%-8s %-15s %-15s %-12s %-10s %-15s %-10s%n",
                        victim.getPersonId(),
                        victim.getFirstName(),
                        victim.getLastName(),
                        victim.getDateOfBirth() != null ? victim.getDateOfBirth() : "N/A",
                        victim.getGender() != null ? victim.getGender() : "N/A",
                        victim.getPhoneNumber() != null ? victim.getPhoneNumber() : "N/A",
                        victim.getPersonalInventory().size());
            }
        }

        System.out.println("--------------------------------------------------");
    }

    public static void addDisasterVictim() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nAdd New Disaster Victim");
        System.out.println("-----------------------");

        try {
            System.out.print("First Name: ");
            String firstName = scanner.nextLine();

            System.out.print("Last Name: ");
            String lastName = scanner.nextLine();

            System.out.print("Date of Birth (YYYY-MM-DD, optional): ");
            String dob = scanner.nextLine();

            DisasterVictim victim;
            if (!dob.isEmpty()) {
                victim = new DisasterVictim(firstName, lastName, dob);
            } else {
                victim = new DisasterVictim(firstName, lastName);
            }

            System.out.print("Gender (optional): ");
            String gender = scanner.nextLine();
            if (!gender.isEmpty()) victim.setGender(gender);

            System.out.print("Phone Number (XXX-XXX-XXXX, optional): ");
            String phone = scanner.nextLine();
            if (!phone.isEmpty()) victim.setPhoneNumber(phone);

            System.out.print("Comments (optional): ");
            String comments = scanner.nextLine();
            if (!comments.isEmpty()) victim.setComments(comments);

            // Add initial supplies
            boolean addMore = true;
            while (addMore) {
                System.out.print("\nAdd supply to inventory? (y/n): ");
                String choice = scanner.nextLine().toLowerCase();

                if (choice.equals("y")) {
                    System.out.print("Supply type: ");
                    String type = scanner.nextLine();

                    System.out.print("Supply name/description: ");
                    String name = scanner.nextLine();

                    Supply supply = new Supply(name, type);
                    victim.addItem(supply);
                    System.out.println("Supply added to inventory.");
                } else {
                    addMore = false;
                }
            }

            personController.addPerson(victim);
            System.out.println("\nDisaster victim added successfully! ID: " + victim.getPersonId());

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }






}
