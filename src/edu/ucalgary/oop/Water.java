/**
 * Water.java
 * Version: 1.0
 * Author: Jibran Somroo
 * Date: April 7, 2025
 */

package edu.ucalgary.oop;

public class Water extends Supply {
    private String allocationDate;
    public static final int EXPIRATION_DAYS = 1;


    /**
     * Constructor for creating a Water supply.
     *
     * @param supplyName The name of the water supply
     * @param supplyType The type of the supply
     */
    public Water(String supplyName, String supplyType) {
        super(supplyName, supplyType);
    }

    /**
     * Retrieves the allocation date of the supply.
     *
     * @return The allocation date as a string.
     */
    public String getAllocationDate() {
        return allocationDate;
    }

    /**
     * Sets the allocation date for the supply.
     *
     * @param allocationDate The allocation date to be set for the supply.
     */
    public void setAllocationDate(String allocationDate) {
        this.allocationDate = allocationDate;
    }


}