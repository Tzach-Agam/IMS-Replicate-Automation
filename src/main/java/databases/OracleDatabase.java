package databases;

import oracle.jdbc.driver.DatabaseError;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class OracleDatabase {

    // JDBC URL, username, and password of the Oracle database
    private static final String DB_URL = "jdbc:oracle:thin:@tzachoracle19.qdinetnew:1521/orcl"; // Replace with your DB URL
    private static final String USER = "system"; // Replace with your username
    private static final String PASSWORD = "oracle"; // Replace with your password

    // Connection object
    private Connection connection;

    /**
     * Establishes a connection to the Oracle database.
     *
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
            System.out.println("Query has been executed");
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
    public void dropUser(String userName) {
        String dropUserQuery = "DROP USER \"" + userName + "\"";
        try {
            executeQuery(dropUserQuery);
            System.out.println("User " + userName + " dropped");
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
     * Drops all tables in the specified schema/user.
     *
     * @param schema The schema/user whose tables should be dropped
     * @throws SQLException if a database access error occurs
     */
    public void dropAllTables(String schema) throws SQLException {
        String fetchTablesQuery = "SELECT table_name FROM all_tables WHERE owner = '" + schema + "'";
        try (ResultSet resultSet = fetchResults(fetchTablesQuery)) {
            while (resultSet.next()) {
                String tableName = resultSet.getString("table_name");
                String dropTableQuery = "DROP TABLE \"" + schema + "\".\"" + tableName + "\" CASCADE CONSTRAINTS";
                executeQuery(dropTableQuery);
                System.out.println("Dropped table: " + tableName);
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
        String fetchTablesQuery = "SELECT table_name FROM all_tables WHERE owner = '" + schema + "'";
        try (ResultSet tablesResultSet = fetchResults(fetchTablesQuery);
             FileWriter writer = new FileWriter(filePath)) {

            while (tablesResultSet.next()) {
                String tableName = tablesResultSet.getString("table_name");
                writer.write("Table: \"" + tableName + "\"\n");

                // Fetch metadata and data
                String fetchTableDataQuery = "SELECT * FROM \"" + schema + "\".\"" + tableName + "\"";
                try (ResultSet tableResultSet = fetchResults(fetchTableDataQuery)) {
                    ResultSetMetaData metaData = tableResultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    // Write column names
                    for (int i = 1; i <= columnCount; i++) {
                        writer.write(metaData.getColumnName(i) + (i < columnCount ? "," : "\n"));
                    }

                    // Write rows
                    while (tableResultSet.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            writer.write(tableResultSet.getString(i) + (i < columnCount ? "," : "\n"));
                        }
                    }
                }

                writer.write("\n"); // Separate tables with a blank line
            }
        }

        System.out.println("Schema exported to CSV file: " + filePath);
    }

    /**
     * Creates a CSV file containing metadata and data for all tables in the specified schema.
     *
     * @param schema The schema/user whose tables should be exported
     * @param filePath The path of the CSV file to create
     * @throws SQLException if a database access error occurs
     * @throws IOException if a file access error occurs
     */
    public void exportSchemaToCSV2(String schema, String filePath) throws SQLException, IOException {
        String fetchTablesQuery = "SELECT table_name FROM all_tables WHERE owner = '" + schema + "'";
        try (ResultSet tablesResultSet = fetchResults(fetchTablesQuery);
             FileWriter writer = new FileWriter(filePath)) {

            boolean dataWritten = false;
            while (tablesResultSet.next()) {
                String tableName = tablesResultSet.getString("table_name");
                writer.write("Table: \"" + tableName + "\"\n");

                // Fetch column metadata
                String fetchColumnsQuery = "SELECT column_name, data_type, data_length FROM all_tab_columns WHERE owner = '" + schema + "' AND table_name = '" + tableName + "'";
                try (ResultSet columnsResultSet = fetchResults(fetchColumnsQuery)) {
                    while (columnsResultSet.next()) {
                        String columnName = columnsResultSet.getString("column_name");
                        String dataType = columnsResultSet.getString("data_type");
                        String dataLength = columnsResultSet.getString("data_length");
                        writer.write("Column: " + columnName + ", Type: " + dataType + ("VARCHAR2".equals(dataType) ? ", Length: " + dataLength : "") + ": ");
                    }
                    writer.write("\n");
                }

                // Fetch data
                String fetchTableDataQuery = "SELECT * FROM \"" + schema + "\".\"" + tableName + "\"";
                try (ResultSet tableResultSet = fetchResults(fetchTableDataQuery)) {
                    ResultSetMetaData metaData = tableResultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    if (columnCount == 0) {
                        writer.write("No columns found for table: \"" + tableName + "\"\n\n");
                        continue;
                    }

                    // Write column names
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

                writer.write("\n"); // Separate tables with a blank line
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

    public static void main(String[] args) {
        OracleDatabase dbAccess = new OracleDatabase();

        try {
            dbAccess.connect();

            // Example: Execute an update query
            //String updateQuery = "UPDATE \"replicate_selenium_source\".\"test_table1\" SET COL2 = 'TTTTTT' WHERE COL1 = 404";
            //dbAccess.executeQuery(updateQuery);

            //dbAccess.dropUser("REPLICATE_SELENIUM111");
            //dbAccess.dropAllTables("REPLICATE_SELENIUM");
            dbAccess.exportSchemaToCSV2("replicate_selenium444", "export.csv");


            //resultSet.close();
            dbAccess.closeConnection();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
