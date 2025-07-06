package FL_CDC.parent_2_childs;

import org.testng.annotations.Test;
import java.io.IOException;
import java.sql.SQLException;
import static utilities.UtilitiesMethods.*;


public class Parent2Child extends Settings {

    @Test
    public void Parent2ChildTest() throws InterruptedException, SQLException, IOException {
        String taskName = "IMS_2_Oracle_Parent2Child";
        ///String taskName = "IMS_2_SQL_Parent2Child";
        taskCreation(taskName);
        imsDB.retrieveFile("HOSPDLET", config.getJCLPath("DELOutput.txt", Parent2Child.class));
        imsDB.retrieveFile("P2CF", config.getJCLPath("FLOutput.txt", Parent2Child.class));
        designerPage.runNewTask();
        designerPage.startTaskWait();
        monitorPage.waitForFullLoadCompletion("3");
        monitorPage.clickChangeProcessingTab();
        ///imsDB.retrieveFile("P2CC", config.getJCLPath("CDCOutput.txt", Parent2Child.class));
        ///monitorPage.checkInsertStatus("0", "3", "4");
        ///monitorPage.checkDeleteStatus("0", "0", "2");
        monitorPage.stopTask();
        monitorPage.waitForTaskStop();
        moveFileToTargetDir(config.getReplicateTaskLogPath(), config.getTaskLogPath(Parent2Child.class), "reptask_" + TaskName + ".log");
        oracleDB.exportSchemaToCSV(targetSchema, config.getResultsPath(Parent2Child.class) + taskName + ".csv");
        ///sqlDB.exportSchemaToCSV(targetSchema, config.getResultsPath(Parent2Child.class) + taskName + ".csv");
        compareFiles(config.getResultsPath(Parent2Child.class) + taskName + ".good", config.getResultsPath(Parent2Child.class) + taskName + ".csv");
    }
}