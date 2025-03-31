package edu.ucalgary.oop;

import java.util.Scanner;


public class UserView {



    public static void displayMenuOptions() {
        System.out.println("\nDisaster Victim Information System\n");
        System.out.println("1. Location Details");
        System.out.println("2. Person Details");
        System.out.println("3. Supply Details");
        System.out.println("4. Inquiry Details");
        System.out.println("5. Medical Details");
        System.out.println("0. Exit");
        System.out.print("\nEnter your choice: ");
    }

    public static void displayPersonSubmenu() {
        System.out.println("\nPerson Management\n");
        System.out.println("1. View All Persons");
        System.out.println("2. Add New Person");
        System.out.println("3. Update Person");
        System.out.println("4. View Disaster Victims");
        System.out.println("0. Back to Main Menu");
        System.out.print("\nEnter your choice: ");
    }



}
