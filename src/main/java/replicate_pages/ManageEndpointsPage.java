package replicate_pages;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import static utilities.UtilitiesMethods.safeClick;
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
     * @param driver WebDriver instance for Selenium automation.
     */
    public ManageEndpointsPage(WebDriver driver, ConfigurationManager configFile) {
        this.driver = driver;
        this.actions = new Actions(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.config = configFile;
    }

    /**
     * Click the 'New Endpoint Connection' button to create a new endpoint.
     */
    public void newEndpointConnection() {
        WebElement newEndpoint = this.wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[text()='New Endpoint Connection']")));
        safeClick(newEndpoint);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'New Endpoint Connection ')]")));
    }

    /**
     * Creates a random name of the endpoint.
     * @param name The name of the endpoint.
     */
    public String randomEndpointName(String name) {
        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(900000); // Ensures a 6-digit number
        return name + randomNumber;
    }

    /**
     * Enter the name of the endpoint.
     * @param name The name of the endpoint.
     */
    public void enterEndpointName(String name) {
        WebElement endpointName = this.wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='endpointName']")));
        endpointName.clear();
        endpointName.sendKeys(name);
    }

    /**
     * Enter a description for the endpoint.
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

    /**
     * Choose the endpoints 'type' (IMS, Oracle, SQL Server)
     */
    public void enterType(String type){
        WebElement inputElement = driver.findElement(By.cssSelector("[class='textInputInRowWrap']>[type='text']"));
        inputElement.sendKeys(type);
    }

    /**
     * IMS methods for specific IMS settings
     */
    public void chooseIMSEndpointType(String type){
        enterType(type);
        WebElement endpointToSelect = driver.findElement(By.xpath("//*[text()='IBM IMS']"));
        safeClick(endpointToSelect);
    }

    public void chooseIMSServer(String server){
        WebElement imsServer = driver.findElement(By.xpath("//*[@id=\"server\"]"));
        imsServer.sendKeys(server);
    }

    public void chooseIMSPort(String port){
        WebElement imsPort = driver.findElement(By.xpath("//*[@ng-model=\"numberData\"]"));
        imsPort.clear();
        imsPort.sendKeys(port);
    }

    public void chooseIMSUsername(String username){
        WebElement imsUsername = driver.findElement(By.xpath("//*[@id=\"user\"]"));
        imsUsername.sendKeys(username);
    }

    public void chooseIMSPassword(String password){
        WebElement imsPassword = driver.findElement(By.xpath("//*[@id=\"password\"]"));
        imsPassword.sendKeys(password);
    }

    public void chooseIMSWorkspace(String workspace){
        WebElement imsWorksapce = driver.findElement(By.xpath("//*[@errlabel=\"queryWorkspace\"]"));
        imsWorksapce.sendKeys(workspace);
    }

    public void chooseIMSDataSource(String datasource){
        WebElement imsDataSource = driver.findElement(By.xpath("//*[@errlabel=\"queryDatasource\"]"));
        imsDataSource.sendKeys(datasource);
    }

    public void chooseIMSCDCWorkspace(String cdcWorkspace){
        WebElement imsDataSource = driver.findElement(By.xpath("//*[@errlabel=\"captureWorkspace\"]"));
        imsDataSource.sendKeys(cdcWorkspace);
    }

    public void chooseIMSCDCAdapter(String cdcAdapter){
        WebElement imsDataSource = driver.findElement(By.xpath("//*[@errlabel=\"captureAdapter\"]"));
        imsDataSource.sendKeys(cdcAdapter);
    }

    /**
     * Create an IMS IBM source endpoint with config.ini parameters
     */
    public void createIMSsource(String endpointName) throws InterruptedException {
        newEndpointConnection();
        enterEndpointName(endpointName);
        enterEndpointDescription("IMS Source endpoint");
        chooseIMSEndpointType("IMS");
        chooseIMSServer(config.getIMSServer());
        chooseIMSPort(config.getIMSPort());
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
        WebElement imsCDCWorkspace = driver.findElement(By.xpath("//*[@id=\"cdcWorkspace\"]"));
        imsCDCWorkspace.sendKeys(config.getIMSCDCWorkspace());
        WebElement imsCDCAdapter = driver.findElement(By.xpath("//*[@id=\"cdcAdapter\"]"));
        imsCDCAdapter.sendKeys(config.getIMSCDCAdapter());
        Thread.sleep(500);
        testConnectionValid();
        save();
    }

    /**
     * Create an IMS endpoint with user custom parameters without save
     */
    public void createIMSsource2(String endpointName,String description, String type, String server, String port, String username, String password, String workspace, String datasource, String cdcWorkspace, String cdcAdapter) throws InterruptedException {
        newEndpointConnection();
        enterEndpointName(endpointName);
        enterEndpointDescription(description);
        chooseIMSEndpointType(type);
        chooseIMSServer(server);
        chooseIMSPort(port);
        chooseIMSUsername(username);
        chooseIMSPassword(password);
        chooseIMSWorkspace(workspace);
        chooseIMSDataSource(datasource);
        chooseIMSCDCWorkspace(cdcWorkspace);
        chooseIMSCDCAdapter(cdcAdapter);
        Thread.sleep(500);
    }

    /**
     * Create an IMS endpoint with user custom parameters and save
     */
    public void createIMSsource3(String endpointName,String description, String type, String server, String port, String username, String password, String workspace, String datasource, String cdcWorkspace, String cdcAdapter ) throws InterruptedException {
        newEndpointConnection();
        enterEndpointName(endpointName);
        enterEndpointDescription(description);
        chooseIMSEndpointType(type);
        chooseIMSServer(server);
        chooseIMSPort(port);
        chooseIMSUsername(username);
        chooseIMSPassword(password);
        chooseIMSWorkspace(workspace);
        chooseIMSDataSource(datasource);
        chooseIMSCDCWorkspace(cdcWorkspace);
        chooseIMSCDCAdapter(cdcAdapter);
        Thread.sleep(500);
        testConnectionValid();
        save();
    }

    /**
     * Create an Oracle target endpoint with config.ini parameters
     */
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
        Thread.sleep(500);
        testConnectionValid();
        save();
    }

    /**
     * Create a SQL Server target endpoint with config.ini parameters
     */
    public void createSQLServertarget(String endpointName) throws InterruptedException {
        newEndpointConnection();
        enterEndpointName(endpointName);
        enterEndpointDescription("SQL Server target endpoint");
        chooseTargetRole();
        enterType("Server");
        WebElement endpointToSelect = driver.findElement(By.xpath("//*[text()='Microsoft SQL Server']"));
        safeClick(endpointToSelect);
        WebElement sqlAuthCheckbox = driver.findElement(By.xpath("//*[@id='sqlAuth']/span[1]"));
        safeClick(sqlAuthCheckbox);
        WebElement sqlServer = driver.findElement(By.xpath("//*[@id=\"server\"]"));
        sqlServer.sendKeys(config.getMSSQLServer());
        WebElement sqlUsername = driver.findElement(By.xpath("//*[@id=\"username\"]"));
        sqlUsername.sendKeys(config.getMSSQLUsername());
        WebElement sqlPassword = driver.findElement(By.xpath("//*[@id=\"password\"]"));
        sqlPassword.sendKeys(config.getMSSQLPassword());
        WebElement sqlDatabase = driver.findElement(By.xpath("//input[@name='database']"));
        sqlDatabase.sendKeys(config.getMSSQLDatabase());
        Thread.sleep(500);
        testConnectionValid();
        save();
    }

    /**
     * Click the 'Test Connection' button to test the database connection.
     */
    public void testConnection() throws InterruptedException {
        WebElement testConnectionElement = driver.findElement(By.xpath("//*[text()='Test Connection']"));
        safeClick(testConnectionElement);
    }

    /**
     * Click the 'Test Connection' button to test the database connection and hps for valid response.
     */
    public void testConnectionValid() throws InterruptedException {
        testConnection();
        this.wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(@custom-tooltip-text,'Test connection succeeded')]")));
        Thread.sleep(500);
    }

    /**
     * Click the 'Save' button to save the endpoint configuration.
     */
    public void save() throws InterruptedException {
        WebElement saveButton = driver.findElement(By.xpath("//*[text()='Save']"));
        safeClick(saveButton);
        this.wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='Database was successfully saved']")));
        Thread.sleep(500);
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
    public void deleteEndpointByName(String endpointName) throws InterruptedException {
        WebElement endpoint = driver.findElement(By.xpath("//*[text()='" + endpointName + "']"));
        safeClick(endpoint);
        actions.contextClick(endpoint).perform();
        driver.findElement(By.xpath("//*[@id='5']")).click();
        WebElement okButton = driver.findElement(By.xpath("//button[text()='OK']"));
        safeClick(okButton);
        System.out.println("Deleted endpoint: " + endpointName);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[text()='\" + endpointName + \"']")));
    }

}


