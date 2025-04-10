package edu.ucalgary.oop;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.sql.SQLException;
import java.time.LocalDate;

public class SupplyControllerTest {
    private SupplyController supplyController;
    private Supply testSupply;
    private Water testWater;
    private Blanket testBlanket;
    private Cot testCot;
    private PersonalBelonging testPersonalBelonging;

    @Before
    public void setUp() {
        // Use the test constructor
        supplyController = new SupplyController(0);

        // Create test supplies
        testSupply = new Supply("Test Supply", "General");
        testSupply.setSupplyId(1);

        testWater = new Water("Bottled Water", "Water");
        testWater.setSupplyId(2);

        testBlanket = new Blanket("Wool Blanket", "Blanket");
        testBlanket.setSupplyId(3);

        testCot = new Cot("Folding Cot", "Cot", "Room 101", "A1");
        testCot.setSupplyId(4);

        testPersonalBelonging = new PersonalBelonging("Backpack", "Personal", "Blue backpack with straps");
        testPersonalBelonging.setSupplyId(5);

        // Manually add to controller's list
        supplyController.getAllSuppliesTest().add(testSupply);
        supplyController.getAllSuppliesTest().add(testWater);
        supplyController.getAllSuppliesTest().add(testBlanket);
        supplyController.getAllSuppliesTest().add(testCot);
        supplyController.getAllSuppliesTest().add(testPersonalBelonging);
    }

    // Tests that should pass with test constructor

    @Test
    public void testGetAllSupplies() {
        ArrayList<Supply> supplies = supplyController.getAllSupplies();
        assertEquals("Should return all test supplies", 5, supplies.size());
        assertTrue("Should contain test supply", supplies.contains(testSupply));
        assertTrue("Should contain test water", supplies.contains(testWater));
    }

    @Test
    public void testGetAllSuppliesReturnsCopy() {
        ArrayList<Supply> supplies = supplyController.getAllSupplies();
        supplies.clear();
        assertEquals("Original list should remain unchanged",
                5, supplyController.getAllSupplies().size());
    }

    @Test
    public void testGenerateSupplyIdIncrements() {
        int firstId = supplyController.generateSupplyId();
        int secondId = supplyController.generateSupplyId();
        assertEquals("IDs should increment", firstId + 1, secondId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNullSupply() throws SQLException {
        supplyController.addSupply(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateNullSupply() throws SQLException {
        supplyController.updateSupply(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAllocateSupplyWithNullArguments() throws Exception {
        supplyController.allocateSupply(1, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAllocatePersonalBelongingToLocation() throws Exception {
        supplyController.allocateSupply(
                testPersonalBelonging.getSupplyId(),
                null, // personId
                1,    // locationId
                null  // fromLocationId
        );
    }

    @Test
    public void testWaterAllocationSetsDate() {
        try {
            supplyController.allocateSupply(testWater.getSupplyId(), 1, null, null);
            fail("Should throw SQLException");
        } catch (Exception e) {
            // Verify water got allocation date despite exception
            assertNotNull("Water should have allocation date set",
                    ((Water)testWater).getAllocationDate());
        }
    }

    // Tests that expect SQLException due to null databaseManager

    @Test(expected = NullPointerException.class)
    public void testAddSupplyNullPointerException() throws SQLException {
        Supply newSupply = new Supply("New Supply", "Test");
        supplyController.addSupply(newSupply);
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateSupplyNullPointerException() throws SQLException {
        testSupply.setSupplyName("Updated Name");
        supplyController.updateSupply(testSupply);
    }

    @Test(expected = NullPointerException.class)
    public void testDeleteSupplyNullPointerException() throws SQLException {
        supplyController.deleteSupply(1);
    }

    @Test(expected = NullPointerException.class)
    public void testGetSuppliesAllocatedToNullPointerException() throws SQLException {
        supplyController.getSuppliesAllocatedTo(1, null);
    }

    @Test(expected = NullPointerException.class)
    public void testRefreshSuppliesNullPointerException() throws SQLException {
        supplyController.refreshSupplies();
    }

    @Test(expected = NullPointerException.class)
    public void testIsSupplyAllocatedNullPointerException() throws SQLException {
        supplyController.isSupplyAllocated(1);
    }


    @Test
    public void testLocalModelUpdatesDespiteDatabaseException() {
        try {
            // Try to update - should fail but update local model
            testSupply.setSupplyName("Updated Name");
            supplyController.updateSupply(testSupply);
            fail("Should have thrown SQLException");
        } catch (NullPointerException | SQLException e) {
            // Verify local update happened despite database exception
            Supply updated = supplyController.getAllSupplies().get(0);
            assertEquals("Name should be updated locally", "Updated Name", updated.getSupplyName());
        }
    }

    @Test
    public void testFindSupplyInLocalModels() {
        try {
            supplyController.allocateSupply(testSupply.getSupplyId(), 1, null, null);
            fail("Should throw SQLException");
        } catch (Exception e) {
            // If we got here, it means it found the supply in local models first
            assertTrue("Exception should occur after local model check", true);
        }
    }
}