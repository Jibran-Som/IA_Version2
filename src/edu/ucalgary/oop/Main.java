package edu.ucalgary.oop;

import java.sql.SQLException;

public class Main {
    public static UserView userView = new UserView();
    public static LocationController locationController = new LocationController();
    public static TranslationManager translationManager;


    public static void main(String[] args) {
        userView.displaySupplyDetails();





        //
        // System.out.println(locationController.getAllLocations());

        //translationManager = TranslationManager.getInstance();
        //System.out.println(translationManager.getTranslation("gender_man"));







    }

}

/*

        FileModel fileModel = new FileModel("data/en-CA.xml", null);
        fileModel.openFile();
        fileModel.readFile();
        System.out.println(fileModel.getFileContent());
        translationManager = TranslationManager.getInstance();
        System.out.println(translationManager.getTranslation("gender_man"));

        translationManager = TranslationManager.getInstance();
        translationManager.loadTranslations("data/en-CA.xml");
        System.out.println(translationManager.getTranslation("gender_nb"));






*/