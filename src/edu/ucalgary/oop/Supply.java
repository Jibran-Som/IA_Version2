/**
 * Supply.java
 * Version: 1.0
 * Author: Jibran Somroo
 * Date: April 7, 2025
 */

package edu.ucalgary.oop;

import java.sql.SQLException;

public class Supply {
    private String supplyName;
    private String supplyType;
    private int supplyId = -1;
    private static TranslationManager translationManager = TranslationManager.getInstance();


    /**
     * Constructs a Supply object with the specified supply name and supply type.
     *
     * @param supplyName The name of the supply
     * @param supplyType The type of the supply
     * @throws IllegalArgumentException If supply type is null or empty.
     */
    public Supply(String supplyName, String supplyType) {
        if (supplyType == null || supplyType.trim().isEmpty()) {
            throw new IllegalArgumentException(translationManager.getTranslation("error.supplyTypeNullOrEmpty"));
        }
        this.supplyName = supplyName;
        this.supplyType = supplyType;
    }


    /**
     * Retrieves the name of the supply.
     *
     * @return The name of the supply
     */    public String getSupplyName() {
        return supplyName;
    }

    /**
     * Retrieves the type of the supply.
     *
     * @return The type of the supply
     */
    public String getSupplyType() {
        return supplyType;
    }

    /**
     * Retrieves the id of the supply.
     *
     * @return The id of the supply
     */
    public int getSupplyId() {
        return supplyId;
    }


    /**
     * Sets the name of the supply.
     *
     * @param supplyName The name of the supply to set
     * @throws IllegalArgumentException if the supply name is null or empty.
     */
    public void setSupplyName(String supplyName) {
        if (supplyName == null || supplyName.trim().isEmpty()) {
            throw new IllegalArgumentException(translationManager.getTranslation("error.supplyNameNullOrEmpty"));
        }
        this.supplyName = supplyName;
    }


    /**
     * Sets the type of the supply.
     *
     * @param supplyType The type of the supply to set
     * @throws IllegalArgumentException if the supply type is null or empty.
     */
    public void setSupplyType(String supplyType) {
        if (supplyType == null || supplyType.trim().isEmpty()) {
            throw new IllegalArgumentException(translationManager.getTranslation("error.supplyTypeNullOrEmpty"));
        }
        this.supplyType = supplyType;
    }


    /**
     * Sets the id of the supply.
     *
     * @param supplyId The id of the supply to set
     */
    public void setSupplyId(int supplyId) {
        this.supplyId = supplyId;
    }









}
