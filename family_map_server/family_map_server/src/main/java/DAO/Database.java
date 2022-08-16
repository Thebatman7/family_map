package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class Database {
    private Connection connection = null;

    /**
     * Connects with the Database to do transactions
     * @return a Connection object when we get ot connect to the Database
     * @throws DataAccessException when we are unsuccessful to open a connection with the Database
     */
    public Connection openConnection() throws DataAccessException {//DataAccessException is an exception we have created
       /*
       Whenever we want to make a change to our database we must open a connection and use statements
       created by that connection to initiate transaction(modify the database)
       */
        try  {
            /*
            The path assumes you start in the root of your project unless given a non-relative path to where the file is located
            The structure for this connection is driver:Language:path. The path is the path to where the database.db file is
            */
            final String CONNECTION_URL = "jdbc:sqlite:FamilyServer.db";

            //open a database connection
            connection = DriverManager.getConnection(CONNECTION_URL);//this a method of the DriverManager class that returns a connection object

            /*
            Every statement in JDBC executes in its own transaction unless we specify that we want it to be part of
            a larger transaction.
            By default auto commit, setAutoCommit(), is true which means everytime we execute a statement it automatically
            commits. The way to make a transaction is to just make it so that doesn't happen. So all we have to do is
            to set setAutoCommit(false) and that means that every statement after that is in the same transaction until
            we commit or rollback. This will be true until we get a different connection.
            */
            connection.setAutoCommit(false);
        }
        catch (SQLException exception) {//we catch SQL to know the error found when using SQLite but we don't want anyone else
            exception.printStackTrace();//to know that we are using SQLite because we can change languages so we through our own exception
            throw new DataAccessException("Unable to open connection to database");//whatever calls this method will catch the DataAccessException
        }
        return connection;
    }

    public Connection getConnection() throws DataAccessException {
        if (connection == null) {
            return openConnection();
        }
        else {
            return connection;
        }
    }


    /**
     * Closes the connection with the Database
     * @param commit which is a boolean parameter
     * @throws DataAccessException when closing connection was unsuccessful
     */
    public void closeConnection(boolean commit) throws DataAccessException {
        /*
        When we are done manipulating the database it is important to close the connection. This will end the transaction
        end the transaction and allow us to either commit our changes to the database or rollback any changes that were
        made before we encountered a potential error.
        CAUTION: if we fail to close a connection and try to reopen the database this wil cause the database to lock.
        our code must always include a closure of the database no matter what errors or problems we encounter
        */
        try {
            if (commit) {
                //this will commit the changes to the database
                connection.commit();
            } else {//a rollback is an operation which returns the database to some previous state
                /*
                If we find out something went wrong, pass a false into closeConnection and this will rollback any
                changes we made during this connection
                */
                connection.rollback();
            }
            //we close connection
            connection.close();
            connection = null;
        } catch (SQLException exception) {//we catch SQL to know the error found when using SQLite but we don't want anyone else
            exception.printStackTrace();//to know that we are using SQLite because we can change languages so we through our own exception
            throw new DataAccessException("Unable to close database connection");
        }
    }


    public void clearTables() throws DataAccessException {
        try (Statement stmt = connection.createStatement()){
            String sql = "DELETE FROM Event";
            stmt.executeUpdate(sql);//notice that we pass sql when we don't need to prepare a statement
            sql = "DELETE FROM Person";
            stmt.executeUpdate(sql);
            sql = "DELETE FROM User";
            stmt.executeUpdate(sql);
            sql = "DELETE FROM AuthToken";
            stmt.executeUpdate(sql);
            sql = "DELETE FROM Person";
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("SQL Error encountered while clearing tables");
        }
    }
}

