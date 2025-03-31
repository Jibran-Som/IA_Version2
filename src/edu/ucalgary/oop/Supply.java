package edu.ucalgary.oop;

public abstract class Supply {
    private String supplyName;
    private String supplyType;
    private int supplyId = createSupplyId();
    private static int counter = 100;


    // Constructor
    public Supply(String supplyName, String supplyType) {
        if (supplyName == null || supplyName.trim().isEmpty()) {
            throw new IllegalArgumentException("Supply name cannot be null or empty");
        }
        if (supplyType == null || supplyType.trim().isEmpty()) {
            throw new IllegalArgumentException("Supply type cannot be null or empty");
        }
        this.supplyName = supplyName;
        this.supplyType = supplyType;
    }


    // Getters
    public String getSupplyName() {
        return supplyName;
    }

    public String getSupplyType() {
        return supplyType;
    }

    public int getSupplyId() {
        return supplyId;
    }


    // Setters
    public void setSupplyName(String supplyName) {
        if (supplyName == null || supplyName.trim().isEmpty()) {
            throw new IllegalArgumentException("Supply name cannot be null or empty");
        }
        this.supplyName = supplyName;
    }

    public void setSupplyType(String supplyType) {
        if (supplyType == null || supplyType.trim().isEmpty()) {
            throw new IllegalArgumentException("Supply type cannot be null or empty");
        }
        this.supplyType = supplyType;
    }


    public void setSupplyId(int supplyId) {
        this.supplyId = supplyId;
    }






    // Private Code for Checking or Initialization

    private int createSupplyId() {
        return counter++;
    }

}
