package edu.ucalgary.oop;

import java.util.ArrayList;

public class DisasterVictim extends Person {
    private ArrayList<Supply> personalInventory;


    // Constructors
    public DisasterVictim(String firstName, String lastName) {
        super(firstName, lastName);
        this.personalInventory = new ArrayList<>();
    }

    public DisasterVictim(String firstName, String lastName, String dateOfBirth) {
        super(firstName, lastName, dateOfBirth);
        this.personalInventory = new ArrayList<>();
    }


    // Getters
    public ArrayList<Supply> getPersonalInventory() {
        return personalInventory;
    }



    // Setters
    public void setPersonalInventory(ArrayList<Supply> personalInventory) {
        if (personalInventory == null) {
            this.personalInventory = new ArrayList<>(); // Initialize empty list instead of null
        } else {
            this.personalInventory = personalInventory;
        }
    }



    // Class Specific Code
    public void addItem(Supply supply) {
        personalInventory.add(supply);
    }

    public void removeItem(Supply supply) {
        personalInventory.remove(supply);
    }







}
