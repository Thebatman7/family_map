package DAOTests;

import DAO.DataAccessException;
import DAO.Database;
import DAO.EventDAO;
import Model.Event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;


public class EventDAOTest {
    private Database db;
    private Event bestEvent;
    private Event event1;
    private Event event2;
    private Event event3;
    private EventDAO eDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        //we create a database
        db = new Database();
        //we create an event that we will insert
        bestEvent = new Event("Biking_123A", "Gale", "Gale123A",
                35.9f, 140.1f, "Japan", "Ushiku",
                "Biking_Around", 2016);
        event1 = new Event("Fishing_123A", "Gale1", "Gale1A",
                35.9f, 140.1f, "Japan", "Tokyo",
                "Biking_Around", 2016);
        event2 = new Event("Skydiving_123A", "Gale", "Gale2A",
                35.9f, 140.1f, "Japan", "Osaka",
                "Biking_Around", 2016);
        event3 = new Event("Golfing_123A", "Gale", "Gale3A",
                35.9f, 140.1f, "Japan", "YouSucka",
                "Biking_Around", 2016);
        //we'll open the connection in preparation for the test case to use it
        Connection connection = db.getConnection();
        //we clear the database as well so any lingering data doesn't affect our tests
        db.clearTables();
        //Then we pass that connection to the EventDAO so it can access the database
        eDao = new EventDAO(connection);
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
        eDao.insert(bestEvent);
        //we use find method to get the event that we just inserted
        Event compareTest = eDao.find(bestEvent.getEventID());
        /*
        We check if find method found anything at all. If it did then we know if nothing
        else something was put into our database, since we cleared it in the beginning.
        */
        Assertions.assertNotNull(compareTest);
        /*
        We make sure that what we put in is exactly the same as what we got out. If this passes
        then we know that our insert did put something in, and that it didn't change the data in any way.
        */
        Assertions.assertEquals(bestEvent, compareTest);
    }

    @Test//insertion NEGATIVE test
    public void insertFail() throws DataAccessException {
        /*
        We test insert method trying to make it fail.
        If we called the method the first time it will insert it successfully.
        */
        eDao.insert(bestEvent);
        /*
        Our SQL table is set up so that eventID must be unique. So trying to insert it again
        will cause the method to throw an exception.
        Note: This call uses a lambda function. This line of code runs the code that comes after
        the "()->" and expects it to throw an instance of the class in the first parameter.
        */
        Assertions.assertThrows(DataAccessException.class, ()-> eDao.insert(bestEvent));
    }

    @Test//retrieve POSITIVE test
    public void retrievePass() throws DataAccessException {
        //we make sur there is nothing in our table by checking the number of rows
        Assertions.assertEquals(0, eDao.getNumRows());
        //we insert an event in our table
        eDao.insert(bestEvent);
        //we use find method to get the event that we just inserted
        Event compareTest = eDao.find(bestEvent.getEventID());
        //we check that find didn't return a null since we know there is an event in our database
        Assertions.assertNotNull(compareTest);
        /*
        We make sure that find returns the value requested. If this passes we know that find can find an event in
        the table where there is only one event
        */
        Assertions.assertEquals(bestEvent, compareTest);
        //we clear the table to test multiple events in the table
        eDao.clear();
        Assertions.assertEquals(0, eDao.getNumRows());
        //we insert multiple events to see if find can find the desired event in a table with different events in it
        eDao.insert(bestEvent);
        eDao.insert(event1);
        eDao.insert(event2);
        eDao.insert(event3);
        //we use find method to get the event that we just inserted
        compareTest = eDao.find(event2.getEventID());
        //we check that find didn't return a null since we know there is an event in our database
        Assertions.assertNotNull(compareTest);
        /*
        We make sure that find returns the value requested. We picked a random event among the ones that were inserted.
        If this passes we know for sure that find can find an event in the table that contains multiple different events.
        */
        Assertions.assertEquals(event2, compareTest);
    }

    @Test//retrieve Negative test
    public void retrieveFail() throws DataAccessException {
        //we insert an event in our table
        eDao.insert(bestEvent);
        //we use find method to get the event that we just inserted
        Event compareTest = eDao.find(bestEvent.getEventID());
        //we check that find didn't return a null since we know there is an event in our database
        Assertions.assertNotNull(compareTest);
        //we make sure that find returns the value requested
        Assertions.assertEquals(bestEvent, compareTest);
        //we clear the table so we can try to find event, but it should fail because we know it's not in the database anymore
        eDao.clear();
        //find method should return null since the value is not in the database anymore
        Assertions.assertNull(eDao.find(bestEvent.getEventID()));
    }


    @Test//clear POSITIVE test
    public void clearPass() throws DataAccessException {
        eDao.insert(bestEvent);
        //we use find method to get the event that we just inserted
        Event compareTest = eDao.find(bestEvent.getEventID());
        /*
        We check if find method found anything at all. If it did then we know if nothing
        else something was put into our database, since we cleared it in the beginning
        */
        Assertions.assertNotNull(compareTest);
        //we make sure that what we put in is exactly the same as what we got out.
        Assertions.assertEquals(bestEvent, compareTest);
        //once we know we have inserted the event desired we check the number of rows in the table is in fact 1
        Assertions.assertEquals(1, eDao.getNumRows());
        //we delete the only row in the table
        eDao.clear();
        //we check that indeed we deleted the row by checking the number of rows in the table
        Assertions.assertEquals(0, eDao.getNumRows());
        //we can also check that the event is not in our table using the find method. We should get a null.
        Assertions.assertNull(eDao.find(bestEvent.getEventID()));
        /*
        We try inserting the same event to make sure we actually deleted it from our database. If we are successful to
        insert it we know for sure that it was not in our database and we actually deleted it.
        */
        eDao.insert(bestEvent);
    }

    @Test
    public void deleteAssociatedEvents() throws DataAccessException {
        //we clear the table to test multiple events in the table
        eDao.clear();
        Assertions.assertEquals(0, eDao.getNumRows());
        //we insert multiple events to see if find can find the desired event in a table with different events in it
        eDao.insert(bestEvent);
        eDao.insert(event1);
        eDao.insert(event2);
        eDao.insert(event3);
        //we check we have inserted the 4 events in the Event table
        Assertions.assertEquals(4, eDao.getNumRows());
        //we delete all events that have the specific associatedUsername
        eDao.clearRelatedEvents(bestEvent.getUsername());
        //we check we have deleted all events except for one
        Assertions.assertEquals(1, eDao.getNumRows());
    }
}
