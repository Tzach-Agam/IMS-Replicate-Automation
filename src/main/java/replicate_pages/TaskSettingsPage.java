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
 * The TaskSettings class represents the 'Task Settings' dialog box on Qlik Replicate. In it, you configure
 * task-specific replication settings. The class will provide methods for the task settings page functionality,
 * like setting target replication schemas or adding logs.
 */
public class TaskSettingsPage {

    private WebDriver driver;
    private Actions actions;
    private WebDriverWait wait;

    /** Initialize the TaskSettings object
     * @param driver WebDriver instance for Selenium automation.
     */
    public TaskSettingsPage(WebDriver driver) {
        this.driver = driver;
        this.actions = new Actions(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Click on the Metadata tab for configuring the target tables metadata
     */
    public void metadataTab() {
        WebElement metadataTabElement = driver.findElement(By.xpath("//*[text()='Metadata']"));
        safeClick(metadataTabElement);
    }

    /**
     * Click on 'Target Metadata' sub-tab under 'Metadata' tab
     */
    public void targetSchemaClick() {
        WebElement targetSchemaElement = driver.findElement(By.xpath("//*[text()='Target Metadata']"));
        safeClick(targetSchemaElement);
    }

    /**
     * Enter the 'target schema' for the replication task. The 'target schema' is the schema in which the replicated
     * tables will be created under.
     * @param schema The name of the 'target schema'
     */
    public void targetSchema(String schema) {
        WebElement targetSchemaElement = driver.findElement(By.cssSelector("[ng-model='vm.currentFullTask.task_settings.target_settings.default_schema']"));
        String targetSchemaValue = targetSchemaElement.getAttribute("value");
        if (targetSchemaValue != null && !targetSchemaValue.isEmpty()) {
            targetSchemaElement.clear();
            targetSchemaElement.sendKeys(schema);
        } else {
            targetSchemaElement.sendKeys(schema);
        }
    }

    /**
     * Click on 'Control Tables' sub-tab under 'Metadata' tab
     */
    public void controlTablesClick() {
        WebElement controlTablesElement = driver.findElement(By.xpath("//*[text()='Control Tables']"));
        safeClick(controlTablesElement);
    }

    /**
     * Enter the 'control tables schema' for the replication task. The 'control tables schema' is a schema that is
     * created in the target as part of the replication task, and it contains the 'control tables'. Control Tables
     * provide information about the replication task like errors and changes.
     * @param schema The name of the 'control tables schema'
     */
    public void controlSchema(String schema) {
        WebElement controlSchemaElement = driver.findElement(By.cssSelector("[ng-model='vm.currentFullTask.task_settings.target_settings.control_schema']"));
        String controlSchemaValue = controlSchemaElement.getAttribute("value");
        if (controlSchemaValue != null && !controlSchemaValue.isEmpty()) {
            controlSchemaElement.clear();
            controlSchemaElement.sendKeys(schema);
        } else {
            controlSchemaElement.sendKeys(schema);
        }
    }

    /**
     * Click on the 'Full Load' tab for configuration of FL-related settings.
     */
    public void fullLoadTab() {
        WebElement flTabElement = driver.findElement(By.xpath("//*[text()='Full Load']"));
        safeClick(flTabElement);
    }

    /**
     * Click on the 'Change Processing' tab for configuration of CDC-related settings.
     */
    public void changeProcessing() {
        WebElement changeProcessingElement = driver.findElement(By.xpath("//*[text()='Change Processing']"));
        safeClick(changeProcessingElement);
    }

    /**
     * Turn 'Storage Changes Processing' on to include the 'Store Changes' table in the replication task. The 'Store
     * Changes' table will be created in the target database and will contain all the changes done (DML's + DDL's)
     * on the replicated tables.
     */
    public void storeChanges() {
        WebElement storeChangesElement = driver.findElement(By.xpath("//*[text()='Store Changes Settings']"));
        safeClick(storeChangesElement);
        WebElement storeChangesOn = driver.findElement(By.cssSelector("[class='StoreChanges toggleButton']"));
        safeClick(storeChangesOn);
    }

    /**
     * Click on 'Change Processing Tuning' sub-tab under 'Change Processing' tab
     */
    public void changeProcessingTuning() {
        WebElement changeProcessingTuningElement = driver.findElement(By.xpath("//*[text()='Change Processing Tuning']"));
        safeClick(changeProcessingTuningElement);
    }

    /**
     * Change the Processing mode of CDC events from the default 'Batch optimized apply' to 'Transactional apply'
     */
    public void transactionalModeChange() {
        this.changeProcessing();
        this.changeProcessingTuning();
        WebElement processingMode = driver.findElement(By.xpath("//*[text()='Batch optimized apply']"));
        safeClick(processingMode);
        WebElement transactional = driver.findElement(By.xpath("//*[text()='Transactional apply']"));
        safeClick(transactional);
    }

    /**
     * Click on 'Logging' tab to change the logging levels of the task
     */
    public void taskLogging() {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[text()='Logging']")));
        safeClick(element);
    }

    /**
     * Change the logging level for specified components.
     * @param components The names of the components for which the logging level should be changed.
     * @param loggingLevel The desired logging level to be set for the specified components. Supported levels are
     * 'TRACE' and 'VERBOSE'.
     */
    public void changeComponentLogging(String loggingLevel, String... components) {
        taskLogging();
        try {
            for (String component : components) {
                for (int i = 1; i <= 27; i++) {
                    WebElement loggingElement = driver.findElement(By.xpath(
                            "//*[@id='configByLogContainer']/div[" + i + "]/div[1]/span"));
                    String loggingElementText = loggingElement.getText();
                    if (component.equalsIgnoreCase(loggingElementText)) {
                        WebElement loggingSlider = driver.findElement(By.xpath("//*[@id='" + component.toUpperCase() + "']/span"));
                        if (loggingLevel.equalsIgnoreCase("TRACE")) {
                            actions.clickAndHold(loggingSlider).moveByOffset(80, 0).release().perform();
                        } else if (loggingLevel.equalsIgnoreCase("VERBOSE")) {
                            actions.clickAndHold(loggingSlider).moveByOffset(132, 0).release().perform();
                        } else {
                            throw new IllegalArgumentException("Logging level '" + loggingLevel + "' not recognized");
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Drag operation was unsuccessful.", e);
        }
    }

    /**
     * Click the 'OK' button on 'task settings' while saving the current settings and closing 'task settings'.
     */
    public void okButton() {
        WebElement okButtonElement = driver.findElement(By.xpath("//*[text()='OK']"));
        safeClick(okButtonElement);
    }

    /**
     * Click the 'Cancel' button on 'task settings' while not saving the current settings and closing 'task settings'.
     */
    public void cancelButton() {
        WebElement cancelButtonElement = driver.findElement(By.xpath("//*[text()='Cancel']"));
        safeClick(cancelButtonElement);
    }

    /**
     * Set the general task settings including target schema and control schema. This method combines previous methods
     * in order to set default task settings for the automated replication task.
     * @param targetSchema The name of the target schema.
     * @param controlSchema The name of the control schema.
     */
    public void setTaskSettingsGeneral(String targetSchema, String controlSchema) {
        this.targetSchema(targetSchema);
        this.controlTablesClick();
        this.controlSchema(controlSchema);
    }
}

