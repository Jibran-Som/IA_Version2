package edu.ucalgary.oop;

import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorLoggerTest {
    private static final String TEST_LOG_PATH = "data/errorlog.txt";
    private ErrorLogger logger;

    @Before
    public void setUp() {
        // Clear the log file before each test
        File logFile = new File(TEST_LOG_PATH);
        if (logFile.exists()) {
            logFile.delete();
        }

        // Ensure the data directory exists
        new File("data").mkdirs();

        logger = ErrorLogger.getInstance();
    }

    @After
    public void tearDown() {
        // Clean up after tests
        File logFile = new File(TEST_LOG_PATH);
        if (logFile.exists()) {
            logFile.delete();
        }
    }

    @Test
    public void testSingletonInstance() {
        ErrorLogger anotherInstance = ErrorLogger.getInstance();
        assertSame("Singleton should return the same instance", logger, anotherInstance);
    }

    @Test
    public void testLogErrorCreatesFile() {
        Exception testException = new IOException("Test exception");
        logger.logError(testException, "testLogErrorCreatesFile");

        File logFile = new File(TEST_LOG_PATH);
        assertTrue("Log file should be created", logFile.exists());
    }

    @Test
    public void testLogErrorContent() throws IOException {
        Exception testException = new NullPointerException("Test NPE");
        String context = "testLogErrorContent";
        logger.logError(testException, context);

        // Read file content using BufferedReader
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(TEST_LOG_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        assertFalse("Log file should not be empty", lines.isEmpty());

        // Check timestamp format
        String firstLine = lines.get(0);
        assertTrue("First line should contain timestamp and context",
                firstLine.matches("\\[\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\] ERROR in " + context));

        // Check exception type
        boolean containsExceptionType = false;
        boolean containsExceptionMessage = false;
        boolean containsStackTraceHeader = false;

        for (String line : lines) {
            if (line.contains("Exception: java.lang.NullPointerException")) {
                containsExceptionType = true;
            }
            if (line.contains("Message: Test NPE")) {
                containsExceptionMessage = true;
            }
            if (line.equals("Stack Trace:")) {
                containsStackTraceHeader = true;
            }
        }

        assertTrue("Should contain exception type", containsExceptionType);
        assertTrue("Should contain exception message", containsExceptionMessage);
        assertTrue("Should contain stack trace header", containsStackTraceHeader);
    }

    @Test
    public void testLogErrorAppendsToFile() throws IOException {
        // First log entry
        Exception firstException = new IllegalArgumentException("First exception");
        logger.logError(firstException, "firstTest");

        // Second log entry
        Exception secondException = new IllegalStateException("Second exception");
        logger.logError(secondException, "secondTest");

        // Count log entries
        int entryCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(TEST_LOG_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("ERROR in")) {
                    entryCount++;
                }
            }
        }

        assertEquals("Should have two log entries", 2, entryCount);
    }

}