package FL_CDC.parent_only;
import general.General;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import java.io.IOException;
import java.sql.SQLException;

public class Settings extends General {

      @BeforeMethod
      protected void SetUp() throws IOException, SQLException {
         super.SetUp();
      }

      @AfterMethod
      protected void TearDown() throws SQLException, InterruptedException {
      super.TearDown();
   }

     void endpointsCreation() throws InterruptedException {
        IMSEndpointName = this.manageEndpoints.randomEndpointName("IMS_Source");
        OracleEndpointName = this.manageEndpoints.randomEndpointName("OracleTarget");
        //SQLEndpointName = this.manageEndpoints.randomEndpointName("SQL_Target");
        this.tasksGeneralPage.enterManageEndpoints();
        this.manageEndpoints.createIMSsource(IMSEndpointName);
        this.manageEndpoints.createOracletarget(OracleEndpointName);
        //this.manageEndpoints.createSQLServertarget(SQLEndpointName);
        this.manageEndpoints.close();
    }

     void taskCreation(String thisTaskName) throws InterruptedException {
        endpointsCreation();
        TaskName = this.newTaskPage.randomTaskName(thisTaskName);
        this.tasksGeneralPage.createNewTask();
        this.newTaskPage.newTaskCreation(TaskName, "task");
        this.commonMethods.taskDataLoader();
        this.designerPage.chooseSourceTarget(IMSEndpointName, OracleEndpointName);
        this.designerPage.confirmIMS();
        this.designerPage.enterTableSelection();
        this.tableSelection.selectChosenTables(new String[]{"HOSPITAL"});
        this.designerPage.enterTaskSettings();
        this.taskSettingsPage.setTaskSettingsGeneral(config.getTargetSchema(), config.getControlSchema());
        this.taskSettingsPage.changeComponentLogging("VERBOSE", "TARGET_LOAD", "SOURCE_CAPTURE");
        this.taskSettingsPage.okButton();
    }
}
