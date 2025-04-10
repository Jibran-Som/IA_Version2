/**
 * ErrorLogger.java
 * Version: 4.0
 * Author: Jibran Somroo
 * Date: April 9, 2025
 */

package edu.ucalgary.oop;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorLogger {
    private static final String LOG_FILE_PATH = "data/errorlog.txt";
    private static ErrorLogger instance;

    /**
     * Private constructor to ensure that the ErrorLogger can only be instantiated from within the class.
     */
    private ErrorLogger() {
        // Ensure the data directory exists
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
    }

    /**
     * Returns the singleton instance of the ErrorLogger.
     *
     * @return The singleton instance of ErrorLogger.
     */
    public static synchronized ErrorLogger getInstance() {
        if (instance == null) {
            instance = new ErrorLogger();
        }
        return instance;
    }

    /**
     * Logs recoverable error details to a log file.
     *
     * @param exception The exception to be logged, containing the error details (type, message, stack trace).
     * @param context A description of the context in which the error occurred, typically the method or class name.
     */
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


    /**
     * Logs a fatal error and displays an error message to the user.
     *
     * @param exception The exception to be logged, including details like type, message, and stack trace.
     * @param context A description of the context in which the error occurred (e.g., method name or class).
     * @param userMessage A message explaining the fatal error, to be displayed to the user.
     */
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