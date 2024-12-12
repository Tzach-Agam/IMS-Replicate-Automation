import browsers.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import replicate_pages.*;

import java.io.IOException;
import java.time.Duration;
import configuration.ConfigurationManager;

public class Test1 {

    private WebDriver driver;
    private TasksPage tasksGeneralPage;
    private NewTaskPage newTaskPage;
    private CommonMethods commonMethods;
    private ConfigurationManager config;
    private ManageEndpointsPage manageEndpoints;
    private DesignerPage designerPage;
    private TableSelection tableSelection;
    private TaskSettingsPage taskSettingsPage;
    private String IMSEndpointName;
    private String OracleEndpointName;
    private String SQLEndpointName;
    private String taskName;


    @BeforeMethod
    void setup() throws IOException {
        initializeWebObjects();
        initializeWebDriver();
    }

    @Test
    void test1() throws InterruptedException {
        taskCreation("IMS2Oracle_FL_CDC");
    }

    @AfterMethod
    void teardown() throws InterruptedException {
        Thread.sleep(5000);
        this.driver.quit();
    }




    public void initializeWebObjects() throws IOException {
        this.config = new ConfigurationManager("src/main/resources/config.ini");
        this.driver = WebDriverFactory.chromeDriver();
        this.tasksGeneralPage = new TasksPage(driver);
        this.newTaskPage = new NewTaskPage(driver);
        this.commonMethods = new CommonMethods(driver);
        this.manageEndpoints = new ManageEndpointsPage(driver);
        this.designerPage = new DesignerPage(driver);
        this.tableSelection = new TableSelection(driver);
        this.taskSettingsPage = new TaskSettingsPage(driver);


    }
    public void initializeWebDriver(){
        this.driver.get(config.getLoginURL());
        this.driver.manage().window().maximize();
        this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        this.commonMethods.loaderIconOpeningReplicate();
    }

    public void taskCreation(String name) throws InterruptedException {
        endpointsCreation();
        taskName = this.newTaskPage.randomTaskName(name);
        this.tasksGeneralPage.createNewTask();
        this.newTaskPage.newTaskCreation(taskName, "task");
        this.commonMethods.taskDataLoader();
        this.designerPage.chooseSourceTarget(IMSEndpointName, OracleEndpointName);
        this.designerPage.confirmIMS();
        this.designerPage.enterTableSelection();
        this.tableSelection.selectChosenTables(new String[]{"STUDENT", "STUDENT_COURSE", "STUDENT_ST"});
        this.designerPage.enterTaskSettings();
        this.taskSettingsPage.setTaskSettingsGeneral("replicate_target_schema","replicate_control_schema");
        this.taskSettingsPage.changeComponentLogging("VERBOSE","TARGET_LOAD", "SOURCE_CAPTURE");
        this.taskSettingsPage.okButton();
    }

    public void endpointsCreation() throws InterruptedException {
        IMSEndpointName = this.manageEndpoints.randomEndpointName("IMS_Source");
        OracleEndpointName = this.manageEndpoints.randomEndpointName("OracleTarget");
        SQLEndpointName = this.manageEndpoints.randomEndpointName("SQL_Target");
        this.tasksGeneralPage.enterManageEndpoints();
        this.manageEndpoints.createIMSsource(IMSEndpointName);
        this.manageEndpoints.createOracletarget(OracleEndpointName);
        //this.manageEndpoints.createSQLServertarget(SQLEndpointName);
        this.manageEndpoints.close();



    }



}
