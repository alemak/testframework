package com.netaporter.test.utils.dataaccess.database;

import com.netaporter.test.utils.enums.RegionEnum;
import org.springframework.beans.factory.annotation.Value;

import java.sql.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: c.dawson@london.net-a-porter.com
 * Date: 01/03/2013
 * Time: 11:31
 */
public abstract class DatabaseClient {

    abstract Connection createConnection();

    /**
     * Not a PUBLIC method as we want to encourage devs to extend this class with test suite-specific data-access functions.
     * To use this, extend this class in your project, with the same package name
     *
     * @param sqlQuery
     * @return
     */
    List<Map> executeSelect(String sqlQuery) {

        Statement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        try {
            connection = createConnection();
            statement = connection.createStatement();

            resultSet = statement.executeQuery(sqlQuery);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            List rows = new ArrayList();
            while (resultSet.next()) {
                Map row = new HashMap(columnCount);
                for (int i = 1; i <= columnCount; ++i) {
                    row.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                rows.add(row);
            }

            return rows;

        } catch (SQLException e) {
            throw new RuntimeException("Unable to execute select query [ " + sqlQuery + " ]. " + e.getMessage());
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException sqlEx) {
                } // ignore
                resultSet = null;
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException sqlEx) {
                } // ignore
                statement = null;
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException sqlEx) {
                } // ignore
                connection = null;
            }
        }
    }

    /**
     * Not a PUBLIC method as we want to encourage devs to extend this class with test suite-specific data-access functions.
     * To use this, extend this class in your project, with the same package name
     *
     * @param sqlQuery
     * @return
     */
    int executeUpdate(String sqlQuery) {

        Statement statement = null;
        Connection connection = null;

        try {
            connection = createConnection();
            statement = connection.createStatement();
            int numberOfAffectedRows = statement.executeUpdate(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            return numberOfAffectedRows;
        } catch (SQLException e) {
            e.printStackTrace();
            String stacktrace = "";
            for (StackTraceElement ste: e.getStackTrace()){
                stacktrace +=ste.toString();
            }
            throw new RuntimeException("Unable to execute update query [ " + sqlQuery + " ]. " + e.getMessage() );

        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException sqlEx) {
                } // ignore
                statement = null;
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException sqlEx) {
                } // ignore
                connection = null;
            }
        }
    }

    /**
     * Not a PUBLIC method as we want to encourage devs to extend this class with test suite-specific data-access functions.
     * To use this, extend this class in your project, with the same package name
     *
     * @param query
     * @param lastIdTable
     * @return
     */
    String executeUpdateAndReturnTheLastInsertedId(String query, String lastIdTable) {
        Statement statement = null;
        Connection connection = null;

        try {
            connection = createConnection();
            statement = connection.createStatement();
            int numberOfAffectedRows = statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);

            String lastInsertedIdQuery = "SELECT LAST_INSERT_ID() FROM " + lastIdTable + " LIMIT 0,1";

            ResultSet resultSet = statement.executeQuery(lastInsertedIdQuery);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            if (columnCount > 1 || columnCount <= 0)
                throw new RuntimeException("Wrong column number");
            List rows = new ArrayList();
            resultSet.next();
            String lastInsertedId = resultSet.getString(1);
            return lastInsertedId;


        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to execute update query [ " + query + " ]. " + e.getMessage());

        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException sqlEx) {
                } // ignore
                statement = null;
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException sqlEx) {
                } // ignore
                connection = null;
            }
        }
    }
}