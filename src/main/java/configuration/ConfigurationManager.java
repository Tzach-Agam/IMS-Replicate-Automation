package configuration;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Utility for managing configuration settings using a configuration file (config.ini).
 */
public class ConfigurationManager {

    private final Ini ini;

    /**
     * Initialize the ConfigurationManager class object
     * @param configFilePath: Path to the configuration file (config.ini). with all the necessary settings.
     */
    public ConfigurationManager(String configFilePath) throws IOException {
        ini = new Ini(new File(configFilePath));
    }


    /**
     * General configuration
     */
    public Map<String, String> getSection(String sectionName) {
        Section section = ini.get(sectionName);
        return section != null ? section : null;
    }

    public String getLoginURL() {
        String baseUrl = ini.get("Website", "base_url");
        String username = ini.get("Credentials", "username");
        String password = ini.get("Credentials", "password");

        // Constructing the desired URL
        return String.format("%s://%s:%s@%s/login/",
                baseUrl.split("://")[0],  // Protocol (http or https)
                username,
                password,
                baseUrl.split("://")[1]   // Host and path
        );
    }


    /**
     * Replicate configuration
     */
    public String getBaseUrl() {
        return ini.get("Website", "base_url");
    }

    public String getReplicateTaskLogPath() {return ini.get("Task_log_Path", "source_directory");}

    public String getTargetSchema() {return ini.get("Default_Schemas", "target_schema");}

    public String getControlSchema() {
        return ini.get("Default_Schemas", "control_schema");
    }


    /**
     * Oracle Database configuration
     */
    public String getOracleDSN() {return ini.get("Oracle_db", "dsn");}

    public String getOracleUsername() {return ini.get("Oracle_db", "username");}

    public String getOraclePassword() {return ini.get("Oracle_db", "Password");}


    /**
     * IMS Database configuration
     */
    public String getIMSServer() {
        return ini.get("IMS_DB", "server");
    }

    public String getIMSPort() {return ini.get("IMS_DB", "port");    }

    public String getIMSUsername() {
        return ini.get("IMS_DB", "username");
    }

    public String getIMSPassword() {
        return ini.get("IMS_DB", "password");
    }

    public String getIMSSolution() {
        return ini.get("IMS_DB", "solution");
    }

    public String getIMSWorkspace() {
        return ini.get("IMS_DB", "workspace");
    }

    public String getIMSDataSource() {
        return ini.get("IMS_DB", "dataSource");
    }

    public String getIMSCDCAdapter() {
        return ini.get("IMS_DB", "cdcAdapter");
    }

    public String getIMSCDCWorkspace() {
        return ini.get("IMS_DB", "cdcWorkspace");
    }

    public String getIMSPsb() {
        return ini.get("IMS_DB", "psb");
    }


    /**
     * SQL Server Database configuration
     */
    public String getMSSQLServer() {return ini.get("MSSQL_db", "server");}

    public String getMSSQLDatabase() {return ini.get("MSSQL_db", "database");}

    public String getMSSQLUsername() {return ini.get("MSSQL_db", "username");}

    public String getMSSQLPassword() {return ini.get("MSSQL_db", "password");}


    /**
     * Automation task configuration
     */
    public String getJCLPath(String fileName, Class<?> clazz) {
        // Get the base directory of the project.
        String basePath = System.getProperty("user.dir");
        // Construct the package path based on the provided class.
        String packagePath = clazz.getPackageName().replace('.', '/');
        // Combine base path, package path, and file name with the "JCL_result" folder.
        return basePath + "/src/test/java/" + packagePath + "/JCL_result/" + fileName;
    }

    public String getTaskLogPath(Class<?> clazz) {
        // Get the base directory of the project.
        String basePath = System.getProperty("user.dir");
        // Construct the package path based on the provided class.
        String packagePath = clazz.getPackageName().replace('.', '/');
        // Combine base path, package path, and file name with the "JCL_result" folder.
        return basePath + "/src/test/java/" + packagePath + "/task_logs/";
    }

    public String getResultsPath(Class<?> clazz) {
        // Get the base directory of the project.
        String basePath = System.getProperty("user.dir");
        // Construct the package path based on the provided class.
        String packagePath = clazz.getPackageName().replace('.', '/');
        // Combine base path, package path, and file name with the "JCL_result" folder.
        return basePath + "/src/test/java/" + packagePath + "/results_files/";
    }
}
