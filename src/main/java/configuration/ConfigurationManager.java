package configuration;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ConfigurationManager {
    private final Ini ini;

    // Constructor
    public ConfigurationManager(String configFilePath) throws IOException {
        ini = new Ini(new File(configFilePath));
    }

    // Method to get a section as a map
    public Map<String, String> getSection(String sectionName) {
        Section section = ini.get(sectionName);
        return section != null ? section : null;
    }

    // Method to get the base URL
    public String getBaseUrl() {
        return ini.get("Website", "base_url");
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

    // Method to get default schemas
    public Map<String, String> getDefaultSchemas() {
        return getSection("Default_Schemas");
    }

    // Methods for specific paths
    public String getSourceTaskLogPath() {
        return ini.get("Task_log_Path", "source_directory");
    }

    public Map<String, String> getSqlLogsAndResultsPaths() {
        Map<String, String> paths = Map.of(
                "logs", ini.get("Task_log_Path", "sql_task_logs_dir"),
                "goodFiles", ini.get("Good_Files_Path", "sql_good_files")
        );
        return paths;
    }


    public Map<String, String> getOracleLogsAndResultsPaths() {
        Map<String, String> paths = Map.of(
                "logs", ini.get("Task_log_Path", "oracle_task_logs_dir"),
                "goodFiles", ini.get("Good_Files_Path", "oracle_good_files")
        );
        return paths;
    }

    // Method to get the IMS DB server
    public String getTargetSchema() {
        return ini.get("Default_Schemas", "replicate_selenium_target");
    }

    public String getControlSchema() {
        return ini.get("Default_Schemas", "replicate_selenium_control");
    }

    // Method to get the IMS DB server
    public String getIMSServer() {
        return ini.get("IMS_DB", "server");
    }

    // Method to get the IMS DB username
    public String getIMSUsername() {
        return ini.get("IMS_DB", "username");
    }

    // Method to get the IMS DB password
    public String getIMSPassword() {
        return ini.get("IMS_DB", "password");
    }

    // Method to get the IMS DB solution
    public String getIMSSolution() {
        return ini.get("IMS_DB", "solution");
    }

    // Method to get the IMS DB workspace
    public String getIMSWorkspace() {
        return ini.get("IMS_DB", "workspace");
    }

    // Method to get the IMS DB dataSource
    public String getIMSDataSource() {
        return ini.get("IMS_DB", "dataSource");
    }

    // Method to get the MSSQL DB server
    public String getMSSQLServer() {return ini.get("MSSQL_db", "server");}

    // Method to get the MSSQL DB database name
    public String getMSSQLDatabase() {return ini.get("MSSQL_db", "database");}

    // Method to get the MSSQL DB username
    public String getMSSQLUsername() {return ini.get("MSSQL_db", "username");}

    // Method to get the MSSQL DB password
    public String getMSSQLPassword() {return ini.get("MSSQL_db", "password");}

    // Method to get the Oracle DB DSN
    public String getOracleDSN() {return ini.get("Oracle_db", "dsn");}

    // Method to get the Oracle DB username
    public String getOracleUsername() {return ini.get("Oracle_db", "username");}

    // Method to get the Oracle DB password
    public String getOraclePassword() {return ini.get("Oracle_db", "Password");}

}
