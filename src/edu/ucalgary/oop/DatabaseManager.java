/**
 * DatabaseManager.java
 * Version: 3.0
 * Author: Jibran Somroo
 * Date: April 10, 2025
 */

package edu.ucalgary.oop;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    private final String DB_URL = "jdbc:postgresql://localhost:5432/ensf380project";
    private final String USER = "oop";
    private final String PASS = "ucalgary";

    /**
     * Constructor for the DatabaseManager class.
     * Attempts to establish a connection to the database.
     * If the connection fails, it logs a fatal error and throws an SQLException to indicate the failure.
     *
     * @throws SQLException If a database connection cannot be established.
     */
    public DatabaseManager() throws SQLException {
        try {
            connect();
        }
        catch (SQLException e) {
            ErrorLogger.getInstance().logFatalError(
                    e,
                    "DatabaseManager constructor - database connection",
                    "FATAL ERROR: Cannot connect to database. The application will now exit."
            );
            throw e;

        }
    }

    /**
     * Establishes a connection to the database using the provided database URL, username, and password.
     *
     * @throws SQLException If a connection to the database cannot be established.
     */
    private void connect() throws SQLException {
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Closes the database connection if it is open.
     * Checks if the connection is not null and is currently open before attempting to close it.
     * If the connection is already closed or null, no action is taken.
     * Mainly used for personal testing and not actual application.
     *
     * @throws SQLException If an error occurs while closing the connection.
     */
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }


    /**
     * Retrieves the singleton instance of the DatabaseManager.
     * If the instance does not already exist, it creates a new one.
     *
     * @return The singleton instance of the DatabaseManager.
     * @throws SQLException If the database connection cannot be established when creating the instance.
     */
    public static synchronized DatabaseManager getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }









    // Code for Supply


    /**
     * Adds a new supply to the database.
     * Inserts a new record into the "Supply" table with the supply type and comments (using supply name as comments).
     * If the insertion is successful, the method retrieves the generated supply ID.
     * Throws an SQLException if the insertion fails or no ID is returned.
     *
     * @param supply The Supply object containing the data to be inserted.
     * @throws SQLException If an error occurs while inserting the supply or if no ID is returned after insertion.
     */
    public void addSupply(Supply supply) throws SQLException {
        String sql = "INSERT INTO Supply (type, comments) VALUES (?, ?) RETURNING supply_id";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, supply.getSupplyType());
            pstmt.setString(2, supply.getSupplyName()); // Using supplyName as comments

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                rs.getInt(1);
                return;
            }
            throw new SQLException("Failed to insert supply, no ID obtained");
        }
    }



    /**
     * Retrieves all supplies from the database and maps them to their corresponding Supply objects.
     * The method fetches all records from the "Supply" table, and for each supply, it determines its type
     * to instantiate the appropriate subclass.
     *
     * @return A list of Supply objects corresponding to the records in the "Supply" table.
     * @throws SQLException If an error occurs while querying the database or processing the result set.
     */
    public List<Supply> getAllSupplies() throws SQLException {
        deleteExpiredSupplies();
        List<Supply> supplies = new ArrayList<>();
        String sql = "SELECT * FROM Supply";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String type = rs.getString("type");
                String name = rs.getString("comments"); // Using comments as supplyName
                int supplyId = rs.getInt("supply_id");
                Supply supply;

                // Create the appropriate subclass based on the type
                switch (type.toLowerCase()) {
                    case "water":
                        Water water = new Water(name, type);
                        setWaterAllocationDate((Water) water, supplyId);
                        if (!isWaterExpired(water.getAllocationDate())) {
                            supply = water;
                        } else {
                            continue;
                        }
                        break;
                    case "cot":
                        String[] cotSpecs = name.split(" ");
                        supply = new Cot(type + supplyId, type, cotSpecs[0], cotSpecs[1]);
                        break;
                    case "personal item":
                        supply = new PersonalBelonging(type + supplyId, type, name);
                        break;
                    case "blanket":
                        supply = new Blanket(name, type);
                        break;
                    default:
                        supply = new Supply(name, type);
                        break;
                }

                supply.setSupplyId(supplyId);
                supplies.add(supply);
            }
        }
        return supplies;
    }


    /**
     * Retrieves the most recent allocation date for a specific water supply from the database
     * and sets it on the provided Water object.
     *
     * @param water The Water object to which the allocation date will be set.
     * @param supplyId The ID of the supply (water) for which the allocation date is being retrieved.
     * @throws SQLException If an error occurs while querying the database or processing the result.
     */
    private void setWaterAllocationDate(Water water, int supplyId) throws SQLException {
        String sql = "SELECT allocation_date FROM SupplyAllocation " +
                "WHERE supply_id = ? AND person_id IS NOT NULL " +
                "ORDER BY allocation_date DESC LIMIT 1";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, supplyId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("allocation_date");
                if (timestamp != null) {
                    water.setAllocationDate(timestamp.toLocalDateTime().toLocalDate().toString());
                }
            }
        }
    }


    /**
     * Checks whether the water supply has expired based on its allocation date.
     * The method compares the current date with the expiration date time.
     *
     * @param allocationDate The allocation date of the water supply, or null if unallocated.
     * @return true if the water has expired, false otherwise.
     */
    private boolean isWaterExpired(String allocationDate) {
        if (allocationDate == null) return false; // Unallocated water doesn't expire

        LocalDate allocation = LocalDate.parse(allocationDate);
        LocalDate expirationDate = allocation.plusDays(Water.EXPIRATION_DAYS);
        return LocalDate.now().isAfter(expirationDate);
    }


    /**
     * Deletes expired water supplies from the database.
     *
     * @throws SQLException If an error occurs while querying the database or deleting the expired supplies.
     */
    public void deleteExpiredSupplies() throws SQLException {
        String query = "SELECT s.supply_id, sa.allocation_date FROM Supply s " +
                "LEFT JOIN SupplyAllocation sa ON s.supply_id = sa.supply_id " +
                "WHERE s.type = 'water' AND sa.person_id IS NOT NULL " +
                "ORDER BY sa.allocation_date DESC";

        List<Integer> expiredIds = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("allocation_date");
                if (timestamp != null) {
                    String allocationDate = timestamp.toLocalDateTime().toLocalDate().toString();
                    if (isWaterExpired(allocationDate)) {
                        expiredIds.add(rs.getInt("supply_id"));
                    }
                }
            }
        }

        // Delete the expired supplies
        if (!expiredIds.isEmpty()) {
            String deleteAllocations = "DELETE FROM SupplyAllocation WHERE supply_id = ?";
            String deleteSupplies = "DELETE FROM Supply WHERE supply_id = ?";

            try (PreparedStatement allocStmt = connection.prepareStatement(deleteAllocations);
                 PreparedStatement supplyStmt = connection.prepareStatement(deleteSupplies)) {

                for (Integer id : expiredIds) {
                    // First delete from SupplyAllocation (child table)
                    allocStmt.setInt(1, id);
                    allocStmt.addBatch();

                    // Then delete from Supply (parent table)
                    supplyStmt.setInt(1, id);
                    supplyStmt.addBatch();
                }

                allocStmt.executeBatch();
                supplyStmt.executeBatch();
            }
        }
    }


    /**
     * Checks if a supply has been allocated to a person.
     * This method queries the "SupplyAllocation" table to count the number of allocations
     * for a specific supply ID. If the count is greater than 0, it indicates that the supply
     * has been allocated to a person, and the method returns true. Otherwise, it returns false.
     *
     * @param supplyId The ID of the supply to check for allocation.
     * @return true if the supply has been allocated, false otherwise.
     * @throws SQLException If an error occurs while querying the database.
     */

    public boolean isSupplyAllocated(int supplyId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM SupplyAllocation WHERE supply_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, supplyId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    /**
     * Checks if a supply is specifically allocated to a person
     * Used only for testing. Was the
     *
     *
     * @param supplyId The ID of the supply to check
     * @return true if the supply is allocated to a person, false otherwise
     * @throws SQLException If there's a database error
     */
    public boolean isSupplyAllocatedToPerson(int supplyId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM SupplyAllocation WHERE supply_id = ? AND person_id IS NOT NULL";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, supplyId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }




    /**
     * Updates the details of an existing supply in the database.
     * This method updates the `type` and `comments` (using the supply name) of a supply record
     * in the "Supply" table
     *
     * @param supply The Supply object containing the updated data.
     * @throws SQLException If an error occurs while updating the supply or if no rows are affected.
     */

    public void updateSupply(Supply supply) throws SQLException {
        String sql = "UPDATE Supply SET type = ?, comments = ? WHERE supply_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, supply.getSupplyType());
            pstmt.setString(2, supply.getSupplyName());
            pstmt.setInt(3, supply.getSupplyId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating supply failed, no rows affected.");
            }
        }
    }

    /**
     * Deletes a supply record from the database.
     * This method removes a supply record from the "Supply" table based on the given `supplyId`.
     * Mainly used for type water.
     *
     * @param supplyId The ID of the supply to be deleted.
     * @throws SQLException If an error occurs while deleting the supply or if no rows are affected.
     */
    public void deleteSupply(int supplyId) throws SQLException {
        String sql = "DELETE FROM Supply WHERE supply_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, supplyId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting supply failed, no rows affected.");
            }
        }
    }


    /**
     * Allocates a supply to either a person or a location.
     * This method checks if the supply is already allocated:
     * - If it is already allocated, the method updates the existing allocation record
     *   by assigning the supply to either a person or a location (but not both at the same time).
     * - If the supply is not already allocated, a new allocation record is created.
     *
     * @param supplyId The ID of the supply to be allocated.
     * @param personId The ID of the person to whom the supply is allocated, or null if allocating to a location.
     * @param locationId The ID of the location to which the supply is allocated, or null if allocating to a person.
     * @throws SQLException If an error occurs while querying or updating the database.
     */
    public void allocateSupply(int supplyId, Integer personId, Integer locationId) throws SQLException {
        // First check if the supply is already allocated
        if (isSupplyAllocated(supplyId)) {
            // If already allocated to a location, update the record
            if (locationId != null) {
                String updateSql = "UPDATE SupplyAllocation SET location_id = ?, person_id = NULL WHERE supply_id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(updateSql)) {
                    pstmt.setInt(1, locationId);
                    pstmt.setInt(2, supplyId);
                    pstmt.executeUpdate();
                }
            }
            // If already allocated to a person, update the record
            else if (personId != null) {
                String updateSql = "UPDATE SupplyAllocation SET person_id = ?, location_id = NULL WHERE supply_id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(updateSql)) {
                    pstmt.setInt(1, personId);
                    pstmt.setInt(2, supplyId);
                    pstmt.executeUpdate();
                }
            }
        } else {
            // Create new allocation record
            String sql = "INSERT INTO SupplyAllocation (supply_id, person_id, location_id) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, supplyId);
                if (personId != null) {
                    pstmt.setInt(2, personId);
                } else {
                    pstmt.setNull(2, Types.INTEGER);
                }
                if (locationId != null) {
                    pstmt.setInt(3, locationId);
                } else {
                    pstmt.setNull(3, Types.INTEGER);
                }
                pstmt.executeUpdate();
            }
        }
    }


    /**
     * Checks if a supply is currently allocated to a specific location.
     * Used for allocating supplies from locations to individuals.
     *
     * @param supplyId The ID of the supply to check.
     * @param locationId The ID of the location to check.
     * @return true if the supply is allocated to the specified location, false otherwise.
     * @throws SQLException If an error occurs while querying the database.
     */
    public boolean isSupplyAtLocation(int supplyId, int locationId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM SupplyAllocation WHERE supply_id = ? AND location_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, supplyId);
            pstmt.setInt(2, locationId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    /**
     * Removes a supply from a specific location.
     * Used for allocating supplies from locations to individuals.
     *
     * @param supplyId The ID of the supply to remove from the location.
     * @param locationId The ID of the location from which the supply should be removed.
     * @throws SQLException If an error occurs while querying or updating the database.
     */
    public void removeSupplyFromLocation(int supplyId, int locationId) throws SQLException {
        String sql = "DELETE FROM SupplyAllocation WHERE supply_id = ? AND location_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, supplyId);
            pstmt.setInt(2, locationId);
            pstmt.executeUpdate();
        }
    }


    /**
     * Retrieves a list of supplies allocated to a specific person or location.
     *
     * @param personId The ID of the person to whom the supplies are allocated, or null if querying by location.
     * @param locationId The ID of the location to which the supplies are allocated, or null if querying by person.
     * @return A list of `Supply` objects allocated to the specified person or location.
     * @throws SQLException If an error occurs while querying the database.
     */
    public List<Supply> getSuppliesAllocatedTo(Integer personId, Integer locationId) throws SQLException {
        List<Supply> supplies = new ArrayList<>();
        String sql = "SELECT s.* FROM Supply s JOIN SupplyAllocation sa ON s.supply_id = sa.supply_id WHERE ";

        if (personId != null) {
            sql += "sa.person_id = ?";
        } else if (locationId != null) {
            sql += "sa.location_id = ?";
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (personId != null) {
                pstmt.setInt(1, personId);
            } else {
                pstmt.setInt(1, locationId);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Supply supply = new Supply(
                        rs.getString("comments"),
                        rs.getString("type")
                ) {};
                supply.setSupplyId(rs.getInt("supply_id"));
                supplies.add(supply);
            }
        }
        return supplies;
    }















    // Code for Location


    /**
     * Adds a new location to the database.
     *
     * @param location The `Location` object containing the details to be inserted.
     * @throws SQLException If an error occurs while inserting the location into the database or retrieving the generated ID.
     */
    public void addLocation(Location location) throws SQLException {
        String sql = "INSERT INTO Location (name, address) VALUES (?, ?) RETURNING location_id";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, location.getLocationName());
            pstmt.setString(2, location.getLocationAddress());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                location.setLocationId(rs.getInt("location_id"));
            }
        }
    }


    /**
     * Retrieves a list of all locations from the database.
     *
     * @return A list of `Location` objects containing the details of all locations in the database.
     * @throws SQLException If an error occurs while querying the database.
     */
    public List<Location> getAllLocations() throws SQLException {
        List<Location> locations = new ArrayList<>();
        String sql = "SELECT * FROM Location";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Location location = new Location(
                        rs.getString("name"),
                        rs.getString("address")
                );
                location.setLocationId(rs.getInt("location_id"));
                locations.add(location);
            }
        }
        return locations;
    }


    /**
     * Updates the details of an existing location in the database.
     *
     * @param location The `Location` object containing the updated details.
     * @throws SQLException If an error occurs while updating the location in the database,
     *                      or if no rows are affected (i.e., the specified location does not exist).
     */

    public void updateLocation(Location location) throws SQLException {
        String sql = "UPDATE Location SET name = ?, address = ? WHERE location_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, location.getLocationName());
            pstmt.setString(2, location.getLocationAddress());
            pstmt.setInt(3, location.getLocationId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating location failed, no rows affected.");
            }
        }
    }


    /**
     * Deletes a location from the database.
     * Really only created for basic tests.
     *
     * @param locationId The `location_id` of the location to be deleted.
     * @throws SQLException If an error occurs while deleting the location from the database,
     */
    public void deleteLocation(int locationId) throws SQLException {
        String sql = "DELETE FROM Location WHERE location_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, locationId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting location failed, no rows affected.");
            }
        }
    }

    /**
     * Retrieves a list of persons currently occupying a specific location.
     *
     * @param locationId The ID of the location for which occupants are to be retrieved.
     * @return A list of `Person` objects representing the occupants at the specified location.
     * @throws SQLException If an error occurs while querying the database.
     */
    public List<Person> getOccupantsAtLocation(int locationId) throws SQLException {
        List<Person> occupants = new ArrayList<>();
        String sql = "SELECT p.* FROM Person p JOIN PersonLocation pl ON p.person_id = pl.person_id WHERE pl.location_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, locationId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Person person = new Person(
                        rs.getString("first_name"),
                        rs.getString("last_name")
                );
                person.setPersonId(rs.getInt("person_id"));
                occupants.add(person);
            }
        }
        return occupants;
    }


    /**
     * Adds a person to a specific location by creating an entry in the `PersonLocation` table.
     * This method inserts a record into the `PersonLocation` table that associates the specified
     * `person_id` with a `location_id`, effectively assigning the person to that location.
     *
     * @param personId The ID of the person to be assigned to the location.
     * @param locationId The ID of the location where the person is to be assigned.
     * @throws SQLException If an error occurs while inserting the record into the database.
     */
    public void addPersonToLocation(int personId, int locationId) throws SQLException {
        String sql = "INSERT INTO PersonLocation (person_id, location_id) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, personId);
            pstmt.setInt(2, locationId);

            pstmt.executeUpdate();
        }
    }

    /**
     * Removes a person from a specific location by deleting the corresponding entry from
     * the `PersonLocation` table. This method will delete the record that associates the given
     * `person_id` with a `location_id`, effectively disassociating the person from that location.
     *
     * @param personId The ID of the person to be removed from the location.
     * @param locationId The ID of the location from which the person is to be removed.
     * @throws SQLException If an error occurs while deleting the record from the database or if no rows are affected.
     */
    public void removePersonFromLocation(int personId, int locationId) throws SQLException {
        String sql = "DELETE FROM PersonLocation WHERE person_id = ? AND location_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, personId);
            pstmt.setInt(2, locationId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Removing person from location failed, no rows affected.");
            }
        }
    }















    // MedicalRecord


    /**
     * Adds a new medical record to the database.
     *
     * @param record The `MedicalRecord` object containing the details of the treatment to be added.
     * @throws SQLException If an error occurs while inserting the record into the database.
     */
    public void addMedicalRecord(MedicalRecord record) throws SQLException {
        String sql = "INSERT INTO MedicalRecord (location_id, person_id, date_of_treatment, treatment_details) " +
                "VALUES (?, ?, ?::timestamp, ?) RETURNING medical_record_id";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, record.getLocation().getLocationId());
            pstmt.setInt(2, record.getPerson().getPersonId());
            pstmt.setString(3, record.getDateOfTreatment() + " 00:00:00"); // Add time component
            pstmt.setString(4, record.getTreatmentDetails());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                record.setMedicalRecordId(rs.getInt("medical_record_id"));
            }
        }
    }

    /**
     * Retrieves all medical records from the database, including information about the
     * person associated with the record, the location where the treatment occurred,
     * and the details of the treatment. This method returns a list of `MedicalRecord` objects.
     *
     * @return A list of all `MedicalRecord` objects
     * @throws SQLException If an error occurs while querying the database.
     */
    public List<MedicalRecord> getAllMedicalRecords() throws SQLException {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT mr.*, p.first_name, p.last_name, l.name as location_name, l.address as location_address " +
                "FROM MedicalRecord mr " +
                "JOIN Person p ON mr.person_id = p.person_id " +
                "JOIN Location l ON mr.location_id = l.location_id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Person person = new Person(
                        rs.getString("first_name"),
                        rs.getString("last_name")
                );
                person.setPersonId(rs.getInt("person_id"));

                Location location = new Location(
                        rs.getString("location_name"),
                        rs.getString("location_address")
                );
                location.setLocationId(rs.getInt("location_id"));

                // Convert timestamp to date string
                Timestamp timestamp = rs.getTimestamp("date_of_treatment");
                String dateStr = timestamp != null ? timestamp.toLocalDateTime().toLocalDate().toString() : "";

                MedicalRecord record = new MedicalRecord(
                        person,
                        location,
                        rs.getString("treatment_details"),
                        dateStr
                );
                record.setMedicalRecordId(rs.getInt("medical_record_id"));
                records.add(record);
            }
        }
        return records;
    }


    /**
     * Updates an existing medical record in the database. This method modifies the details
     * of a specific medical record, including the location, person, date of treatment,
     * and treatment details, based on the provided `MedicalRecord` object.
     *
     * @param record The `MedicalRecord` object containing the updated information.
     * @throws SQLException If an error occurs while executing the update query or if no rows are affected.
     */
    public void updateMedicalRecord(MedicalRecord record) throws SQLException {
        String sql = "UPDATE MedicalRecord SET location_id = ?, person_id = ?, " +
                "date_of_treatment = ?, treatment_details = ? " +
                "WHERE medical_record_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, record.getLocation().getLocationId());
            pstmt.setInt(2, record.getPerson().getPersonId());
            pstmt.setString(3, record.getDateOfTreatment());
            pstmt.setString(4, record.getTreatmentDetails());
            pstmt.setInt(5, record.getMedicalRecordId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating medical record failed, no rows affected.");
            }
        }
    }


    /**
     * Deletes a medical record from the database based on the provided medical record ID.
     * Mainly used for tests.
     *
     * @param medicalRecordId The ID of the medical record to be deleted.
     * @throws SQLException If an error occurs while executing the delete query or if no rows are affected (i.e., the record does not exist).
     */
    public void deleteMedicalRecord(int medicalRecordId) throws SQLException {
        String sql = "DELETE FROM MedicalRecord WHERE medical_record_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, medicalRecordId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting medical record failed, no rows affected.");
            }
        }
    }

    /**
     * Retrieves all medical records associated with a specific person from the database.
     * This method queries the `MedicalRecord` table, joins it with the `Location` table to get location details,
     * and returns a list of `MedicalRecord` objects that are linked to the specified person ID.
     *
     * @param personId The ID of the person whose medical records are to be retrieved.
     * @return A list of `MedicalRecord` objects associated with the specified person.
     * @throws SQLException If an error occurs while executing the SQL query or processing the results.
     */
    public List<MedicalRecord> getMedicalRecordsForPerson(int personId) throws SQLException {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT mr.*, l.name as location_name, l.address as location_address " +
                "FROM MedicalRecord mr " +
                "JOIN Location l ON mr.location_id = l.location_id " +
                "WHERE mr.person_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, personId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Person person = new Person("", "");
                person.setPersonId(personId);

                Location location = new Location(
                        rs.getString("location_name"),
                        rs.getString("location_address")
                );
                location.setLocationId(rs.getInt("location_id"));

                Timestamp timestamp = rs.getTimestamp("date_of_treatment");
                String dateStr = timestamp != null ? timestamp.toLocalDateTime().toLocalDate().toString() : "";

                MedicalRecord record = new MedicalRecord(
                        person,
                        location,
                        rs.getString("treatment_details"),
                        dateStr
                );
                record.setMedicalRecordId(rs.getInt("medical_record_id"));
                records.add(record);
            }
        }
        return records;
    }


    /**
     * Retrieves all medical records associated with a specific location from the database.
     *
     * @param locationId The ID of the location whose medical records are to be retrieved.
     * @return A list of `MedicalRecord` objects associated with the specified location.
     * @throws SQLException If an error occurs while executing the SQL query or processing the results.
     */
    public List<MedicalRecord> getMedicalRecordsAtLocation(int locationId) throws SQLException {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT mr.*, p.first_name, p.last_name, l.name as location_name, l.address as location_address " +
                "FROM MedicalRecord mr " +
                "JOIN Person p ON mr.person_id = p.person_id " +
                "JOIN Location l ON mr.location_id = l.location_id " +
                "WHERE mr.location_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, locationId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Person person = new Person(
                        rs.getString("first_name"),
                        rs.getString("last_name")
                );
                person.setPersonId(rs.getInt("person_id"));

                Location location = new Location(
                        rs.getString("location_name"),
                        rs.getString("location_address")
                );
                location.setLocationId(locationId);

                Timestamp timestamp = rs.getTimestamp("date_of_treatment");
                String dateStr = timestamp != null ? timestamp.toLocalDateTime().toLocalDate().toString() : "";

                MedicalRecord record = new MedicalRecord(
                        person,
                        location,
                        rs.getString("treatment_details"),
                        dateStr
                );
                record.setMedicalRecordId(rs.getInt("medical_record_id"));
                records.add(record);
            }
        }
        return records;
    }











    // Code for Person

    /**
     * Retrieves a list of all people from the database, including information about their supplies if they are disaster victims.
     * If a person is a disaster victim, their allocated supplies are also fetched and added to their personal inventory.
     *
     * @return A list of `Person` objects, potentially including `DisasterVictim` objects if applicable.
     * @throws SQLException If there is an error executing the database queries.
     */
    public List<Person> getAllPeople() throws SQLException {
        List<Person> people = new ArrayList<>();

        // First get all people
        String personSql = "SELECT * FROM Person";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(personSql)) {

            // Pre-fetch all supply allocations to minimize database queries
            Map<Integer, Boolean> hasSuppliesMap = getPeopleWithSuppliesMap();

            while (rs.next()) {
                int personId = rs.getInt("person_id");
                boolean isDisasterVictim = hasSuppliesMap.getOrDefault(personId, false);

                Person person;
                if (isDisasterVictim) {
                    person = new DisasterVictim(
                            rs.getString("first_name"),
                            rs.getString("last_name")
                    );
                } else {
                    person = new Person(
                            rs.getString("first_name"),
                            rs.getString("last_name")
                    );
                }

                person.setPersonId(personId);

                // Set date of birth if available
                String dob = rs.getString("date_of_birth");
                if (dob != null) {
                    person.setDateOfBirth(dob);
                }

                person.setGender(rs.getString("gender"));
                person.setComments(rs.getString("comments"));

                // Handle phone number
                String phoneNumber = rs.getString("phone_number");
                if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                    person.setPhoneNumber(phoneNumber);
                }

                // Handle family group
                int familyGroupId = rs.getInt("family_group");
                if (!rs.wasNull()) {
                    FamilyGroup familyGroup = getFamilyGroupById(familyGroupId);
                    if (familyGroup != null) {
                        person.setFamilyGroup(familyGroup);
                    }
                }

                // If this is a DisasterVictim, load their personal inventory
                if (isDisasterVictim && person instanceof DisasterVictim) {
                    DisasterVictim victim = (DisasterVictim) person;
                    List<Supply> supplies = getSuppliesAllocatedTo(personId, null);
                    victim.setPersonalInventory(new ArrayList<>(supplies));
                }

                people.add(person);
            }
        }
        return people;
    }

    /**
     * Retrieves a map of person IDs that have supplies allocated to them.
     *
     * @return A map where the key is a person ID and the value is `true` if the person has supplies allocated.
     * @throws SQLException If there is an error executing the SQL query.
     */
    private Map<Integer, Boolean> getPeopleWithSuppliesMap() throws SQLException {
        Map<Integer, Boolean> result = new HashMap<>();
        String sql = "SELECT DISTINCT person_id FROM SupplyAllocation WHERE person_id IS NOT NULL";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.put(rs.getInt("person_id"), true);
            }
        }
        return result;
    }

    /**
     * Retrieves a person via ID from database.
     * First database function created, largely a test functions.
     *
     * @return a person object
     * @throws SQLException If there is an error executing the SQL query.
     */
    public Person getPersonById(int personId) throws SQLException {
        String sql = "SELECT * FROM Person WHERE person_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, personId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Determine if this person should be a DisasterVictim
                boolean isDisasterVictim = checkIfPersonHasSupplies(personId);

                Person person;
                if (isDisasterVictim) {
                    // Create as DisasterVictim
                    person = new DisasterVictim(
                            rs.getString("first_name"),
                            rs.getString("last_name")
                    );
                } else {
                    // Create as regular Person
                    person = new Person(
                            rs.getString("first_name"),
                            rs.getString("last_name")
                    );
                }

                person.setPersonId(personId);

                // Set date of birth if available
                String dob = rs.getString("date_of_birth");
                if (dob != null) {
                    person.setDateOfBirth(dob);
                }

                // Set other fields
                person.setGender(rs.getString("gender"));
                person.setComments(rs.getString("comments"));

                // Handle phone number
                String phoneNumber = rs.getString("phone_number");
                if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                    person.setPhoneNumber(phoneNumber);
                }

                // Handle family group
                int familyGroupId = rs.getInt("family_group");
                if (!rs.wasNull()) {
                    FamilyGroup familyGroup = getFamilyGroupById(familyGroupId);
                    if (familyGroup != null) {
                        person.setFamilyGroup(familyGroup);
                    }
                }

                // If this is a DisasterVictim, load their personal inventory
                if (isDisasterVictim && person instanceof DisasterVictim) {
                    DisasterVictim victim = (DisasterVictim) person;
                    List<Supply> supplies = getSuppliesAllocatedTo(personId, null);
                    victim.setPersonalInventory(new ArrayList<>(supplies));
                }

                return person;
            }
        }
        return null;
    }


    /**
     * Checks whether a person has any supplies allocated to them.
     *
     * @param personId The ID of the person to check for supply allocations.
     * @return true if the person has one or more supplies allocated; false otherwise.
     * @throws SQLException if there is an error executing the SQL query.
     */
    private boolean checkIfPersonHasSupplies(int personId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM SupplyAllocation WHERE person_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, personId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    /**
     * Helper method that retrieves a family group based on the given family group ID
     *
     * @param familyGroupId The ID of the family group to retrieve.
     * @return A FamilyGroup object containing the list of people in the family group,
     *         or null if no members are found for the given family group ID.
     * @throws SQLException if there is an error executing the SQL query.
     */
    private FamilyGroup getFamilyGroupById(int familyGroupId) throws SQLException {
        String sql = "SELECT * FROM Person WHERE family_group = ?";
        List<Person> members = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, familyGroupId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Person person = new Person(
                        rs.getString("first_name"),
                        rs.getString("last_name")
                );
                person.setPersonId(rs.getInt("person_id"));
                // Set other fields as needed
                members.add(person);
            }
        }

        if (!members.isEmpty()) {
            FamilyGroup familyGroup = new FamilyGroup(new ArrayList<>(members));
            familyGroup.setFamilyGroupId(familyGroupId);
            return familyGroup;
        }
        return null;
    }



    /**
     * Adds a new person to the database.
     *
     * @param person The person object containing the details to be inserted.
     * @throws SQLException if there is an error executing the SQL query.
     */
    public void addPerson(Person person) throws SQLException {
        String sql = "INSERT INTO Person (first_name, last_name, date_of_birth, gender, comments, phone_number, family_group) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING person_id";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, person.getFirstName());
            pstmt.setString(2, person.getLastName());

            // Handle date_of_birth properly
            if (person.getDateOfBirth() != null && !person.getDateOfBirth().isEmpty()) {
                pstmt.setDate(3, Date.valueOf(person.getDateOfBirth()));
            } else {
                pstmt.setNull(3, Types.DATE);
            }

            pstmt.setString(4, person.getGender());
            pstmt.setString(5, person.getComments());
            pstmt.setString(6, person.getPhoneNumber());

            if (person.getFamilyGroup() != null) {
                pstmt.setInt(7, person.getFamilyGroup().getFamilyGroupId());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                person.setPersonId(rs.getInt("person_id"));
            }
        }
    }


    /**
     * Updates the details of an existing person in the database.
     *
     * @param person The person object containing the updated details.
     * @throws SQLException if there is an error executing the SQL query or if no rows are affected.
     */
    public void updatePerson(Person person) throws SQLException {
        String sql = "UPDATE Person SET first_name = ?, last_name = ?, date_of_birth = ?, " +
                "gender = ?, comments = ?, phone_number = ?, family_group = ? " +
                "WHERE person_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, person.getFirstName());
            pstmt.setString(2, person.getLastName());

            // Handle date_of_birth properly
            if (person.getDateOfBirth() != null && !person.getDateOfBirth().isEmpty()) {
                pstmt.setDate(3, Date.valueOf(person.getDateOfBirth()));
            } else {
                pstmt.setNull(3, Types.DATE);
            }

            pstmt.setString(4, person.getGender());
            pstmt.setString(5, person.getComments());
            pstmt.setString(6, person.getPhoneNumber());

            if (person.getFamilyGroup() != null) {
                pstmt.setInt(7, person.getFamilyGroup().getFamilyGroupId());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }

            pstmt.setInt(8, person.getPersonId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating person failed, no rows affected.");
            }
        }
    }


    /**
     * Deletes a person and all their related records from the database.
     *
     * @param personId The ID of the person to be deleted.
     * @throws SQLException if any error occurs during the database operations.
     */
    public void deletePerson(int personId) throws SQLException {
        // First delete dependent records to maintain referential integrity
        String[] deleteQueries = {
                "DELETE FROM MedicalRecord WHERE person_id = ?",
                "DELETE FROM PersonLocation WHERE person_id = ?",
                "DELETE FROM SupplyAllocation WHERE person_id = ?",
                "DELETE FROM Inquiry WHERE inquirer_id = ? OR seeking_id = ?",
                "DELETE FROM Person WHERE person_id = ?"
        };

        try {
            connection.setAutoCommit(false); // Start transaction

            for (String query : deleteQueries) {
                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    // For the Inquiry query which has two parameters
                    if (query.contains("OR seeking_id")) {
                        pstmt.setInt(1, personId);
                        pstmt.setInt(2, personId);
                    } else {
                        pstmt.setInt(1, personId);
                    }
                    pstmt.executeUpdate();
                }
            }

            connection.commit(); // Commit transaction if all queries succeed
        } catch (SQLException e) {
            connection.rollback(); // Rollback if any query fails
            throw e;
        } finally {
            connection.setAutoCommit(true); // Reset auto-commit
        }
    }










    // Code for Inquiry


    /**
     * Retrieves all inquiries from the database, along with related information such as the inquirer,
     * the person they are seeking, the location of the inquiry, and the date of the inquiry.
     *
     * This method performs a JOIN operation between the Inquiry, Person, and Location tables to
     * gather all the relevant information.
     *
     * @return a list of all inquiries with their associated details.
     * @throws SQLException if any error occurs while accessing the database.
     */
    public List<Inquiry> getAllInquiries() throws SQLException {
        List<Inquiry> inquiries = new ArrayList<>();
        String sql = "SELECT i.*, " +
                "p1.first_name as inquirer_first, p1.last_name as inquirer_last, " +
                "p2.first_name as seeking_first, p2.last_name as seeking_last, " +
                "l.name as location_name, l.address as location_address " +
                "FROM Inquiry i " +
                "JOIN Person p1 ON i.inquirer_id = p1.person_id " +
                "JOIN Person p2 ON i.seeking_id = p2.person_id " +
                "JOIN Location l ON i.location_id = l.location_id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Create inquirer (regular Person)
                Person inquirer = new Person(
                        rs.getString("inquirer_first"),
                        rs.getString("inquirer_last")
                );
                inquirer.setPersonId(rs.getInt("inquirer_id"));

                // Create missing person (DisasterVictim)
                DisasterVictim missingPerson = new DisasterVictim(
                        rs.getString("seeking_first"),
                        rs.getString("seeking_last")
                );
                missingPerson.setPersonId(rs.getInt("seeking_id"));

                // Create location
                Location location = new Location(
                        rs.getString("location_name"),
                        rs.getString("location_address")
                );
                location.setLocationId(rs.getInt("location_id"));

                // Create inquiry
                Timestamp timestamp = rs.getTimestamp("date_of_inquiry");
                String dateStr = timestamp != null ? timestamp.toLocalDateTime().toLocalDate().toString() : "";

                Inquiry inquiry = new Inquiry(
                        inquirer,
                        missingPerson,
                        dateStr,
                        rs.getString("comments"),
                        location
                );
                inquiry.setInquiryId(rs.getInt("inquiry_id"));

                inquiries.add(inquiry);
            }
        }
        return inquiries;
    }




    /**
     * Retrieves a single inquiry from the database based on the inquiry ID, along with related details
     * such as the inquirer, the missing person, the location of the inquiry, and the date of the inquiry.
     * This method performs a JOIN operation between the Inquiry, Person, and Location tables.
     *
     * Mainly used to practice JOIN for queries and seeing there outputs.
     *
     * @param inquiryId the unique identifier of the inquiry to be fetched.
     * @return the Inquiry object corresponding to the given inquiry ID, or null if no inquiry is found.
     * @throws SQLException if any error occurs while accessing the database.
     */
    public Inquiry getInquiryById(int inquiryId) throws SQLException {
        String sql = "SELECT i.*, " +
                "p1.first_name as inquirer_first, p1.last_name as inquirer_last, " +
                "p2.first_name as seeking_first, p2.last_name as seeking_last, " +
                "l.name as location_name, l.address as location_address " +
                "FROM Inquiry i " +
                "JOIN Person p1 ON i.inquirer_id = p1.person_id " +
                "JOIN Person p2 ON i.seeking_id = p2.person_id " +
                "JOIN Location l ON i.location_id = l.location_id " +
                "WHERE i.inquiry_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, inquiryId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Create inquirer (regular Person)
                Person inquirer = new Person(
                        rs.getString("inquirer_first"),
                        rs.getString("inquirer_last")
                );
                inquirer.setPersonId(rs.getInt("inquirer_id"));

                // Create missing person (DisasterVictim)
                DisasterVictim missingPerson = new DisasterVictim(
                        rs.getString("seeking_first"),
                        rs.getString("seeking_last")
                );
                missingPerson.setPersonId(rs.getInt("seeking_id"));

                // Create location
                Location location = new Location(
                        rs.getString("location_name"),
                        rs.getString("location_address")
                );
                location.setLocationId(rs.getInt("location_id"));

                // Create inquiry
                Timestamp timestamp = rs.getTimestamp("date_of_inquiry");
                String dateStr = timestamp != null ? timestamp.toLocalDateTime().toLocalDate().toString() : "";

                Inquiry inquiry = new Inquiry(
                        inquirer,
                        missingPerson,
                        dateStr,
                        rs.getString("comments"),
                        location
                );
                inquiry.setInquiryId(rs.getInt("inquiry_id"));

                return inquiry;
            }
        }
        return null;
    }

    /**
     * Adds a new inquiry to the database. This method inserts a record into the Inquiry table, including
     * the inquirer, missing person, location, date of the inquiry, and comments.
     *
     * @param inquiry the Inquiry object containing all the necessary details to be added.
     * @throws SQLException if any error occurs during database interaction.
     */
    public void addInquiry(Inquiry inquiry) throws SQLException {
        String sql = "INSERT INTO Inquiry (inquirer_id, seeking_id, location_id, date_of_inquiry, comments) " +
                "VALUES (?, ?, ?, ?::timestamp, ?) RETURNING inquiry_id";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, inquiry.getInquirer().getPersonId());
            pstmt.setInt(2, inquiry.getMissingPerson().getPersonId());
            pstmt.setInt(3, inquiry.getLastKnownLocation().getLocationId());
            pstmt.setString(4, inquiry.getDateOfInquiry() + " 00:00:00"); // Add time component
            pstmt.setString(5, inquiry.getInfoProvided());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                inquiry.setInquiryId(rs.getInt("inquiry_id"));
            }
        }
    }

    /**
     * Updates an existing inquiry in the database.
     *
     * @param inquiry the Inquiry object containing the updated details for the inquiry.
     * @throws SQLException if any error occurs during database interaction.
     */
    public void updateInquiry(Inquiry inquiry) throws SQLException {
        String sql = "UPDATE Inquiry SET inquirer_id = ?, seeking_id = ?, location_id = ?, " +
                "date_of_inquiry = ?::timestamp, comments = ? " +
                "WHERE inquiry_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, inquiry.getInquirer().getPersonId());
            pstmt.setInt(2, inquiry.getMissingPerson().getPersonId());
            pstmt.setInt(3, inquiry.getLastKnownLocation().getLocationId());
            pstmt.setString(4, inquiry.getDateOfInquiry() + " 00:00:00"); // Add time component
            pstmt.setString(5, inquiry.getInfoProvided());
            pstmt.setInt(6, inquiry.getInquiryId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating inquiry failed, no rows affected.");
            }
        }
    }


    /**
     * Deletes an inquiry from the database based on its inquiryId. This method removes the record
     * from the Inquiry table where the inquiry_id matches the provided value.
     *
     * @param inquiryId the unique identifier of the inquiry to be deleted.
     * @throws SQLException if any error occurs during database interaction or if no rows are affected.
     */
    public void deleteInquiry(int inquiryId) throws SQLException {
        String sql = "DELETE FROM Inquiry WHERE inquiry_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, inquiryId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting inquiry failed, no rows affected.");
            }
        }
    }








    // ID management


    /**
     * Gets the largest person ID currently in the database
     *
     * @return The largest person ID, or 0 if no supplies exist
     * @throws SQLException If there's a database error
     */
    public int getLargestPersonId() throws SQLException {
        return getLargestIdFromTable("Person", "person_id");
    }

    /**
     * Gets the largest location ID currently in the database
     *
     * @return The largest locations ID, or 0 if no supplies exist
     * @throws SQLException If there's a database error
     */
    public int getLargestLocationId() throws SQLException {
        return getLargestIdFromTable("Location", "location_id");
    }


    /**
     * Gets the largest inquiry ID currently in the database
     *
     * @return The largest inquiry ID, or 0 if no supplies exist
     * @throws SQLException If there's a database error
     */
    public int getLargestInquiryId() throws SQLException {
        return getLargestIdFromTable("Inquiry", "inquiry_id");
    }

    /**
     * Gets the largest supply ID currently in the database
     *
     * @return The largest supply ID, or 0 if no supplies exist
     * @throws SQLException If there's a database error
     */
    public int getLargestSupplyId() throws SQLException {
        return getLargestIdFromTable("Supply", "supply_id");
    }


    /**
     * Gets the largest Medical Record ID currently in the database
     *
     * @return The largest medical record ID, or 0 if no supplies exist
     * @throws SQLException If there's a database error
     */
    public int getLargestMedicalRecordId() throws SQLException {
        return getLargestIdFromTable("MedicalRecord", "medical_record_id");
    }



    /**
     * Gets the largest ID currently in the database
     *
     *
     * @param tableName Takes the specific table name needed
     * @param idColumn Takes the specific columm name
     * @return The largest supply ID, or 0 if no supplies exist
     * @throws SQLException If there's a database error
     */
    private int getLargestIdFromTable(String tableName, String idColumn) throws SQLException {
        String query = "SELECT MAX(" + idColumn + ") AS max_id FROM " + tableName;

        try (
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt("max_id");
            }
            return 0; // Return 0 if table is empty
        }
    }




}