package replicate_pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static utilities.UtilitiesMethods.safeClick;

/**
 * The TasksPage class represents the 'Tasks View' in Qlik Replicate.
 * It provides methods for interacting with elements and performing
 * functionalities like creating, managing, and controlling tasks.
 */
public class TasksPage {

    private WebDriver driver;
    private Actions actions;
    private WebDriverWait wait;


    /**
     * Constructor to initialize the TasksPage object.
     *
     * @param driver WebDriver instance for Selenium automation.
     */
    public TasksPage(WebDriver driver) {
        this.driver = driver;
        this.actions = new Actions(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Opens the 'New Task...' dialog box to create a new task.
     */
    public void createNewTask() {
        WebElement createNewTask = driver.findElement(By.xpath("//span[text()='New Task...']"));
        safeClick(createNewTask);
    }

    /**
     * Opens the 'Manage Endpoint Connections...' dialog box.
     */
    public void enterManageEndpoints() {
        WebElement manageEndpoints = driver.findElement(By.xpath("//span[text()='Manage Endpoint Connections...']"));
        safeClick(manageEndpoints);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()=\"Manage Endpoint Connections\"]")));
    }

    /**
     * Opens the 'Import Task' dialog box to import a task definition.
     */
    public void importTask() {
        WebElement importTask = driver.findElement(By.xpath("//span[text()='Import Task']"));
        safeClick(importTask);
    }

    /**
     * Opens the 'Export Task' dialog box to export a task definition.
     */
    public void exportTask() {
        WebElement exportTask = driver.findElement(By.xpath("//span[text()='Export Task']"));
        safeClick(exportTask);
    }

    /**
     * Finds a task element in the 'Tasks View' by its name.
     *
     * @param taskName The name of the task to find.
     * @return WebElement representing the task.
     */
    public WebElement findTaskElement(String taskName) {
        return driver.findElement(By.xpath("//*[@class='taskImageName ellipsisStyle ng-binding' and text()='" + taskName + "']"));
    }

    /**
     * Selects a task in the 'Tasks View' by its name.
     *
     * @param taskName The name of the task to select.
     */
    public void selectTask(String taskName) {
        WebElement task = findTaskElement(taskName);
        safeClick(task);
    }

    /**
     * Opens a task for editing or reviewing.
     *
     * @param taskName The name of the task to open.
     */
    public void openTask(String taskName) {
        selectTask(taskName);
        WebElement openTaskOption = driver.findElement(By.xpath("//span[text()='Open']"));
        safeClick(openTaskOption);
    }

    /**
     * Deletes a task from the 'Tasks View'.
     *
     * @param taskName The name of the task to delete.
     */
    public void deleteTask(String taskName) {
        selectTask(taskName);
        WebElement deleteTaskButton = driver.findElement(By.xpath("//span[text()='Delete...']"));
        safeClick(deleteTaskButton);
        WebElement okButton = driver.findElement(By.xpath("//button[text()='OK']"));
        safeClick(okButton);
        System.out.println("Deleted task: " + taskName);
    }

    /**
     * Performs a double-click action on a task.
     *
     * @param taskName The name of the task to double-click.
     */
    public void doubleClickTask(String taskName) {
        WebElement task = findTaskElement(taskName);
        actions.doubleClick(task).perform();
    }

    /**
     * Opens the 'View Logs...' dialog box for the selected task.
     */
    public void enterViewLogs() {
        WebElement viewLogs = driver.findElement(By.xpath("//span[text()='View Logs...']"));
        safeClick(viewLogs);
    }

    /**
     * Downloads the logs of the currently viewed task.
     */
    public void downloadLogs() {
        enterViewLogs();
        WebElement downloadLogsIcon = driver.findElement(By.cssSelector("[class='bootstrapGlyphicon glyphicon-download-alt']"));
        safeClick(downloadLogsIcon);
    }

    /**
     * Opens the 'Run Task' dropdown menu for the selected task.
     *
     * @param taskName The name of the task for which to open the dropdown.
     */
    public void runTaskDropdown(String taskName) {
        selectTask(taskName);
        WebElement runDropdown = driver.findElement(By.cssSelector("button[data-toggle='dropdown']>[class='caret']"));
        safeClick(runDropdown);
    }

    /**
     * Starts processing a new task.
     *
     * @param taskName The name of the task to start.
     */
    public void runNewTask(String taskName) {
        runTaskDropdown(taskName);
        WebElement startProcessing = driver.findElement(By.xpath("//li[@title='Start Processing']/a[text()='Start Processing']"));
        safeClick(startProcessing);
    }

    /**
     * Reloads the target data for a task.
     *
     * @param taskName The name of the task to reload.
     */
    public void reloadTask(String taskName) {
        runTaskDropdown(taskName);
        WebElement reloadTask = driver.findElement(By.xpath("//*[@title='Reload Target...']/a[text()='Reload Target...']"));
        safeClick(reloadTask);
        WebElement yesButton = driver.findElement(By.xpath("//button[text()='Yes']"));
        safeClick(yesButton);
    }

    /**
     * Stops a task that is currently running.
     *
     * @param taskName The name of the task to stop.
     */
    public void stopTask(String taskName) {
        selectTask(taskName);
        WebElement stopTaskElement = driver.findElement(By.xpath("//span[text()='Stop']"));
        safeClick(stopTaskElement);
        WebElement yesButton = driver.findElement(By.xpath("//button[text()='Yes']"));
        safeClick(yesButton);
    }
}
