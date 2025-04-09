package edu.ucalgary.oop;

import java.sql.SQLException;

public class Blanket extends Supply {


    /**
     * Constructor for creating a Blanket object.
     * Initializes the supply name and supply type by calling the parent class constructor.
     *
     * @param supplyName The name of the blanket supply.
     * @param supplyType The type or category of the blanket supply.
     */
    public Blanket(String supplyName, String supplyType) {
        super(supplyName, supplyType); // Call parent constructor
    }


}