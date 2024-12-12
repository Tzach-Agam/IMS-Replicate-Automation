package replicate_pages;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import static utilities.UtilitiesMethods.safeClick;
import java.io.IOException;
import java.time.Duration;
import java.util.Random;
import configuration.ConfigurationManager;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


/**
 * The ManageEndpoints class represents the 'Manage Endpoints Connections' window in Qlik Replicate.
 * It provides methods to interact with functionalities like creating, deleting, and configuring source
 * and target endpoints used for replication tasks.
 */
public class ManageEndpointsPage {

    private WebDriver driver;
    private Actions actions;
    private ConfigurationManager config;
    private WebDriverWait wait;

    /**
     * Initialize the ManageEndpoints object.
     *
     * @param driver WebDriver instance for Selenium automation.
     */
    public ManageEndpointsPage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.actions = new Actions(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.config = new ConfigurationManager("src/main/resources/config.ini");
    }

    /**
     * Click the 'New Endpoint Connection' button to create a new endpoint.
     */
    public void newEndpointConnection() {
        WebElement newEndpoint = this.wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[text()='New Endpoint Connection']")));
        safeClick(newEndpoint);
    }

    /**
     * Creates a random name of the endpoint.
     *
     * @param name The name of the endpoint.
     */
    public String randomEndpointName(String name) {
        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(900000); // Ensures a 6-digit number
        String newName = name + randomNumber;
        return newName;
    }

    /**
     * Enter the name of the endpoint.
     *
     * @param name The name of the endpoint.
     */
    public void enterEndpointName(String name) {
        try {
            WebElement endpointName = this.wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='endpointName']")));
            endpointName.clear();
            endpointName.sendKeys(name);
        } catch (StaleElementReferenceException e) {
            WebElement endpointName = this.wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='endpointName']")));
            endpointName.clear();
            endpointName.sendKeys(name);
        }
    }

    /**
     * Enter a description for the endpoint.
     *
     * @param description The description of the endpoint.
     */
    public void enterEndpointDescription(String description) {
        WebElement endpointDesc = driver.findElement(By.cssSelector("[name='endpointDescription']"));
        endpointDesc.sendKeys(description);
    }

    /**
     * Choose 'target' as the role of the endpoint.
     */
    public void chooseTargetRole() {
        WebElement targetRole = driver.findElement(By.cssSelector("[id='targetRB']>[class='radioBtnSpan ']"));
        safeClick(targetRole);
    }

    public void enterType(String type){
        WebElement inputElement = driver.findElement(By.cssSelector("[class='textInputInRowWrap']>[type='text']"));
        inputElement.sendKeys(type);
    }

    /**
     * Create IMS IBM source as the type of the endpoint.
     */
    public void createIMSsource(String endpointName) throws InterruptedException {
        newEndpointConnection();
        enterEndpointName(endpointName);
        enterEndpointDescription("IMS Source endpoint");
        enterType("IMS");
        WebElement endpointToSelect = driver.findElement(By.xpath("//*[text()='IBM IMS']"));
        safeClick(endpointToSelect);
        WebElement imsServer = driver.findElement(By.xpath("//*[@id=\"server\"]"));
        imsServer.sendKeys(config.getIMSServer());
        //WebElement imsUsername = driver.findElement(By.xpath("//*[@id=\"user\"]"));
        //imsUsername.sendKeys(config.getIMSUsername());
        //WebElement imsPassword = driver.findElement(By.xpath("//*[@id=\"password\"]"));
        //imsPassword.sendKeys(config.getIMSPassword());
        //WebElement imsSolution = driver.findElement(By.xpath("//*[@id=\"arcSolution\"]"));
        //imsSolution.sendKeys(config.getIMSSolution());
        WebElement imsWorksapce = driver.findElement(By.xpath("//*[@id=\"arcWorkspace\"]"));
        imsWorksapce.sendKeys(config.getIMSWorkspace());
        WebElement imsDataSource = driver.findElement(By.xpath("//*[@id=\"arcDatasource\"]"));
        imsDataSource.sendKeys(config.getIMSDataSource());
        Thread.sleep(200);
        testConnectionValid();
        save();
    }

    public void createSQLServertarget(String endpointName) throws InterruptedException {
        newEndpointConnection();
        enterEndpointName(endpointName);
        enterEndpointDescription("SQL Server target endpoint");
        chooseTargetRole();
        enterType("Server");
        WebElement endpointToSelect = driver.findElement(By.xpath("//*[text()='Microsoft SQL Server']"));
        safeClick(endpointToSelect);
        sqlAuthOption();
        WebElement sqlServer = driver.findElement(By.xpath("//*[@id=\"server\"]"));
        sqlServer.sendKeys(config.getMSSQLServer());
        WebElement sqlUsername = driver.findElement(By.xpath("//*[@id=\"username\"]"));
        sqlUsername.sendKeys(config.getMSSQLUsername());
        WebElement sqlPassword = driver.findElement(By.xpath("//*[@id=\"password\"]"));
        sqlPassword.sendKeys(config.getMSSQLPassword());
        WebElement sqlDatabase = driver.findElement(By.xpath("//input[@name='database']"));
        sqlDatabase.sendKeys(config.getMSSQLDatabase());
        Thread.sleep(200);
        testConnectionValid();
        save();
    }

    public void createOracletarget(String endpointName) throws InterruptedException {
        newEndpointConnection();
        enterEndpointName(endpointName);
        enterEndpointDescription("Oracle target endpoint");
        chooseTargetRole();
        enterType("oracle");
        WebElement endpointToSelect = driver.findElement(By.xpath("//*[text()='Oracle']"));
        safeClick(endpointToSelect);
        WebElement oracleDSN = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"server\"]")));
        oracleDSN.sendKeys(config.getOracleDSN());
        WebElement oracleUsername = driver.findElement(By.xpath("//*[@id=\"username\"]"));
        oracleUsername.sendKeys(config.getOracleUsername());
        WebElement oraclePassword = driver.findElement(By.xpath("//*[@id=\"password\"]"));
        oraclePassword.sendKeys(config.getOraclePassword());
        Thread.sleep(200);
        testConnectionValid();
        save();
    }

    /**
     * Choose 'SQL Server authentication' for an SQL Server endpoint.
     */
    public void sqlAuthOption() {
        WebElement sqlAuthCheckbox = driver.findElement(By.xpath("//*[@id='sqlAuth']/span[1]"));
        safeClick(sqlAuthCheckbox);
    }

    /**
     * Enter the server information for the database.
     *
     * @param server The server information.
     */
    public void enterServer(String server) {
        WebElement serverElement = driver.findElement(By.cssSelector("[input-id='server']"));
        serverElement.sendKeys(server);
    }

    /**
     * Enter the username for database authentication.
     *
     * @param username The username to be entered.
     */
    public void enterUsername(String username) {
        WebElement usernameElement = driver.findElement(By.cssSelector("[name='username']"));
        usernameElement.sendKeys(username);
    }

    /**
     * Enter the password for database authentication.
     *
     * @param password The password to be entered.
     */
    public void enterPassword(String password) {
        WebElement passwordElement = driver.findElement(By.cssSelector("[name='password']"));
        passwordElement.sendKeys(password);
    }

    /**
     * Enter the database name.
     *
     * @param database The database name.
     */
    public void enterDatabase(String database) {
        WebElement databaseElement = driver.findElement(By.cssSelector("[name='database']"));
        databaseElement.sendKeys(database);
    }

    /**
     * Click the 'Test Connection' button to test the database connection.
     */
    public void testConnectionValid() throws InterruptedException {
        WebElement testConnectionElement = driver.findElement(By.xpath("//*[text()='Test Connection']"));
        safeClick(testConnectionElement);
        this.wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(@custom-tooltip-text,'Test connection succeeded')]")));
        Thread.sleep(200);
    }

    /**
     * Click the 'Save' button to save the endpoint configuration.
     */
    public void save() throws InterruptedException {
        WebElement saveButton = driver.findElement(By.xpath("//*[text()='Save']"));
        safeClick(saveButton);
        this.wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='Database was successfully saved']")));
        Thread.sleep(200);
    }

    /**
     * Click the 'Close' button to close the 'Manage Endpoints Connections' page.
     */
    public void close() {
        WebElement closeButton = driver.findElement(By.xpath("//*[text()='Close']"));
        safeClick(closeButton);
    }

    /**
     * Close 'Manage Endpoints Connections' page and save changes if prompted.
     */
    public void closeAndSave() {
        close();
        WebElement saveButton = driver.findElement(By.xpath("//div[@class='modal-footer ng-scope']/*[text()='Save']"));
        safeClick(saveButton);
    }

    /**
     * Close 'Manage Endpoints Connections' page without saving changes.
     */
    public void closeNoSave() {
        close();
        WebElement noSaveButton = driver.findElement(By.xpath("//div[@class='modal-footer ng-scope']/*[text()='Don't save']"));
        safeClick(noSaveButton);
    }

    /**
     * Delete an endpoint by its name.
     *
     * @param endpointName The name of the endpoint to be deleted.
     */
    public void deleteEndpointByName(String endpointName) {
        WebElement endpoint = driver.findElement(By.xpath("//*[text()='" + endpointName + "']"));
        actions.contextClick(endpoint).perform();
        driver.findElement(By.xpath("//*[@id='5']")).click();
        WebElement okButton = driver.findElement(By.xpath("//button[text()='OK']"));
        safeClick(okButton);
    }

}


