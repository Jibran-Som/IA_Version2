package edu.ucalgary.oop;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class FileModelTest {
    private FileModel fileModel;
    private String testFilePath;
    private String testFileContent;

    @Before
    public void setUp() {
        testFilePath = "test.txt";
        testFileContent = "This is a test.";
        fileModel = new FileModel(testFilePath, testFileContent);

        createTestFile(testFilePath, testFileContent);
    }

    @Test
    public void testOpenFile() {
        //Arrange
        fileModel.setFilePath(testFilePath);

        //Act
        fileModel.openFile();
        fileModel.readFile(); //Function also is supposed to input file content into variable fileContent

        //Assert
        assertNotNull("File should be opened and file content should not be null", fileModel.getFileContent());
    }


    @Test
    public void testReadFile() {
        //Arrange
        fileModel.setFilePath(testFilePath);
        fileModel.openFile();

        //Act
        fileModel.readFile();

        //Assert
        assertEquals("File content should match the expected value", testFileContent, fileModel.getFileContent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOpenFileWithInvalidPath() {
        fileModel.setFilePath("nonexistent.txt");

        fileModel.openFile();
    }

    @Test(expected = IllegalStateException.class)
    public void testReadFileWithoutOpening() {
        //Arrange
        fileModel.setFilePath(testFilePath);

        fileModel.readFile();
    }

    @Test(expected = IllegalStateException.class)
    public void testCloseFileWithoutOpening() {
        fileModel.setFilePath(testFilePath);
        fileModel.closeFile();
    }

    //Cerate temp file
    private void createTestFile(String filePath, String content) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
        } catch (IOException e) {
            fail("Failed to create test file: " + e.getMessage());
        }
    }

    private void deleteTestFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (!file.delete()) {
                fail("Failed to delete test file: " + filePath);
            }
        }
    }

    @After
    public void tearDown() {
        deleteTestFile(testFilePath);
    }
}