package edu.ucalgary.oop;

public class Water extends Supply {
    private String allocationDate;
    public static final int EXPIRATION_DAYS = 1;


    public Water(String supplyName, String supplyType) {
        super(supplyName, supplyType);
    }

    public String getAllocationDate() {
        return allocationDate;
    }

    public void setAllocationDate(String allocationDate) {
        if (!isValidDateFormat(allocationDate)) {
            throw new IllegalArgumentException("Invalid date format. Expected format: YYYY-MM-DD");
        }
        this.allocationDate = allocationDate;
    }


    private static boolean isValidDateFormat(String date) {
        if (date == null || date.length() != 10) {
            return false;
        }

        if (date.charAt(4) != '-' || date.charAt(7) != '-') {
            return false;
        }

        String yearStr = date.substring(0, 4);
        String monthStr = date.substring(5, 7);
        String dayStr = date.substring(8, 10);


        int year = Integer.parseInt(yearStr);
        int month = Integer.parseInt(monthStr);
        int day = Integer.parseInt(dayStr);

        if (month < 1 || month > 12) {
            return false;
        }

        if (day < 1 || day > 31) {
            return false;
        }

        if(year < 1900 || year > 2026) {
            return false;
        }

        return true;

    }


}