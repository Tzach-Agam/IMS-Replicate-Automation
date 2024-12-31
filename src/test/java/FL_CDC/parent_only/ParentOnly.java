package FL_CDC.parent_only;

import org.testng.annotations.Test;
import java.io.IOException;
import java.sql.SQLException;
import static utilities.UtilitiesMethods.*;


public class ParentOnly extends Settings {

    @Test
    public void ParentOnlyTest() throws InterruptedException, SQLException, IOException {
        taskCreation("IMS_2_OracleParentOnly");
        imsDB.retrieveFile("HOSPDLET", config.getJCLPath("DELOutput.txt", ParentOnly.class));
        imsDB.retrieveFile("POF", config.getJCLPath("FLOutput.txt", ParentOnly.class));
        designerPage.runNewTask();
        designerPage.startTaskWait();
        monitorPage.waitForFullLoadCompletion("1");
        monitorPage.clickChangeProcessingTab();
        imsDB.retrieveFile("POC", config.getJCLPath("CDCOutput.txt", ParentOnly.class));
        //monitorPage.stopTask();
        //monitorPage.waitForTaskStop();
        moveFileToTargetDir(config.getReplicateTaskLogPath(), config.getTaskLogPath(ParentOnly.class), "reptask_" + TaskName + ".log");
        oracleDB.exportSchemaToCSV(targetSchema, config.getResultsPath(ParentOnly.class) + "IMS_2_Oracle_ParentOnly.csv");
        compareFiles(config.getResultsPath(ParentOnly.class) + "IMS_2_Oracle_ParentOnly.good", config.getResultsPath(ParentOnly.class) + "IMS_2_Oracle_ParentOnly.csv");
    }
}