package edu.ucalgary.oop;

import java.io.*;

public class FileModel {
    private String filePath;
    private String fileContent;
    private BufferedReader reader;
    private boolean isFileOpen = false;
    private ErrorLogger errorLogger = ErrorLogger.getInstance();



    // Constructor
    public FileModel(String filePath, String fileContent) {
        this.filePath = filePath;
        this.fileContent = fileContent;
    }



    // Getters
    public String getFilePath() {
        return filePath;
    }

    public String getFileContent() {
        return fileContent;
    }





    // Setters

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }




    // Class Specific Code
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