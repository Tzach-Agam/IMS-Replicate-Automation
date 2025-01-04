package general;

import browsers.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import replicate_pages.*;
import databases.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import configuration.ConfigurationManager;

public class General {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected ConfigurationManager config;
    protected CommonMethods commonMethods;
    protected TasksPage tasksGeneralPage;
    protected NewTaskPage newTaskPage;
    protected ManageEndpointsPage manageEndpoints;
    protected DesignerPage designerPage;
    protected TableSelection tableSelection;
    protected TaskSettingsPage taskSettingsPage;
    protected MonitorPage monitorPage;
    protected String TaskName;
    protected String IMSEndpointName;
    protected String OracleEndpointName;
    protected String SQLEndpointName;
    protected IMSJCLSubmit imsDB;
    protected SqlServerDatabase sqlDB;
    protected OracleDatabase oracleDB;
    protected String targetSchema;
    protected String controlSchema;


    protected void SetUp() throws IOException, SQLException {
        initializeWebObjects();
        initializeDatabases();
        initializeWebDriver();
    }

    protected void TearDown() throws InterruptedException, SQLException {
        imsDB.logout();
        oracleDB.closeConnection();
        //sqlDB.closeConnection();
        deleteTaskEndpoint();
        driver.quit();
    }

    protected void initializeWebObjects() throws IOException {
        this.config = new ConfigurationManager("src/main/resources/config.ini");
        this.driver = WebDriverFactory.chromeDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.tasksGeneralPage = new TasksPage(driver);
        this.newTaskPage = new NewTaskPage(driver);
        this.commonMethods = new CommonMethods(driver);
        this.manageEndpoints = new ManageEndpointsPage(driver, config);
        this.designerPage = new DesignerPage(driver);
        this.tableSelection = new TableSelection(driver);
        this.taskSettingsPage = new TaskSettingsPage(driver);
        this.monitorPage = new MonitorPage(driver);
    }

    protected void initializeDatabases() throws SQLException, IOException {
        this.imsDB = new IMSJCLSubmit(config);
        this.oracleDB = new OracleDatabase(config);
        //this.sqlDB = new SqlServerDatabase(config);
        imsDB.login();
        oracleDB.connect();
        //sqlDB.connect();
        targetSchema = config.getTargetSchema();
        controlSchema = config.getControlSchema();
        oracleDB.dropAllTables(targetSchema);
        oracleDB.dropAllTables(controlSchema);
        oracleDB.dropUser(targetSchema);
        oracleDB.dropUser(controlSchema);
        oracleDB.createUser(targetSchema);
        oracleDB.createUser(controlSchema);
//        sqlDB.dropAllTables(targetSchema);
//        sqlDB.dropAllTables(controlSchema);
//        sqlDB.dropSchema(targetSchema);
//        sqlDB.dropSchema(controlSchema);
//        sqlDB.createSchema(targetSchema);
//        sqlDB.createSchema(controlSchema);
    }

    protected void initializeWebDriver() {
        this.driver.get(config.getLoginURL());
        this.driver.manage().window().maximize();
        this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        this.commonMethods.loaderIconOpeningReplicate();
    }

    protected void deleteTaskEndpoint(){
        commonMethods.navigateToMainPage("tasks");
        tasksGeneralPage.deleteTask(TaskName);
        tasksGeneralPage.enterManageEndpoints();
        manageEndpoints.deleteEndpointByName(IMSEndpointName);
        manageEndpoints.deleteEndpointByName(OracleEndpointName);
        //manageEndpoints.deleteEndpointByName(SQLEndpointName);
    }

}

