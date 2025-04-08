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

    // Constructor
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
    // Class Specific Code
    private void connect() throws SQLException {
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            throw e;
        }
    }

    // Close the database connection
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // Singleton
    public static synchronized DatabaseManager getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }









    // Code for Supply

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

    // Getter
    public List<Supply> getAllSupplies() throws SQLException {
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
                            continue; // Skip expired water
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

    private boolean isWaterExpired(String allocationDate) {
        if (allocationDate == null) return false; // Unallocated water doesn't expire

        LocalDate allocation = LocalDate.parse(allocationDate);
        LocalDate expirationDate = allocation.plusDays(Water.EXPIRATION_DAYS);
        return LocalDate.now().isAfter(expirationDate);
    }

    public void deleteExpiredSupplies() throws SQLException {
        // First get all water supplies with their allocation dates
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




    // Updater
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

    // Deleter
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

    // Allocate
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

    public boolean isSupplyAtLocation(int supplyId, int locationId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM SupplyAllocation WHERE supply_id = ? AND location_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, supplyId);
            pstmt.setInt(2, locationId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public void removeSupplyFromLocation(int supplyId, int locationId) throws SQLException {
        String sql = "DELETE FROM SupplyAllocation WHERE supply_id = ? AND location_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, supplyId);
            pstmt.setInt(2, locationId);
            pstmt.executeUpdate();
        }
    }


    // Getter for Location and DV
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

    public void addPersonToLocation(int personId, int locationId) throws SQLException {
        String sql = "INSERT INTO PersonLocation (person_id, location_id) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, personId);
            pstmt.setInt(2, locationId);

            pstmt.executeUpdate();
        }
    }

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

    // In DatabaseManager.java - update getMedicalRecordsAtLocation
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











    //Person
    // In DatabaseManager.java

    // Person-related methods
// In DatabaseManager.java

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

    private boolean checkIfPersonHasSupplies(int personId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM SupplyAllocation WHERE person_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, personId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // New helper method to get FamilyGroup by ID
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





    // Add these methods to DatabaseManager.java

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


}