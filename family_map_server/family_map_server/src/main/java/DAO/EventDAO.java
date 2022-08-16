//Data Access Object (DAO)
package DAO;//we will be using Java Database Connectivity (JDBC) code in the DAOs
import Model.Event;//we need to import the class so we can use it


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {
    //once a final variable has been assigned, it always contains the same value.
    private final Connection connection;

    //parameterized constructor
    public EventDAO(Connection connection) {//this constructor gets a connection as a parameter which allows the connection to the database
        this.connection = connection;
    }

    /**
     * Inserts an event into the Event table of the database
     * @param event which is an Event object
     * @throws DataAccessException if inserting event was unsuccessful
     */
    public void insert(Event event) throws DataAccessException {
        PreparedStatement statement = null;//this is a PreparedStatement object which will combine SQL and JAVA
        /*
        We can construct our string to be similar to a sql command, but if we insert question marks we can
        change them later with help from the statement
        */
        String sql = "INSERT INTO Event (eventID, associatedUsername, personID, latitude, longitude, " +
                "country, city, eventType, year) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";//we use ? for security, to prevent SQL injection attack
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
            statement.setString(1, event.getEventID());//notice the type is String
            statement.setString(2, event.getUsername());
            statement.setString(3, event.getPersonID());
            statement.setFloat(4, event.getLatitude());//notice the type is Float
            statement.setFloat(5, event.getLongitude());
            statement.setString(6, event.getCountry());
            statement.setString(7, event.getCity());
            statement.setString(8, event.getEventType());
            statement.setInt(9, event.getYear());//notice the type is Int

            /*
            executeUpdate returns the number of rows that were updated and this is used for insert or delete
            */
            statement.executeUpdate();//notice we don't pass the sql as a parameter because we used prepareStatement
        }
        catch (SQLException exception) {
            /*
            We catch SQL to know the error found when using SQLite but we don't want anyone else
            to know that we are using SQLite because we can change languages so we through our own exception.
            Whatever calls this method will catch the DataAccessException.
            */
            exception.printStackTrace();//provides information trace up until where the error occurred
            //since DataAccessException is a class we've created we need to use new to create one that we throw
            throw new DataAccessException("Error encountered while inserting into the database");
        }
        finally {
            if (statement != null) {//we need to close statement to save resource we have a limited number of them
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
     * Inserts multiple new data into the Event table
     * @param events which is a list of Event objects
     * @throws DataAccessException if inserting multiple events was unsuccessful
     */
    public void insertMultipleEvents(List<Event> events) throws DataAccessException {
        PreparedStatement statement = null;//this is a PreparedStatement object which will combine SQL and JAVA
        /*
        We can construct our string to be similar to a sql command, but if we insert question marks we can
        change them later with help from the statement
        */
        String sql = "INSERT INTO Event (eventID, associatedUsername, personID, latitude, longitude, " +
                "country, city, eventType, year) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";//we use ? for security, to prevent SQL injection attack
        try {
            for(Event event : events) {
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
                statement.setString(1, event.getEventID());//notice the type is String
                statement.setString(2, event.getUsername());
                statement.setString(3, event.getPersonID());
                statement.setFloat(4, event.getLatitude());//notice the type is Float
                statement.setFloat(5, event.getLongitude());
                statement.setString(6, event.getCountry());
                statement.setString(7, event.getCity());
                statement.setString(8, event.getEventType());
                statement.setInt(9, event.getYear());//notice the type is Int

                /*
                executeUpdate returns the number of rows that were updated and this is used for insert or delete
                */
                statement.executeUpdate();
            }
        }
        catch (SQLException exception) {
            /*
            We catch SQL to know the error found when using SQLite but we don't want anyone else
            to know that we are using SQLite because we can change languages so we through our own exception.
            Whatever calls this method will catch the DataAccessException.
            */
            exception.printStackTrace();//provides information trace up until where the error occurred
            //since DataAccessException is a class we've created we need to use new to create a one that we throw
            throw new DataAccessException("Error encountered while inserting multiple events into the Event table");
        }
        finally {
            if (statement != null) {//we need to close statement to save resource we have a limited number of them
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
     * Finds an event in the Database
     * @param eventID of Event object to be searched
     * @return Event object
     * @throws DataAccessException if event not found
     */
    public Event find(String eventID) throws DataAccessException {
        Event event;//we create an Event object which will be what we return
        ResultSet result = null;//this will hold the result object from executeQuery()
        PreparedStatement statement = null;

        //* means select all the columns from the table Event
        String sql = "SELECT * FROM Event WHERE eventID = ?;";//we use ? for security, to prevent SQL injection attack
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
            statement.setString(1, eventID);

            /*
            We execute the executable JDBC statement this will access the database, pass in the query, get the result,
            package that result up into a Java object and return it.
            */
            result = statement.executeQuery();

            /*
            result contains the query result. This will have a pointer that points to a particular point which is above
            the first row. So .next moves that pointer(courser) to the next row
            */
            while(result.next()) {//in this case we are only finding a single event. If follows the order of the database table
                event = new Event (result.getString("eventID"), result.getString("associatedUsername"),
                        result.getString("personID"), result.getFloat("latitude"), result.getFloat("longitude"),
                        result.getString("country"), result.getString("city"), result.getString("eventType"),
                        result.getInt("year"));//notice we used new
                return event;//since we are finding a single event we can return it inside the while loop
            }
        }
        catch (SQLException exception) {
            /*
            We catch SQL to know the error found when using SQLite but we don't want anyone else
            to know that we are using SQLite because we can change languages so we through our own exception.
            Whatever calls this method will catch the DataAccessException.
            */
            exception.printStackTrace();//provides information trace up until where the error occurred
            throw new DataAccessException("Error encountered while finding event");
        }
        finally {
            /*
            We need to close statement to save resource we have a limited number of them.
            Remember that when we close a result the statement also closes so we don't need to have another if statement
            checking if statement is not null and closing it if it is not.
            */
            if (result != null) {
                try {
                    result.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;//we return null
    }


    /**
     * Retrieves every event associated to a specific user
     * @param associatedUsername which will be searched to find all events of relatives
     * @return a List of Event objects
     * @throws DataAccessException if unable to find events
     */
    public List<Event> findAllFamilyEvents(String associatedUsername) throws DataAccessException {
        List<Event> events = new ArrayList<>(); //we create a List of Person objects which will be what we return
        Event event;
        ResultSet result = null;//this will hold the result object from executeQuery()
        PreparedStatement statement = null;
        //* means select all the columns from the table Person
        String sql = "SELECT * FROM Event WHERE associatedUsername = ?;";//we use ? for security, to prevent SQL injection attack
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
            statement.setString(1, associatedUsername);
            /*
            We execute the executable JDBC statement this will access the database, pass in the query, get the result,
            package that result up into a Java object and return it.
            */
            result = statement.executeQuery();
            /*
            result contains a Set Result. This will have a pointer that points to a particular point which is above
            the first row. So .next moves that pointer/courser to the next row. In this case we might have multiple rows
            because we might have multiple persons related to a username. While loop will go through each row (will move pointer
            to each row) thus we will be able to get the data of each row nad create a person to add it to our list that
            will be returned at the end
            */
            while(result.next()){
                event = new Event (result.getString("eventID"), result.getString("associatedUsername"),
                        result.getString("personID"), result.getFloat("latitude"), result.getFloat("longitude"),
                        result.getString("country"), result.getString("city"), result.getString("eventType"),
                        result.getInt("year"));//notice we used new
                events.add(event);
            }
            return events;
        }
        catch (SQLException exception){
            exception.printStackTrace();
            throw new DataAccessException("Error encountered while retrieving all events related to a username");
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
    }

    /**
     * Deletes all rows from the Event table
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
            String sql = "DELETE FROM Event;";
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException exception) {
            /*
            We can use exception.printStackTrace() that provides information trace up until where the error occurred or
            we can call the other parameterized constructor we built where we pass the message and the exception thrown to use
            this will do the same as .printStackTrace, it will inform us where there error occurred
            */
            throw new DataAccessException("Error encountered while deleting all rows from the Event table", exception);
        }
    }

    /**
     * Deletes all events associated to a specific username
     * @param associatedUsername which is a username that connects a user to events
     * @throws DataAccessException if deleting rows from table was unsuccessful
     */
    public void clearRelatedEvents(String associatedUsername) throws DataAccessException {
        //we create a String holding our SQL command
        String sql = "DELETE FROM Event WHERE associatedUsername = ?;";//we use ? for security, to prevent SQL injection attack
        PreparedStatement statement = null;
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
            statement.setString(1, associatedUsername);
            /*
            ExecuteUpdate update our table and close our connections as opposed to executeQuery where we must close
            the connection ourselves.
            */
            statement.executeUpdate();
            statement.close();
        } catch (SQLException exception) {
            /*
            We can use exception.printStackTrace() that provides information trace up until where the error occurred or
            we can call the other parameterized constructor we built where we pass the message and the exception thrown to use
            this will do the same as .printStackTrace, it will inform us where there error occurred
            */
            throw new DataAccessException("Error encountered while deleting all events associated " +
                    "to the user from the Event table", exception);
        }
    }

    /**
     * Gets the number of rows in the Event table
     * @return an integer which represents the number of rows
     * @throws DataAccessException if getting the number of rows was unsuccessful
     */
    public int getNumRows() throws DataAccessException{
        int numRows = 0;
        try {
            Statement statement = connection.createStatement();
            String sql = "SELECT COUNT(*) FROM Event";
            ResultSet result = statement.executeQuery(sql);
            while(result.next()){
                numRows = numRows + result.getInt("COUNT(*)");
            }
            /*
            An alternative way to get the number of rows:
            result.next();
            numRows = result.getInt(1);
            */
            result.close();
            return numRows;
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            throw new DataAccessException("Error while trying to get the number of rows in the table");
        }
    }
}
