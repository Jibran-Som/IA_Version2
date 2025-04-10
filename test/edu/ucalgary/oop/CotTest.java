package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class CotTest {

    private Cot cot;


    @Before
    public void setUp() {
        cot = new Cot("Folding Cot", "Bedding", "Room 101", "Grid A1");
    }

    @Test
    public void testCotConstructor() {
        assertNotNull("Cot constructor failed", cot);
        assertEquals("Supply name not set correctly", "Folding Cot", cot.getSupplyName());
        assertEquals("Supply type not set correctly", "Bedding", cot.getSupplyType());
        assertEquals("Room location not set correctly", "Room 101", cot.getRoomLocation());
        assertEquals("Grid location not set correctly", "Grid A1", cot.getGridLocation());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testCotConstructorNullGridLocation() {
        Cot cotTwo = new Cot("Test", "Bedding", "Room 101", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCotConstructorEmptyGridLocation() {
        Cot cotTwo = new Cot("Test", "Bedding", "Room 101", "");
    }


    @Test
    public void testSetRoomLocation() {
        cot.setRoomLocation("Room 102");
        assertEquals("Room location not updated correctly", "Room 102", cot.getRoomLocation());
    }

    @Test
    public void testSetGridLocation() {
        cot.setGridLocation("Grid B2");
        assertEquals("Grid location not updated correctly", "Grid B2", cot.getGridLocation());
    }

    @Test
    public void testCotSetSupplyName() {
        cot.setSupplyName("Test");
        assertEquals("Grid location not updated correctly", "Test", cot.getSupplyName());
    }

    @Test
    public void testCotSetSupplyType() {
        cot.setSupplyType("Test");
        assertEquals("Grid location not updated correctly", "Test", cot.getSupplyType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCotSetEmptySupplyType() {
        cot.setSupplyType("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCotSetNullSupplyType() {
        cot.setSupplyType(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCotSetEmptySupplyName() {
        cot.setSupplyName("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNullSupplyName() {
        cot.setSupplyName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNullGridLocation() {
        cot.setGridLocation(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetEmptyGridLocation() {
        cot.setGridLocation("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetEmptyRoomLocation() {
        cot.setRoomLocation("");
    }




}