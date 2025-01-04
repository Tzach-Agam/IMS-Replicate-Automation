package databases;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import configuration.ConfigurationManager;

/**
 * A class for interacting with an Oracle database.
 * This class provides functionality to connect to an Oracle database,
 * execute queries, manage schemas and users, fetch results, and export metadata and data.
 */
public class OracleDatabase {

    private String DB_URL;
    private String USER; // Replace with your username
    private String PASSWORD; // Replace with your password
    private ConfigurationManager config;
    private Connection connection;

    /**
     * Constructor to initialize OracleDatabase with configuration.
     * Sets up database connection details based on the configuration file.
     * @param configFile ConfigurationManager object containing database settings
     */
    public OracleDatabase(ConfigurationManager configFile) {
        config = configFile;
        DB_URL = "jdbc:oracle:thin:@" + config.getOracleDSN();
        USER = "system";
        PASSWORD = "oracle";
    }

    /**
     * Establishes a connection to the Oracle database.
     * @throws SQLException if a database access error occurs
     */
    public void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connected to the database successfully.");
        }
    }

    /**
     * Executes a query (INSERT, UPDATE, DELETE, etc.) on the Oracle database.
     *
     * @param query The SQL query to execute
     * @throws SQLException if a database access error occurs
     */
    public void executeQuery(String query) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        }
    }

    /**
     * Executes a SELECT query and fetches the result set.
     *
     * @param query The SQL SELECT query to execute
     * @return The ResultSet object containing query results
     * @throws SQLException if a database access error occurs
     */
    public ResultSet fetchResults(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    /**
     * Checks if the specified schema/user does not exist in the database.
     * This method queries the DBA_USERS view to determine if the user does not exist in the Oracle database.
     * If the user does not exist, it returns true; otherwise, it returns false.
     * @param schema The name of the schema/user to check for non-existence.
     * @return true if the user does not exist, false otherwise.
     * @throws SQLException if a database access error occurs during the check.
     */
    public boolean doesUserNotExist(String schema) throws SQLException {
        String checkUserQuery = "SELECT 1 FROM dba_users WHERE username = '" + schema + "'";
        try (ResultSet resultSet = fetchResults(checkUserQuery)) {
            return !resultSet.next(); // Return true if no result is found
        } catch (SQLException e) {
            System.err.println("Error checking if user exists: " + e.getMessage());
            throw e; // Rethrow exception to let the caller handle it
        }
    }

    /**
     * Create a new user in the Oracle database, and immediately give it Admin privileges. Users act like schemas
     * In an oracle database.
     * @param userName: The name of the user to create
     */
    public void createUser(String userName) {
        String create_user_query = "create user \"" + userName + "\" identified by oracle";
        String grant_user_query = "GRANT DBA TO \"" + userName + "\" WITH ADMIN OPTION";
        try {
            executeQuery(create_user_query);
            executeQuery(grant_user_query);
            System.out.println("User " + userName + " created");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Drops a user in the Oracle database in an oracle database.
     * @param userName: The name of the user to create
     */
    public void dropUser(String userName) throws SQLException {
        // Check if the schema exists first
        if (doesUserNotExist(userName)) {
            System.err.println("User '" + userName + "' does not exist.");
            return;
        }
        String dropUserQuery = "DROP USER \"" + userName + "\"";
        try {
            executeQuery(dropUserQuery);
            System.out.println("User " + userName + " dropped");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Drops all tables in the specified schema/user.
     *
     * @param schema The schema/user whose tables should be dropped
     * @throws SQLException if a database access error occurs
     */
    public void dropAllTables(String schema) throws SQLException {
        // Check if the schema exists first
        if (doesUserNotExist(schema)) {
            System.err.println("User '" + schema + "' does not exist.");
            return;
        }

        String fetchTablesQuery = "SELECT table_name FROM all_tables WHERE owner = '" + schema + "'";
        try (ResultSet resultSet = fetchResults(fetchTablesQuery)) {
            while (resultSet.next()) {
                String tableName = resultSet.getString("table_name");
                String dropTableQuery = "DROP TABLE \"" + schema + "\".\"" + tableName + "\" CASCADE CONSTRAINTS";
                try {
                    executeQuery(dropTableQuery);
                    System.out.println("Dropped table: " + tableName);
                } catch (SQLException e) {
                    System.err.println("Error dropping table: " + tableName);
                    throw e; // Rethrow the error if needed
                }
            }
        }
    }

    /**
     * Creates a CSV file containing metadata and data for all tables in the specified schema.
     *
     * @param schema   The schema/user whose tables should be exported.
     * @param filePath The path of the CSV file to create.
     * @throws SQLException if a database access error occurs.
     * @throws IOException  if a file access error occurs.
     */
    public void exportSchemaToCSV(String schema, String filePath) throws SQLException, IOException {
        String fetchTablesQuery = "SELECT table_name FROM all_tables WHERE owner = '" + schema + "'";
        try (ResultSet tablesResultSet = fetchResults(fetchTablesQuery);
             FileWriter writer = new FileWriter(filePath)) {

            boolean dataWritten = false;

            while (tablesResultSet.next()) {
                String tableName = tablesResultSet.getString("table_name");

                // Write table separator
                writer.write("--------------\n");
                writer.write("Table: \"" + tableName + "\"\n");

                // Fetch primary key columns
                String fetchPKQuery =
                        "SELECT col.column_name " +
                                "FROM all_constraints con " +
                                "JOIN all_cons_columns col " +
                                "ON con.constraint_name = col.constraint_name " +
                                "AND con.owner = col.owner " +
                                "WHERE con.constraint_type = 'P' " +
                                "AND con.owner = '" + schema + "' " +
                                "AND con.table_name = '" + tableName + "' " +
                                "ORDER BY col.position";

                StringBuilder primaryKeyColumns = new StringBuilder();
                try (ResultSet pkResultSet = fetchResults(fetchPKQuery)) {
                    while (pkResultSet.next()) {
                        if (!primaryKeyColumns.isEmpty()) {
                            primaryKeyColumns.append(", ");
                        }
                        primaryKeyColumns.append(pkResultSet.getString("column_name"));
                    }
                }

                if (!primaryKeyColumns.isEmpty()) {
                    writer.write("Primary Key: " + primaryKeyColumns.toString() + "\n");
                } else {
                    writer.write("Primary Key: None\n");
                }

                // Fetch column metadata
                String fetchColumnsQuery =
                        "SELECT column_name, data_type, CHAR_LENGTH AS data_length, nullable " +
                                "FROM all_tab_columns " +
                                "WHERE owner = '" + schema + "' " +
                                "AND table_name = '" + tableName + "'";

                try (ResultSet columnsResultSet = fetchResults(fetchColumnsQuery)) {
                    while (columnsResultSet.next()) {
                        String columnName = columnsResultSet.getString("column_name");
                        String dataType = columnsResultSet.getString("data_type");
                        String dataLength = columnsResultSet.getString("data_length");
                        String allowDBNull = "Y".equals(columnsResultSet.getString("nullable")) ? "True" : "False";

                        String lengthInfo = dataLength != null ? (dataLength.equals("-1") ? "MAX" : dataLength) : "";
                        writer.write("Column: " + columnName + ", Type: " + dataType +
                                (!lengthInfo.isEmpty() ? ", Length: " + lengthInfo : "") +
                                ", AllowDBNull: " + allowDBNull + "\n");
                    }
                    writer.write("\n");
                }

                // Fetch table data
                String fetchTableDataQuery = "SELECT * FROM \"" + schema + "\".\"" + tableName + "\"";
                try (ResultSet tableResultSet = fetchResults(fetchTableDataQuery)) {
                    ResultSetMetaData metaData = tableResultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    if (columnCount == 0) {
                        writer.write("No columns found for table: \"" + tableName + "\"\n\n");
                        continue;
                    }

                    // Write column headers
                    for (int i = 1; i <= columnCount; i++) {
                        writer.write(metaData.getColumnName(i) + (i < columnCount ? "," : "\n"));
                    }

                    // Write rows
                    while (tableResultSet.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            String value = tableResultSet.getString(i);
                            writer.write((value != null ? value : "NULL") + (i < columnCount ? "," : "\n"));
                        }
                    }

                    dataWritten = true;
                }

                writer.write("\n"); // Blank line after table data
            }

            if (!dataWritten) {
                writer.write("No data found in schema: \"" + schema + "\"\n");
            }

        } catch (SQLException | IOException e) {
            System.err.println("Error during CSV export: " + e.getMessage());
            throw e;
        }

        System.out.println("Schema exported to CSV file: " + filePath);
    }


    /**
     * Appends metadata and data for all tables in the specified schema to an existing CSV file.
     *
     * @param schema The schema/user whose tables should be exported
     * @param filePath The path of the CSV file to append to
     * @throws SQLException if a database access error occurs
     * @throws IOException if a file access error occurs
     */
    public void appendSchemaToCSV(String schema, String filePath) throws SQLException, IOException {
        File file = new File(filePath);

        // Create the file if it doesn't exist
        if (!file.exists()) {
            file.createNewFile();
        }

        String fetchTablesQuery = "SELECT table_name FROM all_tables WHERE owner = '" + schema + "'";
        try (ResultSet tablesResultSet = fetchResults(fetchTablesQuery);
             FileWriter writer = new FileWriter(file, true)) { // Open file in append mode

            boolean dataWritten = false;

            while (tablesResultSet.next()) {
                String tableName = tablesResultSet.getString("table_name");

                // Write table separator
                writer.write("--------------\n");
                writer.write("Table: \"" + tableName + "\"\n");

                // Fetch primary key columns
                String fetchPKQuery =
                        "SELECT cols.column_name " +
                                "FROM all_constraints cons " +
                                "JOIN all_cons_columns cols ON cons.constraint_name = cols.constraint_name " +
                                "AND cons.owner = cols.owner " +
                                "WHERE cons.constraint_type = 'P' " +
                                "AND cons.owner = '" + schema + "' " +
                                "AND cons.table_name = '" + tableName + "'";

                StringBuilder primaryKeyColumns = new StringBuilder();
                try (ResultSet pkResultSet = fetchResults(fetchPKQuery)) {
                    while (pkResultSet.next()) {
                        if (!primaryKeyColumns.isEmpty()) {
                            primaryKeyColumns.append(", ");
                        }
                        primaryKeyColumns.append(pkResultSet.getString("COLUMN_NAME"));
                    }
                }

                if (!primaryKeyColumns.isEmpty()) {
                    writer.write("Primary Key: " + primaryKeyColumns.toString() + "\n");
                } else {
                    writer.write("Primary Key: None\n");
                }

                // Fetch column metadata
                String fetchColumnsQuery =
                        "SELECT column_name, data_type, CHAR_LENGTH AS data_length, nullable " +
                                "FROM all_tab_columns " +
                                "WHERE owner = '" + schema + "' " +
                                "AND table_name = '" + tableName + "'";

                try (ResultSet columnsResultSet = fetchResults(fetchColumnsQuery)) {
                    while (columnsResultSet.next()) {
                        String columnName = columnsResultSet.getString("column_name");
                        String dataType = columnsResultSet.getString("data_type");
                        String dataLength = columnsResultSet.getString("data_length");
                        String allowDBNull = "Y".equals(columnsResultSet.getString("nullable")) ? "True" : "False";

                        String lengthInfo = dataLength != null ? (dataLength.equals("-1") ? "MAX" : dataLength) : "";
                        writer.write("Column: " + columnName + ", Type: " + dataType +
                                (!lengthInfo.isEmpty() ? ", Length: " + lengthInfo : "") +
                                ", AllowDBNull: " + allowDBNull + "\n");
                    }
                    writer.write("\n");
                }

                // Fetch table data
                String fetchTableDataQuery = "SELECT * FROM \"" + schema + "\".\"" + tableName + "\"";
                try (ResultSet tableResultSet = fetchResults(fetchTableDataQuery)) {
                    ResultSetMetaData metaData = tableResultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    if (columnCount == 0) {
                        writer.write("No columns found for table: \"" + tableName + "\"\n\n");
                        continue;
                    }

                    // Write column headers
                    for (int i = 1; i <= columnCount; i++) {
                        writer.write(metaData.getColumnName(i) + (i < columnCount ? "," : "\n"));
                    }

                    // Write rows
                    while (tableResultSet.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            String value = tableResultSet.getString(i);
                            writer.write((value != null ? value : "NULL") + (i < columnCount ? "," : "\n"));
                        }
                    }

                    dataWritten = true;
                }

                writer.write("\n"); // Blank line after table data
            }

            if (!dataWritten) {
                writer.write("No data found in schema: \"" + schema + "\"\n");
            }

        } catch (SQLException | IOException e) {
            System.err.println("Error during CSV export: " + e.getMessage());
            throw e;
        }

        System.out.println("Schema appended to CSV file: " + filePath);
    }

    /**
     * Closes the database connection.
     * @throws SQLException if a database access error occurs
     */
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Database connection closed.");
        }
    }

    public static void main(String args[]) throws IOException, SQLException {
        ConfigurationManager config = new ConfigurationManager("src/main/resources/config.ini");
        OracleDatabase oracledb = new OracleDatabase(config);
        oracledb.connect();
        oracledb.appendSchemaToCSV("replicate_selenium_target", "export.csv");
        oracledb.appendSchemaToCSV("replicate_selenium_control", "export.csv");
        oracledb.closeConnection();
    }
}
