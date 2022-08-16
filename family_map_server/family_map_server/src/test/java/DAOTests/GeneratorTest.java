package DAOTests;

import DAO.*;
import Model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class GeneratorTest {
    private Generator generatorTest;
    private Database db;
    private Person personTest;
    private PersonDAO pDao;


    @BeforeEach
    public void setUp() throws Exception {
        //we create a database
        db = new Database();
        personTest = new Person("PersonTestID", "personTest1", "Hamster",
                "Test", "M", null, null , null);
        //we'll open the connection in preparation for the test case to use it
        Connection connection = db.getConnection();
        //we clear the database as well so any lingering data doesn't affect our tests
        db.clearTables();
        //Then we pass that connection to the AuthTokenDAO so it can access the database
        pDao = new PersonDAO(connection);
        //we create a generator object
        generatorTest = new Generator(connection);
        //we set it up
        generatorTest.setUp();
    }
    @AfterEach
    public void tearDown() throws DataAccessException {
        /*
        Here we close the connection to the database file so it can be opened elsewhere.
        We will leave commit to false because we have no need to save the changes to database between test cases.
        */
        try {
            if (!db.getConnection().isClosed()) db.closeConnection(false);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
    @Test
    public void setUpTest() throws Exception {
        generatorTest.setUp();
    }

    @Test
    public void defaultGenerationTest() throws DataAccessException, SQLException {
        //if (!db.getConnection().isClosed()) db.closeConnection(false);
        generatorTest.ancestorGenerator(personTest,"HamsterTest", 4);
        Assertions.assertEquals(31, generatorTest.getNumPeople());
        System.out.println(generatorTest.getNumPeople());
    }

    @Test
    public void simpleGenerationTest() throws DataAccessException, SQLException {
        //if (!db.getConnection().isClosed()) db.closeConnection(false);
        generatorTest.ancestorGenerator(personTest,"HamsterTest", 2);
        Assertions.assertEquals(7, generatorTest.getNumPeople());
        System.out.println(generatorTest.getNumPeople());
    }
}
