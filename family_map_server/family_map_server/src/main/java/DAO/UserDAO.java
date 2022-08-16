//Data Access Object (DAO)
package DAO;//we will be using Java Database Connectivity (JDBC) code in the DAOs
import Model.Event;
import Model.User;//we need to import the class so we can use it

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    //once a final variable has been assigned, it always contains the same value.
    private final Connection connection;

    //parameterized constructor
    public UserDAO (Connection connection) {//this constructor gets a connection as a parameter which allows the connection to the database
        this.connection = connection;
    }

    /**
     * Inserts a user into the User table of the Database
     * @param user which is a User object
     * @throws DataAccessException if inserting user was unsuccessful
     */
    public void insert(User user) throws DataAccessException {
        PreparedStatement statement = null;//this is a PreparedStatement object which will combine SQL and JAVA
        /*
        We can construct our string to be similar to a sql command, but if we insert question marks we can
        change them later with help from the statement
        */
        String sql = "INSERT INTO User (username, password, email, firstName, lastName, gender, personID)" +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";//we use ? for security, to prevent SQL injection attack
        try {
            /*
            This will make the PreparedStatement object into an executable SQL statement. This is the connection
            between SQL and JDBC or Java
            */
            statement = connection.prepareStatement(sql);//here is where a SQLite error could occur

            /*
            Using the statements built-in setTYPE functions we can pick questions marks we want to fill in and give it a
            proper value. The first argument corresponds to the first question mark found in our sql string.
            Notice we use the event passed in as a parameter for this method.
            */
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getFirstName());
            statement.setString(5, user.getLastName());
            statement.setString(6, user.getGender());
            statement.setString(7, user.getPersonID());

            /*
            executeUpdate returns the number of rows that were updated and this is used for insert or delete
            */
            statement.executeUpdate();
        }
        catch (SQLException exception){
            /*
            We catch SQL to know the error found when using SQLite but we don't want anyone else
            to know that we are using SQLite because we can change languages so we through our own exception.
            Whatever calls this method will catch the DataAccessException.
            */
            throw new DataAccessException("Error encountered while inserting into the database");
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Inserts multiple new data into the User table
     * @param users which is a list of User objects
     * @throws DataAccessException if inserting multiple user was unsuccessful
     */
    public void insertMultipleUsers(List<User> users) throws DataAccessException {
        PreparedStatement statement = null;//this is a PreparedStatement object which will combine SQL and JAVA
        /*
        We can construct our string to be similar to a sql command, but if we insert question marks we can
        change them later with help from the statement
        */
        String sql = "INSERT INTO User (username, password, email, firstName, lastName, gender, personID)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";//we use ? for security, to prevent SQL injection attack
        try {
            for(User user : users) {
                /*
                This will make the PreparedStatement object into an executable SQL statement. This is the connection
                between SQL and JDBC or Java
                */
                statement = connection.prepareStatement(sql);//here is where a SQLite error could occur

                /*
                Using the statements built-in setTYPE functions we can pick questions marks we want to fill in and give it a
                proper value. The first argument corresponds to the first question mark found in our sql string.
                Notice we use the event passed in as a parameter for this method.
                */
                statement.setString(1, user.getUsername());
                statement.setString(2, user.getPassword());
                statement.setString(3, user.getEmail());
                statement.setString(4, user.getFirstName());
                statement.setString(5, user.getLastName());
                statement.setString(6, user.getGender());
                statement.setString(7, user.getPersonID());

                /*
                executeUpdate returns the number of rows that were updated and this is used for insert or delete
                */
                statement.executeUpdate();
            }
        }
        catch (SQLException exception){
            /*
            We catch SQL to know the error found when using SQLite but we don't want anyone else
            to know that we are using SQLite because we can change languages so we through our own exception.
            Whatever calls this method will catch the DataAccessException.
            */
            throw new DataAccessException("Error encountered while inserting multiple users into the user Table");
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Finds a user in the Database
     * @param username of User object to be searched
     * @return User object
     * @throws DataAccessException if user is not found
     */
    public User find(String username) throws DataAccessException {
        User user;//we create a User object which will be what we return
        ResultSet result = null;//this will hold the result object from executeQuery()
        PreparedStatement statement = null;

        //* means select all the columns from the table User
        String sql = "SELECT * FROM User WHERE username = ?;";//we use ? for security, to prevent SQL injection attack
        try {
            /*
            This will make the PreparedStatement object into an executable SQL statement. This is the connection
            between SQL and JDBC or Java
            */
            statement = connection.prepareStatement(sql);//here is where a SQLite error could occur
            /*
            Using the statements built-in setTYPE functions we can pick questions marks we want to fill in and give it a
            proper value. The first argument corresponds to the first question mark found in our sql string.
            */
            statement.setString(1, username);
            /*
            We execute the executable JDBC statement this will access the database, pass in the query, get the result,
            package that result up into a Java object and return it.
            */
            result = statement.executeQuery();
            /*
            result contains the query result. This will have a pointer that points to a particular point which is above
            the first row. So .next moves that pointer(courser) to the next row
            */
            while(result.next()) {//in this case we are only finding a single user. If follows the order of the database table
                user = new User(result.getString("username"), result.getString("password"),
                        result.getString("email"), result.getString("firstName"), result.getString("lastName"),
                        result.getString("gender"), result.getString("personID"));//notice we used new
                return user;//since we are finding a single user we can return it inside the while loop
            }
        }
        catch(SQLException exception) {
            /*
            We catch SQL to know the error found when using SQLite but we don't want anyone else
            to know that we are using SQLite because we can change languages so we through our own exception.
            Whatever calls this method will catch the DataAccessException.
            */
            exception.printStackTrace();
            throw new DataAccessException("Error encountered while finding user");
        }
        finally {
            if (result != null) {
                try {
                    result.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    /**
     * Deletes all rows from the User table
     * @throws DataAccessException if deleting rows from table was unsuccessful
     */
    public void clear() throws DataAccessException {
        //we create a String holding our SQL command
        String sql = "DELETE FROM User;";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            /*
            ExecuteUpdate update our table and close our connections as opposed to executeQuery where we must close
            the connection ourselves.
            */
            statement.executeUpdate();
        } catch (SQLException exception) {
            /*
            We can use exception.printStackTrace() that provides information trace up until where the error occurred or
            we can call the other parameterized constructor we built where we pass the message and the exception thrown to use
            this will do the same as .printStackTrace, it will inform us where there error occurred
             */
            throw new DataAccessException("Error encountered while deleting all rows from the User table", exception);//MAYBE FIX ME
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Gets the number of rows in the User table
     * @return an integer which represents the number of rows
     * @throws DataAccessException if getting the number of rows was unsuccessful
     */
    public int getNumRows() throws DataAccessException{
        int numRows = 0;
        try {
            Statement statement = connection.createStatement();
            String sql = "SELECT COUNT(*) FROM User";
            ResultSet result = statement.executeQuery(sql);
            /*
            An alternative way to get the number of rows:
            while(result.next()){
                numRows = numRows + result.getInt("COUNT(*)");
            }*/
            result.next();
            numRows = result.getInt(1);
            result.close();
            return numRows;
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            throw new DataAccessException("Error while trying to get the number of rows in the table");
        }
    }
}
