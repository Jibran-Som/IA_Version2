/**
 * WaterTest.java
 * Version: 2.0
 * Author: Jibran Somroo
 * Date: March 28, 2025
 */

package edu.ucalgary.oop;


import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;


public class WaterTest {

    private Water water;

    @Before
    public void setUp() {
        water = new Water("Bottled Water", "Drink");
    }

    @Test
    public void testWaterConstructor() {
        assertNotNull("Water constructor failed", water);
        assertEquals("Supply name not set correctly", "Bottled Water", water.getSupplyName());
        assertEquals("Supply type not set correctly", "Drink", water.getSupplyType());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testWaterConstructorWithEmptyType() {
        Water water = new Water("Tap Water", "");
    }


    @Test(expected = IllegalArgumentException.class)
    public void testWaterConstructorWithNullType() {
        Water water = new Water("Mineral Water", null);
    }

    @Test
    public void testSetAllocationDate() {
        water.setAllocationDate("2023-01-01");
        assertEquals("Allocation date not set correctly", "2023-01-01", water.getAllocationDate());
    }


    @Test
    public void testSetSupplyNameForWater() {
        //arrange
        String expectedName = "Bottled Mineral Water";

        Water waterTwo = new Water("Bottled Water", "Drink");
        //act
        waterTwo.setSupplyName(expectedName);
        //assert
        assertEquals("Allocation date not set correctly", expectedName, waterTwo.getSupplyName());
    }

    @Test
    public void testSetSupplyTypeForWater() {
        //arrange
        String expectedType = "Unpurified";
        Water waterTwo = new Water("Bottled Water", "Drink");
        //act
        waterTwo.setSupplyType(expectedType);

        //Assert
        assertEquals("Allocation date not set correctly", expectedType, waterTwo.getSupplyType());
    }


}