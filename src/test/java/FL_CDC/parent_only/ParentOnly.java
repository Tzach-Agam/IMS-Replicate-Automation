package FL_CDC.parent_only;

import org.testng.annotations.Test;
import java.io.IOException;
import java.sql.SQLException;
import static utilities.UtilitiesMethods.*;


public class ParentOnly extends Settings {

    @Test
    public void ParentOnlyTest() throws InterruptedException, SQLException, IOException {
        String taskName = "IMS_2_Oracle_ParentOnly";
        //String taskName = "IMS_2_SQL_ParentOnly";
        taskCreation(taskName);
        imsDB.retrieveFile("HOSPDLET", config.getJCLPath("DELOutput.txt", ParentOnly.class));
        imsDB.retrieveFile("POF", config.getJCLPath("FLOutput.txt", ParentOnly.class));
        designerPage.runNewTask();
        designerPage.startTaskWait();
        monitorPage.waitForFullLoadCompletion("1");
        monitorPage.clickChangeProcessingTab();
        moveFileToTargetDir(config.getReplicateTaskLogPath(), config.getTaskLogPath(ParentOnly.class), "reptask_" + TaskName + ".log");
        oracleDB.exportSchemaToCSV(targetSchema, config.getResultsPath(ParentOnly.class) + taskName + ".csv");
        //sqlDB.exportSchemaToCSV(targetSchema, config.getResultsPath(ParentOnly.class) + taskName + ".csv");
        compareFiles(config.getResultsPath(ParentOnly.class) + taskName + ".good", config.getResultsPath(ParentOnly.class) + taskName + ".csv");
    }
}