package com.example.zerine;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHelper {

    public static Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            // Load the JDBC driver
            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            // Replace {your_password_here} with the actual password
            String password = "100Letters!";

            // Connection string from Azure
            String connectionString = "jdbc:jtds:sqlserver://zerine-server.database.windows.net:1433;" +
                    "database=ZerineDB;user=adminZerine01@zerine-server;password=" + password +
                    ";encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";

            // Establish the connection
            connection = DriverManager.getConnection(connectionString);

        } catch (ClassNotFoundException e) {
            // Handle the exception or log it appropriately
            e.printStackTrace();
            throw new SQLException("JDBC Driver not found.", e);
        } finally {
            // Close the connection in a finally block to ensure it gets closed even if an exception occurs
            // Note: It's a good practice to use try-with-resources for automatic resource management, but in this case,
            //       you might want to handle the connection closure manually based on your application's needs.
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    // Handle the exception or log it appropriately
                    e.printStackTrace();
                }
            }
        }

        return connection;
    }
}