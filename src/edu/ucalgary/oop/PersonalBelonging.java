package edu.ucalgary.oop;

public class PersonalBelonging extends Supply {
    private String itemDescription;


    // Constructor
    public PersonalBelonging(String supplyName, String supplyType, String itemDescription) {
        super(supplyName, supplyType);
        this.itemDescription = itemDescription;
    }


    // Getters
    public String getItemDescription() {
        return itemDescription;
    }


    // Setters
    public void setItemDescription(String itemDescription) {
        if (itemDescription == null || itemDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Item description cannot be null or empty");
        }
        this.itemDescription = itemDescription;
    }
}