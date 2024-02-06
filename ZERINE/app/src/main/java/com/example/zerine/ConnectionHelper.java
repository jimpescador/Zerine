package com.example.zerine;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionHelper {
    Connection con;
    String server,database, username, password;

    public Connection connectionclass() {
        server = "tcp:zerine-server.database.windows.net,1433";
        database = "ZerineDB";
        username = "adminZerine01";
        password = "100Letters!";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String connectionUrl = null;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionUrl = "jdbc:jtds:sqlserver://" + server + ";databaseName=" + database + ";user=" + username + ";password=" + password + ";encrypt=true;trustServerCertificate=false;loginTimeout=30;";
            connection = DriverManager.getConnection(connectionUrl);
        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());
        }
        return connection;
    }
}
