//Data Access Object (DAO)
package DAO;//we will be using Java Database Connectivity (JDBC) code in the DAOs
import Model.AuthToken;//we need to import the class so we can use it


import java.sql.*;


public class AuthTokenDAO {
    //once a final variable has been assigned, it always contains the same value.
    private final Connection connection;

    //parameterized constructor
    public AuthTokenDAO(Connection connection) {//this constructor gets a connection as a parameter which allows the connection to the database
        this.connection = connection;
    }

    /**
     * Inserts a new authorization token into the AuthToken table in the Database
     * @param authToken which is a AuthToken object
     * @throws DataAccessException if inserting authorization token was unsuccessful
     */
    public void insert(AuthToken authToken) throws DataAccessException {
        PreparedStatement statement = null;//this is a PreparedStatement object which will combine SQL and JAVA
        /*
        We can construct our string to be similar to a sql command, but if we insert question marks we can
        change them later with help from the statement
        */
        String sql = "INSERT INTO AuthToken (authToken, username) VALUES (?, ?)";//we use ? for security, to prevent SQL injection attack
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
            statement.setString(1, authToken.getAuthToken());
            statement.setString(2, authToken.getUsername());

            /*
            executeUpdate returns the number of rows that were updated and this is used for insert or delete
            */
            statement.executeUpdate();

        } catch (SQLException exception) {
            /*
            We catch SQL to know the error found when using SQLite but we don't want anyone else
            to know that we are using SQLite because we can change languages so we through our own exception.
            Whatever calls this method will catch the DataAccessException.
            */
            throw new DataAccessException("Error encountered while inserting into the database.");
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
     * Finds an authorization token in the Database
     * @param username of AuthToken to be searched
     * @return AuthToken object
     * @throws DataAccessException if authorization token not found
     */
    public AuthToken find(String username) throws DataAccessException {
        AuthToken authToken;
        ResultSet result = null;//this will hold the result object from executeQuery()
        PreparedStatement statement = null;

        //* means select all the columns from the table Person
        String sql = "SELECT * FROM AuthToken WHERE username = ?;";//we use ? for security, to prevent SQL injection attack
        try{
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
            while(result.next()){//in this case we are only finding a single authorization token. If follows the order of the database table
                authToken = new AuthToken(result.getString("authToken"), result.getString("username"));
                return authToken;//since we are finding a single authorization token we can return it inside the while loop
            }
        }
        catch(SQLException exception) {
            /*
            We catch SQL to know the error found when using SQLite but we don't want anyone else
            to know that we are using SQLite because we can change languages so we through our own exception.
            Whatever calls this method will catch the DataAccessException.
            */
            exception.printStackTrace();
            throw new DataAccessException("Error encountered while finding authorization token");
        }
        finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    /**
     * Finds an AuthToken object in the Database
     * @param givenAuthToken which is a string with a authToken to be searched
     * @return AuthToken object
     * @throws DataAccessException if authorization token not found
     */
    public AuthToken findWithToken(String givenAuthToken) throws DataAccessException {
        AuthToken authToken;
        ResultSet result = null;//this will hold the result object from executeQuery()
        PreparedStatement statement = null;

        //* means select all the columns from the table Person
        String sql = "SELECT * FROM AuthToken WHERE authToken = ?;";//we use ? for security, to prevent SQL injection attack
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
            statement.setString(1, givenAuthToken);
            /*
            We execute the executable JDBC statement this will access the database, pass in the query, get the result,
            package that result up into a Java object and return it.
            */
            result = statement.executeQuery();

            /*
            result contains the query result. This will have a pointer that points to a particular point which is above
            the first row. So .next moves that pointer(courser) to the next row
            */
            while(result.next()){//in this case we are only finding a single authorization token. If follows the order of the database table
                authToken = new AuthToken(result.getString("authToken"), result.getString("username"));
                return authToken;//since we are finding a single authorization token we can return it inside the while loop
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
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return  null;
    }



    /**
     * Deletes all rows from the AuthToken table
     * @throws DataAccessException if deleting rows from table was unsuccessful
     */
    public void clear() throws DataAccessException {
        try {
            /*
            Notice that we do not create a prepareStatement because there is nothing to prepare, we only need to execute
            the SQL command. Therefore, we use Statement and createStatement. Notice how we pass the sql string as a parameter
            to executeUpdate()
            */
            Statement statement = connection.createStatement();
            String sql = "DELETE FROM AuthToken;";
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException exception) {
            /*
            We can use exception.printStackTrace() that provides information trace up until where the error occurred or
            we can call the other parameterized constructor we built where we pass the message and the exception thrown to use
            this will do the same as printStackTrace, it will inform us where there error occurred
             */
            throw new DataAccessException("Error encountered while deleting all rows from the AuthToken table", exception);//MAYBE FIX ME
        }
    }

    /**
     * Gets the number of rows in the AuthToken table
     * @return an integer which represents the number of rows
     * @throws DataAccessException if getting the number of rows was unsuccessful
     */
    public int getNumRows() throws DataAccessException{
        int numRows = 0;
        try {
            Statement statement = connection.createStatement();
            String sql = "SELECT COUNT(*) FROM AuthToken";
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
