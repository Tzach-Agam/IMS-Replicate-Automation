package utilities;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.ElementNotInteractableException;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class UtilitiesMethods {
    /**
     * Safely clicks a web element while handling common exceptions.
     * @param element The web element to click.
     */
    public static void safeClick(WebElement element) {
        try {
            element.click();
        } catch (NoSuchElementException | ElementNotInteractableException e) {
            System.out.println("Error clicking element: " + e.getMessage());
        }
    }

    /**
     * Moves a chosen file from source directory to target directory while preserving the file in target dir if it already exists.
     * @param sourceDir The path of the source directory.
     * @param targetDir The path of the target directory.
     * @param fileName The name of the file to move.
     */
    public static void moveFileToTargetDir(String sourceDir, String targetDir, String fileName) {
        Path sourcePath = Paths.get(sourceDir, fileName);
        Path targetPath = Paths.get(targetDir, fileName);

        if (Files.exists(targetPath)) {
            String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
            String extension = fileName.substring(fileName.lastIndexOf('.'));
            String randomSuffix = String.valueOf(new Random().nextInt(99999) + 1);
            String newFileName = baseName + "_" + randomSuffix + extension;
            targetPath = Paths.get(targetDir, newFileName);
        }

        try {
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Moved '" + fileName + "' to '" + targetDir + "'");
        } catch (IOException e) {
            System.out.println("Error moving file: " + e.getMessage());
        }
    }

    /**
     * Compares files and deletes the data file if it's identical to the good file.
     *
     * @param goodFilePath The path for the good file, which is the expected result of the test executed.
     *                     The good file contains the data that is expected to be in the target endpoint's
     *                     tables after the end of the test and replication process.
     * @param dataFilePath The path for the data file, which is the current result of the test. The data file
     *                     contains the data in the target endpoint's tables after the end of the test and
     *                     replication process.
     * @throws AssertionError if the files are not identical.
     * @throws IOException    if there are issues reading or deleting the files.
     */
    public static void compareFiles(String goodFilePath, String dataFilePath) throws IOException {
        Path goodFile = Paths.get(Objects.requireNonNull(goodFilePath, "Good file path cannot be null"));
        Path dataFile = Paths.get(Objects.requireNonNull(dataFilePath, "Data file path cannot be null"));

        // Compare the contents of the files
        if (Files.mismatch(goodFile, dataFile) == -1L) {
            // Files are identical; delete the data file
            Files.delete(dataFile);
            System.out.println("Data file is deleted since it's identical to the Good file");
        } else {
            // Files are not identical; throw an assertion error
            throw new AssertionError("Data file is not identical to Good file");
        }
    }
    }
