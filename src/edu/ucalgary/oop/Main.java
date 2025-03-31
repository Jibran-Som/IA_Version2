package edu.ucalgary.oop;

public class Main {


    public static void main(String[] args) {
        FileModel fileModel = new FileModel("data/en-CA.xml", null);
        fileModel.openFile();
        fileModel.readFile();
        System.out.println(fileModel.getFileContent());

    }

}

/*import java.util.HashMap;
import java.util.Map;

public class TranslationManager {
    private Map<String, String> translations = new HashMap<>();

    public void addTranslation(String key, String value) {
        translations.put(key, value);
    }

    public String getTranslation(String key) {
        return translations.get(key);
    }
}*/