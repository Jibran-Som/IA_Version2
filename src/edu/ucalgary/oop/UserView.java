/**
 * UserView.java
 * Version: 4.0
 * Author: Jibran Somroo
 * Date: April 9, 2025
 */

package edu.ucalgary.oop;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;


public class UserView {

    private static TranslationManager translationManager;
    private static LocationController locationController;
    private static SupplyController supplyController;
    private static MedicalRecordController medicalRecordController;
    private static PersonController personController;
    private static InquiryController inquiryController;
    private static ErrorLogger errorLogger = ErrorLogger.getInstance();



    /**
     * UserView Constructor
     *
     * This constructor initializes the UserView object and handles loading translations based on the user's input language code.
     *
     * The constructor also initializes various controller objects used throughout the application, including:
     * - SupplyController: Manages supply-related operations
     * - LocationController: Manages location-related operations
     * - MedicalRecordController: Manages medical records
     * - PersonController: Manages person-related operations
     * - InquiryController: Handles user inquiries
     *
     * The translationManager is used to load and manage translation files for different languages.
     */
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
                errorLogger.logFatalError(defaultException, "UserView constructor - loading default translations",
                        "Fatal error: Could not load any translations!");
                System.err.println("Fatal error: Could not load default translations!");
                throw new RuntimeException("Failed to load any translations", defaultException);
            }
        }

        supplyController = new SupplyController();
        locationController = new LocationController();
        medicalRecordController = new MedicalRecordController();
        personController = new PersonController();
        inquiryController = new InquiryController();


    }




    /**
     * Displays the main menu options and handles user input.
     * Also loops until either a proper option is chosen in which
     * case it calls that specific function or if exit is chose it
     * ends the program.
     */
    public static void displayMenuOptions() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n" + translationManager.getTranslation("program_title") + "\n");
            System.out.println("1. " + translationManager.getTranslation("location_detail"));
            System.out.println("2. " + translationManager.getTranslation("person_detail"));
            System.out.println("3. " + translationManager.getTranslation("supply_detail"));
            System.out.println("4. " + translationManager.getTranslation("inquiry_detail"));
            System.out.println("5. " + translationManager.getTranslation("medical_detail"));
            System.out.println("0. " + translationManager.getTranslation("exit"));
            System.out.print("\n" + translationManager.getTranslation("ask_choice") + " ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        displayLocationDetails();
                        break;
                    case 2:
                        displayPersonDetails();
                        break;
                    case 3:
                        displaySupplyDetails();
                        break;
                    case 4:
                        displayInquiryDetails();
                        break;
                    case 5:
                        displayMedicalDetails();
                        break;
                    case 0:
                        exit = true;
                        System.out.println(translationManager.getTranslation("system_exit"));
                        break;
                    default:
                        System.out.println(translationManager.getTranslation("invalid_choice_main_menu"));
                }
            } catch (NumberFormatException e) {
                System.out.println(translationManager.getTranslation("invalid_input"));
            } catch (Exception e) {
                System.out.println(translationManager.getTranslation("error1") + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        scanner.close();
    }





    // Supply


    /**
     * Displays the supply details menu and handles user input for supply-related operations.
     * If the user enters an invalid option or non-numeric input, an appropriate error message is shown.
     */
    public static void displaySupplyDetails() {
        Scanner scanner = new Scanner(System.in);
        boolean stayInMenu = true;

        while (stayInMenu) {
            // Display menu options
            System.out.println("\n" + translationManager.getTranslation("supply_detail"));
            System.out.println("1. " + translationManager.getTranslation("view_all_supply"));
            System.out.println("2. " + translationManager.getTranslation("add_new_supply"));
            System.out.println("3. " + translationManager.getTranslation("update_supply"));
            System.out.println("4. " + translationManager.getTranslation("allocate_supply"));
            System.out.println("5. " + translationManager.getTranslation("view_allocated_supplies"));
            System.out.println(translationManager.getTranslation("back_to_menu"));
            System.out.print("\n" + translationManager.getTranslation("ask_choice") + " ");

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
                        allocateSupply();
                        break;
                    case 5:
                        viewAllocatedSupplies();
                        break;
                    case 0:
                        stayInMenu = false;
                        break;
                    default:
                        System.out.println(translationManager.getTranslation("invalid_choice_supply"));
                }
            } catch (NumberFormatException e) {
                System.out.println(translationManager.getTranslation("invalid_input"));
            } catch (Exception e) {
                System.out.println(translationManager.getTranslation("error1") + ": " + e.getMessage());
                e.printStackTrace(); // Useful for debugging
            }
        }
    }

    /**
     * Displays a list of all supplies with their details in a formatted table.
     * A separator line is printed before and after the list to improve the readability of the output.
     */
    public static void viewAllSupplies() {
        System.out.println("\n-----------------------------------------------------------");
        System.out.printf("%-8s %-15s %-20s%n", translationManager.getTranslation("id"),
                translationManager.getTranslation("type2"), translationManager.getTranslation("alloc_date"));
        System.out.println("-----------------------------------------------------------");

        // Print each supply with consistent formatting
        for (Supply supply : supplyController.getAllSupplies()) {
            if(supply instanceof Water){
                System.out.printf("%-8s %-15s %-20s%n",
                        supply.getSupplyId(),
                        supply.getSupplyType(),
                        ((Water) supply).getAllocationDate());
            }

        }
        System.out.println("-----------------------------------------------------------");


        System.out.println("\n-----------------------------------------------------------");
        System.out.printf("%-8s %-15s %-20s %-20s%n", translationManager.getTranslation("id"),
                translationManager.getTranslation("type2"), translationManager.getTranslation("grid_loc"),
                translationManager.getTranslation("room_loc"));
        System.out.println("-----------------------------------------------------------");

        // Print each supply with consistent formatting
        for (Supply supply : supplyController.getAllSupplies()) {
            if(supply instanceof Cot){
                System.out.printf("%-8s %-15s %-20s %-20s%n",
                        supply.getSupplyId(),
                        supply.getSupplyType(),
                        ((Cot) supply).getGridLocation(),
                        ((Cot) supply).getRoomLocation());
            }

        }
        System.out.println("-----------------------------------------------------------");

        System.out.println("\n-----------------------------------------------------------");
        System.out.printf("%-8s %-15s %-20s%n", translationManager.getTranslation("id"),
                translationManager.getTranslation("type2"), translationManager.getTranslation("comments2"));
        System.out.println("-----------------------------------------------------------");

        // Print each supply with consistent formatting
        for (Supply supply : supplyController.getAllSupplies()) {
            if(supply instanceof PersonalBelonging){
                System.out.printf("%-8s %-15s %-20s%n",
                        supply.getSupplyId(),
                        supply.getSupplyType(),
                        ((PersonalBelonging) supply).getItemDescription());
            }

        }
        System.out.println("-----------------------------------------------------------");

        System.out.println("\n-----------------------------------------------------------");
        System.out.printf("%-8s %-15s %-20s%n", translationManager.getTranslation("id"),
                translationManager.getTranslation("type2"), translationManager.getTranslation("comments2"));
        System.out.println("-----------------------------------------------------------");

        // Print each supply with consistent formatting
        for (Supply supply : supplyController.getAllSupplies()) {
            if(supply instanceof Blanket){
                System.out.printf("%-8s %-15s %-20s%n",
                        supply.getSupplyId(),
                        supply.getSupplyType(),
                        ((Blanket) supply).getSupplyName());
            }

        }
        System.out.println("-----------------------------------------------------------");


        System.out.println("\n-----------------------------------------------------------");
        System.out.printf("%-8s %-15s %-20s%n", translationManager.getTranslation("id"),
                translationManager.getTranslation("type3"),
                translationManager.getTranslation("comments2"));
        System.out.println("-----------------------------------------------------------");

        // Print each supply with consistent formatting
        for (Supply supply : supplyController.getAllSupplies()) {
            if(!(supply instanceof Blanket) && !(supply instanceof PersonalBelonging) && !(supply instanceof Cot) && !(supply instanceof Water)){
                System.out.printf("%-8s %-15s %-20s%n",
                        supply.getSupplyId(),
                        supply.getSupplyType(),
                        supply.getSupplyName());
            }

        }
        System.out.println("-----------------------------------------------------------");



    }


    /**
     * Allows the user to add a new supply to the system by selecting a supply type and entering relevant details.
     *
     * This method provides a menu for the user to select the type of supply they wish to add, including:
     * - Blanket
     * - Cot
     * - Personal Belonging
     * - Water
     * - Other (general supply type)
     *
     * If an error occurs (e.g., invalid input), appropriate error messages are shown.
     */
    public static void addNewSupply() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n" + translationManager.getTranslation("add_new_supply"));
        System.out.println("----------------");

        try {
            // Select supply type
            System.out.println(translationManager.getTranslation("supply_selection_title"));
            System.out.println(translationManager.getTranslation("supply_selection1"));
            System.out.println(translationManager.getTranslation("supply_selection2"));
            System.out.println(translationManager.getTranslation("supply_selection3"));
            System.out.println(translationManager.getTranslation("supply_selection4"));
            System.out.println(translationManager.getTranslation("supply_selection5"));
            System.out.println(translationManager.getTranslation("supply_selection0"));
            System.out.print(translationManager.getTranslation("supply_selection_choices") + " ");
            int typeChoice = Integer.parseInt(scanner.nextLine());

            if (typeChoice == 0) {
                System.out.println(translationManager.getTranslation("supply_selection_return"));
                return;  // This exits the method and returns to the calling menu
            }

            //System.out.print("Enter supply name: ");
            //String name = scanner.nextLine();


            Supply newSupply;

            switch (typeChoice) {
                case 1: // Blanket
                    newSupply = new Blanket(null, "blanket");
                    break;

                case 2: // Cot
                    System.out.print(translationManager.getTranslation("cot_input_grid") + " ");
                    String gridLocation = scanner.nextLine();

                    System.out.print(translationManager.getTranslation("cot_input_room") + " ");
                    String roomLocation = scanner.nextLine();


                    newSupply = new Cot(roomLocation + " " + gridLocation, "cot", roomLocation, gridLocation);
                    break;

                case 3: // Personal Belonging
                    System.out.print(translationManager.getTranslation("personal_item_input_description") + " ");
                    String description = scanner.nextLine();

                    newSupply = new PersonalBelonging(description, "personal item", description);
                    break;

                case 4: // Water
                    newSupply = new Water(null, "water");

                    System.out.print(translationManager.getTranslation("water_input_date") + " ");
                    String allocationDate = scanner.nextLine();
                    if(allocationDate.isEmpty()) {
                        ((Water)newSupply).setAllocationDate(null);
                    }else {
                        ((Water) newSupply).setAllocationDate(allocationDate);
                    }
                    break;

                default:
                    System.out.print(translationManager.getTranslation("general_supply_input_type") + " ");
                    String supplyType = scanner.nextLine();
                    System.out.print(translationManager.getTranslation("general_supply_input_comments") + " ");
                    String name = scanner.nextLine();
                    newSupply = new Supply(name, supplyType);
                    break;

            }

            // Add the supply through the controller
            supplyController.addSupply(newSupply);

            System.out.println("\n" + translationManager.getTranslation("successful_supply_addition"));
            System.out.println(translationManager.getTranslation("type") + ": " + newSupply.getSupplyType());
            System.out.println(translationManager.getTranslation("comments") + ": " + newSupply.getSupplyName());

            // Display additional info based on type
            if (newSupply instanceof Cot) {
                Cot cot = (Cot)newSupply;
                System.out.println(translationManager.getTranslation("room_location") + ": " + cot.getRoomLocation());
                System.out.println(translationManager.getTranslation("grid_location") + ": " + cot.getGridLocation());
            }
            else if (newSupply instanceof PersonalBelonging) {
                PersonalBelonging pb = (PersonalBelonging)newSupply;
                System.out.println("Description" + ": " + pb.getItemDescription());
            }
            else if (newSupply instanceof Water) {
                Water water = (Water)newSupply;
                System.out.println(translationManager.getTranslation("allocation_date") + ": " + water.getAllocationDate());
            }

        } catch (NumberFormatException e) {
            System.out.println(translationManager.getTranslation("invalid_input"));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(translationManager.getTranslation("error1")+": " + e.getMessage());
        }
    }


    /**
     * Displays a menu for viewing allocated supplies based on either a person or a location.
     */
    public static void viewAllocatedSupplies() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("\n" + translationManager.getTranslation("view_allocated_supplies"));
            System.out.println(translationManager.getTranslation("view_person_allocated_supplies"));
            System.out.println(translationManager.getTranslation("view_location_allocated_supplies"));
            System.out.println(translationManager.getTranslation("view_allocated_supplies_return"));
            System.out.print("\n" + translationManager.getTranslation("ask_choice") + " ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    viewSuppliesAllocatedToPerson();
                    break;
                case 2:
                    viewSuppliesAllocatedToLocation();
                    break;
                case 0:
                    return;
                default:
                    System.out.println(translationManager.getTranslation("view_allocated_supplies_invalid"));
            }
        } catch (NumberFormatException e) {
            System.out.println(translationManager.getTranslation("invalid_input"));
        } catch (Exception e) {
            System.out.println(translationManager.getTranslation("error1") + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Displays the list of supplies allocated to a specific person based on their ID.
     *
     * This method first retrieves and displays all available people by listing their IDs and names.
     * The user is then prompted to enter the ID of the person for whom they want to view the allocated supplies.
     *
     * @throws SQLException If there is an error retrieving data from the database.
     */
    private static void viewSuppliesAllocatedToPerson() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        // First show all people
        System.out.println("\n" + translationManager.getTranslation("available_people"));
        for (Person person : personController.getAllPeople()) {
            System.out.println(person.getPersonId() + ": " +
                    person.getFirstName() + " " + person.getLastName());
        }

        System.out.print("\n" + translationManager.getTranslation("enter_person_id") + " ");
        int personId = Integer.parseInt(scanner.nextLine());

        // Get allocated supplies
        ArrayList<Supply> supplies = supplyController.getSuppliesAllocatedTo(personId, null);

        // Display results
        System.out.println("\n" + translationManager.getTranslation("supplies_allocated_person_id") + " " + personId + ":");
        System.out.println("--------------------------------------------------");
        System.out.printf("%-8s %-15s %-20s%n", translationManager.getTranslation("id"),
                translationManager.getTranslation("type2"),
                translationManager.getTranslation("name2"));
        System.out.println("--------------------------------------------------");

        for (Supply supply : supplies) {
            System.out.printf("%-8s %-15s %-20s%n",
                    supply.getSupplyId(),
                    supply.getSupplyType(),
                    supply.getSupplyName());
        }

        System.out.println("--------------------------------------------------");
    }

    /**
     * Displays the list of supplies allocated to a specific location based on its ID.
     *
     * This method first retrieves and displays all available locations by listing their IDs, names, and addresses.
     * The user is then prompted to enter the ID of the location for which they want to view the allocated supplies.
     *
     * @throws SQLException If there is an error retrieving data from the database.
     */
    private static void viewSuppliesAllocatedToLocation() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        // First show all locations
        System.out.println("\n" + translationManager.getTranslation("available_locations"));
        for (Location location : locationController.getAllLocations()) {
            System.out.println(location.getLocationId() + ": " +
                    location.getLocationName() + " - " + location.getLocationAddress());
        }

        System.out.print("\n" + translationManager.getTranslation("enter_location_id") + " ");
        int locationId = Integer.parseInt(scanner.nextLine());

        // Get allocated supplies
        ArrayList<Supply> supplies = supplyController.getSuppliesAllocatedTo(null, locationId);

        // Display results
        System.out.println("\n" + translationManager.getTranslation("supplies_allocated_location_id") + " " + locationId + ":");
        System.out.println("--------------------------------------------------");
        System.out.printf("%-8s %-15s %-20s%n", translationManager.getTranslation("id"),
                translationManager.getTranslation("type2"), translationManager.getTranslation("name2"));
        System.out.println("--------------------------------------------------");

        for (Supply supply : supplies) {
            System.out.printf("%-8s %-15s %-20s%n",
                    supply.getSupplyId(),
                    supply.getSupplyType(),
                    supply.getSupplyName());
        }

        System.out.println("--------------------------------------------------");
    }


    /**
     * Updates the details of an existing supply.
     *
     * The method handles the following types of supplies by various specification:
     * - Cot: Updates room location and grid location.
     * - Personal Belonging: Updates item description.
     * - Water: Updates the allocation date.
     *
     * @throws SQLException If there is an error when interacting with the database.
     */
    public static void updateSupply() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n"+ translationManager.getTranslation("update_supply"));
        System.out.println("----------------");

        try {
            // First show all available supplies
            viewAllSupplies();

            System.out.print("\n" + translationManager.getTranslation("supplies_input_id_update") + " ");
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
                System.out.println(translationManager.getTranslation("supplies_no_found_id") + " " + supplyId);
                return;
            }

            // Display current details
            System.out.println("\n" + translationManager.getTranslation("current_supply_details"));
            System.out.println(translationManager.getTranslation("type") + ": " + supplyToUpdate.getSupplyType());
            System.out.println(translationManager.getTranslation("comments") + ": " + supplyToUpdate.getSupplyName());

            // Display type-specific details
            if (supplyToUpdate instanceof Cot) {
                Cot cot = (Cot)supplyToUpdate;
                System.out.println(translationManager.getTranslation("room_location") + ": " + cot.getRoomLocation());
                System.out.println(translationManager.getTranslation("grid_location") + cot.getGridLocation());
            }
            else if (supplyToUpdate instanceof PersonalBelonging) {
                PersonalBelonging pb = (PersonalBelonging)supplyToUpdate;
                System.out.println(translationManager.getTranslation("description") + ": " + pb.getItemDescription());
            }
            else if (supplyToUpdate instanceof Water) {
                Water water = (Water)supplyToUpdate;
                System.out.println(translationManager.getTranslation("allocation_date") +": " + water.getAllocationDate());
            }

            // Get updated information
            System.out.println("\n" + translationManager.getTranslation("supply_new_details"));

            System.out.print(translationManager.getTranslation("new_name") + " [" + supplyToUpdate.getSupplyName() + "]: ");
            String newName = scanner.nextLine();
            if (!newName.isEmpty()) {
                supplyToUpdate.setSupplyName(newName);
            }

            System.out.print(translationManager.getTranslation("new_type") + " [" + supplyToUpdate.getSupplyType() + "]: ");
            String newType = scanner.nextLine();
            if (!newType.isEmpty()) {
                supplyToUpdate.setSupplyType(newType);
            }

            // Handle type-specific updates
            if (supplyToUpdate instanceof Cot) {
                Cot cot = (Cot) supplyToUpdate;

                System.out.print(translationManager.getTranslation("new") + " " +
                        translationManager.getTranslation("room_location") + " [" + cot.getRoomLocation() + "]: ");
                String newRoomLocation = scanner.nextLine();
                if (!newRoomLocation.isEmpty()) {
                    cot.setRoomLocation(newRoomLocation);
                }

                System.out.print(translationManager.getTranslation("new") + " " +
                        translationManager.getTranslation("grid_location") + " [" + cot.getGridLocation() + "]: ");
                String newGridLocation = scanner.nextLine();
                if (!newGridLocation.isEmpty()) {
                    cot.setGridLocation(newGridLocation);
                }
            }
            else if (supplyToUpdate instanceof PersonalBelonging) {
                PersonalBelonging pb = (PersonalBelonging)supplyToUpdate;

                System.out.print(translationManager.getTranslation("new_desc") + " [" + pb.getItemDescription() + "]: ");
                String newDescription = scanner.nextLine();
                if (!newDescription.isEmpty()) {
                    pb.setItemDescription(newDescription);
                }
            }
            else if (supplyToUpdate instanceof Water) {
                Water water = (Water) supplyToUpdate;

                System.out.print(translationManager.getTranslation("new_alloc") + " [" + water.getAllocationDate() + "]: ");
                String newAllocationDate = scanner.nextLine();
                if (!newAllocationDate.isEmpty()) {
                    water.setAllocationDate(newAllocationDate);
                }
            }

            // Update the supply through the controller
            supplyController.updateSupply(supplyToUpdate);

            System.out.println("\n" + translationManager.getTranslation("supply_update_success"));

        } catch (NumberFormatException e) {
            System.out.println(translationManager.getTranslation("supply_invalid_input"));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(translationManager.getTranslation("error.userViewUpdatingSupply") + ": " + e.getMessage());
        }
    }


    /**
     * Allocates a supply to a person, location, or a person at a specific location.
     *
     * This method allows the user to allocate available supplies in three different ways:
     * 1. Allocate a supply to a person.
     * 2. Allocate a supply to a location.
     * 3. Allocate a supply to a person at a specific location (from the location's supplies).
     *
     * The method works as follows:
     * - First, the user is prompted to choose the allocation type.
     * - The method displays the available unallocated supplies.
     * - The user selects a supply to allocate
     * - If allocating to a person or location, the supply is allocated
     * - If allocating a supply to a person at a specific location, the supply is moved from the location's inventory to the person's inventory.
     *
     * This method also includes specific handling for Water supplies where the allocation date is required.
     *
     * @throws SQLException If a database error occurs during the allocation process.
     * @throws NumberFormatException If the user inputs invalid Ids
     * @throws Exception If any other error occurs during the allocation process.
     */

    public static void allocateSupply() {
        Scanner scanner = new Scanner(System.in);

        try {
            // Ask allocation type first
            System.out.println("\n" + translationManager.getTranslation("allocate_to"));
            System.out.println(translationManager.getTranslation("allocate_to1"));
            System.out.println(translationManager.getTranslation("allocate_to2"));
            System.out.println(translationManager.getTranslation("allocate_to3"));
            System.out.print(translationManager.getTranslation("allocate_to_choice") + " ");
            int allocationChoice = Integer.parseInt(scanner.nextLine());

            if (allocationChoice < 1 || allocationChoice > 3) {
                System.out.println(translationManager.getTranslation("allocate_to_invalid_choice"));
                return;
            }

            if (allocationChoice == 1) {
                // Option 1: Allocate to person (original code)
                System.out.println("\n" + translationManager.getTranslation("available_supplies"));
                System.out.println("\n" + translationManager.getTranslation("available_supplies1"));
                ArrayList<Supply> availableSupplies = new ArrayList<>();
                for (Supply supply : supplyController.getAllSupplies()) {
                    if (!supplyController.isSupplyAllocated(supply.getSupplyId())) {
                        availableSupplies.add(supply);
                        System.out.printf("%-8s %-15s %-20s%n",
                                supply.getSupplyId(),
                                supply.getSupplyType(),
                                supply.getSupplyName());
                    }
                }
                if (availableSupplies.isEmpty()) {
                    System.out.println(translationManager.getTranslation("available_supplies_empty"));
                    return;
                }

                System.out.print("\n" + translationManager.getTranslation("input_supply_id_to_allocate") + " ");
                int supplyId = Integer.parseInt(scanner.nextLine());

                Supply supplyToAllocate = null;
                for (Supply supply : supplyController.getAllSupplies()) {
                    if (supply.getSupplyId() == supplyId) {
                        supplyToAllocate = supply;
                        break;
                    }
                }

                if (supplyToAllocate == null) {
                    System.out.println(translationManager.getTranslation("supply_id_not_found") + " " + supplyId);
                    return;
                }

                viewDisasterVictims();
                System.out.print("\n" + translationManager.getTranslation("input_person_id_for_allocation") + " ");
                int personId = Integer.parseInt(scanner.nextLine());

                Person person = personController.getPersonById(personId);
                if (person == null) {
                    System.out.println(translationManager.getTranslation("error.userViewNoPersonIdFound") + ": " + personId);
                    return;
                }

                if(!(person instanceof DisasterVictim)){
                    System.out.println(translationManager.getTranslation("error.userViewPersonNotDisasterVictim"));
                    return;
                }

                if (supplyToAllocate instanceof Water) {
                    System.out.print(translationManager.getTranslation("input_allocation_date") + " ");
                    String allocationDate = scanner.nextLine();
                    ((Water)supplyToAllocate).setAllocationDate(allocationDate);
                }

                supplyController.allocateSupply(supplyToAllocate.getSupplyId(), personId, null, null);

                if (person instanceof DisasterVictim) {
                    ((DisasterVictim)person).addItem(supplyToAllocate);
                }

                System.out.println(translationManager.getTranslation("successful_supply_allocation"));

            } else if (allocationChoice == 2) {
                // Option 2: Allocate to location (original code)
                System.out.println("\n" + translationManager.getTranslation("available_supplies1"));
                ArrayList<Supply> availableSupplies = new ArrayList<>();
                for (Supply supply : supplyController.getAllSupplies()) {
                    if (!supplyController.isSupplyAllocated(supply.getSupplyId())) {
                        availableSupplies.add(supply);
                        System.out.printf("%-8s %-15s %-20s%n",
                                supply.getSupplyId(),
                                supply.getSupplyType(),
                                supply.getSupplyName());
                    }
                }
                if (availableSupplies.isEmpty()) {
                    System.out.println(translationManager.getTranslation("available_supplies_empty"));
                    return;
                }

                System.out.print("\n" + translationManager.getTranslation("input_supply_allocation_id") + " ");
                int supplyId = Integer.parseInt(scanner.nextLine());

                Supply supplyToAllocate = null;
                for (Supply supply : availableSupplies) {
                    if (supply.getSupplyId() == supplyId) {
                        supplyToAllocate = supply;
                        break;
                    }
                }

                if (supplyToAllocate == null) {
                    System.out.println(translationManager.getTranslation("supplies_no_found_id") + " " + supplyId);
                    return;
                }

                viewAllLocations();
                System.out.print("\n" + translationManager.getTranslation("input_allocation_location_id") + " ");
                int locationId = Integer.parseInt(scanner.nextLine());

                Location location = locationController.getLocationById(locationId);
                if (location == null) {
                    System.out.println(translationManager.getTranslation("error.userViewNoLocationIdFound") + ": " + locationId);
                    return;
                }

                if (supplyToAllocate instanceof Water) {
                    System.out.print(translationManager.getTranslation("input_allocation_date") + " ");
                    String allocationDate = scanner.nextLine();
                    ((Water)supplyToAllocate).setAllocationDate(allocationDate);
                }

                supplyController.allocateSupply(supplyToAllocate.getSupplyId(), null, locationId, null);
                location.addItem(supplyToAllocate);

                System.out.println(translationManager.getTranslation("successful_supply_to_location_allocation"));

            } else if (allocationChoice == 3) {
                // Option 3: Allocate to person at specific location (from location's supplies)
                System.out.println("\n" + translationManager.getTranslation("available_locations"));
                viewAllLocations();

                System.out.print("\n" + translationManager.getTranslation("enter_location_id") + " ");
                int locationId = Integer.parseInt(scanner.nextLine());

                Location location = locationController.getLocationById(locationId);
                if (location == null) {
                    System.out.println(translationManager.getTranslation("error.userViewNoLocationIdFound") + ": " + locationId);
                    return;
                }

                // Show supplies at this location
                System.out.println("\n" + translationManager.getTranslation("supplies_available_at_location"));
                ArrayList<Supply> locationSupplies = locationController.getSuppliesAtLocation(locationId);
                if (locationSupplies.isEmpty()) {
                    System.out.println(translationManager.getTranslation("no_supplies_available_at_location"));
                    return;
                }

                for (Supply supply : locationSupplies) {
                    System.out.printf("%-8s %-15s %-20s%n",
                            supply.getSupplyId(),
                            supply.getSupplyType(),
                            supply.getSupplyName());
                }

                System.out.print("\n" + translationManager.getTranslation("input_supply_id_to_allocate") + " ");
                int supplyId = Integer.parseInt(scanner.nextLine());

                // Find the supply at this location
                Supply supplyToAllocate = null;
                for (Supply supply : locationSupplies) {
                    if (supply.getSupplyId() == supplyId) {
                        supplyToAllocate = supply;
                        break;
                    }
                }

                if (supplyToAllocate == null) {
                    System.out.println("No supply found with ID " + supplyId + " at this location.");
                    return;
                }

                // Show occupants at this location
                System.out.println("\n" + translationManager.getTranslation("occupants_at_location"));
                ArrayList<Person> occupants = locationController.getOccupantsAtLocation(locationId);
                if (occupants.isEmpty()) {
                    System.out.println(translationManager.getTranslation("no_occupants_at_location"));
                    return;
                }

                for (Person person : occupants) {
                    System.out.println(person.getPersonId() + ": " +
                            person.getFirstName() + " " + person.getLastName());
                }

                System.out.print("\n" + translationManager.getTranslation("input_person_id_for_allocation") + " ");
                int personId = Integer.parseInt(scanner.nextLine());

                // Verify person is at this location
                boolean personFound = false;
                for (Person person : occupants) {
                    if (person.getPersonId() == personId) {
                        personFound = true;
                        break;
                    }
                }

                if (!personFound) {
                    System.out.println("Person with ID " + personId + " is not at this location.");
                    return;
                }

                // Special handling for Water
                if (supplyToAllocate instanceof Water) {
                    System.out.print(translationManager.getTranslation("input_allocation_date") + " ");
                    String allocationDate = scanner.nextLine();
                    ((Water)supplyToAllocate).setAllocationDate(allocationDate);
                }

                // Perform the allocation
                supplyController.allocateSupply(supplyToAllocate.getSupplyId(), personId, null, locationId);

                // Add to person's inventory if they're a DisasterVictim
                Person person = personController.getPersonById(personId);
                if (person instanceof DisasterVictim) {
                    ((DisasterVictim)person).addItem(supplyToAllocate);
                }

                // Remove from location's supplies
                location.removeItem(supplyToAllocate);

                System.out.println(translationManager.getTranslation("successful_supply_from_location_allocation_to_person"));
            }

        } catch (NumberFormatException e) {
            System.out.println(translationManager.getTranslation("invalid_input"));
        } catch (SQLException e) {
            System.out.println("Database error during allocation: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(translationManager.getTranslation("error1") + ": " + e.getMessage());
        }
    }














    // Person













    // Location
    /**
     * Displays the location management menu and handles user input for location-related operations.
     *
     * This method presents a menu to the user with options to:
     * 1. View all locations.
     * 2. Add a new location.
     * 3. Update an existing location.
     * 4. View the occupants of a location.
     * 5. Add an occupant to a location.
     * 6. Remove an occupant from a location.
     * 0. Exit back to the main menu.
     *
     * It allows the user to interact with the location management system, providing the ability
     * to manage locations and their respective occupants. The method keeps looping the menu
     * until the user chooses to exit by selecting the '0' option.
     *
     * @throws NumberFormatException If the user enters an invalid number when prompted for a choice.
     * @throws Exception If any other error occurs during the operation.
     */
    public static void displayLocationDetails() {
        Scanner scanner = new Scanner(System.in);
        boolean stayInMenu = true;

        while (stayInMenu) {
            System.out.println("\n" + translationManager.getTranslation("location_management"));
            System.out.println(translationManager.getTranslation("location_management1"));
            System.out.println(translationManager.getTranslation("location_management2"));
            System.out.println(translationManager.getTranslation("location_management3"));
            System.out.println(translationManager.getTranslation("location_management4"));
            System.out.println(translationManager.getTranslation("location_management5"));
            System.out.println(translationManager.getTranslation("location_management6"));
            System.out.println(translationManager.getTranslation("location_management7"));
            System.out.print("\n" + translationManager.getTranslation("ask_choice") + " ");

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
                        System.out.println(translationManager.getTranslation("location_management8"));
                }
            } catch (NumberFormatException e) {
                System.out.println(translationManager.getTranslation("invalid_input"));
            } catch (Exception e) {
                System.out.println(translationManager.getTranslation("error1") + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    /**
     * Displays all locations in the system in a formatted table.
     * The table shows the ID, Name, and Address of each location.
     */
    public static void viewAllLocations() {
        System.out.println("\n------------------------------------------------------------");
        System.out.printf("%-8s %-25s %-30s%n", translationManager.getTranslation("id"),
                translationManager.getTranslation("name2"),
                translationManager.getTranslation("address2"));
        System.out.println("------------------------------------------------------------");

        for (Location location : locationController.getAllLocations()) {
            System.out.printf("%-8s %-25s %-30s%n",
                    location.getLocationId(),
                    location.getLocationName(),
                    location.getLocationAddress());
        }

        System.out.println("------------------------------------------------------------");
    }

    /**
     * Prompts the user to input a new location's name and address, then adds the location to the system.
     * Displays a success message along with the details of the newly added location.
     *
     * @throws IllegalArgumentException if the input is invalid
     */
    public static void addNewLocation() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n" + translationManager.getTranslation("add_new_location_title"));
        System.out.println("----------------");

        try {
            System.out.print(translationManager.getTranslation("input_location_name")+ ": ");
            String name = scanner.nextLine();

            System.out.print(translationManager.getTranslation("input_location_address") + ": ");
            String address = scanner.nextLine();

            Location newLocation = new Location(name, address);
            locationController.addLocation(newLocation);

            System.out.println("\n" + translationManager.getTranslation("successful_location_addition"));
            System.out.println("ID: " + newLocation.getLocationId());
            System.out.println("Name: " + newLocation.getLocationName());
            System.out.println("Address: " + newLocation.getLocationAddress());

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(translationManager.getTranslation("error1") + ": " + e.getMessage());
        }
    }

    /**
     * Allows the user to update an existing location's details, such as name and address.
     * The user is prompted to select a location by ID, and then they can update the name and address.
     *
     * @throws NumberFormatException if the input for the location ID is not a valid number.
     */
    public static void updateLocation() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n" + translationManager.getTranslation("update_location_title"));
        System.out.println("----------------");

        try {
            viewAllLocations();

            System.out.print("\n" + translationManager.getTranslation("input_location_id_to_update") + " ");
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
                System.out.println(translationManager.getTranslation("error.userViewNoLocationIdFound")   + ": " + locationId);
                return;
            }

            System.out.println("\n" + translationManager.getTranslation("current_location_details"));
            System.out.println("Name: " + locationToUpdate.getLocationName());
            System.out.println("Address: " + locationToUpdate.getLocationAddress());

            System.out.println("\n" + translationManager.getTranslation("supply_new_details"));

            System.out.print(translationManager.getTranslation("new_name") + " [" + locationToUpdate.getLocationName() + "]: ");
            String newName = scanner.nextLine();
            if (!newName.isEmpty()) {
                locationToUpdate.setLocationName(newName);
            }

            System.out.print(translationManager.getTranslation("new_address")+ " [" + locationToUpdate.getLocationAddress() + "]: ");
            String newAddress = scanner.nextLine();
            if (!newAddress.isEmpty()) {
                locationToUpdate.setLocationAddress(newAddress);
            }

            locationController.updateLocation(locationToUpdate);
            System.out.println("\n" + translationManager.getTranslation("successful_location_update"));

        } catch (NumberFormatException e) {
            System.out.println(translationManager.getTranslation("invalid_input_for_location_update"));
        } catch (Exception e) {
            System.out.println("An error occurred while updating the location: " + e.getMessage());
        }
    }

    /**
     * Displays a list of occupants at a specific location.
     * The user is prompted to enter the location ID, and if the location is found,
     * the details of its occupants (ID, first name, and last name) are shown.
     *
     * @throws NumberFormatException if the input for the location ID is not a valid number.
     */
    public static void viewLocationOccupants() {
        Scanner scanner = new Scanner(System.in);

        try {
            viewAllLocations();
            System.out.print("\n" + translationManager.getTranslation("input_location_id_for_occupants_view") + " ");
            int locationId = Integer.parseInt(scanner.nextLine());

            ArrayList<Person> occupants = locationController.getOccupantsAtLocation(locationId);
            Location locationTest = locationController.getLocationById(locationId);

            if (locationTest == null) {
                System.out.println(translationManager.getTranslation("error.userViewNoLocationIdFound") + ": " + locationId);
            }
            else {
                System.out.println("\n" + translationManager.getTranslation("occupants_at_location") + " " + locationId + ":");
                System.out.println("--------------------------------------------------");
                System.out.printf("%-8s %-15s %-15s%n", translationManager.getTranslation("id"),
                        translationManager.getTranslation("first_name"),
                        translationManager.getTranslation("last_name"));
                System.out.println("--------------------------------------------------");

                for (Person person : occupants) {
                    System.out.printf("%-8s %-15s %-15s%n",
                            person.getPersonId(),
                            person.getFirstName(),
                            person.getLastName());
                }

                System.out.println("--------------------------------------------------");
            }
            } catch (NumberFormatException e) {
                System.out.println(translationManager.getTranslation("invalid_input"));
            } catch (Exception e) {
                System.out.println(translationManager.getTranslation("error1") + ": " + e.getMessage());
            }

    }

    /**
     * Adds a person to a specific location.
     * The user is prompted to select a location and a person who is not currently assigned to any location.
     * Once the person and location are selected, the person is added to the location.
     *
     * @throws NumberFormatException if the input for the location ID or person ID is not a valid number.
     */
    public static void addOccupantToLocation() {
        Scanner scanner = new Scanner(System.in);

        try {
            viewAllLocations();
            System.out.print("\n" + translationManager.getTranslation("enter_location_id") + " ");
            int locationId = Integer.parseInt(scanner.nextLine());

            viewPeopleNotAtAnyLocation();
            System.out.print(translationManager.getTranslation("input_person_id_for_adding") + " ");
            int personId = Integer.parseInt(scanner.nextLine());

            locationController.addPersonToLocation(personId, locationId);
            System.out.println(translationManager.getTranslation("successful_person_to_location_addition"));

        } catch (NumberFormatException e) {
            System.out.println(translationManager.getTranslation("invalid_input"));
        } catch (Exception e) {
            System.out.println(translationManager.getTranslation("error1") + ": " + e.getMessage());
        }
    }

    /**
     * Displays a list of people who are not assigned to any location.
     * This method retrieves all people and checks their location status.
     * If a person is not assigned to any location, they are added to the result list,
     * which is then displayed to the user.
     */
    public static void viewPeopleNotAtAnyLocation() {
        try {
            // Get all people
            ArrayList<Person> allPeople = personController.getAllPeople();
            ArrayList<Person> peopleNotAtLocation = new ArrayList<>();

            // Check each person's location status
            for (Person person : allPeople) {
                boolean isAtLocation = false;

                // Check all locations to see if this person is at any of them
                for (Location location : locationController.getAllLocations()) {
                    ArrayList<Person> occupants = locationController.getOccupantsAtLocation(location.getLocationId());
                    if (occupants != null && occupants.contains(person)) {
                        isAtLocation = true;
                        break;
                    }
                }

                if (!isAtLocation) {
                    peopleNotAtLocation.add(person);
                }
            }

            // Display results
            if (peopleNotAtLocation.isEmpty()) {
                System.out.println(translationManager.getTranslation("all_people_assigned_to_location"));
            } else {
                System.out.println("\n" + translationManager.getTranslation("people_not_assigned_to_location"));
                System.out.println("------------------------------------");
                for (Person person : peopleNotAtLocation) {
                    System.out.printf("ID: %d, Name: %s %s%n",
                            person.getPersonId(),
                            person.getFirstName(),
                            person.getLastName());
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving data: " + e.getMessage());
        }
    }

    /**
     * Removes a person from a specific location.
     *
     * @throws NumberFormatException if the input for location ID or person ID is not a valid number.
     */
    public static void removeOccupantFromLocation() {
        Scanner scanner = new Scanner(System.in);

        try {
            viewAllLocations();
            System.out.print("\n" + translationManager.getTranslation("enter_location_id") + " ");
            int locationId = Integer.parseInt(scanner.nextLine());

            // First show current occupants
            ArrayList<Person> occupants = locationController.getOccupantsAtLocation(locationId);
            System.out.println("\n" + translationManager.getTranslation("current_occupants_title"));
            for (Person person : occupants) {
                System.out.println(person.getPersonId() + ": " +
                        person.getFirstName() + " " + person.getLastName());
            }

            System.out.print(translationManager.getTranslation("input_person_id_for_location_removal") + " ");
            int personId = Integer.parseInt(scanner.nextLine());

            locationController.removePersonFromLocation(personId, locationId);
            System.out.println(translationManager.getTranslation("successful_person_location_removal"));

        } catch (NumberFormatException e) {
            System.out.println(translationManager.getTranslation("invalid_input"));
        } catch (Exception e) {
            System.out.println(translationManager.getTranslation("error1") + ": " + e.getMessage());
        }
    }






    /**
     * Displays the menu for managing medical records and allows the user to select different operations.
     *
     * This method provides an interactive menu for users to:
     * 1. View all medical records
     * 2. Add a new medical record
     * 3. Update an existing medical record

     *
     * @throws NumberFormatException if the user enters an invalid number when selecting an option.
     */
    public static void displayMedicalDetails() {
        Scanner scanner = new Scanner(System.in);
        boolean stayInMenu = true;

        while (stayInMenu) {
            System.out.println("\n" + translationManager.getTranslation("medical_title"));
            System.out.println(translationManager.getTranslation("view_all_medical_records"));
            System.out.println(translationManager.getTranslation("add_new_medical_record"));
            System.out.println(translationManager.getTranslation("update_medical_record"));
            System.out.println(translationManager.getTranslation("back_to_menu"));
            System.out.print("\n" + translationManager.getTranslation("ask_choice") + " ");

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
                    case 0:
                        stayInMenu = false;
                        break;
                    default:
                        System.out.println(translationManager.getTranslation("invalid_choice_medical"));
                }
            } catch (NumberFormatException e) {
                System.out.println(translationManager.getTranslation("invalid_input"));
            } catch (Exception e) {
                System.out.println(translationManager.getTranslation("error1") + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Displays all medical records in a formatted table.
     *
     * This method retrieves all medical records from the controller and displays them with the following details:
     * - ID of the medical record
     * - Patient's full name
     * - Location where the treatment took place
     * - Date of treatment
     * - Details of the treatment
     */
    public static void viewAllMedicalRecords() {
        System.out.println("\n----------------------------------------------------------------------------------------------------");
        System.out.printf("%-8s %-25s %-25s %-15s %-30s%n",
                translationManager.getTranslation("id"),
                translationManager.getTranslation("patient2"),
                translationManager.getTranslation("loc2"),
                translationManager.getTranslation("date2"),
                translationManager.getTranslation("treat_details"));
        System.out.println("----------------------------------------------------------------------------------------------------");

        for (MedicalRecord record : medicalRecordController.getAllMedicalRecords()) {
            String patientName = record.getPerson().getFirstName() + " " + record.getPerson().getLastName();
            System.out.printf("%-8s %-25s %-25s %-15s %-30s%n",
                    record.getMedicalRecordId(),
                    patientName,
                    record.getLocation().getLocationName(),
                    record.getDateOfTreatment(),
                    record.getTreatmentDetails());
        }

        System.out.println("----------------------------------------------------------------------------------------------------");
    }

    /**
     * Adds a new medical record to the system.
     *
     * This method allows the user to input details for a new medical record, including:
     * - The patient (person) associated with the medical record.
     * - The location where the treatment took place.
     * - The treatment details.
     * - The date of treatment.
     *
     * The user will be prompted to choose a patient from the available list of people and a location
     * from the available list of locations. Once the details are provided, a new medical record is
     * created and added to the system.
     *
     * @throws NumberFormatException if an invalid number is entered for IDs.
     * @throws IllegalArgumentException if the selected person or location is invalid.
     */
    public static void addNewMedicalRecord() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n" + translationManager.getTranslation("Add New Medical Record_title"));
        System.out.println("----------------------");

        try {
            // List available people
            System.out.println("\n" + translationManager.getTranslation("available_people"));
            ArrayList<Person> people = personController.getAllPeople();
            for (Person person : people) {
                System.out.println(person.getPersonId() + ": " +
                        person.getFirstName() + " " + person.getLastName());
            }

            System.out.print("\n" + translationManager.getTranslation("input_patient_id") + " ");
            int personId = Integer.parseInt(scanner.nextLine());

            // Find selected person
            Person selectedPerson = people.stream()
                    .filter(p -> p.getPersonId() == personId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid person ID"));

            // List available locations
            System.out.println("\n" + translationManager.getTranslation("available_locations"));
            ArrayList<Location> locations = locationController.getAllLocations();
            for (Location location : locations) {
                System.out.println(location.getLocationId() + ": " +
                        location.getLocationName() + " - " + location.getLocationAddress());
            }

            System.out.print("\n" + translationManager.getTranslation("enter_location_id") + " ");
            int locationId = Integer.parseInt(scanner.nextLine());

            // Find selected location
            Location selectedLocation = locations.stream()
                    .filter(l -> l.getLocationId() == locationId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(translationManager.getTranslation("invalid_location_id_mr")));

            // Get treatment details
            System.out.print("\n" + translationManager.getTranslation("input_treatment_details") + " ");
            String treatmentDetails = scanner.nextLine();

            // Get date
            System.out.print(translationManager.getTranslation("input_treatment_date") + " ");
            String dateOfTreatment = scanner.nextLine();

            MedicalRecord newRecord = new MedicalRecord(
                    selectedPerson,
                    selectedLocation,
                    treatmentDetails,
                    dateOfTreatment
            );

            medicalRecordController.addMedicalRecord(newRecord);
            System.out.println("\n" + translationManager.getTranslation("successful_medical_record_addition"));
            System.out.println("Record ID:" + " " + newRecord.getMedicalRecordId());

        } catch (NumberFormatException e) {
            System.out.println(translationManager.getTranslation("invalid_input"));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(translationManager.getTranslation("error1") + ": " + e.getMessage());
        }
    }


    /**
     * Updates an existing medical record.
     *
     * This method allows the user to update an existing medical record. The user can modify the following
     * details of the medical record:
     * - Patient's first and last name.
     * - Location name.
     * - Date of treatment.
     * - Treatment details.
     *
     * @throws NumberFormatException if an invalid number is entered for the record ID.
     * @throws IllegalArgumentException if an invalid input is provided for the details being updated.
     */
    public static void updateMedicalRecord() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n" + translationManager.getTranslation("update_medical_record_title"));
        System.out.println("---------------------");

        try {
            // First show all available records
            viewAllMedicalRecords();

            System.out.print("\n" + translationManager.getTranslation("input_mr_to_update_id") + " ");
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
                System.out.println(translationManager.getTranslation("no_mr_id_found") + " " + recordId);
                return;
            }

            // Display current details
            System.out.println("\n" + translationManager.getTranslation("current_mr_details_title"));
            System.out.println("Patient: " + recordToUpdate.getPerson().getFirstName() + " " +
                    recordToUpdate.getPerson().getLastName());
            System.out.println("Location: " + recordToUpdate.getLocation().getLocationName());
            System.out.println("Date: " + recordToUpdate.getDateOfTreatment());
            System.out.println("Treatment Details: " + recordToUpdate.getTreatmentDetails());

            // Get updated information
            System.out.println("\n" + translationManager.getTranslation("supply_new_details"));


            viewAllLocations();
            System.out.print("New location id [" + recordToUpdate.getLocation().getLocationName() + "]: ");
            String newLocationId = scanner.nextLine();
            if (!newLocationId.isEmpty()) {
                recordToUpdate.setLocation(locationController.getLocationById(Integer.parseInt(newLocationId)));
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
            System.out.println(translationManager.getTranslation("invalid_input"));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred while updating the medical record: " + e.getMessage());
        }
    }


    // Person
    // In UserView.java

    /**
     * Displays a menu for managing person details and handles user input.
     *
     * The following options are available:
     * 1. View All Persons - Display a list of all persons.
     * 2. Add New Person - Add a new person to the system.
     * 3. Update Person - Update details of an existing person.
     * 4. View Disaster Victims - Display a list of disaster victims.
     * 5. Add Disaster Victim - Add a new disaster victim.
     * 6. Convert to Disaster Victim - Convert an existing person to a disaster victim.
     * 7. Add/Change Family Group - Assign or change the family group for a person.
     * 0. Back to Main Menu - Exit the person management menu and return to the main menu.
     *
     * If the user enters an invalid choice or input, an error message is displayed.
     *
     * @throws NumberFormatException if the user enters an invalid number when selecting a menu option.
     */
    public static void displayPersonDetails() {
        Scanner scanner = new Scanner(System.in);
        boolean stayInMenu = true;

        while (stayInMenu) {
            System.out.println("\n" + translationManager.getTranslation("person_management"));
            System.out.println(translationManager.getTranslation("view_all_persons"));
            System.out.println(translationManager.getTranslation("add_new_person"));
            System.out.println(translationManager.getTranslation("update_person"));
            System.out.println(translationManager.getTranslation("view_disaster_victims"));
            System.out.println(translationManager.getTranslation("add_disaster_victim"));
            System.out.println(translationManager.getTranslation("convert_to_disaster_victim"));
            System.out.println(translationManager.getTranslation("add_change_family_group"));
            System.out.println(translationManager.getTranslation("back_to_menu"));
            System.out.print("\n" + translationManager.getTranslation("ask_choice") + " ");

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
                    case 6:
                        convertToDisasterVictim();
                        break;
                    case 7:
                        changePersonFamilyGroup();
                        break;
                    case 0:
                        stayInMenu = false;
                        break;
                    default:
                        System.out.println(translationManager.getTranslation("invalid_choice_7"));
                }
            } catch (NumberFormatException e) {
                System.out.println(translationManager.getTranslation("invalid_input"));
            } catch (Exception e) {
                System.out.println(translationManager.getTranslation("error1") + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Displays a list of all persons in the system along with their details.
     *
     * This method prints a table containing the following information for each person:
     * - Person ID
     * - First name
     * - Last name
     * - Date of birth (if available)
     * - Gender (if available)
     * - Phone number (if available)
     * - Family group ID (if available)
     *
     * The method uses the `personController.getAllPeople()` method to retrieve all persons from the system.
     */
    public static void viewAllPersons() {
        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.printf("%-8s %-15s %-15s %-12s %-20s %-15s %-10s%n",
                translationManager.getTranslation("id"), translationManager.getTranslation("first_name"),
                translationManager.getTranslation("last_name"), translationManager.getTranslation("dob"),
                translationManager.getTranslation("gender"), translationManager.getTranslation("phone"),
                translationManager.getTranslation("family_group"));
        System.out.println("----------------------------------------------------------------------------------------------------------");

        for (Person person : personController.getAllPeople()) {
            System.out.printf("%-8s %-15s %-15s %-12s %-20s %-15s %-10s%n",
                    person.getPersonId(),
                    person.getFirstName(),
                    person.getLastName(),
                    person.getDateOfBirth() != null ? person.getDateOfBirth() : "N/A",
                    person.getGender() != null ? person.getGender() : "N/A",
                    person.getPhoneNumber() != null ? person.getPhoneNumber() : "N/A",
                    person.getFamilyGroup() != null ? person.getFamilyGroup().getFamilyGroupId() : "N/A");
        }

        System.out.println("----------------------------------------------------------------------------------------------------------");
    }

    /**
     * Prompts the user to input details for a new person and adds the person to the system.
     *
     * This method performs the following steps:
     * 1. Prompts the user for the person's first name, last name, date of birth (optional),
     *    gender (optional), phone number (optional), and comments (optional).
     * 2. Creates a new `Person` object using the provided details.
     * 3. If any optional fields (gender, phone number, comments) are provided, they are set in the `Person` object.
     * 4. Adds the newly created person to the system using `personController.addPerson()`.
     * 5. Prints a success message along with the ID of the newly added person.
     *
     * If any validation errors or exceptions occur during input or person creation, an error message is displayed.
     *
     * @throws IllegalArgumentException if any invalid arguments are passed when creating the person.
     */
    public static void addNewPerson() {
        Scanner scanner = new Scanner(System.in);
        TranslationManager translator = TranslationManager.getInstance();

        System.out.println("\n" + translationManager.getTranslation("add_new_person_title"));
        System.out.println("--------------");

        try {
            System.out.print(translationManager.getTranslation("input_firstname") + " ");
            String firstName = scanner.nextLine();

            System.out.print(translationManager.getTranslation("input_lastname") + " ");
            String lastName = scanner.nextLine();

            System.out.print(translationManager.getTranslation("input_date_of_birth_adding_person") + " ");
            String dob = scanner.nextLine();

            // Gender selection with same options as updatePerson()
            System.out.println("\n" + translationManager.getTranslation("input_gender_person"));
            System.out.println("1. " + translator.getTranslation("gender_man"));
            System.out.println("2. " + translator.getTranslation("gender_woman"));
            System.out.println("3. " + translator.getTranslation("gender_nb"));
            System.out.print(translationManager.getTranslation("gender_person_options_selection") + " ");
            String genderChoice = scanner.nextLine();
            String gender = null;

            if (!genderChoice.isEmpty()) {
                switch (genderChoice) {
                    case "1":
                        gender = translator.getTranslation("gender_man");
                        break;
                    case "2":
                        gender = translator.getTranslation("gender_woman");
                        break;
                    case "3":
                        gender = translator.getTranslation("gender_nb");
                        break;
                    default:
                        System.out.println(translationManager.getTranslation("invalid_gender_person_options_selection"));
                }
            }

            System.out.print(translationManager.getTranslation("input_phone_num_person") + " ");
            String phone = scanner.nextLine();

            System.out.print(translationManager.getTranslation("input_comments_person") + " ");
            String comments = scanner.nextLine();

            Person person;
            if (!dob.isEmpty()) {
                person = new Person(firstName, lastName, dob);
            } else {
                person = new Person(firstName, lastName);
            }

            if (gender != null) person.setGender(gender);
            if (!phone.isEmpty()) person.setPhoneNumber(phone);
            if (!comments.isEmpty()) person.setComments(comments);

            personController.addPerson(person);
            System.out.println("\n" + translationManager.getTranslation("successful_person_addition") + " " + person.getPersonId());

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(translationManager.getTranslation("error1") + ": " + e.getMessage());
        }
    }

    /**
     * Allows the user to update an existing person's details.
     *
     * This method performs the following steps:
     * 1. Displays a list of all persons by calling `viewAllPersons()`.
     * 2. Prompts the user to input the ID of the person they want to update.
     * 3. If the person exists, it displays the current details of the person.
     * 4. Prompts the user to select a field (1-6) to update or 0 to cancel.
     * 5. If the field to be updated is the gender, the user is presented with predefined gender options.
     *    If any other field is selected, the user is prompted to enter a new value.
     * 6. The selected field of the person is updated with the new value.
     *
     * @throws NumberFormatException if the input cannot be parsed as an integer or invalid number is entered.
     * @throws IllegalArgumentException if the user provides invalid data for a field (e.g., empty field when it's not allowed).
     */
    public static void updatePerson() {
        Scanner scanner = new Scanner(System.in);
        TranslationManager translator = TranslationManager.getInstance();

        System.out.println("\n" + translationManager.getTranslation("update_person_title"));
        System.out.println("-------------");

        try {
            viewAllPersons();
            System.out.print("\n" + translationManager.getTranslation("update_person_id_input") + " ");
            int personId = Integer.parseInt(scanner.nextLine());

            Person person = personController.getPersonById(personId);
            if (person == null) {
                System.out.println(translationManager.getTranslation("invalid_update_person_id_input") + " " + personId);
                return;
            }

            System.out.println("\nCurrent Details:");
            System.out.println("1. First Name:" + " " + person.getFirstName());
            System.out.println("2. Last Name:" + " " + person.getLastName());
            System.out.println("3. Date of Birth: " + (person.getDateOfBirth() != null ? person.getDateOfBirth() : "N/A"));
            System.out.println("4. Gender: " + (person.getGender() != null ? person.getGender() : "N/A"));
            System.out.println("5. Phone Number: " + (person.getPhoneNumber() != null ? person.getPhoneNumber() : "N/A"));
            System.out.println("6. Comments: " + (person.getComments() != null ? person.getComments() : "N/A"));

            System.out.print("\n" + translationManager.getTranslation("enter_choices_6") + " ");
            int field = Integer.parseInt(scanner.nextLine());

            if (field == 0) return;

            String newValue;
            if (field == 4) { // Gender field
                System.out.println("\n" + translationManager.getTranslation("gender_person_options_title"));
                System.out.println("1. " + translator.getTranslation("gender_man"));
                System.out.println("2. " + translator.getTranslation("gender_woman"));
                System.out.println("3. " + translator.getTranslation("gender_nb"));
                System.out.print(translationManager.getTranslation("gender_person_options_selection") + " ");
                int genderChoice = Integer.parseInt(scanner.nextLine());

                switch (genderChoice) {
                    case 1:
                        newValue = translator.getTranslation("gender_man");
                        break;
                    case 2:
                        newValue = translator.getTranslation("gender_woman");
                        break;
                    case 3:
                        newValue = translator.getTranslation("gender_nb");
                        break;
                    default:
                        System.out.println("Invalid gender option");
                        return;
                }
            } else {
                System.out.print("Enter new value: ");
                newValue = scanner.nextLine();
            }

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
                    // person.setComments(newValue);
                default:
                    System.out.println("Invalid field number");
                    return;
            }

            personController.updatePerson(person);
            System.out.println("Person updated successfully!");

        } catch (NumberFormatException e) {
            System.out.println(translationManager.getTranslation("invalid_input"));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(translationManager.getTranslation("error1") + ": " + e.getMessage());
        }
    }

    /**
     * Converts an existing person into a disaster victim.
     *
     * This method performs the following actions:
     * 1. Displays a list of all persons by calling `viewAllPersons()`.
     * 2. Prompts the user to input the ID of the person they want to convert to a disaster victim.
     * 3. Attempts to convert the selected person into a disaster victim by calling the `convertToDisasterVictim()` method of the person controller.
     * 4. Displays a success message if the conversion is successful.
     *
     * @throws NumberFormatException if the input cannot be parsed as an integer or invalid number is entered.
     */
    public static void convertToDisasterVictim() {
        Scanner scanner = new Scanner(System.in);

        try {
            viewAllPersons();
            System.out.print("\nEnter ID of person to convert to Disaster Victim: ");
            int personId = Integer.parseInt(scanner.nextLine());

            personController.convertToDisasterVictim(personId);
            System.out.println("Person converted to Disaster Victim successfully!");
        } catch (NumberFormatException e) {
            System.out.println(translationManager.getTranslation("invalid_input"));
        } catch (Exception e) {
            System.out.println(translationManager.getTranslation("error1") + ": " + e.getMessage());
        }
    }


    /**
     * Displays a list of all disaster victims.
     *
     * This method iterates through all people and checks if each person is an instance of
     * the `DisasterVictim` class. For each disaster victim found, it displays their
     * information in a tabular format, including:
     * - ID
     * - First Name
     * - Last Name
     * - Date of Birth
     * - Gender
     * - Phone Number
     * - Inventory Size (number of items in the victim's personal inventory)
     *
     * If no disaster victims are found, the table will simply be empty.
     */
    public static void viewDisasterVictims() {
        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.printf("%-8s %-15s %-15s %-12s %-20s %-15s %-10s%n",
                translationManager.getTranslation("id"), translationManager.getTranslation("first_name"),
                translationManager.getTranslation("last_name"), translationManager.getTranslation("dob"),
                translationManager.getTranslation("gender"), translationManager.getTranslation("phone"),
                translationManager.getTranslation("inventory_size1"));
        System.out.println("----------------------------------------------------------------------------------------------------------");

        for (Person person : personController.getAllPeople()) {
            if (person instanceof DisasterVictim) {
                DisasterVictim victim = (DisasterVictim) person;
                System.out.printf("%-8s %-15s %-15s %-12s %-20s %-15s %-10s%n",
                        victim.getPersonId(),
                        victim.getFirstName(),
                        victim.getLastName(),
                        victim.getDateOfBirth() != null ? victim.getDateOfBirth() : "N/A",
                        victim.getGender() != null ? victim.getGender() : "N/A",
                        victim.getPhoneNumber() != null ? victim.getPhoneNumber() : "N/A",
                        victim.getPersonalInventory().size());
            }
        }

        System.out.println("----------------------------------------------------------------------------------------------------------");
    }

    /**
     * Adds a new disaster victim to the system.
     *
     * This method collects information about a new disaster victim, including personal details
     * (such as first name, last name, date of birth, gender, phone number, and comments),
     * and adds them to the system.
     *
     * The process involves the following steps:
     * 1. Collecting basic personal information for the victim (first name, last name, date of birth, gender, phone number, comments).
     * 2. Allowing the user to add items to the victim's inventory
     * 3. Storing the victim's information and inventory in the system.
     *
     * Note: The main difference between a DisasterVictim and a Person is that a Person does not have an inventory
     */
    public static void addDisasterVictim() {
        Scanner scanner = new Scanner(System.in);
        TranslationManager translator = TranslationManager.getInstance();

        System.out.println("\n" + translationManager.getTranslation("add_new_DisasterVictim"));
        System.out.println("-----------------------");

        try {
            System.out.print(translationManager.getTranslation("input_firstname") + " ");
            String firstName = scanner.nextLine();

            System.out.print(translationManager.getTranslation("input_lastname")+ " ");
            String lastName = scanner.nextLine();

            System.out.print(translationManager.getTranslation("input_date_of_birth_adding_person") + " ");
            String dob = scanner.nextLine();

            DisasterVictim victim;
            if (!dob.isEmpty()) {
                victim = new DisasterVictim(firstName, lastName, dob);
            } else {
                victim = new DisasterVictim(firstName, lastName);
            }

            // Gender selection with same options as updatePerson()
            System.out.println("\n" + translationManager.getTranslation("gender_person_options_title") + " ");
            System.out.println("1. " + translator.getTranslation("gender_man"));
            System.out.println("2. " + translator.getTranslation("gender_woman"));
            System.out.println("3. " + translator.getTranslation("gender_nb"));
            System.out.print(translationManager.getTranslation("gender_person_options_selection") + " ");
            String genderChoice = scanner.nextLine();
            String gender = null;

            if (!genderChoice.isEmpty()) {
                switch (genderChoice) {
                    case "1":
                        gender = translator.getTranslation("gender_man");
                        break;
                    case "2":
                        gender = translator.getTranslation("gender_woman");
                        break;
                    case "3":
                        gender = translator.getTranslation("gender_nb");
                        break;
                    default:
                        System.out.println("Invalid gender option, leaving blank");
                }
            }
            if (gender != null) victim.setGender(gender);

            System.out.print(translationManager.getTranslation("input_phone_num_person") + " ");
            String phone = scanner.nextLine();
            if (!phone.isEmpty()) victim.setPhoneNumber(phone);

            System.out.print(translationManager.getTranslation("input_comments_person") + " ");
            String comments = scanner.nextLine();
            if (!comments.isEmpty()) victim.setComments(comments);


            personController.addPerson(victim);
            System.out.println("\nDisaster victim added successfully! ID: " + victim.getPersonId());

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(translationManager.getTranslation("error1") + ": " + e.getMessage());
        }
    }


    /**
     * Allows the user to modify a person's family group.
     *
     * This method provides options to:
     * 1. Add the person to an existing family group.
     * 2. Create a new family group with the person and other members.
     * 3. Remove the person from their current family group.
     */
    public static void changePersonFamilyGroup() {
        Scanner scanner = new Scanner(System.in);

        try {
            // First show all people
            viewAllPersons();
            System.out.print("\nEnter ID of person to update family group: ");
            int personId = Integer.parseInt(scanner.nextLine());

            Person person = personController.getPersonById(personId);
            if (person == null) {
                System.out.println("No person found with ID: " + personId);
                return;
            }

            // Show current family group if any
            if (person.getFamilyGroup() != null) {
                System.out.println("\nCurrent Family Group:");
                System.out.println("ID: " + person.getFamilyGroup().getFamilyGroupId());
                System.out.println("Members:");
                for (Person member : person.getFamilyGroup().getMembers()) {
                    System.out.println("  " + member.getPersonId() + ": " +
                            member.getFirstName() + " " + member.getLastName());
                }
            } else {
                System.out.println("\nThis person is not currently in a family group.");
            }

            // Show options
            System.out.println("\nOptions:");
            System.out.println("1. Add to existing family group");
            System.out.println("2. Create new family group");
            System.out.println("3. Remove from current family group");
            System.out.print("Enter your choice (1-3): ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1: // Add to existing family group
                    // Show available family groups
                    System.out.println("\nAvailable Family Groups:");
                    ArrayList<FamilyGroup> familyGroups = personController.getAllFamilyGroups();
                    for (FamilyGroup group : familyGroups) {
                        System.out.println("ID: " + group.getFamilyGroupId() +
                                " - Members: " + group.getMembers().size());
                    }

                    System.out.print("Enter family group ID to add to: ");
                    int familyId = Integer.parseInt(scanner.nextLine());

                    try {
                        personController.addPersonToFamilyGroup(personId, familyId);
                        System.out.println("Person added to family group successfully");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 2: // Create new family group
                    System.out.print("\nEnter IDs of other family members including original chosen (comma separated): ");
                    String[] memberIds = scanner.nextLine().split(",");
                    ArrayList<Person> members = new ArrayList<>();
                    members.add(person); // Include the current person

                    for (String idStr : memberIds) {
                        try {
                            int id = Integer.parseInt(idStr.trim());
                            Person member = personController.getPersonById(id);
                            if (member != null) {
                                members.add(member);
                            }
                        } catch (Exception e) {
                            System.out.println("Invalid ID: " + idStr);
                        }
                    }

                    try {
                        FamilyGroup newFamily = personController.createFamilyGroup(members);
                        System.out.println("New family group created with ID: " + newFamily.getFamilyGroupId());
                    } catch (Exception e) {
                        System.out.println("Error creating family group: " + e.getMessage());
                    }
                    break;

                case 3: // Remove from current family group
                    try {
                        personController.removePersonFromFamilyGroup(personId);
                        System.out.println("Person removed from family group");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;


                default:
                    System.out.println("Invalid choice");
            }



        } catch (NumberFormatException e) {
            System.out.println(translationManager.getTranslation("invalid_input"));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(translationManager.getTranslation("error1") + ": " + e.getMessage());
        }
    }




    /**
     * Displays a menu for managing inquiries, allowing the user to view, add, update, delete, and generate reports for inquiries.
     *
     * This method provides an interactive menu that enables the user to choose from the following options:
     * 1. View All Inquiries: Displays all inquiries in the system.
     * 2. Add New Inquiry: Prompts the user to add a new inquiry.
     * 3. Update Inquiry: Allows the user to update an existing inquiry.
     * 4. Delete Inquiry: Allows the user to delete an inquiry by selecting it.
     * 5. Generate Inquiry Report: Generates a report based on the existing inquiries in the system.
     * 0. Back to Main Menu: Exits the inquiry management menu and returns to the main menu.
     */
    public static void displayInquiryDetails() {
        Scanner scanner = new Scanner(System.in);
        boolean stayInMenu = true;

        while (stayInMenu) {
            System.out.println("\n" + translationManager.getTranslation("inquiry_management"));
            System.out.println(translationManager.getTranslation("inquiry_management1"));
            System.out.println(translationManager.getTranslation("inquiry_management2"));
            System.out.println(translationManager.getTranslation("inquiry_management3"));
            System.out.println(translationManager.getTranslation("inquiry_management4"));
            System.out.println(translationManager.getTranslation("inquiry_management5"));  // New option
            System.out.println(translationManager.getTranslation("inquiry_management6"));
            System.out.print("\n" + translationManager.getTranslation("ask_choice") + " ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        viewAllInquiries();
                        break;
                    case 2:
                        addNewInquiry();
                        break;
                    case 3:
                        updateInquiry();
                        break;
                    case 4:
                        deleteInquiry();
                        break;
                    case 5:
                        generateInquiryReport();
                        break;
                    case 0:
                        stayInMenu = false;
                        break;
                    default:
                        System.out.println(translationManager.getTranslation("inquiry_management7"));
                }
            } catch (NumberFormatException e) {
                System.out.println(translationManager.getTranslation("invalid_input"));
            } catch (Exception e) {
                System.out.println(translationManager.getTranslation("error1") + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Displays a list of all inquiries in the system, showing details such as the inquirer, missing person,
     * the date of the inquiry, location, and additional comments.
     *
     * This method retrieves all inquiries through the InquiryController.
     * Each row includes the following information:
     * - Inquiry ID
     * - Inquirer’s name (First and Last)
     * - Missing Person's name (First and Last)
     * - Date of the Inquiry
     * - Last Known Location
     * - Additional Comments provided by the inquirer
     */
    public static void viewAllInquiries() {
        System.out.println("\n------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-8s %-25s %-25s %-15s %-25s %-30s%n",
                translationManager.getTranslation("id"), translationManager.getTranslation("inquirer2"),
                translationManager.getTranslation("missing_person2"), translationManager.getTranslation("date2"),
                translationManager.getTranslation("loc2"), translationManager.getTranslation("comments2"));
        System.out.println("------------------------------------------------------------------------------------------------------------------------------");

        for (Inquiry inquiry : inquiryController.getAllInquiries()) {
            String inquirerName = inquiry.getInquirer().getFirstName() + " " + inquiry.getInquirer().getLastName();
            String missingPersonName = inquiry.getMissingPerson().getFirstName() + " " + inquiry.getMissingPerson().getLastName();
            String locationName = inquiry.getLastKnownLocation().getLocationName();

            System.out.printf("%-8s %-25s %-25s %-15s %-25s %-30s%n",
                    inquiry.getInquiryId(),
                    inquirerName,
                    missingPersonName,
                    inquiry.getDateOfInquiry(),
                    locationName,
                    inquiry.getInfoProvided());
        }

        System.out.println("------------------------------------------------------------------------------------------------------------------------------");
    }

    /**
     * Allows the user to add a new inquiry to the system.
     * This method guides the user through the process of creating a new inquiry by:
     * - Selecting an inquirer (from available people in the system),
     * - Selecting a missing person (who is a disaster victim),
     * - Selecting the last known location of the missing person,
     * - Providing the date of the inquiry and additional information related to the inquiry.
     */
    public static void addNewInquiry() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nAdd New Inquiry");
        System.out.println("----------------");

        try {
            // List available people (potential inquirers)
            System.out.println("\nAvailable Inquirers:");
            for (Person person : personController.getAllPeople()) {
                System.out.println(person.getPersonId() + ": " +
                        person.getFirstName() + " " + person.getLastName());
            }

            System.out.print("\n" + "Enter ID of inquirer:" + " ");
            int inquirerId = Integer.parseInt(scanner.nextLine());
            Person inquirer = personController.getPersonById(inquirerId);
            if (inquirer == null) {
                throw new IllegalArgumentException("Invalid inquirer ID");
            }

            // List available disaster victims (missing persons)
            System.out.println("\nAvailable Missing Persons:");
            for (Person person : personController.getAllPeople()) {
                if (person instanceof DisasterVictim) {
                    System.out.println(person.getPersonId() + ": " +
                            person.getFirstName() + " " + person.getLastName());
                }
            }

            System.out.print("\nEnter ID of missing person: ");
            int missingPersonId = Integer.parseInt(scanner.nextLine());
            Person person = personController.getPersonById(missingPersonId);
            if (!(person instanceof DisasterVictim)) {
                throw new IllegalArgumentException("Selected person is not a Disaster Victim");
            }
            DisasterVictim missingPerson = (DisasterVictim) person;

            // List available locations
            System.out.println("\nAvailable Locations:");
            for (Location location : locationController.getAllLocations()) {
                System.out.println(location.getLocationId() + ": " +
                        location.getLocationName() + " - " + location.getLocationAddress());
            }

            System.out.print("\nEnter ID of last known location: ");
            int locationId = Integer.parseInt(scanner.nextLine());
            Location location = locationController.getLocationById(locationId);
            if (location == null) {
                throw new IllegalArgumentException("Invalid location ID");
            }

            // Get inquiry details
            System.out.print("\nEnter date of inquiry (YYYY-MM-DD): ");
            String date = scanner.nextLine();

            System.out.print("Enter information provided: ");
            String info = scanner.nextLine();

            // Create and add the inquiry
            Inquiry inquiry = new Inquiry(inquirer, missingPerson, date, info, location);
            inquiryController.addInquiry(inquiry);

            System.out.println("\nInquiry added successfully! ID: " + inquiry.getInquiryId());

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter valid numbers for IDs.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(translationManager.getTranslation("error1") + ": " + e.getMessage());
        }
    }

    /**
     * Allows the user to update an existing inquiry in the system.
     * This method presents the user with a list of all inquiries and allows them to select
     * an inquiry to update. The user can choose to update the following fields:
     * - Inquirer: The person making the inquiry.
     * - Missing Person: The person who is missing (must be a disaster victim).
     * - Last Known Location: The location where the missing person was last seen.
     * - Date of Inquiry: The date when the inquiry is being made.
     * - Information Provided: Additional information related to the inquiry.
     */
    public static void updateInquiry() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n" + translationManager.getTranslation("update_inquiry_title"));
        System.out.println("--------------");

        try {
            // First show all inquiries
            viewAllInquiries();

            System.out.print("\n" + translationManager.getTranslation("update_inquiry_input_id") + " ");
            int inquiryId = Integer.parseInt(scanner.nextLine());

            Inquiry inquiry = inquiryController.getInquiryById(inquiryId);
            if (inquiry == null) {
                System.out.println("No inquiry found with ID: " + inquiryId);
                return;
            }

            // Display current details
            System.out.println("\nCurrent Inquiry Details:");
            System.out.println("1. Inquirer: " + inquiry.getInquirer().getFirstName() + " " +
                    inquiry.getInquirer().getLastName());
            System.out.println("2. Missing Person: " + inquiry.getMissingPerson().getFirstName() + " " +
                    inquiry.getMissingPerson().getLastName());
            System.out.println("3. Location: " + inquiry.getLastKnownLocation().getLocationName());
            System.out.println("4. Date: " + inquiry.getDateOfInquiry());
            System.out.println("5. Info Provided: " + inquiry.getInfoProvided());

            System.out.print("\n" + "Enter field number to update (1-5) or 0 to cancel: ");
            int field = Integer.parseInt(scanner.nextLine());

            if (field == 0) return;

            System.out.print("Enter new value: ");
            String newValue = scanner.nextLine();

            switch (field) {
                case 1:
                    // Update inquirer
                    Person newInquirer = personController.getPersonById(Integer.parseInt(newValue));
                    if (newInquirer == null) {
                        throw new IllegalArgumentException("Invalid person ID");
                    }
                    inquiry.setInquirer(newInquirer);
                    break;
                case 2:
                    // Update missing person
                    Person person = personController.getPersonById(Integer.parseInt(newValue));
                    if (!(person instanceof DisasterVictim)) {
                        throw new IllegalArgumentException(translationManager.getTranslation("person_is_not_dv"));
                    }
                    inquiry.setMissingPerson((DisasterVictim) person);
                    break;
                case 3:
                    // Update location
                    Location newLocation = locationController.getLocationById(Integer.parseInt(newValue));
                    if (newLocation == null) {
                        throw new IllegalArgumentException(translationManager.getTranslation("invalid_location_id_mr"));
                    }
                    inquiry.setLastKnownLocation(newLocation);
                    break;
                case 4:
                    // Update date
                    inquiry.setDateOfInquiry(newValue);
                    break;
                case 5:
                    // Update info
                    inquiry.setInfoProvided(newValue);
                    break;
                default:
                    System.out.println(translationManager.getTranslation("invalid_field_num"));
                    return;
            }

            inquiryController.updateInquiry(inquiry);
            System.out.println(translationManager.getTranslation("successful_inquiry_update"));

        } catch (NumberFormatException e) {
            System.out.println(translationManager.getTranslation("invalid_input"));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(translationManager.getTranslation("error1") + ": " + e.getMessage());
        }
    }

    /**
     * Allows the user to delete an existing inquiry from the system.
     */
    public static void deleteInquiry() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n" + translationManager.getTranslation("delete_inquiry_title"));
        System.out.println("--------------");

        try {
            viewAllInquiries();
            System.out.print("\n" + translationManager.getTranslation("delete_inquiry_id_input") + " ");
            int inquiryId = Integer.parseInt(scanner.nextLine());


            inquiryController.deleteInquiry(inquiryId);
            System.out.println(translationManager.getTranslation("successful_inquiry_deletion"));

        } catch (NumberFormatException e) {
            System.out.println(translationManager.getTranslation("invalid_input"));
        } catch (Exception e) {
            System.out.println(translationManager.getTranslation("error1") + ": " + e.getMessage());
        }
    }

    /**
     * Generates a detailed report for a specific inquiry (It just writes it
     * in a more complicated way).
     */
    public static void generateInquiryReport() {
        Scanner scanner = new Scanner(System.in);
        TranslationManager translator = TranslationManager.getInstance();

        try {
            // First show all inquiries
            viewAllInquiries();

            System.out.print("\n" + translationManager.getTranslation("inquiry_report_id_input") + " ");
            int inquiryId = Integer.parseInt(scanner.nextLine());

            Inquiry inquiry = inquiryController.getInquiryById(inquiryId);
            if (inquiry == null) {
                System.out.println(translationManager.getTranslation("invalid_inquiry_report_id_input") + " " + inquiryId);
                return;
            }

            // Get the required fields for the report
            String personName = inquiry.getMissingPerson().getFirstName() + " " +
                    inquiry.getMissingPerson().getLastName();
            String facilityName = inquiry.getLastKnownLocation().getLocationName();
            String entryDate = inquiry.getDateOfInquiry();

            // Format the report using the translation
            String report = String.format(
                    translator.getTranslation("report_person"),
                    personName,
                    facilityName,
                    entryDate
            );

            System.out.println("\n" + translationManager.getTranslation("inquiry_report_title"));
            System.out.println("----------------");
            System.out.println(report);
            System.out.println(translationManager.getTranslation("inquiry_report_additional_info") + " " + inquiry.getInfoProvided());
            System.out.println(translationManager.getTranslation("inquiry_report_inquirer") + " " + inquiry.getInquirer().getFirstName() + " " +
                    inquiry.getInquirer().getLastName());
            System.out.println("----------------");

        } catch (NumberFormatException e) {
            System.out.println(translationManager.getTranslation("invalid_input"));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(translationManager.getTranslation("error1") + ": " + e.getMessage());
        }
    }







}
