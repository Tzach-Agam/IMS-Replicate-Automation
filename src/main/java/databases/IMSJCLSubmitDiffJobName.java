package databases;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.File;
import java.util.concurrent.*;
import configuration.ConfigurationManager;


public class IMSJCLSubmitDiffJobName {
    private ConfigurationManager config;

    public IMSJCLSubmitDiffJobName() throws IOException {
        this.config = new ConfigurationManager("src/main/resources/config.ini");
    }

    public void runFtpCommands(String datasetName) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Callable<Void> task = () -> {
                Process process = new ProcessBuilder("cmd").redirectErrorStream(true).start();

                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                    // List of commands to execute
                    String[] commands = {
                            "ftp " + config.getIMSServer(),
                            config.getIMSUsername(), // Enter username
                            config.getIMSPassword(), // Enter password
                            "quote site filetype=jes",
                            "get 'TZACHA.IMS.CNTL(" + datasetName + ")'"
                    };

                    // Execute the commands
                    for (String command : commands) {
                        writer.write(command);
                        writer.newLine();
                        writer.flush();
                    }
                }
                return null; // Task finished
            };

            // Submit the task and enforce a timeout
            Future<Void> future = executor.submit(task);
            future.get(10, TimeUnit.SECONDS);

            System.out.println("FTP command execution finished.");
        } catch (TimeoutException e) {
            System.err.println("Timeout occurred. Task terminated.");
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        } finally {
            executor.shutdownNow(); // Ensure the executor is cleaned up
        }
    }

    public void terminateFtpProcess() {
        try {
            // List all processes and find "ftp.exe"
            Process listProcesses = new ProcessBuilder("tasklist").start();
            try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(listProcesses.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("ftp.exe")) {
                        // Terminate the process
                        new ProcessBuilder("taskkill", "/F", "/IM", "ftp.exe").start().waitFor();
                        System.out.println("Terminated ftp.exe process.");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to terminate ftp.exe process: " + e.getMessage());
        }
    }

    public void deleteTmpFiles() {
        File currentDir = new File(".");
        File[] tmpFiles = currentDir.listFiles(file -> file.getName().startsWith("Tmp") && file.getName().endsWith(".tmp"));

        if (tmpFiles == null || tmpFiles.length == 0) {
            throw new AssertionError("No temporary files found to delete.");
        }

        for (File tmpFile : tmpFiles) {
            if (tmpFile.delete()) {
                System.out.println("Deleted: " + tmpFile.getName());
            } else {
                System.err.println("Failed to delete: " + tmpFile.getName());
            }
        }
    }

    public void submitJob(String datasetName){
        runFtpCommands(datasetName);
        terminateFtpProcess();
        deleteTmpFiles();
    }

    public static void main(String[] args) throws IOException {
        IMSJCLSubmitDiffJobName submitter = new IMSJCLSubmitDiffJobName();
        submitter.submitJob("HOSPI");
//        submitter.runFtpCommands("HOSPD");
//        submitter.terminateFtpProcess();
//        submitter.deleteTmpFiles(); // Delete all temporary files after execution
    }
}
