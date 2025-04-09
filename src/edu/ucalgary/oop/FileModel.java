package edu.ucalgary.oop;

import java.io.*;

public class FileModel {
    private String filePath;
    private String fileContent;
    private BufferedReader reader;
    private boolean isFileOpen = false;
    private ErrorLogger errorLogger = ErrorLogger.getInstance();



    /**
     * Constructs a new FileModel object with the specified file path and content.
     *
     * @param filePath The path to the file, either for reading or writing.
     * @param fileContent The content of the file to be read or written.
     */
    public FileModel(String filePath, String fileContent) {
        this.filePath = filePath;
        this.fileContent = fileContent;
    }



    /**
     * Returns the path of the file associated with this FileModel.
     *
     * @return The file path as a String.
     */
    public String getFilePath() {
        return filePath;
    }


    /**
     * Returns the content of the file
     * Used predominately for debugging
     *
     * @return The content of the file as a String.
     */
    public String getFileContent() {
        return fileContent;
    }





    // Setters

    /**
     * Sets the file path for this FileModel.
     * Mainly used for debugging
     *
     * @param filePath The path to the file, which can be an absolute or relative path.
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    /**
     * Sets the content of the file for this FileModel.
     *
     * @param fileContent The content of the file as a String.
     */
    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }




    /**
     * Opens the file for reading.
     *
     * @throws IllegalStateException If the file is already open.
     * @throws IllegalArgumentException If the file does not exist or the path is invalid, or if
     *                                  the file cannot be opened.
     */
    public void openFile() {
        if (isFileOpen) {
            errorLogger.logError(new IllegalStateException("File is already open"),
                    "FileModel.openFile()");
            throw new IllegalStateException("File is already open");
        }

        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("File does not exist or path is invalid");
        }

        try {
            // Reset the reader each time we open the file
            reader = new BufferedReader(new FileReader(filePath));
            isFileOpen = true;
        } catch (FileNotFoundException e) {
            errorLogger.logError(e, "FileModel.openFile()");
            throw new IllegalArgumentException("File could not be opened: " + e.getMessage());
        }
    }

    /**
     * Reads the content of the file.
     *
     * @throws IllegalStateException If the file is not open when attempting to read.
     * @throws IllegalStateException If an error occurs while reading the file.
     */
    public void readFile() {
        if (!isFileOpen) {
            errorLogger.logError(new IllegalStateException("File not open"),
                    "FileModel.readFile()");
            throw new IllegalStateException("File must be opened before reading");
        }

        try {
            StringBuilder content = new StringBuilder();
            String line;

            // Read the entire file content
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            // Update the fileContent field
            fileContent = content.toString();

            // Close the reader since we've read everything
            reader.close();
            isFileOpen = false;
        } catch (IOException e) {
            errorLogger.logError(e, "FileModel.readFile()");
            throw new IllegalStateException("Error reading file: " + e.getMessage());
        }
    }

    /**
     * Closes the currently open file.
     * This function was made separate from readFile as it was having issues at the time of development.
     * This function was purely created in case of readFile closing failure which occured a lot in beta.
     *
     * @throws IllegalStateException If no file is currently open when attempting to close.
     * @throws IllegalStateException If an error occurs while closing the file.
     */
    public void closeFile() {
        if (!isFileOpen) {
            throw new IllegalStateException("No file is currently open");
        }

        try {
            reader.close();
            isFileOpen = false;
        } catch (IOException e) {
            throw new IllegalStateException("Error closing file: " + e.getMessage());
        }
    }
}