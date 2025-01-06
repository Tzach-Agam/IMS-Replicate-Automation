package FL_CDC.parent_child_grandchild;

import org.testng.annotations.Test;
import java.io.IOException;
import java.sql.SQLException;
import static utilities.UtilitiesMethods.*;


public class ParentChildGran extends Settings {

    @Test
    public void ParentChildGranTest() throws InterruptedException, SQLException, IOException {
        String taskName = "IMS_2_Oracle_ParChiGra";
        ///String taskName = "IMS_2_SQL_ParChiGra";
        taskCreation(taskName);
        imsDB.retrieveFile("HOSPDLET", config.getJCLPath("DELOutput.txt", ParentChildGran.class));
        imsDB.retrieveFile("PCGF", config.getJCLPath("FLOutput.txt", ParentChildGran.class));
        designerPage.runNewTask();
        designerPage.startTaskWait();
        monitorPage.waitForFullLoadCompletion("3");
        monitorPage.clickChangeProcessingTab();
        monitorPage.stopTask();
        monitorPage.waitForTaskStop();
        moveFileToTargetDir(config.getReplicateTaskLogPath(), config.getTaskLogPath(ParentChildGran.class), "reptask_" + TaskName + ".log");
        oracleDB.exportSchemaToCSV(targetSchema, config.getResultsPath(ParentChildGran.class) + taskName + ".csv");
        ///sqlDB.exportSchemaToCSV(targetSchema, config.getResultsPath(ParentChildGran.class) + taskName + ".csv");
        compareFiles(config.getResultsPath(ParentChildGran.class) + taskName + ".good", config.getResultsPath(ParentChildGran.class) + taskName + ".csv");
    }
}