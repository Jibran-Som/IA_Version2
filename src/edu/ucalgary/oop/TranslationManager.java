package edu.ucalgary.oop;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslationManager {
    private final Map<String, String> translations = new HashMap<>();
    private static TranslationManager instance;

    // Private constructor for singleton pattern
    private TranslationManager() {
    }

    // Singleton instance getter
    public static synchronized TranslationManager getInstance() {
        if (instance == null) {
            instance = new TranslationManager();
        }
        return instance;
    }

    // Load translations from XML file
    public void loadTranslations(String filePath) {
        FileModel fileModel = new FileModel(filePath, "");
        try {
            fileModel.openFile();
            fileModel.readFile();
            parseTranslations(fileModel.getFileContent());
        } finally {
            if (fileModel.getFileContent() != null) {
                try {
                    fileModel.closeFile();
                } catch (Exception e) {
                    // Test
                }
            }
        }
    }

    // Parse XML content and populate translations map
    private void parseTranslations(String xmlContent) {
        translations.clear(); // Clear any existing translations

        // Regex pattern to match translation entries
        Pattern pattern = Pattern.compile(
                "<key>(.*?)</key>\\s*<value>(.*?)</value>",
                Pattern.DOTALL
        );

        Matcher matcher = pattern.matcher(xmlContent);

        while (matcher.find()) {
            String key = matcher.group(1).trim();
            String value = matcher.group(2).trim();
            translations.put(key, value);
        }
    }

    // Get translation by key
    public String getTranslation(String key) {
        if (!translations.containsKey(key)) {
            throw new IllegalArgumentException("No translation found for key: " + key);
        }
        return translations.get(key);
    }

    // Check if a translation exists
    public boolean hasTranslation(String key) {
        return translations.containsKey(key);
    }
}