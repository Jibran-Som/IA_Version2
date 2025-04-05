package edu.ucalgary.oop;

import java.sql.SQLException;
import java.util.Scanner;


public class UserView {

    public static TranslationManager translationManager;
    public static SupplyController supplyController;



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
    public static void displayPersonDetails() {
        System.out.println("\nPerson Management\n");
        System.out.println("1. View All Persons");
        System.out.println("2. Add New Person");
        System.out.println("3. Update Person");
        System.out.println("4. View Disaster Victims");
        System.out.println("0. Back to Main Menu");
        System.out.print("\nEnter your choice: ");
    }





}
