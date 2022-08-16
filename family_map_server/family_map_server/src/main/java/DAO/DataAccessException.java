package DAO;

import java.sql.SQLException;

public class DataAccessException extends Exception {//extends form the Exception java class already created
    //the super keyword refers to superclass (parent) objects
    DataAccessException() { super(); }//default constructor

    //we use the parent's constructor Exception(String message) this constructs a new exception with the specified detail message.
    DataAccessException(String message) { super(message); }//parameterized constructor

    //Added
    DataAccessException(String message, SQLException sqlException) { super(message, sqlException); }//parameterized constructor


}
