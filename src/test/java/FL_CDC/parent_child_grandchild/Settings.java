package FL_CDC.parent_child_grandchild;
import databases.IMSJCLSubmit;
import general.General;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import java.io.IOException;
import java.sql.SQLException;

public class Settings extends General {

      @BeforeMethod
      protected void SetUp() throws IOException, SQLException {
         super.SetUp();
         this.imsDB.logout();
         this.imsDB = new IMSJCLSubmit("zos5.qliktech.com", "VICTORK", "VICTORK", "TZACHA.IMS.CNTL");
         System.out.println("using a different IMS");
         imsDB.login();
      }

      @AfterMethod
      protected void TearDown() throws SQLException, InterruptedException {
      super.TearDown();
   }

     void endpointsCreation() throws InterruptedException {
        IMSEndpointName = this.manageEndpoints.randomEndpointName("IMS_Source");
        OracleEndpointName = this.manageEndpoints.randomEndpointName("OracleTarget");
        ///SQLEndpointName = this.manageEndpoints.randomEndpointName("SQL_Target");
        this.tasksGeneralPage.enterManageEndpoints();
        this.manageEndpoints.createIMSsource3(IMSEndpointName, "Endpoint", "IMS", "zos5.qliktech.com", "5461", "VICTORK", "VICTORK", "", "HOSP62_BULK", "HOSP62", "HOSP62_ag", "HOSP62_ag");
        ///this.manageEndpoints.createIMSsource(IMSEndpointName);
        this.manageEndpoints.createOracletarget(OracleEndpointName);
        ///this.manageEndpoints.createSQLServertarget(SQLEndpointName);
        this.manageEndpoints.close();
    }

     void taskCreation(String thisTaskName) throws InterruptedException {
        endpointsCreation();
        TaskName = this.newTaskPage.randomTaskName(thisTaskName);
        this.tasksGeneralPage.createNewTask();
        this.newTaskPage.newTaskCreation(TaskName, "task");
        this.designerPage.waitForNewTask(TaskName);
        this.designerPage.chooseSourceTarget(IMSEndpointName, OracleEndpointName);
        ///this.designerPage.chooseSourceTarget(IMSEndpointName, SQLEndpointName);
        this.designerPage.enterTableSelection();
        this.tableSelection.selectChosenTables("HOSPITAL", "WARD", "PATIENT");
        this.designerPage.enterTaskSettings();
        this.taskSettingsPage.setTaskSettingsGeneral(config.getTargetSchema(), config.getControlSchema());
        this.taskSettingsPage.okButton();
    }
}
