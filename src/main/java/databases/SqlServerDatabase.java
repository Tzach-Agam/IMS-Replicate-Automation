package databases;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import configuration.ConfigurationManager;

public class SqlServerDatabase {

    private String DB_URL;
    private String USER; // Replace with your username
    private String PASSWORD; // Replace with your password
    private ConfigurationManager config;
    private Connection connection;

    public SqlServerDatabase(ConfigurationManager configFile) throws IOException {
        config = configFile;
        DB_URL = "jdbc:sqlserver://" + config.getMSSQLServer() + ";databaseName=" + config.getMSSQLDatabase() +";encrypt=true;TrustServerCertificate=true";
        USER = "sa"; // Default SQL Server admin user
        PASSWORD = "nrWEL7zZwmXPZueb"; // Replace with your password
    }

    /**
     * Establishes a connection to the SQL Server database.
     * @throws SQLException if a database access error occurs
     */
    public void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connected to the database successfully.");
        }
    }

    /**
     * Executes a query (INSERT, UPDATE, DELETE, etc.) on the SQL Server database.
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
     * @param query The SQL SELECT query to execute
     * @return The ResultSet object containing query results
     * @throws SQLException if a database access error occurs
     */
    public ResultSet fetchResults(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    /**
     * Checks if the specified schema exists in the database.
     * This method queries the sys.schemas view to determine if the schema exists in the SQL Server database.
     * If the schema does not exist, it returns true; otherwise, it returns false.
     * @param schema The name of the schema to check for existence.
     * @return true if the schema does not exist, false otherwise.
     * @throws SQLException if a database access error occurs during the check.
     */
    public boolean doesSchemaNotExist(String schema) throws SQLException {
        String checkSchemaQuery = "SELECT 1 FROM sys.schemas WHERE name = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(checkSchemaQuery)) {
            preparedStatement.setString(1, schema);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return !resultSet.next(); // Return true if no result is found
            }
        } catch (SQLException e) {
            System.err.println("Error checking if schema exists: " + e.getMessage());
            throw e; // Rethrow exception to let the caller handle it
        }
    }

    public void createSchema(String schemaName) {
        String createSchemaQuery = "CREATE SCHEMA " + schemaName;
        try {
            executeQuery(createSchemaQuery);
            System.out.println("Schema " + schemaName + " created");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Drops the specified schema from the SQL Server database if it exists.
     * This method uses the SQL Server 'DROP SCHEMA IF EXISTS' feature.
     * @param schemaName The name of the schema to be dropped.
     */
    public void dropSchema(String schemaName) {
        // Query to drop the schema if it exists in the database
        String dropSchemaQuery = "DROP SCHEMA IF EXISTS " + schemaName;
        try {
            // Execute the drop schema query
            executeQuery(dropSchemaQuery);
            System.out.println("Schema " + schemaName + " dropped");

        } catch (SQLException e) {
            // Handle any SQL exceptions that might occur
            System.err.println("Error dropping schema: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Drops all tables in the specified schema/user if the schema exists.
     * @param schema The schema/user whose tables should be dropped
     * @throws SQLException if a database access error occurs
     */
    public void dropAllTables(String schema) throws SQLException {
        // Check if the schema exists using the doesUserNotExist method
        if (doesSchemaNotExist(schema)) {
            // If the schema does not exist, print a message and exit the method
            System.out.println("Schema " + schema + " does not exist.");
            return;
        }
        // Fetch all tables in the schema and drop them
        String fetchTablesQuery = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ?";
        try (PreparedStatement fetchStmt = connection.prepareStatement(fetchTablesQuery)) {
            fetchStmt.setString(1, schema);

            try (ResultSet resultSet = fetchStmt.executeQuery()) {
                while (resultSet.next()) {
                    String tableName = resultSet.getString("TABLE_NAME");
                    String dropTableQuery = "DROP TABLE [" + schema + "].[" + tableName + "]";
                    executeQuery(dropTableQuery);
                    System.out.println("Dropped table: " + tableName);
                }
            }
        }
    }


    /**
     * Creates a CSV file containing metadata and data for all tables in the specified schema.
     *
     * @param schema The schema/user whose tables should be exported
     * @param filePath The path of the CSV file to create
     * @throws SQLException if a database access error occurs
     * @throws IOException if a file access error occurs
     */
    public void exportSchemaToCSV(String schema, String filePath) throws SQLException, IOException {
        String fetchTablesQuery = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '" + schema + "' ORDER BY TABLE_NAME";
        try (ResultSet tablesResultSet = fetchResults(fetchTablesQuery);
             FileWriter writer = new FileWriter(filePath)) {

            boolean dataWritten = false;

            while (tablesResultSet.next()) {
                String tableName = tablesResultSet.getString("TABLE_NAME");

                // Write table separator
                writer.write("--------------\n");
                writer.write("Table: \"" + tableName + "\"\n");

                // Fetch primary key columns
                String fetchPKQuery =
                        "SELECT COLUMN_NAME " +
                                "FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS TC " +
                                "JOIN INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE CCU " +
                                "ON TC.CONSTRAINT_NAME = CCU.CONSTRAINT_NAME " +
                                "WHERE TC.CONSTRAINT_TYPE = 'PRIMARY KEY' " +
                                "AND TC.TABLE_SCHEMA = '" + schema + "' " +
                                "AND TC.TABLE_NAME = '" + tableName + "'";

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
                        "SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, IS_NULLABLE " +
                                "FROM INFORMATION_SCHEMA.COLUMNS " +
                                "WHERE TABLE_SCHEMA = '" + schema + "' " +
                                "AND TABLE_NAME = '" + tableName + "'";

                try (ResultSet columnsResultSet = fetchResults(fetchColumnsQuery)) {
                    while (columnsResultSet.next()) {
                        String columnName = columnsResultSet.getString("COLUMN_NAME");
                        String dataType = columnsResultSet.getString("DATA_TYPE");
                        String length = columnsResultSet.getString("CHARACTER_MAXIMUM_LENGTH");
                        String allowDBNull = "YES".equals(columnsResultSet.getString("IS_NULLABLE")) ? "True" : "False";

                        writer.write("Column: " + columnName + ", Type: " + dataType +
                                (length != null ? ", Length: " + length : "") +
                                ", AllowDBNull: " + allowDBNull + "\n");
                    }
                    writer.write("\n");
                }

                // Fetch table data
                String fetchTableDataQuery = "SELECT * FROM [" + schema + "].[" + tableName + "]";
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

        // Check if the file exists; if not, create it
        if (!file.exists()) {
            file.createNewFile();
        }

        String fetchTablesQuery = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '" + schema + "' ORDER BY TABLE_NAME";
        try (ResultSet tablesResultSet = fetchResults(fetchTablesQuery);
             FileWriter writer = new FileWriter(file, true)) { // Open file in append mode

            while (tablesResultSet.next()) {
                String tableName = tablesResultSet.getString("TABLE_NAME");

                // Write table separator
                writer.write("--------------\n");
                writer.write("Table: \"" + tableName + "\"\n");

                // Fetch primary key columns
                String fetchPKQuery =
                        "SELECT COLUMN_NAME " +
                                "FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE " +
                                "WHERE TABLE_SCHEMA = '" + schema + "' " +
                                "AND TABLE_NAME = '" + tableName + "'";

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
                        "SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, IS_NULLABLE " +
                                "FROM INFORMATION_SCHEMA.COLUMNS " +
                                "WHERE TABLE_SCHEMA = '" + schema + "' AND TABLE_NAME = '" + tableName + "'";

                try (ResultSet columnsResultSet = fetchResults(fetchColumnsQuery)) {
                    while (columnsResultSet.next()) {
                        String columnName = columnsResultSet.getString("COLUMN_NAME");
                        String dataType = columnsResultSet.getString("DATA_TYPE");
                        String length = columnsResultSet.getString("CHARACTER_MAXIMUM_LENGTH");
                        String allowDBNull = "YES".equals(columnsResultSet.getString("IS_NULLABLE")) ? "True" : "False";

                        writer.write("Column: " + columnName + ", Type: " + dataType +
                                (length != null ? ", Length: " + length : "") +
                                ", AllowDBNull: " + allowDBNull + "\n");
                    }
                    writer.write("\n");
                }

                // Fetch table data
                String fetchTableDataQuery = "SELECT * FROM [" + schema + "].[" + tableName + "]";
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
                }

                writer.write("\n"); // Blank line after table data
            }

        } catch (SQLException | IOException e) {
            System.err.println("Error during CSV export: " + e.getMessage());
            throw e;
        }

        System.out.println("Schema appended to CSV file: " + filePath);
    }

    /**
     * Closes the database connection.
     *
     * @throws SQLException if a database access error occurs
     */
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Database connection closed.");
        }
    }
}

