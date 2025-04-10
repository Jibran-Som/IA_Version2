/**
 * TranslationManager.java
 * Version: 2.0
 * Author: Jibran Somroo
 * Date: April 8, 2025
 */

package edu.ucalgary.oop;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslationManager {
    private final Map<String, String> TRANSLATIONS = new HashMap<>();
    private static TranslationManager instance;

    /**
     * Private constructor for singleton pattern
     */
    private TranslationManager() {}

    /**
     * Singleton class to manage the translation system.
     *
     * @return instance of TranslationManager
     */
    public static synchronized TranslationManager getInstance() {
        if (instance == null) {
            instance = new TranslationManager();
        }
        return instance;
    }

    /**
     * Loads translation data from a specified file.
     *
     * @param filePath The path to the file containing the translation data.
     */
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

    /**
     * Parses the given XML content to extract translations and stores them in the translations map.
     *
     * @param xmlContent The XML content as a string that contains the translation entries.
     */
    private void parseTranslations(String xmlContent) {
        TRANSLATIONS.clear(); // Clear any existing translations

        // Regex pattern to match translation entries
        Pattern pattern = Pattern.compile(
                "<key>(.*?)</key>\\s*<value>(.*?)</value>",
                Pattern.DOTALL
        );

        Matcher matcher = pattern.matcher(xmlContent);

        while (matcher.find()) {
            String key = matcher.group(1).trim();
            String value = matcher.group(2).trim();
            TRANSLATIONS.put(key, value);
        }
    }

    /**
     * Retrieves the translation for the specified key.
     *
     * @param key The translation key whose value is to be retrieved.
     * @return The translation value corresponding to the given key.
     * @throws IllegalArgumentException if the key does not exist in the translations map.
     */
    public String getTranslation(String key) {
        if (!TRANSLATIONS.containsKey(key)) {
            throw new IllegalArgumentException("No translation found for key: " + key);
        }
        return TRANSLATIONS.get(key);
    }

    /**
     * Checks if a translation exists for the specified key.
     *
     * @param key The translation key to check for existence.
     * @return true if the key exists in the translations map, false otherwise.
     */
    public boolean hasTranslation(String key) {
        return TRANSLATIONS.containsKey(key);
    }
}