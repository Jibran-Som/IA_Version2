package edu.ucalgary.oop;

import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;

public class TranslationManagerTest {
    private TranslationManager manager;
    private final String TEST_FILE_PATH = "test_translations.xml";

    @Before
    public void setUp() {
        manager = TranslationManager.getInstance();
        createTestFile();
    }

    @After
    public void tearDown() {
        deleteTestFile();
    }

    private void createTestFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TEST_FILE_PATH))) {
            writer.println("<translations>");
            writer.println("    <translation>");
            writer.println("        <key>gender_man</key>");
            writer.println("        <value>Homme</value>");
            writer.println("    </translation>");
            writer.println("    <translation>");
            writer.println("        <key>gender_woman</key>");
            writer.println("        <value>Femme</value>");
            writer.println("    </translation>");
            writer.println("    <translation>");
            writer.println("        <key>program_title</key>");
            writer.println("        <value>Système d'information sur les victimes de catastrophes</value>");
            writer.println("    </translation>");
            writer.println("    <translation>");
            writer.println("        <key>invalid_choice_main_menu</key>");
            writer.println("        <value>Choix invalide. Veuillez saisir un nombre compris entre 0 et 5.</value>");
            writer.println("    </translation>");
            writer.println("</translations>");
        } catch (IOException e) {
            fail("Failed to create test file: " + e.getMessage());
        }
    }

    private void deleteTestFile() {
        File file = new File(TEST_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testSingletonInstance() {
        TranslationManager anotherInstance = TranslationManager.getInstance();
        assertSame("Should return the same instance", manager, anotherInstance);
    }

    @Test
    public void testLoadTranslations() {
        manager.loadTranslations(TEST_FILE_PATH);

        assertTrue("Should have translation for 'gender_man'", manager.hasTranslation("gender_man"));
        assertTrue("Should have translation for 'program_title'", manager.hasTranslation("program_title"));
    }

    @Test
    public void testGetTranslation() {
        manager.loadTranslations(TEST_FILE_PATH);

        assertEquals("Homme", manager.getTranslation("gender_man"));
        assertEquals("Femme", manager.getTranslation("gender_woman"));
        assertEquals("Système d'information sur les victimes de catastrophes",
                manager.getTranslation("program_title"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTranslationForMissingKey() {
        manager.loadTranslations(TEST_FILE_PATH);
        manager.getTranslation("nonexistent_key");
    }

    @Test
    public void testHasTranslation() {
        manager.loadTranslations(TEST_FILE_PATH);

        assertTrue(manager.hasTranslation("gender_man"));
        assertTrue(manager.hasTranslation("invalid_choice_main_menu"));
        assertFalse(manager.hasTranslation("nonexistent_key"));
    }

    @Test
    public void testParseTranslationsWithWhitespace() {
        String xmlContent =
                "<translations>\n" +
                        "    <translation>\n" +
                        "        <key>  gender_man  </key>\n" +
                        "        <value>  Homme  </value>\n" +
                        "    </translation>\n" +
                        "    <translation>\n" +
                        "        <key>program_title</key>\n" +
                        "        <value>Système d'information</value>\n" +
                        "    </translation>\n" +
                        "</translations>";

        try {
            java.lang.reflect.Method method = TranslationManager.class.getDeclaredMethod(
                    "parseTranslations", String.class);
            method.setAccessible(true);
            method.invoke(manager, xmlContent);

            assertEquals("Homme", manager.getTranslation("gender_man"));
            assertEquals("Système d'information", manager.getTranslation("program_title"));
        } catch (Exception e) {
            fail("Failed to test private method: " + e.getMessage());
        }
    }

    @Test
    public void testLoadTranslationsWithEmptyFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TEST_FILE_PATH))) {
            writer.println("");
        } catch (IOException e) {
            fail("Failed to create empty test file");
        }

        manager.loadTranslations(TEST_FILE_PATH);
        assertFalse(manager.hasTranslation("gender_man"));
    }

    @Test
    public void testLoadTranslationsWithMalformedXML() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TEST_FILE_PATH))) {
            writer.println("<translation>");
            writer.println("    <key>gender_man</key>");
            writer.println("    <value>Homme"); // Missing closing tag
            writer.println("</translation>");
        } catch (IOException e) {
            fail("Failed to create malformed test file");
        }

        manager.loadTranslations(TEST_FILE_PATH);
        assertFalse("Should not load malformed entries", manager.hasTranslation("gender_man"));
    }

    @Test
    public void testTranslationWithSpecialCharacters() {
        String xmlContent =
                "<translations>\n" +
                        "    <translation>\n" +
                        "        <key>program_title</key>\n" +
                        "        <value>Système d'information sur les victimes de catastrophes</value>\n" +
                        "    </translation>\n" +
                        "</translations>";

        try {
            java.lang.reflect.Method method = TranslationManager.class.getDeclaredMethod(
                    "parseTranslations", String.class);
            method.setAccessible(true);
            method.invoke(manager, xmlContent);

            assertEquals("Système d'information sur les victimes de catastrophes",
                    manager.getTranslation("program_title"));
        } catch (Exception e) {
            fail("Failed to test special characters: " + e.getMessage());
        }
    }
}