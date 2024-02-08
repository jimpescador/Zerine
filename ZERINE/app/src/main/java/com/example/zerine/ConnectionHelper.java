package com.example.zerine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHelper {

    public static Connection getConnection() throws SQLException {
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
            return DriverManager.getConnection(connectionString);

        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC Driver not found.", e);
        }
    }
}




