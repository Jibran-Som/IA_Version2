package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class PersonalBelongingTest {

    private PersonalBelonging personalBelonging;



    @Before
    public void setUp() {
        personalBelonging = new PersonalBelonging("Backpack", "Bag", "Blue backpack");
    }



    @Test
    public void testPersonalBelongingConstructor() {
        assertNotNull("PersonalBelonging constructor failed", personalBelonging);
        assertEquals("Supply name not set correctly", "Backpack", personalBelonging.getSupplyName());
        assertEquals("Supply type not set correctly", "Bag", personalBelonging.getSupplyType());
        assertEquals("Item description not set correctly", "Blue backpack", personalBelonging.getItemDescription());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testPersonalBelongingConstructorNullType() {
        PersonalBelonging personalBelongingTwo = new PersonalBelonging("Test", null, "Testing");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPersonalBelongingConstructorEmptyType() {
        PersonalBelonging personalBelongingTwo = new PersonalBelonging("Test", "", "Testing");
    }


    @Test
    public void testSetItemDescription() {
        personalBelonging.setItemDescription("Red backpack");
        assertEquals("Item description not updated correctly", "Red backpack", personalBelonging.getItemDescription());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetItemDescriptionEmtpy() {
        personalBelonging.setItemDescription("");
    }


}