package replicate_pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static utilities.UtilitiesMethods.safeClick;


/**
 * The TableSelection class represents the 'Table Selection' window on Qlik Replicate. In it, you choose the schemas
 * and tables that will be replicated during the replication task from the source to the target.
 * The methods in the class will interact with the elements on the 'Table Selection' window and will allow choosing
 * the schemas and tables for the automated tasks.
 */
public class TableSelection {

    private WebDriver driver;
    private WebDriverWait wait;

    /** Initialize the TableSelection object
     * @param driver WebDriver instance for Selenium automation.
     */
    public TableSelection(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /** Enter the name of source schema for the replication task */
    public void chooseSchema(String sourceSchema) {
        WebElement schemaInput = driver.findElement(By.cssSelector("[place-holder='Select a Schema in the list or search']>div>input"));
        schemaInput.clear();
        schemaInput.sendKeys(sourceSchema);
        schemaInput.sendKeys(Keys.ENTER);
    }

    /** Enter the name of the table that is wished to be replicated */
    public void chooseTable(String sourceTable) {
        WebElement tableInput = driver.findElement(By.cssSelector("[class='controlsValues']>input"));
        tableInput.clear();
        tableInput.sendKeys(sourceTable);
        tableInput.sendKeys(Keys.ENTER);
    }

    /** Clicks on the "Use exact table name" checkbox */
    public void exactTableName() {
        WebElement exactTableCheckbox = driver.findElement(By.xpath("//span[@class='checkBoxSpan ']"));
        exactTableCheckbox.click();
    }

    /** Click on the 'Include' button to set the chosen schema in the 'Table Selection Patterns'.
     * By doing so, all the tables under that schema will be replicated to the target.
     */
    public void includeSchemaButton() {
        WebElement includeButton = driver.findElement(By.xpath("//*[text()='Include']"));
        safeClick(includeButton);
    }

    /** Click on the 'Remove' button to remove a schema from 'Table Selection Patterns'.
     * By doing so, the schema and the tables under it won't be part of the replication task.
     */
    public void removeSchemaButton() {
        WebElement removeButton = driver.findElement(By.xpath("//*[text()='Remove']"));
        safeClick(removeButton);
    }

    /** Click on the 'Search' button to search for tables under a chosen schema */
    public void searchForTables() {
        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[text()='Search']")));
        safeClick(searchButton);
    }

    /** Select one table of the tables available under a schema */

    public void selectOneTable() {
        WebElement oneTableButton = driver.findElement(By.cssSelector("[class='bottomButtons']>button:nth-child(1)"));
        safeClick(oneTableButton);
    }

    /** Select all the tables available under a schema */
    public void selectAllTables() {
        WebElement allTablesButton = driver.findElement(By.cssSelector("[class='bottomButtons']>button:nth-child(3)"));
        safeClick(allTablesButton);
    }

    /** Click on the 'OK' button to save the changes and configurations */
    public void okButtonClick() {
        WebElement okButton = driver.findElement(By.xpath("//*[text()='OK']"));
        safeClick(okButton);
    }

    /** Click on the 'Cancel' button to leave the 'Table Selection' window without saving the configurations */
    public void cancelButtonClick() {
        WebElement cancelButton = driver.findElement(By.xpath("//*[text()='Cancel']"));
        safeClick(cancelButton);
    }

    /** Choose a source schema and include it. This method combines previous methods in order to set default settings
     * for the automated replication task. It will select a source schema, include it, and confirm the selection by
     * clicking 'OK'.
     * @param sourceSchema The name of the source schema to be selected and included.
     */
    public void chooseSourceSchema(String sourceSchema) {

        this.chooseSchema(sourceSchema);
        this.includeSchemaButton();
        this.okButtonClick();
    }

    /**
     * Selects the specified tables for replication. This method searches for the provided table names, clicks on each
     * matching table element, and confirms the selection for replication. It assumes that the necessary web elements
     * for table search and selection are available in the UI.
     * @param tables Table names to be selected for replication.
     */
    public void selectChosenTables(String... tables) {
        searchForTables();
        for (String table : tables) {
            WebElement chosenTable = driver.findElement(By.xpath("//*[text()='" + table + "']"));
            chosenTable.click();
            selectOneTable();
        }
        okButtonClick();
    }
}

