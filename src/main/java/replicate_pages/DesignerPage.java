package replicate_pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.UtilitiesMethods;

import java.time.Duration;

import static utilities.UtilitiesMethods.safeClick;

public class DesignerPage {
    /**
     * The DesignerPage represents the 'Designer mode' on Qlik Replicate. In it, you define endpoints, select tables to
     * be replicated and modify table and task settings. This is the default mode when you open a task.
     * The DesignerPage class will provide various functionalities on Designer Mode, including running tasks, selecting
     * endpoints and, entering the pages 'Task Settings', 'Manage Endpoint', 'Table Selection' and 'Monitor Mode'.
     */

    private WebDriver driver;
    private Actions actions;
    private WebDriverWait wait;

    public DesignerPage(WebDriver driver) {
        /** Initialize the DesignerPage object
         * @param driver WebDriver instance for Selenium automation.
         */
        this.driver = driver;
        this.actions = new Actions(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void chooseSourceTarget(String sourceEndpoint, String targetEndpoint) {
        /** Choose source and target endpoints for a task by their names on the 'Endpoint Connections' list in Designer Mode.
         * The names will be taken from a dictionary that contain the endpoint's definition.
         * @param sourceEndpointData Information about the source endpoint.
         * @param targetEndpointData Information about the target endpoint.
         */
        driver.findElement(By.xpath("//*[text()='" + targetEndpoint + "']/following-sibling::span[1]")).click();
        driver.findElement(By.xpath("//*[text()='" + sourceEndpoint + "']/following-sibling::span[1]")).click();
    }

    public void confirmIMS() {
        WebElement yesButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[text()='Yes']")));
        safeClick(yesButton);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'is now Full Load only')]")));
    }

    public void enterTaskSettings() {
        /** Enter to 'Task Settings' dialog box, where the task-specific replication settings will be configured. */

        WebElement taskSettings = driver.findElement(By.xpath("//span[text()='Task Settings...']"));
        safeClick(taskSettings);
    }

    public void enterManageEndpoints() {
        /**
         * Enter to 'Manage Endpoint Connections' window, the endpoint management page, where an endpoint is created,
         * edited and configured.
         */
        WebElement manageEndpoints = driver.findElement(By.xpath("//span[text()='Manage Endpoint Connections...']"));
        safeClick(manageEndpoints);
    }

    public void enterTableSelection() {
        /** Enter to 'Table Selection' window - where schemas and tables will be selected for the replication task. */

        WebElement tableSelection = driver.findElement(By.xpath("//span[text()='Table Selection...']"));
        safeClick(tableSelection);
    }

    public void saveTask() {
        /** Saves the configured task. */

        WebElement saveButton = driver.findElement(By.xpath("//span[text()='Save']"));
        safeClick(saveButton);
    }

    public void enterChosenSource() {
        /** Double-click on the chosen source endpoint. */

        WebElement sourceElement = driver.findElement(By.cssSelector(".dbItemDetails.ng-scope:nth-child(1)"));
        actions.doubleClick(sourceElement).perform();
    }

    public void enterChosenTarget() {
        /** Double-click on the chosen target endpoint. */

        WebElement targetElement = driver.findElement(By.cssSelector(".dbItemDetails.ng-scope:nth-child(2)"));
        actions.doubleClick(targetElement).perform();
    }

    public void enterMonitorPage() {
        /** Enter to the task's 'Monitor Mode'. */

        WebElement monitor = driver.findElement(By.xpath("//span[text()='Monitor']"));
        safeClick(monitor);
    }

    public void runTaskDropdown() {
        /** Click the 'Run' task dropdown menu. */

        WebElement runDropdown = driver.findElement(By.cssSelector("button[data-toggle='dropdown']>[class='caret']"));
        safeClick(runDropdown);
    }

    public void runNewTask() {
        /** Start a new task by clicking on the 'Start Processing' option in the run options dropdown. */

        runTaskDropdown();
        WebElement startProcessing = driver.findElement(By.xpath("//li[@title='Start Processing']/a[text()='Start Processing']"));
        safeClick(startProcessing);
    }

    public void startTaskWait() {
        /** Wait for the task to start and provide status messages. */
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='Starting task']")));
            System.out.println("Starting task");
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[text()='Starting task']")));
            System.out.println("Task started");
        } catch (Exception e) {
            System.out.println("Element did not become visible within the timeout or became stale.");
        }
    }

    public void stopTask() {
        /** Stop the task entirely. */

        WebElement stopTaskElement = driver.findElement(By.xpath("//span[text()='Stop']"));
        safeClick(stopTaskElement);
        WebElement yesButton = driver.findElement(By.xpath("//button[text()='Yes']"));
        safeClick(yesButton);
    }

    public void stopTaskWait() {
        /** Wait for the task to stop and provide status messages. */
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='Stopping task']")));
            System.out.println("Stopping task.");
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[text()='Stopping task']")));
            System.out.println("Task stopped");
        } catch (Exception e) {
            System.out.println("Element did not become visible within the timeout or became stale.");
        }
    }
}
