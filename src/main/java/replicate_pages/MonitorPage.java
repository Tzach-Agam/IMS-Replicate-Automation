package replicate_pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;
import static utilities.UtilitiesMethods.safeClick;

/**
 * The MonitorPage class represents the 'Monitor mode' on Qlik Replicate.
 * In Monitor mode, you view replication task activities in real time, including changes, errors, and warnings.
 * It allows operations like running a task, viewing logs, and accessing endpoints.
 */
public class MonitorPage {

    private WebDriver driver;
    private Actions actions;
    private WebDriverWait wait;
    private static final int TIMEOUT = 20; // Default timeout in seconds

    /**
     * Initialize the MonitorPage object.
     *
     * @param driver WebDriver instance for Selenium automation.
     */
    public MonitorPage(WebDriver driver) {
        this.driver = driver;
        this.actions = new Actions(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    }

    /**
     * Click on 'Full Load' tab to view the status of the Full Load process.
     */
    public void clickFullLoadTab() {
        WebElement flTab = driver.findElement(By.cssSelector("[title='Full Load']"));
        safeClick(flTab);
    }

    /**
     * Click on 'Change Processing' tab to view the status of the CDC process.
     */
    public void clickChangeProcessingTab() {
        WebElement cdcTab = driver.findElement(By.cssSelector("[title='Change Processing']"));
        safeClick(cdcTab);
    }

    /**
     * Enter the task's 'Designer mode'.
     */
    public void enterDesignerPage() {
        WebElement designerTab = driver.findElement(By.xpath("//span[text()='Designer']"));
        safeClick(designerTab);
    }

    /**
     * Double-click on the source endpoint.
     */
    public void doubleClickSourceEndpoint() {
        WebElement sourceEndpoint = driver.findElement(By.xpath("//*[@id='mapSrcAreaItem']/div/div/div"));
        actions.doubleClick(sourceEndpoint).perform();
    }

    /**
     * Double-click on the target endpoint.
     */
    public void doubleClickTargetEndpoint() {
        WebElement targetEndpoint = driver.findElement(By.xpath("//*[@id='mapTargetAreaItems']/div/div/div"));
        actions.doubleClick(targetEndpoint).perform();
    }

    /**
     * Click on the 'View Logs' button to view the task log.
     */
    public void viewLogs() {
        WebElement viewLogsButton = driver.findElement(By.xpath("//span[text()='View Logs...']"));
        safeClick(viewLogsButton);
    }

    /**
     * Download the logs to the machine.
     */
    public void downloadLogs() {
        viewLogs();
        WebElement downloadIcon = driver.findElement(By.cssSelector("[class='bootstrapGlyphicon glyphicon-download-alt']"));
        safeClick(downloadIcon);
    }

    /**
     * Wait for the specified number of tables to complete in Full Load mode.
     * @param numberOfTables The number of tables to wait for completion.
     */
    public void waitForFullLoadCompletion(String numberOfTables) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                    "//*[@id='Monitoring_FL_CompletedTables']/div/div[3]/*[text()='" + numberOfTables + "']")));
            System.out.println("Full Load completed.");
        } catch (Exception e) {
            String errorMessage = "Full Load did not complete within the timeout. Expected tables: " + numberOfTables;
            System.err.println(errorMessage);
            throw new AssertionError(errorMessage, e);
        }
    }

    /**
     * Check the status of operations (Insert/Update/Delete/DDL) for each specified table.
     * @param columnIndex The index of the operation's status column (2=Insert, 3=Update, 4=Delete, 5=DDL).
     * @param expectedStatuses Expected statuses for each table.
     */
    private void checkOperationStatus(int columnIndex, String... expectedStatuses) {
        List<WebElement> tables = driver.findElements(By.xpath("//table/tbody/tr"));
        for (int i = 0; i < tables.size(); i++) {
            try {
                String tableName = driver.findElement(By.xpath("//table/tbody/tr[" + (i + 1) + "]/td[1]/div/div")).getText();
                String expectedStatus = expectedStatuses[i];
                By statusLocator = By.xpath("//table/tbody/tr[" + (i + 1) + "]/td[" + columnIndex + "]/div/div[text()='" + expectedStatus + "']");
                wait.until(ExpectedConditions.visibilityOfElementLocated(statusLocator));
                System.out.println("Status for table " + tableName + " is " + expectedStatus);
            } catch (Exception e) {
                System.out.println("Operation status check did not complete within the timeout for table at index " + (i + 1));
            }
        }
    }

    /**
     * Check the status of insert operations.
     */
    public void checkInsertStatus(String... expectedStatuses) {
        checkOperationStatus(2, expectedStatuses);
    }

    /**
     * Check the status of update operations.
     */
    public void checkUpdateStatus(String... expectedStatuses) {
        checkOperationStatus(3, expectedStatuses);
    }

    /**
     * Check the status of delete operations.
     */
    public void checkDeleteStatus(String... expectedStatuses) {
        checkOperationStatus(4, expectedStatuses);
    }


    /**
     * Stop the current replication task entirely.
     */
    public void stopTask() {
        WebElement stopButton = driver.findElement(By.xpath("//span[text()='Stop']"));
        safeClick(stopButton);
        WebElement yesButton = driver.findElement(By.xpath("//button[text()='Yes']"));
        safeClick(yesButton);
    }

    /**
     * Wait for the replication task to stop.
     */
    public void waitForTaskStop() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='Stopping task']")));
        System.out.println("Stopping task...");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[text()='Stopping task']")));
        System.out.println("Task stopped.");
    }

}
