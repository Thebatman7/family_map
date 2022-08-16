package DAOTests;

import DAO.DataAccessException;
import DAO.Database;
import DAO.UserDAO;
import Model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

public class UserDAOTest {
    private Database db;
    private User userTest;
    private User user1;
    private User user2;
    private User user3;
    private UserDAO uDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        //we create a database
        db = new Database();
        //we create an event that we will insert
        userTest = new User("TheNight", "The_night12345", "thenight@gmail.com", "Noche",
                "Nit", "M", "Gale123A");
        user1 = new User("TheNight1", "The_night1", "thenight1@gmail.com", "Noche",
                "Nit", "M", "Gale123A");
        user2 = new User("TheNight2", "The_night2", "thenight2@gmail.com", "Noche",
                "Nit", "M", "Gale123B");
        user3 = new User("TheNight3", "The_night3", "thenight3@gmail.com", "Noche",
                "Nit", "f", "Gale123C");
        //we'll open the connection in preparation for the test case to use it
        Connection connection = db.getConnection();
        //we clear the database as well so any lingering data doesn't affect our test
        db.clearTables();
        uDao = new UserDAO(connection);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        /*
        Here we close the connection to the database file so it can be opened elsewhere.
        We will leave commit to false because we have no need to save the changes to database between test cases.
        */
        db.closeConnection(false);
    }

    @Test//insertion POSITIVE test
    public void insertPass() throws DataAccessException {
        /*
        While insert returns a boolean we can't use that to verify that our method actually worked
        only that it ran without causing an error.
        */
        uDao.insert(userTest);
        //we use find method to get the user that we just inserted
        User compareTest = uDao.find(userTest.getUsername());
        /*
        We check if find method found anything at all. If it did then we know if nothing
        else something was put into our database, since we cleared it in the beginning
        */
        Assertions.assertNotNull(compareTest);
        /*
        We make sure that what we put in is exactly the same as what we got out. If this passes
        then we know that our insert did put something in, and that it didn't change the data in any way.
        */
        Assertions.assertEquals(userTest, compareTest);
    }

    @Test//insertion NEGATIVE test
    public void insertFail() throws DataAccessException {
        /*
        We test insert method trying to make it fail.
        If we called the method the first time it will insert it successfully.
        */
        uDao.insert(userTest);
        /*
        Our SQL table is set up so that username must be unique. So trying to insert it again
        will cause the method to throw an exception.
        Note: This call uses a lambda function. This line of code runs the code that comes after
        the "()->" and expects it to throw an instance of the class in the first parameter.
        */
        Assertions.assertThrows(DataAccessException.class, ()-> uDao.insert(userTest));
    }

    @Test//retrieve POSITIVE test
    public void retrievePass() throws DataAccessException {
        //we make sur there is nothing in our table by checking the number of rows
        Assertions.assertEquals(0, uDao.getNumRows());
        //we insert a user in our table
        uDao.insert(userTest);
        //we use find method to get the user that we just inserted
        User compareTest = uDao.find(userTest.getUsername());
        //we check that find didn't return a null since we know there is a user in our database
        Assertions.assertNotNull(compareTest);
        /*
        We make sure that find returns the value requested. If this passes we know that find can find a user in
        the table where there is only one user
        */
        Assertions.assertEquals(userTest, compareTest);
        //we clear the table to test multiple users in the table
        uDao.clear();
        Assertions.assertEquals(0, uDao.getNumRows());
        //we insert multiple users to see if find can find the desired person in a table with different users in it
        uDao.insert(userTest);
        uDao.insert(user1);
        uDao.insert(user2);
        uDao.insert(user3);
        //we use find method to get the user that we just inserted
        compareTest = uDao.find(user2.getUsername());
        //we check that find didn't return a null since we know there is a user in our database
        Assertions.assertNotNull(compareTest);
        /*
        We make sure that find returns the value requested. We picked a random user among the ones that were inserted.
        If this passes we know for sure that find can find a user in the table that contains multiple different users.
        */
        Assertions.assertEquals(user2, compareTest);
    }

    @Test//retrieve Negative test
    public void retrieveFail() throws DataAccessException {
        //we insert a user in our table
        uDao.insert(userTest);
        //we use find method to get the user that we just inserted
        User compareTest = uDao.find(userTest.getUsername());
        //we check that find didn't return a null since we know there is an user in our database
        Assertions.assertNotNull(compareTest);
        //we make sure that find returns the value requested
        Assertions.assertEquals(userTest, compareTest);
        //we clear the table so we can try to find user, but it should fail because we know it's not in the database anymore
        uDao.clear();
        //find method should return null since the value is not in the database anymore
        Assertions.assertNull(uDao.find(userTest.getUsername()));
    }

    @Test//clear POSITIVE test
    public void clearPass() throws DataAccessException {
        uDao.insert(userTest);
        //we use find method to get the user that we just inserted
        User compareTest = uDao.find(userTest.getUsername());
        /*
        We check if find method found anything at all. If it did then we know if nothing
        else something was put into our database, since we cleared it in the beginning
        */
        Assertions.assertNotNull(compareTest);
        //we make sure that what we put in is exactly the same as what we got out.
        Assertions.assertEquals(userTest, compareTest);
        //once we know we have inserted the event desired we check the number of rows in the table is in fact 1
        Assertions.assertEquals(1, uDao.getNumRows());
        //we delete the only row in the table
        uDao.clear();
        //we check that indeed we deleted the row by checking the number of rows in the table
        Assertions.assertEquals(0, uDao.getNumRows());
        //we can also check that the user is not in our table using the find method. We should get a null.
        Assertions.assertNull(uDao.find(userTest.getUsername()));
        /*
        We try inserting the same user to make sure we actually deleted it from our database. If we are successful to
        insert it we know for sure that it was not in our database and we actually deleted it.
        */
        uDao.insert(userTest);
    }
}
