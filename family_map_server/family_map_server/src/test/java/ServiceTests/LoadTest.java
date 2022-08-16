package ServiceTests;

import DAO.DataAccessException;
import DAO.Database;
import DAO.PersonDAO;
import Encoder.JsonSerializer;
import Model.Event;
import Model.Person;
import Model.User;
import Service.Load;
import Service.Request.LoadRequest;
import Service.Result.LoadResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.sql.Connection;


public class LoadTest {
    private Database db;
    private Load load;
    private LoadRequest request;
    private LoadRequest request1;
    private LoadRequest request2;
    private LoadResult result;



    @BeforeEach
    public void setUp() throws IOException, DataAccessException {
        User user = new User("sheila", "parker", "sheila@parker.com", "Sheila", "Parker",
                "F", "Sheila_Parker");
        User user1 = new User("patrick", "spencer", "patrick@spencer.com", "Patrick",
                "Spencer", "M", "Patrick_Spencer");
        User[] users = new User[]{user, user1};

        Person person1 = new Person("Sheila_Parker", "sheila", "Sheila", "Parker",
                "F", "Blaine_McGary", "Betty_White", "Davis_Hyer");
        Person person2 = new Person("Davis_Hyer", "Davis", "Davis", "Parker",
                "M", "Blaine", "White", "Sheila_Parker");
        Person person3 = new Person("Patrick_Spencer", "patrick", "Patrick", "Spencer",
                "M", "Blaine7", "White7", "Laura6");
        Person person4 = new Person("Laura6", "Lola", "Laura", "Spencer",
                "F", "McGary", "Betty", "Patrick_Spencer");
        Person[] persons = new Person[]{person1, person2, person3, person4};

        Event event1 = new Event("Sheila_Birth", "sheila", "Sheila_Parker",
                -36.1833F, 144.9667F, "Australia", "Melbourne", "birth", 1970);
        Event event2 = new Event("marriage", "sheila", "Sheila_Parker",
                -34.1833F, 100.9667F, "Australia", "Melbourne", "birth", 2012);
        Event event3 = new Event("completed asteroids", "sheila", "Sheila_Parker",
                77.4667F, -68.7667F, "Denmark", "Qaanaaq", "birth", 2012);

        Event event4 = new Event("birth", "patrick", "Patrick_Spencer",
                77.4667F, -68.7667F, "Denmark", "Qaanaaq", "birth", 2012);
        Event event5 = new Event("theMarriage", "patrick", "Patrick_Spencer",
                77.4667F, -68.7667F, "Denmark", "Qaanaaq", "Marriage", 2012);
        Event event6 = new Event("Mission", "patrick", "Patrick_Spencer",
                77.4667F, -68.7667F, "Denmark", "Qaanaaq", "Service", 2012);
        Event[] events = new Event[] {event1, event2, event3, event4, event5, event6};

        User user2 = new User(null, "parker", "sheila@parker.com", "Sheila", "Parker",
                "F", "Sheila_Parker");
        User user3 = new User("patrick", "spencer", "patrick@spencer.com", "Patrick",
                "Spencer", "M", "Patrick_Spencer");
        User[] users2 = new User[]{user2, user3};

        Person person5 = new Person("Sheila_Parker", "sheila", "Sheila", "Parker",
                "F", "Blaine_McGary", "Betty_White", "Davis_Hyer");
        Person person6 = new Person("Davis_Hyer", "Davis", "Davis", "Parker",
                "M", "Blaine", "White", "Sheila_Parker");
        Person person7 = new Person("Patrick_Spencer", "patrick", "Patrick", "Spencer",
                "M", "Blaine7", "White7", "Laura6");
        Person person8 = new Person("Laura6", "Lola", "Laura", "Spencer",
                "F", "McGary", "Betty", "Patrick_Spencer");
        Person[] persons2 = new Person[]{person5, person6, person7, person8};

        Event event7 = new Event("Sheila_Birth", "sheila", "Sheila_Parker",
                -36.1833F, 144.9667F, "Australia", "Melbourne", "birth", 1970);
        Event event8 = new Event("marriage", "sheila", "Sheila_Parker",
                -34.1833F, 100.9667F, "Australia", "Melbourne", "birth", 2012);
        Event event9 = new Event("completed asteroids", "sheila", "Sheila_Parker",
                77.4667F, -68.7667F, "Denmark", "Qaanaaq", "birth", 2012);

        Event event10 = new Event("birth", "patrick", "Patrick_Spencer",
                77.4667F, -68.7667F, "Denmark", "Qaanaaq", "birth", 2012);
        Event event11 = new Event("theMarriage", "patrick", "Patrick_Spencer",
                77.4667F, -68.7667F, "Denmark", "Qaanaaq", "Marriage", 2012);
        Event event12 = new Event("Mission", "patrick", "Patrick_Spencer",
                77.4667F, -68.7667F, "Denmark", "Qaanaaq", "Service", 2012);
        Event[] events2 = new Event[] {event7, event8, event9, event10, event11, event12};
        db = new Database();
        db.openConnection();
        db.clearTables();
        result = new LoadResult();
        load = new Load();
        request1 = new LoadRequest();
        request1.setUsers(users);
        request1.setPersons(persons);
        request1.setEvents(events);
        request2 = new LoadRequest();
        request2.setUsers(users2);
        request2.setPersons(persons2);
        request2.setEvents(events2);
        db.closeConnection(true);
        //we get the path to the LoadData.json file in a string
        String filePathName = "passoffFiles/LoadData.json";
        //we create a file and pass the path to find the file
        File fileName = new File(filePathName);
        //we convert the file to InputStream
        InputStream fileData = new FileInputStream(fileName);
        //we read and convert the input stream into a string
        String stringOfData = readString(fileData);
        //we convert the string into an object
        request = JsonSerializer.deserialize(stringOfData, LoadRequest.class);
    }

    @AfterEach
    public void cleaningUp() throws DataAccessException {
        db.openConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    private String readString(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        char[] buffer = new char[1024];
        int length;
        while ((length = streamReader.read(buffer)) > 0) {
            stringBuilder.append(buffer, 0, length);
        }
        return stringBuilder.toString();
    }

    @Test
    public void testLoadTest() throws DataAccessException {
        Connection connection = db.getConnection();
        PersonDAO personDAO = new PersonDAO(connection);
        //we check that table of persons is empty
        Assertions.assertEquals(0, personDAO.getNumRows());
        db.closeConnection(false);
        result = load.loadData(request1);
        db.openConnection();
        Assertions.assertEquals(true, result.getSuccess());
        PersonDAO pDAO = new PersonDAO(db.getConnection());
        //we verify that right number persons were inserted
        Assertions.assertEquals(4, pDAO.getNumRows());
        db.closeConnection(false);
    }

    @Test
    public void testBigLoadTest() throws DataAccessException {
        Connection connection = db.getConnection();
        PersonDAO personDAO = new PersonDAO(connection);
        //we check that table of persons is empty
        Assertions.assertEquals(0, personDAO.getNumRows());
        db.closeConnection(false);
        result = load.loadData(request);
        db.openConnection();
        Assertions.assertEquals(true, result.getSuccess());
        PersonDAO pDAO = new PersonDAO(db.getConnection());
        //we verify that right number persons were inserted
        Assertions.assertEquals(11, pDAO.getNumRows());
        db.closeConnection(false);
    }

    @Test
    public void testLoadTestFail() throws DataAccessException {
        Connection connection = db.getConnection();
        PersonDAO personDAO = new PersonDAO(connection);
        //we check that table of persons is empty
        Assertions.assertEquals(0, personDAO.getNumRows());
        db.closeConnection(false);
        result = load.loadData(request2);
        Assertions.assertEquals(false, result.getSuccess());
        Assertions.assertEquals("Error: User's invalid or missing value.", result.getMessage());
    }
}
