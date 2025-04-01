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

    public int addSupply(Supply supply) throws SQLException {
        String sql = "INSERT INTO Supply (type, comments) VALUES (?, ?) RETURNING supply_id";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, supply.getSupplyType());
            pstmt.setString(2, supply.getSupplyName()); // Using supplyName as comments

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
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
}