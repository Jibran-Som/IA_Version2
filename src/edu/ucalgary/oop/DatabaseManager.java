package edu.ucalgary.oop;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    private final String DB_URL = "jdbc:postgresql://localhost:5432/ensf380project";
    private final String USER = "oop";
    private final String PASS = "ucalgary";

    // Constructor
    public DatabaseManager() throws SQLException {
        connect();
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
                Supply supply = new Supply(
                        rs.getString("comments"), // Using comments as supplyName
                        rs.getString("type")
                ) {};
                supply.setSupplyId(rs.getInt("supply_id"));
                supplies.add(supply);
            }
        }
        return supplies;
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
        String sql = "SELECT * FROM Person";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Person person = new Person(
                        rs.getString("first_name"),
                        rs.getString("last_name")
                );
                person.setPersonId(rs.getInt("person_id"));

                // Only set date of birth if it's not null
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
                if (!rs.wasNull()) {  // Check if family_group was not NULL
                    FamilyGroup familyGroup = getFamilyGroupById(familyGroupId);
                    if (familyGroup != null) {
                        person.setFamilyGroup(familyGroup);
                    }
                }

                people.add(person);
            }
        }
        return people;
    }

    public Person getPersonById(int personId) throws SQLException {
        String sql = "SELECT * FROM Person WHERE person_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, personId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Person person = new Person(
                        rs.getString("first_name"),
                        rs.getString("last_name")
                );
                person.setPersonId(personId);

                // Only set date of birth if it's not null
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
                if (!rs.wasNull()) {  // Check if family_group was not NULL
                    FamilyGroup familyGroup = getFamilyGroupById(familyGroupId);
                    if (familyGroup != null) {
                        person.setFamilyGroup(familyGroup);
                    }
                }

                return person;
            }
        }
        return null;
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
            pstmt.setString(3, person.getDateOfBirth());
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






}