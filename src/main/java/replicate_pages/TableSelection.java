package replicate_pages;

import com.google.common.base.Strings;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Keys;

import java.util.List;

import static utilities.UtilitiesMethods.safeClick;

public class TableSelection {
    /**
     * The TableSelection class represents the 'Table Selection' window on Qlik Replicate. In it, you choose the schemas
     * and tables that will be replicated during the replication task from the source to the target.
     * The methods in the class will interact with the elements on the 'Table Selection' window and will allow choosing
     * the schemas and tables for the automated tasks.
     */

    private WebDriver driver;

    public TableSelection(WebDriver driver) {
        /** Initialize the TableSelection object
         * @param driver WebDriver instance for Selenium automation.
         */
        this.driver = driver;
    }

    public void chooseSchema(String sourceSchema) {
        /** Enter the name of source schema for the replication task */

        WebElement schemaInput = driver.findElement(By.cssSelector("[place-holder='Select a Schema in the list or search']>div>input"));
        schemaInput.clear();
        schemaInput.sendKeys(sourceSchema);
        schemaInput.sendKeys(Keys.ENTER);
    }

    public void chooseTable(String sourceTable) {
        /** Enter the name of the table that is wished to be replicated */

        WebElement tableInput = driver.findElement(By.cssSelector("[class='controlsValues']>input"));
        tableInput.clear();
        tableInput.sendKeys(sourceTable);
        tableInput.sendKeys(Keys.ENTER);
    }

    public void exactTableName() {
        WebElement exactTableCheckbox = driver.findElement(By.xpath("//span[@class='checkBoxSpan ']"));
        exactTableCheckbox.click();
    }

    public void includeSchemaButton() {
        /** Click on the 'Include' button to set the chosen schema in the 'Table Selection Patterns'.
         * By doing so, all the tables under that schema will be replicated to the target.
         */

        WebElement includeButton = driver.findElement(By.xpath("//*[text()='Include']"));
        safeClick(includeButton);
    }

    public void removeSchemaButton() {
        /** Click on the 'Remove' button to remove a schema from 'Table Selection Patterns'.
         * By doing so, the schema and the tables under it won't be part of the replication task.
         */

        WebElement removeButton = driver.findElement(By.xpath("//*[text()='Remove']"));
        safeClick(removeButton);
    }

    public void searchForTables() {
        /** Click on the 'Search' button to search for tables under a chosen schema */

        WebElement searchButton = driver.findElement(By.xpath("//*[text()='Search']"));
        safeClick(searchButton);
    }

    public void selectOneTable() {
        /** Select one table of the tables available under a schema */

        WebElement oneTableButton = driver.findElement(By.cssSelector("[class='bottomButtons']>button:nth-child(1)"));
        safeClick(oneTableButton);
    }

    public void selectAllTables() {
        /** Select all the tables available under a schema */

        WebElement allTablesButton = driver.findElement(By.cssSelector("[class='bottomButtons']>button:nth-child(3)"));
        safeClick(allTablesButton);
    }

    public void okButtonClick() {
        /** Click on the 'OK' button to save the changes and configurations */

        WebElement okButton = driver.findElement(By.xpath("//*[text()='OK']"));
        safeClick(okButton);
    }

    public void cancelButtonClick() {
        /** Click on the 'Cancel' button to leave the 'Table Selection' window without saving the configurations */

        WebElement cancelButton = driver.findElement(By.xpath("//*[text()='Cancel']"));
        safeClick(cancelButton);
    }

    public void chooseSourceSchema(String sourceSchema) {
        /** Choose a source schema and include it. This method combines previous methods in order to set default settings
         * for the automated replication task. It will select a source schema, include it, and confirm the selection by
         * clicking 'OK'.
         * @param sourceSchema The name of the source schema to be selected and included.
         */

        this.chooseSchema(sourceSchema);
        this.includeSchemaButton();
        this.okButtonClick();
    }

    public void selectChosenTables(String[] tables) {
        searchForTables();
        for (String table : tables) {
            WebElement chosenTable = driver.findElement(By.xpath("//*[text()='" + table + "']"));
            chosenTable.click();
            selectOneTable();
        }
        okButtonClick();

    }
}

