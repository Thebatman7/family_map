package ServiceTests;

import DAO.*;
import Model.AuthToken;
import Model.Person;
import Model.User;
import Service.PersonService;
import Service.Request.PersonRequest;
import Service.Result.PersonsResult;
import Service.Result.SinglePersonResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PersonServiceTest {
    private Database db;
    private PersonService personService;
    private SinglePersonResult singlePersonResult;
    private PersonsResult personsResult;

    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new Database();
        db.openConnection();
        db.clearTables();
        personService = new PersonService();
        singlePersonResult = new SinglePersonResult();
        personsResult = new PersonsResult();

        Person person1 = new Person("Person1", "redFox", "Bruce", "Dallas",
                "M", null, null, null);
        Person person2 = new Person("Person2", "blueFox", "Tommy", "Denny",
                "M", null, null, null);
        Person person3 = new Person("Person3", "greyFox", "Robin", "Turner",
                "M", null, null, null);

        Person person = new Person("Person", "somePerson", "Tommy", "Turner",
                "M", "Person4", "Person5", null);
        Person person4 = new Person("Person4", "somePerson", "Robin", "Turner",
                "M", null, null, null);
        Person person5 = new Person("Person5", "somePerson", "Lauren", "Turner",
                "F", null, null, null);
        PersonDAO personDAO = new PersonDAO(db.getConnection());
        personDAO.insert(person);
        personDAO.insert(person4);
        personDAO.insert(person5);
        personDAO.insert(person1);
        personDAO.insert(person2);
        personDAO.insert(person3);

        User user = new User("somePerson", "Fox1", "fox@emial.com", "TheFox",
                "ElZorro", "M", "Person");

        User user1 = new User("redFox", "redFox1", "red@emial.com", "Fox",
                "Zorro", "M", "Person1");
        User user2 = new User("blueFox", "blueFox1", "blue@emial.com", "Foxter",
                "Zorro", "M", "Person2");
        User user3 = new User("greyFox", "greyFox1", "grey@emial.com", "Foxy",
                "Zorro", "M", "Person3");
        UserDAO userDAO = new UserDAO(db.getConnection());
        userDAO.insert(user);
        userDAO.insert(user1);
        userDAO.insert(user2);
        userDAO.insert(user3);

        AuthToken authToken = new AuthToken("ThePower", "somePerson");
        AuthToken authToken1 = new AuthToken("Power1", "redFox");
        AuthToken authToken2 = new AuthToken("Power2", "blueFox");
        AuthToken authToken3 = new AuthToken("Power3", "greyFox");
        AuthTokenDAO authTokenDAO = new AuthTokenDAO(db.getConnection());
        authTokenDAO.insert(authToken);
        authTokenDAO.insert(authToken1);
        authTokenDAO.insert(authToken2);
        authTokenDAO.insert(authToken3);
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
    public void singlePersonTestPass() {
        //Test assumes we pass personID and an authToken to the service class
        PersonRequest request = new PersonRequest("Person1", "Power1");
        singlePersonResult = personService.getSinglePerson(request);
        Assertions.assertEquals(true, singlePersonResult.getSuccess());
    }

    @Test
    public void singlePersonTestFail() {
        //Test assumes we pass personID and an authToken to the service class
        PersonRequest request = new PersonRequest("Person1", "Powell");
        singlePersonResult = personService.getSinglePerson(request);
        Assertions.assertEquals(false, singlePersonResult.getSuccess());
    }

    @Test
    public void personsTestPass() {
        PersonRequest request = new PersonRequest("ThePower");
        personsResult = personService.getAllPersons(request);
        Assertions.assertEquals(true, personsResult.getSuccess());
        Person[] persons = personsResult.getData();
        for(int i = 0; i < persons.length; ++i) {
            System.out.println(persons[i].toString());
            System.out.println("");
        }
    }

    @Test
    public void personsTestFail() {
        PersonRequest request = new PersonRequest("ThePower1");
        personsResult = personService.getAllPersons(request);
        Assertions.assertEquals(false, personsResult.getSuccess());
        Assertions.assertEquals("Error: The auth token is invalid.", personsResult.getMessage());
        System.out.println(personsResult.getMessage());
    }
}
