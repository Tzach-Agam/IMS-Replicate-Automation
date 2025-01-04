package databases;

import configuration.ConfigurationManager;
import org.apache.commons.net.ftp.FTPClient;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * A class for submitting JCL and interacting with IMS datasets via FTP.
 * This class facilitates interactions with an IMS database by allowing operations such as
 * logging into the FTP server, retrieving files from IMS datasets, and managing FTP sessions.
 * It utilizes the Apache Commons Net FTPClient for FTP operations.
 */
public class IMSJCLSubmit {

    private ConfigurationManager config;
    private String server;       // FTP server address
    private int port;            // FTP port
    private String user;         // FTP username
    private String pass;         // FTP password
    private String psb;          // PSB dataset prefix
    private FTPClient ftpClient; // FTP client object

    /**
     * Initialize the IMSJCLSubmit instance.
     * @param configFile An instance of ConfigurationManager to retrieve IMS database configuration.
     */
    public IMSJCLSubmit(ConfigurationManager configFile) {
        this.config = configFile;
        this.server = config.getIMSServer();
        this.port = 21;
        this.user = config.getIMSUsername();
        this.pass = config.getIMSPassword();
        this.psb = config.getIMSPsb();
        this.ftpClient = new FTPClient();
    }

    /**
     * Log in to the FTP server using the credentials provided in the configuration.
     */
    public void login() {
        try {
            ftpClient.connect(server, port);
            System.out.println("Connected to the server.");

            boolean loginSuccess = ftpClient.login(user, pass);
            if (loginSuccess) {
                System.out.println("Login successful.");
            } else {
                System.out.println("Login failed.");
            }
        } catch (IOException e) {
            System.out.println("Error during login: " + e.getMessage());
        }
    }

    /**
     * Retrieve a file from an IMS dataset using FTP.
     * @param datasetName   The name of the dataset to retrieve.
     * @param localFileName The path to save the retrieved file locally.
     */
    public void retrieveFile(String datasetName, String localFileName) {
        try {
            // Execute SITE command to set file type to JES
            int replyCode = ftpClient.site("filetype=jes");
            if (replyCode != 200) {
                System.out.println("SITE command failed with reply code: " + replyCode);
                return;
            }
            System.out.println("SITE command executed successfully.");

            // Retrieve the file
            String remoteFile = "'" + psb + "(" + datasetName + ")" + "'"; // Full path to file

            try (FileOutputStream fos = new FileOutputStream(localFileName)) {
                boolean success = ftpClient.retrieveFile(remoteFile, fos);
                if (success) {
                    System.out.println("File retrieved successfully: " + localFileName);
                } else {
                    System.out.println("Failed to retrieve the file. Server reply: " + ftpClient.getReplyString());
                }
            }
        } catch (IOException e) {
            System.out.println("Error during file retrieval: " + e.getMessage());
        }
    }

    /**
     * Log out from the FTP server and disconnect.
     */
    public void logout() {
        try {
            ftpClient.logout();
            System.out.println("Logged out.");
        } catch (IOException e) {
            System.out.println("Error during logout: " + e.getMessage());
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.disconnect();
                    System.out.println("Disconnected from the server.");
                }
            } catch (IOException ex) {
                System.out.println("Error during disconnection: " + ex.getMessage());
            }
        }
    }
}
