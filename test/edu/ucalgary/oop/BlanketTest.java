package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class BlanketTest {

    private Blanket blanket;

    @Before
    public void setUp() {
        blanket = new Blanket("Wool Blanket", "Bedding");
    }

    @Test
    public void testBlanketConstructor() {
        assertNotNull("Blanket constructor has failed", blanket);
        assertEquals("Supply name not set correctly", "Wool Blanket", blanket.getSupplyName());
        assertEquals("Supply type not set correctly", "Bedding", blanket.getSupplyType());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testBlanketConstructorEmptySupplyType() {
        Blanket blanketTwo = new Blanket("Warm Blanket", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBlanketConstructorNullSupplyType() {
        Blanket blanketTwo = new Blanket("Warm Blanket", null);
    }

    @Test
    public void testBlanketSetSupplyName() {
        //arrange
        String expectedSupplyName = "Wool Blanket";
        Blanket blanketTwo = new Blanket("Heated Blanket", "Bedding");

        //act
        blanketTwo.setSupplyName(expectedSupplyName);

        //assert
        assertEquals("Supply name not set correctly", expectedSupplyName, blanketTwo.getSupplyName());
    }

    @Test
    public void testBlanketSetSupplyType() {
        //arrange
        String expectedSupplyType = "Emergency Blanket";
        Blanket blanketTwo = new Blanket("Heated Blanket", "Warm Bedding");

        //act
        blanketTwo.setSupplyType(expectedSupplyType);


        //assert
        assertEquals("Supply type not set correctly", expectedSupplyType, blanketTwo.getSupplyType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBlanketSetSupplyTypeWithNullSupplyType() {
        Blanket blanketTwo = new Blanket("Heated Blanket", "Warm Bedding");
        blanketTwo.setSupplyType(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBlanketSetSupplyNameWithNullSupplyName() {
        Blanket blanketTwo = new Blanket("Heated Blanket", "Warm Bedding");
        blanketTwo.setSupplyName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBlanketSetSupplyNameWithEmptySupplyName() {
        Blanket blanketTwo = new Blanket("Heated Blanket", "Warm Bedding");
        blanketTwo.setSupplyName("");
    }


    @Test(expected = IllegalArgumentException.class)
    public void testBlanketSetSupplyTypeWithEmptySupplyType() {
        Blanket blanketTwo = new Blanket("Heated Blanket", "Warm Bedding");
        blanketTwo.setSupplyType("");
    }





}