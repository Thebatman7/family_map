package ServiceTests;

import DAO.DataAccessException;
import DAO.Database;
import DAO.PersonDAO;
import DAO.UserDAO;
import Model.Person;
import Model.User;
import Service.Fill;
import Service.Request.FillRequest;
import Service.Result.FillResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

public class FillTest {
    Database db;
    Fill fill;
    FillRequest fillRequest;
    FillResult fillResult;
    UserDAO userDAO;
    PersonDAO personDAO;
    Connection connection;


    @BeforeEach
    public void setUp() throws DataAccessException {
        User user = new User("Thebat7", "password", "bat@emial.com", "Remy",
                "Pardo", "M", "Batman");
        Person person = new Person("Batman", "Thebat7", "Remy", "Pardo",
                "M", "Ratpanat", "Batwoman", null);
        db = new Database();
        connection = db.getConnection();
        db.clearTables();
        fillRequest = new FillRequest();
        userDAO = new UserDAO(connection);
        personDAO = new PersonDAO(connection);
        userDAO.insert(user);
        personDAO.insert(person);
        db.closeConnection(true);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        //we open connection to clean up
        db.openConnection();
        //we clear the database so it is empty for next use
        db.clearTables();
        /*
        Here we close the connection to the database file so it can be opened elsewhere.
        We will commit to true because we need to save the cleaning of the database after the test cases.
        */
        db.closeConnection(true);
    }

    @Test
    public void fillServiceTest() throws DataAccessException {
        //default num of generations will be 4
        fill = new Fill();
        fillRequest.setUsername("Thebat7");
        fillResult = fill.fillData(fillRequest);
        Assertions.assertEquals(true, fillResult.getSuccess());
        //we compare what we expect with what we get from the result object
        Assertions.assertEquals("Successfully added 31 persons and 93 events in the Database.", fillResult.getMessage());
    }

    @Test
    public void fillWithGenerationsTest() throws DataAccessException {
        fill = new Fill();
        fillRequest.setUsername("Thebat7");
        fillRequest.setGenerations(3);
        fillResult = fill.fillData(fillRequest);
        Assertions.assertEquals(true, fillResult.getSuccess());
        //we compare what we expect with what we get from the result object
        Assertions.assertEquals("Successfully added 15 persons and 45 events in the Database.", fillResult.getMessage());
    }

    @Test
    public void fillServiceTestFail() throws DataAccessException {
        //default num of generations will be 4
        fill = new Fill();
        fillRequest.setUsername("Batman");
        fillResult = fill.fillData(fillRequest);
        //username is not in database
        Assertions.assertEquals(false, fillResult.getSuccess());
        fillRequest.setUsername("Thebat7");
        //zero is not acceptable becuase it is the user itself. This should fail
        fillRequest.setGenerations(0);
        fillResult = fill.fillData(fillRequest);
        Assertions.assertEquals(false, fillResult.getSuccess());
        System.out.println( fillResult.getMessage());
    }
}
