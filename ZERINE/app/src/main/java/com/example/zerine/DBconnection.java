package com.example.zerine;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {

    public static Connection getConnection()  {
        Connection connection = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String connectionUrl = "\"jdbc:jtds:sqlserver://zerine-server.database.windows.net:1433;"
                    + "database=ZerineDB;"
                    + "user=adminZerine01"
                    + "password=100Letters!"
                    + "encrypt=true;"
                    + "trustServerCertificate=false;"
                    + "hostNameInCertificate=*.database.windows.net;"
                    + "loginTimeout=30;";

            return DriverManager.getConnection(connectionUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}