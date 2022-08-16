package DAOTests;

import DAO.AuthTokenDAO;
import DAO.DataAccessException;
import DAO.Database;
import Model.AuthToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

public class AuthTokenDAOTest {
    private Database db;
    private AuthToken authTokenTest;
    private AuthToken authToken1;
    private AuthToken authToken2;
    private AuthToken authToken3;
    private AuthTokenDAO aDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        //we create a database
        db = new Database();
        //we create an event that we will insert
        authTokenTest = new AuthToken("IronMan777!", "Tony_Stark");
        authToken1 = new AuthToken("Hulk777!", "Bruce_B");
        authToken2 = new AuthToken("CaptainAmerica777!", "Steve_R");
        authToken3 = new AuthToken("WinterSoldier777!", "Bucky_J");

        //we'll open the connection in preparation for the test case to use it
        Connection connection = db.getConnection();
        //we clear the database as well so any lingering data doesn't affect our tests
        db.clearTables();
        //Then we pass that connection to the AuthTokenDAO so it can access the database
        aDao = new AuthTokenDAO(connection);
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
        While insert returns a boolean we can't use that to verify that our function actually worked
        only that it ran without causing an error
        */
        aDao.insert(authTokenTest);
        //we use find method to get the authToken that we just inserted
        AuthToken compareTest = aDao.find(authTokenTest.getUsername());
        /*
        We check if find method found anything at all. If it did then we know if nothing
        else something was put into our database, since we cleared it in the beginning.
        */
        Assertions.assertNotNull(compareTest);
        /*
        We make sure that what we put in is exactly the same as what we got out. If this passes
        then we know that our insert did put something in, and that it didn't change the data in any way.
        */
        Assertions.assertEquals(authTokenTest, compareTest);
    }

    @Test//insertion NEGATIVE test
    public void insertFail() throws DataAccessException {
        /*
        We test insert method trying to make it fail.
        If we called the method the first time it will insert it successfully.
        */
        aDao.insert(authTokenTest);
        /*
        Our SQL table is set up so that username must be unique. So trying to insert it again
        will cause the method to throw an exception.
        Note: This call uses a lambda function. This line of code runs the code that comes after
        the "()->" and expects it to throw an instance of the class in the first parameter.
        */
        Assertions.assertThrows(DataAccessException.class, ()-> aDao.insert(authTokenTest));
    }

    @Test//retrieve POSITIVE test
    public void retrievePass() throws DataAccessException {
        //we make sur there is nothing in our table by checking the number of rows
        Assertions.assertEquals(0, aDao.getNumRows());
        //we insert an authorized token in our table
        aDao.insert(authTokenTest);
        //we use find method to get the authorized token that we just inserted
        AuthToken compareTest = aDao.find(authTokenTest.getUsername());
        //we check that find didn't return a null since we know there is an event in our database
        Assertions.assertNotNull(compareTest);
        /*
        We make sure that find returns the value requested. If this passes we know that find can find an authorized token
        in the table where there is only one authorized token
        */
        Assertions.assertEquals(authTokenTest, compareTest);
        //we clear the table to test multiple authorized tokens in the table
        aDao.clear();
        Assertions.assertEquals(0, aDao.getNumRows());
        //we insert multiple authorized tokens to see if find can find the desired authorized token in a table with different events in it
        aDao.insert(authTokenTest);
        aDao.insert(authToken1);
        aDao.insert(authToken2);
        aDao.insert(authToken3);
        //we use find method to get the authorized token that we just inserted
        compareTest = aDao.find(authToken2.getUsername());
        //we check that find didn't return a null since we know there is an authorized token in our database
        Assertions.assertNotNull(compareTest);
        /*
        We make sure that find returns the value requested. We picked a random authorized token among the ones that
        were inserted. If this passes we know for sure that find can find an authorized token in the table that contains
        multiple different authorized tokens.
        */
        Assertions.assertEquals(authToken2, compareTest);
    }

    @Test//retrieve Negative test
    public void retrieveFail() throws DataAccessException {
        //we insert an authorized token in our table
        aDao.insert(authTokenTest);
        //we use find method to get the authorized token that we just inserted
        AuthToken compareTest = aDao.find(authTokenTest.getUsername());
        //we check that find didn't return a null since we know there is an authorized token in our database
        Assertions.assertNotNull(compareTest);
        //we make sure that find returns the value requested
        Assertions.assertEquals(authTokenTest, compareTest);
        //we clear the table so we can try to find authorized token, but it should fail because we know it's not in the database anymore
        aDao.clear();
        //find method should return null since the value is not in the database anymore
        Assertions.assertNull(aDao.find(authTokenTest.getUsername()));
    }


    @Test//retrieve POSITIVE test
    public void findPass() throws DataAccessException {
        //we make sur there is nothing in our table by checking the number of rows
        Assertions.assertEquals(0, aDao.getNumRows());
        //we insert an authorized token in our table
        aDao.insert(authTokenTest);
        //we use find method to get the authorized token that we just inserted
        AuthToken compareTest = aDao.findWithToken(authTokenTest.getAuthToken());
        //we check that find didn't return a null since we know there is an event in our database
        Assertions.assertNotNull(compareTest);
        /*
        We make sure that find returns the value requested. If this passes we know that find can find an authorized token
        in the table where there is only one authorized token
        */
        Assertions.assertEquals(authTokenTest, compareTest);
        //we clear the table to test multiple authorized tokens in the table
        aDao.clear();
        Assertions.assertEquals(0, aDao.getNumRows());
        //we insert multiple authorized tokens to see if find can find the desired authorized token in a table with different events in it
        aDao.insert(authTokenTest);
        aDao.insert(authToken1);
        aDao.insert(authToken2);
        aDao.insert(authToken3);
        //we use find method to get the authorized token that we just inserted
        compareTest = aDao.findWithToken(authToken2.getAuthToken());
        //we check that find didn't return a null since we know there is an authorized token in our database
        Assertions.assertNotNull(compareTest);
        /*
        We make sure that find returns the value requested. We picked a random authorized token among the ones that
        were inserted. If this passes we know for sure that find can find an authorized token in the table that contains
        multiple different authorized tokens.
        */
        Assertions.assertEquals(authToken2, compareTest);
    }

    @Test//retrieve Negative test
    public void findFail() throws DataAccessException {
        //we insert an authorized token in our table
        aDao.insert(authTokenTest);
        //we use find method to get the authorized token that we just inserted
        AuthToken compareTest = aDao.findWithToken(authTokenTest.getAuthToken());
        //we check that find didn't return a null since we know there is an authorized token in our database
        Assertions.assertNotNull(compareTest);
        //we make sure that find returns the value requested
        Assertions.assertEquals(authTokenTest, compareTest);
        //we clear the table so we can try to find authorized token, but it should fail because we know it's not in the database anymore
        aDao.clear();
        //find method should return null since the value is not in the database anymore
        Assertions.assertNull(aDao.findWithToken(authTokenTest.getAuthToken()));
    }



    @Test//clear POSITIVE test
    public void clearPass() throws DataAccessException {
        aDao.insert(authTokenTest);
        //we use find method to get the authorized token that we just inserted
        AuthToken compareTest = aDao.find(authTokenTest.getUsername());
        /*
        We check if find method found anything at all. If it did then we know if nothing
        else something was put into our database, since we cleared it in the beginning
        */
        Assertions.assertNotNull(compareTest);
        //we make sure that what we put in is exactly the same as what we got out.
        Assertions.assertEquals(authTokenTest, compareTest);
        //once we know we have inserted the authorized token desired we check the number of rows in the table is in fact 1
        Assertions.assertEquals(1, aDao.getNumRows());
        //we delete the only row in the table
        aDao.clear();
        //we check that indeed we deleted the row by checking the number of rows in the table
        Assertions.assertEquals(0, aDao.getNumRows());
        //we can also check that the authorized token is not in our table using the find method. We should get a null.
        Assertions.assertNull(aDao.find(authTokenTest.getUsername()));
        /*
        We try inserting the same authorized token to make sure we actually deleted it from our database. If we are successful to
        insert it we know for sure that it was not in our database and we actually deleted it.
        */
        aDao.insert(authTokenTest);
    }
}
