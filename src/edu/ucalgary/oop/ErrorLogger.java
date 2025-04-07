package edu.ucalgary.oop;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorLogger {
    private static final String LOG_FILE_PATH = "data/errorlog.txt";
    private static ErrorLogger instance;

    // Private constructor for singleton pattern
    private ErrorLogger() {
        // Ensure the data directory exists
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
    }

    // Singleton instance getter
    public static synchronized ErrorLogger getInstance() {
        if (instance == null) {
            instance = new ErrorLogger();
        }
        return instance;
    }


    public synchronized void logError(Exception exception, String context) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE_PATH, true))) {
            // Format timestamp
            String timestamp = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Write error entry
            writer.println("[" + timestamp + "] ERROR in " + context);
            writer.println("Exception: " + exception.getClass().getName());
            writer.println("Message: " + exception.getMessage());

            // Write stack trace
            writer.println("Stack Trace:");
            exception.printStackTrace(writer);
            writer.println(); // Add blank line between entries

        } catch (IOException e) {
            // If we can't write to the error log, print to console as last resort
            System.err.println("CRITICAL: Failed to write to error log:");
            e.printStackTrace();
        }
    }


    public void logFatalError(Exception exception, String context, String userMessage) {
        // Log the error
        logError(exception, context);

        // Show message to user
        System.err.println("\nFATAL ERROR: " + userMessage);
        System.err.println("Details have been logged to " + LOG_FILE_PATH);

        // Exit cleanly
        System.exit(1);
    }
}