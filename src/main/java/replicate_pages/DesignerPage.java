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
 * The DesignerPage represents the 'Designer mode' on Qlik Replicate. In it, you define endpoints, select tables to
 * be replicated and modify table and task settings. This is the default mode when you open a task.
 * The DesignerPage class will provide various functionalities on Designer Mode, including running tasks, selecting
 * endpoints and, entering the pages 'Task Settings', 'Manage Endpoint', 'Table Selection' and 'Monitor Mode'.
 */
public class DesignerPage {

    private WebDriver driver;
    private Actions actions;
    private WebDriverWait wait;

    /** Initialize the DesignerPage object
     * @param driver WebDriver instance for Selenium automation.
     */
    public DesignerPage(WebDriver driver) {
        this.driver = driver;
        this.actions = new Actions(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /** Choose source and target endpoints for a task by their names on the 'Endpoint Connections' list in Designer Mode.
     * @param sourceEndpoint The name of the Source endpoint.
     * @param targetEndpoint The name of the Target endpoint.
     */
    public void chooseSourceTarget(String sourceEndpoint, String targetEndpoint) {
        driver.findElement(By.xpath("//*[text()='" + targetEndpoint + "']/following-sibling::span[1]")).click();
        driver.findElement(By.xpath("//*[text()='" + sourceEndpoint + "']/following-sibling::span[1]")).click();
    }

    /** Approve FL only when the source is IMS. */
    public void confirmIMS() {
        WebElement yesButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[text()='Yes']")));
        safeClick(yesButton);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'is now Full Load only')]")));
    }

    /** Enter to 'Task Settings' dialog box, where the task-specific replication settings will be configured. */
    public void enterTaskSettings() {
        WebElement taskSettings = driver.findElement(By.xpath("//span[text()='Task Settings...']"));
        safeClick(taskSettings);
    }

    /**
     * Enter to 'Manage Endpoint Connections' window, the endpoint management page, where an endpoint is created,
     * edited and configured.
     */
    public void enterManageEndpoints() {
        WebElement manageEndpoints = driver.findElement(By.xpath("//span[text()='Manage Endpoint Connections...']"));
        safeClick(manageEndpoints);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()=\"Manage Endpoint Connections\"]")));
    }

    /** Enter to 'Table Selection' window - where schemas and tables will be selected for the replication task. */
    public void enterTableSelection() {
        WebElement tableSelection = driver.findElement(By.xpath("//span[text()='Table Selection...']"));
        safeClick(tableSelection);
    }

    /** Saves the configured task. */
    public void saveTask() {
        WebElement saveButton = driver.findElement(By.xpath("//span[text()='Save']"));
        safeClick(saveButton);
    }

    /** Double-click on the chosen source endpoint. */
    public void enterChosenSource() {
        WebElement sourceElement = driver.findElement(By.cssSelector(".dbItemDetails.ng-scope:nth-child(1)"));
        actions.doubleClick(sourceElement).perform();
    }

    /** Double-click on the chosen target endpoint. */
    public void enterChosenTarget() {
        WebElement targetElement = driver.findElement(By.cssSelector(".dbItemDetails.ng-scope:nth-child(2)"));
        actions.doubleClick(targetElement).perform();
    }

    /** Enter to the task's 'Monitor Mode'. */
    public void enterMonitorPage() {
        WebElement monitor = driver.findElement(By.xpath("//span[text()='Monitor']"));
        safeClick(monitor);
    }

    /** Click the 'Run' task dropdown menu. */
    public void runTaskDropdown() {
        WebElement runDropdown = driver.findElement(By.cssSelector("button[data-toggle='dropdown']>[class='caret']"));
        safeClick(runDropdown);
    }

    /** Start a new task by clicking on the 'Start Processing' option in the run options dropdown. */
    public void runNewTask() {
        runTaskDropdown();
        WebElement startProcessing = driver.findElement(By.xpath("//li[@title='Start Processing']/a[text()='Start Processing']"));
        safeClick(startProcessing);
    }

    /** Wait for the task to start and provide status messages. */
    public void startTaskWait() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='Starting task']")));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='Starting']")));
            System.out.println("Starting task");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='Running']")));
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[text()='Running']")));
            System.out.println("Task started");
        } catch (Exception e) {
            System.out.println("Element did not become visible within the timeout or became stale.");
        }
    }

    /** Stop the task entirely. */
    public void stopTask() {
        WebElement stopTaskElement = driver.findElement(By.xpath("//span[text()='Stop']"));
        safeClick(stopTaskElement);
        WebElement yesButton = driver.findElement(By.xpath("//button[text()='Yes']"));
        safeClick(yesButton);
    }

    /** Wait for the task to stop and provide status messages. */
    public void stopTaskWait() {
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
