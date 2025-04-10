/**
 * LocationTest.java
 * Version: 2.0
 * Author: Jibran Somroo
 * Date: April 7, 2025
 */

package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class LocationTest {

    private Location location;

    @Before
    public void setUp() {
        location = new Location("Hospital", "123 St");
        /* Declared in constructor of Location
        ArrayList<DisasterVictim> occupants = new ArrayList<>();
        ArrayList<Supply> inventory = new ArrayList<>();
        */


    }

    @Test
    public void testLocationConstructor() {
        assertNotNull("Location constructor failed", location);
        assertEquals("Name not set correctly", "Hospital", location.getLocationName());
        assertEquals("Address not set correctly", "123 St", location.getLocationAddress());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLocationConstructorWithNullName() {
        new Location(null, "123 St");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLocationConstructorWithNullAddress() {
        new Location("MIT", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLocationConstructorWithEmptyName() {
        new Location("", "123 St");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLocationConstructorEmptyAddress() {
        new Location("Princeton", "");
    }




    @Test
    public void testAddOccupant() {
        DisasterVictim victim = new DisasterVictim("John", "Smith");
        location.addOccupant(victim);
        assertTrue("Occupant not added", location.getOccupants().contains(victim));
    }

    @Test
    public void testRemoveOccupant() {
        DisasterVictim victim = new DisasterVictim("John", "Smith");
        location.addOccupant(victim);
        location.removeOccupant(victim);
        assertFalse("Occupant not removed", location.getOccupants().contains(victim));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveOccupantWithNull() {

        location.removeOccupant(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveOccupantTwice() {
        DisasterVictim victimTwo = new DisasterVictim("Jacob", "Smith");
        location.removeOccupant(victimTwo);
    }

    @Test
    public void testAddItem() {
        Supply supply = new Supply("Juice", "Drink");
        location.addItem(supply);
        assertTrue("Item not added to inventory", location.getLocationInventory().contains(supply));
    }

    @Test
    public void testRemoveItem() {
        Supply supply = new Supply("Juice", "Drink");
        location.addItem(supply);
        location.removeItem(supply);
        assertFalse("Item not removed from inventory", location.getLocationInventory().contains(supply));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testAddNullItem() {
        location.addItem(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddPersonalBelonging() {
        PersonalBelonging personalBelonging = new PersonalBelonging("Bag", "Heavy", "Damaged");
        location.addItem(personalBelonging);
    }

    @Test
    public void testAllocateItem() {
        DisasterVictim victimTwo = new DisasterVictim("Jacob", "Smith");
        Supply supply = new Supply("Juice", "Drink");
        location.addItem(supply);
        location.allocateItem(victimTwo, supply);
        assertTrue("Item was not allocated to the victim", victimTwo.getPersonalInventory().contains(supply));
        assertFalse("Item was not removed from the location's inventory", location.getLocationInventory().contains(supply));
    }

    @Test
    public void testAllocateWater() {
        DisasterVictim victimTwo = new DisasterVictim("Jacob", "Smith");
        Water supply = new Water("Bottled Water", "Drink");
        location.addItem(supply);
        location.allocateItem(victimTwo, supply);
        assertFalse("Water was allocated to the victim when it should have been deleted", victimTwo.getPersonalInventory().contains(supply));
        assertFalse("Item was not removed from the location's inventory", location.getLocationInventory().contains(supply));
    }



}