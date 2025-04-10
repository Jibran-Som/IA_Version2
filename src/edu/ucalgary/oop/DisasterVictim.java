/**
 * DisasterVictim.java
 * Version: 1.0
 * Author: Jibran Somroo
 * Date: April 7, 2025
 */

package edu.ucalgary.oop;

import java.util.ArrayList;

public class DisasterVictim extends Person {
    private ArrayList<Supply> personalInventory;


    /**
     * Constructor for creating a DisasterVictim object.
     * Initializes the first name and last name through the parent class constructor
     * and sets up an empty personal inventory.
     *
     * @param firstName The first name of the disaster victim.
     * @param lastName The last name of the disaster victim.
     */
    public DisasterVictim(String firstName, String lastName) {
        super(firstName, lastName);
        this.personalInventory = new ArrayList<>();
    }

    /**
     * Constructor for creating a DisasterVictim object.
     * Initializes the first name, last name and dob through the parent class constructor
     * and sets up an empty personal inventory.
     *
     * @param firstName The first name of the disaster victim.
     * @param lastName The last name of the disaster victim.
     * @param dateOfBirth The dob of the disaster victim.
     */
    public DisasterVictim(String firstName, String lastName, String dateOfBirth) {
        super(firstName, lastName, dateOfBirth);
        this.personalInventory = new ArrayList<>();
    }


    /**
     * Retrieves the personal inventory of the disaster victim.
     *
     * @return an ArrayList of Supply objects representing the person's personal inventory.
     */
    public ArrayList<Supply> getPersonalInventory() {
        return personalInventory;
    }



    /**
     * Sets the personal inventory of the person.
     *
     * @param personalInventory an ArrayList of Supply objects representing a person's personal inventory.
     */
    public void setPersonalInventory(ArrayList<Supply> personalInventory) {
        if (personalInventory == null) {
            this.personalInventory = new ArrayList<>(); // Initialize empty list instead of null
        } else {
            this.personalInventory = personalInventory;
        }
    }


    /**
     * Adds a supply item to the person's personal inventory.
     *
     * @param supply The Supply object to be added to the inventory.
     */
    public void addItem(Supply supply) {
        personalInventory.add(supply);
    }

    /**
     * Removes a supply item to the person's personal inventory.
     *
     * @param supply The Supply object to be added to the inventory.
     */
    public void removeItem(Supply supply) {
        personalInventory.remove(supply);
    }







}
