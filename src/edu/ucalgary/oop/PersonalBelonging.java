/**
 * PersonalBelonging.java
 * Version: 1.0
 * Author: Jibran Somroo
 * Date: April 7, 2025
 */

package edu.ucalgary.oop;

public class PersonalBelonging extends Supply {
    private String itemDescription;


    /**
     * Constructs a new PersonalBelonging object.
     *
     * @param supplyName The name of the supply
     * @param supplyType The type of the supply
     * @param itemDescription A description of the item
     */
    public PersonalBelonging(String supplyName, String supplyType, String itemDescription) {
        super(supplyName, supplyType);
        this.itemDescription = itemDescription;
    }


    /**
     * Retrieves the description of the personal belonging item.
     *
     * @return A string representing the description of the personal belonging item.
     */
    public String getItemDescription() {
        return itemDescription;
    }


    /**
     * Sets the description for the personal belonging item.
     *
     * @param itemDescription A string representing the description of the personal belonging item.
     * @throws IllegalArgumentException if the item description is null or empty.
     */
    public void setItemDescription(String itemDescription) {
        if (itemDescription == null || itemDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Item description cannot be null or empty");
        }
        this.itemDescription = itemDescription;
    }
}