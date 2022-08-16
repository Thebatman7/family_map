package DAOTests;

import DAO.DataAccessException;
import DAO.Database;
import DAO.PersonDAO;
import Model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class PersonDAOTest {
    private Database db;
    private Person personTest;
    private Person person1;
    private Person person2;
    private Person person3;
    private PersonDAO pDao;


    @BeforeEach
    public void setUP() throws DataAccessException {
        //we create a database
        db = new Database();
        //we create an event that we will insert
        personTest = new Person("Mr.Potato123", "Gale", "John",
                "Smith", "M", "POP", null, null);
        person1 = new Person("Batman", "Thebat7", "Rembrand",
                "Pardo", "M", "Father", "Mutti", "GEORGINASTUART");
        person2 = new Person("Mr.Potato2", "Gale", "Tine",
                "Smith", "F", null, "MOON", null);
        person3 = new Person("Mr.Potato3", "Gale", "John",
                "Smith", "m", null, null, null);
        //Here, we'll open the connection in preparation for the test case to use it
        Connection connection = db.getConnection();
        //we clear the database as well so any lingering data doesn't affect our tests
        db.clearTables();
        //Then we pass that connection to the EventDAO so it can access the database
        pDao = new PersonDAO(connection);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        /*
        Here close the connection to the database file so it can be opened elsewhere.
        We will leave commit to false because we have no need to save the changes to database between test cases
        */
        db.closeConnection(false);
    }

    @Test//insertion POSITIVE test
    public void insertPass() throws DataAccessException {
        /*
        While insert returns a boolean we can't use that to verify that our function actually worked only that it ran
        without causing an error
        */
        pDao.insert(personTest);
        //we use find method to get the person that we just inserted
        Person compareTest = pDao.find(personTest.getPersonID());
        /*
        We check if find method found anything at all. If it did then we know if nothing else that something was put
        into our database, since we cleared it in the beginning.
        */
        Assertions.assertNotNull(compareTest);
        /*
        We make sure we put in is exactly the same as what we got out. If this passes then we know that our insert did
        put something in, and that it didn't change the data in any way.
        */
        Assertions.assertEquals(personTest, compareTest);
    }

    @Test//insertion NEGATIVE test
    public void InsertFail() throws DataAccessException {
        /*
        We test insert method trying to make it fail.
        If we called the method the first time it will insert it successfully.
        */
        pDao.insert(personTest);
        /*
        Our SQL table is set up so that person personID must be unique. SO trying to insert it again will cause
        the method to throw an exception.
        Note: This call uses a lambda function. This line of code runs the code that comes after the the "()->" and
        expects it to throw an instance of the class in the first parameter.
        */
        Assertions.assertThrows(DataAccessException.class, ()-> pDao.insert(personTest));
    }

    @Test//retrieve POSITIVE test
    public void retrievePass() throws DataAccessException {
        //we make sur there is nothing in our table by checking the number of rows
        Assertions.assertEquals(0, pDao.getNumRows());
        //we insert a person in our table
        pDao.insert(personTest);
        //we use find method to get the person that we just inserted
        Person compareTest = pDao.find(personTest.getPersonID());
        //we check that find didn't return a null since we know there is an person in our database
        Assertions.assertNotNull(compareTest);
        /*
        We make sure that find returns the value requested. If this passes we know that find can find a peroson in
        the table where there is only one person
        */
        Assertions.assertEquals(personTest, compareTest);
        //we clear the table to test multiple persons in the table
        pDao.clear();
        Assertions.assertEquals(0, pDao.getNumRows());
        //we insert multiple events to see if find can find the desired person in a table with different persons in it
        pDao.insert(personTest);
        pDao.insert(person1);
        pDao.insert(person2);
        pDao.insert(person3);
        //we use find method to get the person that we just inserted
        compareTest = pDao.find(person1.getPersonID());
        //we check that find didn't return a null since we know there is an person in our database
        Assertions.assertNotNull(compareTest);
        /*
        We make sure that find returns the value requested. We picked a random person among the ones that were inserted.
        If this passes we know for sure that find can find a person in the table that contains multiple different person.
        */
        Assertions.assertEquals(person1, compareTest);
    }

    @Test//retrieve Negative test
    public void retrieveFail() throws DataAccessException {
        //we insert an person in our table
        pDao.insert(personTest);
        //we use find method to get the person that we just inserted
        Person compareTest = pDao.find(personTest.getPersonID());
        //we check that find didn't return a null since we know there is an event in our database
        Assertions.assertNotNull(compareTest);
        //we make sure that find returns the value requested
        Assertions.assertEquals(personTest, compareTest);
        //we clear the table so we can try to find person, but it should fail because we know it's not in the database anymore
        pDao.clear();
        //find method should return null since the value is not in the database anymore
        Assertions.assertNull(pDao.find(personTest.getPersonID()));
    }

    @Test//clear POSITIVE test
    public void clearPass() throws DataAccessException {
        pDao.insert(personTest);
        //we use find method to get the person that we just inserted
        Person compareTest = pDao.find(personTest.getPersonID());
        /*
        We check if find method found anything at all. If it did then we know if nothing
        else something was put into our database, since we cleared it in the beginning
        */
        Assertions.assertNotNull(compareTest);
        //we make sure that what we put in is exactly the same as what we got out.
        Assertions.assertEquals(personTest, compareTest);
        //once we know we have inserted the person desired we check the number of rows in the table is in fact 1
        Assertions.assertEquals(1, pDao.getNumRows());
        //we delete the only row in the table
        pDao.clear();
        //we check that indeed we deleted the row by checking the number of rows in the table
        Assertions.assertEquals(0, pDao.getNumRows());
        //we can also check that the event is not in our table using the find method. We should get a null.
        Assertions.assertNull(pDao.find(personTest.getPersonID()));
        /*
        We try inserting the same person to make sure we actually deleted it from our database. If we are successful to
        insert it we know for sure that it was not in our database and we actually deleted it.
        */
        pDao.insert(personTest);
    }

    @Test
    public void deleteAssociatedPersons() throws DataAccessException {
        //we clear the table to test multiple persons in the table
        pDao.clear();
        Assertions.assertEquals(0, pDao.getNumRows());
        //we insert multiple events to see if find can find the desired person in a table with different persons in it
        pDao.insert(personTest);
        pDao.insert(person1);
        pDao.insert(person2);
        pDao.insert(person3);
        //we check our table contains the four persons
        Assertions.assertEquals(4, pDao.getNumRows());
        //we delete all persons that have the specified associatedUsername
        pDao.clearRelatedPersons(personTest.getAssociatedUsername());
        //All except for one person should be deleted from the Person table
        Assertions.assertEquals(1, pDao.getNumRows());
    }
}
