package com.netaporter.test.utils.dataaccess.database;

import com.netaporter.test.utils.enums.RegionEnum;
import com.netaporter.test.utils.enums.SalesChannelEnum;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: J.Christian@net-a-porter.com
 * Date: 27/03/2013
 * Time: 12:31
 * To change this template use File | Settings | File Templates.
 */
public class LegacyWebAppChannelisedDatabaseClient extends DatabaseClient {

    @Value("${db.password}")
    private String dbPassword;

    @Value("${db.port}")
    private String dbPort;

    @Value("${db.server}")
    private String dbServer;

    @Value("${db.username}")
    private String dbUsername;

    @Value("${db.intlName}")
    private String dbINTLName;

    @Value("${db.amName}")
    private String dbAMName;

    @Value("${db.apacName}")
    private String dbAPACName;

    @Value("${db.defaultChannel}")
    private String dbDefaultChannel;

    private RegionEnum region;

    private SalesChannelEnum channel;


    public LegacyWebAppChannelisedDatabaseClient() {
        this(System.getProperty("region") != null ? RegionEnum.valueOf(System.getProperty("region").toUpperCase()):
             RegionEnum.INTL);
    }

    public LegacyWebAppChannelisedDatabaseClient(RegionEnum region) {
        super();
        this.region = region;
    }
    public void setRegion(String region){
        this.region = parseRegion(region);
    }

    public void setChannel(String channel) {
        this.channel = SalesChannelEnum.valueOf(channel);
    }

    private RegionEnum parseRegion(String region) {
        return RegionEnum.valueOf(region.toUpperCase());
    }

    @Override
    Connection createConnection() {

        Properties connectionProperties = new Properties();
        connectionProperties.put("user", dbUsername);
        connectionProperties.put("password", dbPassword);

        String dbName = "";
        RegionEnum regionToUse = region;
        if (regionToUse == null) {
            // TODO review the need for default channel.  Might go away when we refactor this.
            if ((dbDefaultChannel != null) && (!dbDefaultChannel.equals("${db.defaultChannel}"))) {
                regionToUse = parseRegion(dbDefaultChannel);
            } else {
                throw new RuntimeException("Region must be set before calling createConnection()");
            }
        }

        switch (regionToUse) {
            case INTL:
                dbName = dbINTLName;
                break;
            case AM:
                dbName = dbAMName;
                break;
            case APAC:
                dbName = dbAPACName;
        }

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
