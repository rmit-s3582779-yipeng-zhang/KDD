
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by marco on 31/03/2017.
 */
public class DatabaseManager {

    public Connection connection = null;
    public Statement statement = null;
    public ResultSet resultSet = null;


    public void connect(String city) {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            if(city.equals("LA"))
                connection = DriverManager.getConnection(UsrNamePassword.checkindatabaseURL, UsrNamePassword.userName, UsrNamePassword.password);

            if (connection != null) {
                System.out.println("Connected to database.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void connect() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(UsrNamePassword.databaseURL, UsrNamePassword.userName, UsrNamePassword.password);

            if (connection != null) {
                System.out.println("Connected to database.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectLA() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(UsrNamePassword.database2URL, UsrNamePassword.userName, UsrNamePassword.password);

            if (connection != null) {
                System.out.println("Connected to database.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void close() {

        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connection != null) {
                connection.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void executeQuery(String queryStmt) {

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(queryStmt);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int executeInsert(String insertStmt) {

        try {
            statement = connection.createStatement();
            int number = statement.executeUpdate(insertStmt);

            return number;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

}
