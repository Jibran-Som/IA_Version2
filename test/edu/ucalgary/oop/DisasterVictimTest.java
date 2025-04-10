/**
 * DisasterVictim.java
 * Version: 2.0
 * Author: Jibran Somroo
 * Date: April 7, 2025
 */

package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

//Note: DisasterVictim is an extended class from Person
// which has a majority of the test cases
public class DisasterVictimTest {

    private DisasterVictim victim;

    @Before
    public void setUp() {
        victim = new DisasterVictim("John", "Smith");
    }

    @Test
    public void testDisasterVictimConstructor() {
        DisasterVictim victimTwo = new DisasterVictim("Angel", "Di Maria");

        assertNotNull("DisasterVictim constructor failed", victimTwo);
        assertEquals("First name not set correctly", "Angel", victimTwo.getFirstName());
        assertEquals("Last name not set correctly", "Di Maria", victimTwo.getLastName());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testDisasterVictimConstructorWithNullFirstName() {
        DisasterVictim victimTwo = new DisasterVictim(null, "Di Maria");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDisasterVictimConstructorWithNullLastName() {
        DisasterVictim victimTwo = new DisasterVictim("Lukaku", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDisasterVictimConstructorWithEmptyFirstName() {
        DisasterVictim victimTwo = new DisasterVictim("", "Romelu");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDisasterVictimConstructorWithEmptyLastName() {
        DisasterVictim victimTwo = new DisasterVictim("Angel", "");
    }


    @Test
    public void testDisasterVictimConstructorWithBirthDate(){
        DisasterVictim victimTwo = new DisasterVictim("John", "Smith", "2005-05-05");
        assertNotNull("DisasterVictim constructor failed", victimTwo);
        assertEquals("First name not set correctly", "John", victimTwo.getFirstName());
        assertEquals("Last name not set correctly", "Smith", victimTwo.getLastName());
        assertEquals("Date of birth not set correctly", "2005-05-05", victimTwo.getDateOfBirth());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testDisasterVictimConstructorInvalidBirthDate() {
        DisasterVictim victimTwo = new DisasterVictim("John", "Smith", "2050-05-05");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDisasterVictimConstructorInvalidBirthDateFormat() {
        DisasterVictim victimTwo = new DisasterVictim("John", "Smith", "2022.05.05");
    }


    @Test
    public void testSetPersonalInventory() {
        // Create test data
        Supply supply = new Supply("Juice", "Drink");
        ArrayList<Supply> personalInventory = new ArrayList<>();
        personalInventory.add(supply);

        // Test setting non-null inventory
        victim.setPersonalInventory(personalInventory);
        assertEquals("Personal inventory not set correctly",
                personalInventory,
                victim.getPersonalInventory());

        // Test that the list contains exactly what we added
        assertEquals(1, victim.getPersonalInventory().size());
        assertEquals(supply, victim.getPersonalInventory().get(0));
    }

    @Test
    public void testSetNullPersonalInventory() {
        // Test setting null inventory
        victim.setPersonalInventory(null);
        assertNotNull("Personal inventory should not be null",
                victim.getPersonalInventory());
        assertTrue("Personal inventory should be empty",
                victim.getPersonalInventory().isEmpty());
    }

    @Test
    public void testAddItem() {
        Supply supply = new Supply("Blanket", "Bedding");
        victim.addItem(supply);
        assertTrue("Item not added to inventory", victim.getPersonalInventory().contains(supply));
    }

    @Test
    public void testRemoveItem() {
        Supply supply = new Supply("Juice", "Drink");
        victim.addItem(supply);
        victim.removeItem(supply);
        assertFalse("Item not removed from inventory", victim.getPersonalInventory().contains(supply));
    }

    @Test
    public void testAddPersonalBelongingToInventory() {
        PersonalBelonging item = new PersonalBelonging("Backpack", "Storage", "White with straps");
        victim.addItem(item);
        assertTrue("Item not added to inventory", victim.getPersonalInventory().contains(item));
    }






}