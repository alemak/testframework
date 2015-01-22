package com.netaporter.test.utils.dataaccess.database;

import org.springframework.beans.factory.annotation.Value;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Class to connect to non-channelised (shared) databases
 * Created with IntelliJ IDEA.
 * User: Alexei Makarenko
 * Date: 06/06/13
 * Time: 11:50
 * To change this template use File | Settings | File Templates.
 */
public class SharedDatabaseClient extends DatabaseClient {
    @Value("${db.password}")
    private String dbPassword;

    @Value("${db.port}")
    private String dbPort;
    @Value("${db.server}")
    private String dbServer;

    @Value("${db.username}")
    private String dbUsername;

    @Value("${db.dbname}")
    private String dbName;

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDbPort() {
        return dbPort;
    }

    public void setDbPort(String dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbServer() {
        return dbServer;
    }

    public void setDbServer(String dbServer) {
        this.dbServer = dbServer;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }



    @Override
    Connection createConnection() {
        Properties connectionProperties = new Properties();
        connectionProperties.put("user", dbUsername);
        connectionProperties.put("password", dbPassword);
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://" + dbServer + ":" + dbPort + "/" + dbName, connectionProperties);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to load JDBC driver. " + e.getStackTrace());
        } catch (SQLException e) {
            throw new RuntimeException("Unable to establish database connection. " + e.getStackTrace());
        }
    }
}
