package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;


public class SupplyTest {


    @Test
    public void testConstructor() {
        Supply supply = new Supply("Glasses", "Extra");

        assertNotNull("Constructor did not create an instance", supply);
        assertEquals("Glasses", supply.getSupplyName());
        assertEquals("Extra", supply.getSupplyType());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullSupplyType() {
        Supply supply = new Supply("Glasses", null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptySupplyType() {
        Supply supply = new Supply("Glass","");
    }

    @Test
    public void testSetSupplyName() {
        String expectedSupplyName = "Penicillin";
        Supply supply = new Supply("Paper", "Medicine");

        supply.setSupplyName(expectedSupplyName);

        assertEquals("Setter for supply name doesn't work", expectedSupplyName, supply.getSupplyName());

    }

    @Test
    public void testSetSupplyType() {
        String expectedSupplyType = "Medicine";
        Supply supply = new Supply("Penicillin", "Extra");

        supply.setSupplyType(expectedSupplyType);

        assertEquals("Setter for supply name doesn't work", expectedSupplyType, supply.getSupplyType());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testSetSupplyNameWithNullSupplyName() {
        Supply supply = new Supply("Penicillin", "Medicine");
        supply.setSupplyName(null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetSupplyNameWithNullSupplyType() {
        Supply supply = new Supply("Penicillin", "Medicine");
        supply.setSupplyType(null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testSetSupplyTypeWithEmptySupplyName() {
        Supply supply = new Supply("Penicillin", "Medicine");
        supply.setSupplyName("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetSupplyTypeWithEmptySupplyType() {
        Supply supply = new Supply("Penicillin", "Medicine");
        supply.setSupplyType("");
    }


}