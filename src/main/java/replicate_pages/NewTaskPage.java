package replicate_pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Random;

import static utilities.UtilitiesMethods.safeClick;

/**
 * The NewTaskPage class represents the 'New Task Creation' dialog in Qlik Replicate.
 * It provides methods for entering task details, configuring options, and managing the task creation process.
 */
public class NewTaskPage {
    // WebDriver instance to interact with the browser
    private WebDriver driver;

    /**
     * Constructor to initialize the NewTaskPage object.
     *
     * @param driver WebDriver instance for Selenium automation.
     */
    public NewTaskPage(WebDriver driver) {
        this.driver = driver;
    }


    /**
     * Creates a new task name into the input field.
     *
     * @param taskName The name of the task to be created.
     */
    public String randomTaskName(String taskName) {
        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(900000); // Ensures a 6-digit number
        String newTaskName = taskName + randomNumber;
        return newTaskName;
    }

    /**
     * Enters the task name into the input field.
     *
     * @param taskName The name of the task to be created.
     */
    public void enterTaskName(String taskName) {
        WebElement taskNameInput = driver.findElement(By.xpath("//*[@id='Name']"));
        taskNameInput.clear();
        taskNameInput.sendKeys(taskName);
    }

    /**
     * Enters the task description into the input field.
     *
     * @param description A brief description of the task to be created.
     */
    public void enterDescription(String description) {
        WebElement descriptionInput = driver.findElement(By.xpath("//*[@id='description']"));
        descriptionInput.sendKeys(description);
    }

    /**
     * Clicks the 'OK' button to save and close the new task creation dialog.
     */
    public void closeNewTask() {
        WebElement okButton = driver.findElement(By.xpath("//*[text()='OK']"));
        safeClick(okButton);
    }

    /**
     * Clicks the 'Cancel' button to discard the new task creation process.
     */
    public void cancelNewTask() {
        WebElement cancelButton = driver.findElement(By.xpath("//*[text()='Cancel']"));
        safeClick(cancelButton);
    }

    /**
     * Toggles the 'Store Changes' option during task creation.
     */
    public void chooseStoreChanges() {
        WebElement storeChanges = driver.findElement(By.cssSelector("[class='StoreChanges toggleButton']"));
        safeClick(storeChanges);
    }

    /**
     * Toggles the 'Full Load' option during task creation.
     */
    public void flButton() {
        WebElement fullLoad = driver.findElement(By.cssSelector("[class='FullLoad toggleButton on']"));
        safeClick(fullLoad);
    }

    /**
     * Toggles the 'Apply Changes' option during task creation.
     */
    public void cdcButton() {
        WebElement cdc = driver.findElement(By.cssSelector("[class='ApplyChanges toggleButton on']"));
        safeClick(cdc);
    }

    /**
     * Creates a new task by entering the task name and description and then closing the dialog.
     *
     * @param name        The name of the task to be created.
     * @param description A brief description of the task to be created.
     */
    public void newTaskCreation(String name, String description) {
        enterTaskName(name);
        enterDescription(description);
        closeNewTask();
    }
}
