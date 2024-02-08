package com.example.zerine;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHelperLocal {
    Connection con;
    String uname, pass, ip, port, database;

    public Connection connectionclass()
    {
        ip="10.1.1.65";
        database = "ZERINE_DB";
        uname = "jim";
        pass = "nov111999";
        port = "1433";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;

        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL= "jdbc:jtds:sqlserver://"+ ip + ":"+ port+";"+ "databasename="+ database+";user="+uname+";password="+pass+";";
            connection= DriverManager.getConnection(ConnectionURL);
        }
        catch (ClassNotFoundException | SQLException ex) {
            Log.e("Error", "Failed to establish database connection", ex);
        }

        return connection;
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                Log.e("Error", "Failed to close database connection", e);
            }
        }
    }
}
