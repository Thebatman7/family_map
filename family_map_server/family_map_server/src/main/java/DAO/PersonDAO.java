//Data Access Object (DAO)
package DAO;//we will be using Java Database Connectivity (JDBC) code in the DAOs
import Model.Person;//we need to import the class so we can use it


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonDAO {
    //once a final variable has been assigned, it always contains the same value.
    private final Connection connection;

    //parameterized constructor
    public PersonDAO (Connection connection) {//this constructor gets a connection as a parameter which allows the connection to the database
        this.connection = connection;
    }

    /**
     * Inserts a new person into the Person Table in the Database
     * @param person which is an Person object
     * @throws DataAccessException if inserting person was unsuccessful
     */
    public void insert(Person person) throws DataAccessException {
        PreparedStatement statement = null;//this is a PreparedStatement object which will combine SQL and JAVA
        /*
        We can construct our string to be similar to a sql command, but if we insert question marks we can
        change them later with help from the statement
        */
        String sql = "INSERT INTO Person (personID, associatedUsername, firstName, lastName, gender, fatherID, motherID, spouseID)" +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";//we use ? for security, to prevent SQL injection attack
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
            statement.setString(1, person.getPersonID());
            statement.setString(2, person.getAssociatedUsername());
            statement.setString(3, person.getFirstName());
            statement.setString(4, person.getLastName());
            statement.setString(5, person.getGender());
            statement.setString(6, person.getFatherID());
            statement.setString(7, person.getMotherID());
            statement.setString( 8, person.getSpouseID());

            /*
            executeUpdate returns the number of rows that were updated and this is used for insert or delete
            */
            statement.executeUpdate();
        }
        catch (SQLException exception) {
            /*
            We catch SQL to know the error found when using SQLite but we don't want anyone else
            to know that we are using SQLite because we can change languages so we through our own exception.
            Whatever calls this method will catch the DataAccessException.
            */
            exception.printStackTrace();
            throw new DataAccessException("Error encountered while inserting into the database");
        }
        finally {
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
     * Inserts multiple new data into the Person table
     * @param people which is a list of Person objects
     * @throws DataAccessException if inserting multiple persons was unsuccessful
     */
    public void insertMultiplePersons(List<Person> people) throws DataAccessException {
        PreparedStatement statement = null;//this is a PreparedStatement object which will combine SQL and JAVA
        /*
        We can construct our string to be similar to a sql command, but if we insert question marks we can
        change them later with help from the statement
        */
        String sql = "INSERT INTO Person (personID, associatedUsername, firstName, lastName, gender, fatherID, motherID, spouseID)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";//we use ? for security, to prevent SQL injection attack
        try {
            for(Person person : people){
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
                statement.setString(1, person.getPersonID());
                statement.setString(2, person.getAssociatedUsername());
                statement.setString(3, person.getFirstName());
                statement.setString(4, person.getLastName());
                statement.setString(5, person.getGender());
                statement.setString(6, person.getFatherID());//??? how do we handle variables that might not exist
                statement.setString(7, person.getMotherID());//??? how do we handle variables that might not exist
                statement.setString( 8, person.getSpouseID());//??? how do we handle variables that might not exist

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
            exception.printStackTrace();
            throw new DataAccessException("Error encountered while inserting multiple persons into the Person table");
        }
        finally {
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
     * Finds a person in the Database
     * @param personID of Person object to be searched
     * @return Person object
     * @throws DataAccessException if person not found
     */
    public Person find(String personID) throws DataAccessException {
        Person person;//we create a Person object which will be what we return
        ResultSet result = null;//this will hold the result object from executeQuery()
        PreparedStatement statement = null;
        //* means select all the columns from the table Person
        String sql = "SELECT * FROM Person WHERE personID = ?;";//we use ? for security, to prevent SQL injection attack
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
            statement.setString(1, personID);
            /*
            We execute the executable JDBC statement this will access the database, pass in the query, get the result,
            package that result up into a Java object and return it.
            */
            result = statement.executeQuery();

            /*
            result contains the query result. This will have a pointer that points to a particular point which is above
            the first row. So .next moves that pointer(courser) to the next row
            */
            while(result.next()){//in this case we are only finding a single person. If follows the order of the database table
                person = new Person(result.getString("personID"), result.getString("associatedUsername"),
                        result.getString("firstName"), result.getString("lastName"), result.getString("gender"),
                        result.getString("fatherID"), result.getString("motherID"), result.getString("spouseID"));
                return person;//since we are finding a single person we can return it inside the while loop
            }
        }
        catch(SQLException exception) {
            /*
            We catch SQL to know the error found when using SQLite but we don't want anyone else
            to know that we are using SQLite because we can change languages so we through our own exception.
            Whatever calls this method will catch the DataAccessException.
            */
            exception.printStackTrace();
            throw new DataAccessException("Error encountered while finding person");
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
     * Retrieves every person associated to a specific user
     * @param associatedUsername which will be searched to find all relatives
     * @return a List of Person objects
     * @throws DataAccessException if unable to find persons
     */
    public List<Person> findAllFamily(String associatedUsername) throws DataAccessException{
        List<Person> family = new ArrayList<>();//we create a List of Person objects which will be what we return
        Person person;
        ResultSet result = null;//this will hold the result object from executeQuery()
        PreparedStatement statement = null;
        //* means select all the columns from the table Person
        String sql = "SELECT * FROM Person WHERE associatedUsername = ?;";//we use ? for security, to prevent SQL injection attack
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
            while(result.next()){//If follows the order of the database table
                person = new Person(result.getString("personID"), result.getString("associatedUsername"),
                        result.getString("firstName"), result.getString("lastName"), result.getString("gender"),
                        result.getString("fatherID"), result.getString("motherID"), result.getString("spouseID"));
                family.add(person);
            }
            return family;
        }
        catch(SQLException exception) {
            exception.printStackTrace();
            throw new DataAccessException("Error encountered while retrieving all persons related to a username");
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
     * Deletes all rows from the Person table
     * @throws DataAccessException if deleting rows from table was unsuccessful
     */
    public void clear() throws DataAccessException {
        //we create a String holding our SQL command
        String sql = "DELETE FROM Person;";
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
            throw new DataAccessException("Error encountered while deleting all rows from the Person table", exception);//MAYBE FIX ME
        }
        finally {
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
     * Deletes all persons associated to the a specific username
     * @param associatedUsername which is a username that connects the user to persons
     * @throws DataAccessException if deleting rows from table was unsuccessful
     */
    public void clearRelatedPersons(String associatedUsername) throws DataAccessException {
        //we create a String holding our SQL command
        String sql = "DELETE FROM Person WHERE associatedUsername = ?;";//we use ? for security, to prevent SQL injection attack
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
        } catch (SQLException exception) {
            /*
            We can use exception.printStackTrace() that provides information trace up until where the error occurred or
            we can call the other parameterized constructor we built where we pass the message and the exception thrown to use
            this will do the same as .printStackTrace, it will inform us where there error occurred
             */
            throw new DataAccessException("Error encountered while deleting all persons associated to the " +
                    "user from the Person table.", exception);//MAYBE FIX ME
        }
        finally {
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
     * Gets the number of rows in the Person table
     * @return an integer which represents the number of rows
     * @throws DataAccessException if getting the number of rows was unsuccessful
     */
    public int getNumRows() throws DataAccessException{
        int numRows = 0;
        try {
            Statement statement = connection.createStatement();
            String sql = "SELECT COUNT(*) FROM Person";
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
