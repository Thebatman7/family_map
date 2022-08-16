package ServiceTests;

import DAO.*;
import Model.AuthToken;
import Model.Event;
import Model.Person;
import Model.User;
import Service.EventService;
import Service.Request.EventRequest;
import Service.Result.EventsResult;
import Service.Result.SingleEventResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EventServiceTest {
    private Database db;
    private EventService eventService;
    private SingleEventResult singleEventResult;
    private EventsResult eventsResult;

    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new Database();
        db.openConnection();
        db.clearTables();
        eventService = new EventService();
        singleEventResult = new SingleEventResult();
        eventsResult = new EventsResult();

        Event event = new Event("The_birth", "TheMan", "Person", 40.9f, 140.1f, "Japan", "Tokyo",
                "Birth", 1993);
        Event event1 = new Event("Fishing_123A", "TheMan", "Person1",
                35.9f, 140.1f, "Japan", "Tokyo",
                "Biking_Around", 2016);
        Event event2 = new Event("Skydiving_123A", "TheMan", "Person2",
                35.9f, 140.1f, "Japan", "Osaka",
                "Biking_Around", 2016);
        Event event3 = new Event("Golfing_123A", "TheMan", "Person3",
                35.9f, 140.1f, "Japan", "YouSucka",
                "Biking_Around", 2016);
        EventDAO eventDAO = new EventDAO(db.getConnection());
        eventDAO.insert(event);
        eventDAO.insert(event1);
        eventDAO.insert(event2);
        eventDAO.insert(event3);

        Person person = new Person("Person", "TheMan", "Tommy", "Turner",
                "M", "Person1", "Person2", null);
        Person person1 = new Person("Person1", "redFox", "Fox", "Zorro",
                "M", null, null, null);
        Person person2 = new Person("Person2", "Person", "Lauren", "Turner",
                "F", null, null, null);
        Person person3 = new Person("Person3", "Person", "Robin", "Turner",
                "M", null, null, null);
        PersonDAO personDAO = new PersonDAO(db.getConnection());
        personDAO.insert(person);
        personDAO.insert(person1);
        personDAO.insert(person2);
        personDAO.insert(person3);

        User user = new User("TheMan", "Fox1", "fox@emial.com", "Tommy",
                "Turner", "M", "Person");
        User user1 = new User("redFox", "redFox1", "red@emial.com", "Fox",
                "Zorro", "M", "Person1");
        UserDAO userDAO = new UserDAO(db.getConnection());
        userDAO.insert(user);
        userDAO.insert(user1);

        AuthToken authToken = new AuthToken("ThePower", "TheMan");
        AuthToken authToken1 = new AuthToken("Power1", "redFox");
        AuthTokenDAO authTokenDAO = new AuthTokenDAO(db.getConnection());
        authTokenDAO.insert(authToken);
        authTokenDAO.insert(authToken1);
        db.closeConnection(true);
    }
    @AfterEach
    public void tearDown() throws DataAccessException {
        //we open a connection to clear our database so there is no conflict later one
        db.openConnection();
        //we clear table
        db.clearTables();
        /*
        Here we close the connection to the database file so it can be opened elsewhere.
        We will commit to true because we need to save the cleaning of the database after the test cases.
        */
        db.closeConnection(true);
    }

    @Test
    public void singleEventTestPass() {
        EventRequest request = new EventRequest("The_birth", "ThePower");
        singleEventResult = eventService.getSingleEvent(request);
        Assertions.assertEquals(true, singleEventResult.getSuccess());
    }

    @Test
    public void singleEventTestFail() {
        EventRequest request = new EventRequest("The_birth", "Power1");
        singleEventResult = eventService.getSingleEvent(request);
        Assertions.assertEquals(false, singleEventResult.getSuccess());
        Assertions.assertEquals("Error: Requested event does not belong to provided authToken, Power1.",
                singleEventResult.getMessage());
        System.out.println(singleEventResult.getMessage());
    }

    @Test
    public void eventsTestPass(){
        EventRequest request = new EventRequest("ThePower");
        eventsResult = eventService.getAllEvents(request);
        Assertions.assertEquals(true, eventsResult.getSuccess());
        Event[] events = eventsResult.getData();
        for(int i = 0; i < events.length; ++i) {
            System.out.println(events[i].toString());
            System.out.println("");
        }
    }

    @Test
    public void eventsTestFail(){
        EventRequest request = new EventRequest("Power1");
        eventsResult = eventService.getAllEvents(request);
        Assertions.assertEquals(false, eventsResult.getSuccess());
        Assertions.assertEquals("Error: There are not any events associated to the user in the Database.",
                eventsResult.getMessage());
        System.out.println(eventsResult.getMessage());
    }
}
