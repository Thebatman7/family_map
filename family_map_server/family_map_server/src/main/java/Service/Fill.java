package Service;


import DAO.*;
import Model.Person;
import Model.User;
import Service.Request.FillRequest;
import Service.Result.FillResult;

import java.sql.Connection;
import java.util.UUID;


public class Fill {
    private int numOfEvents;
    private int numOfPersons;

    /**
     * Populates the server's database with generated data for the user
     * @param request which is a FillRequest object containing username and generations(by default is 4)
     * @return a result object (FillResult object if successful)
     */
    public FillResult fillData(FillRequest request) {
        int generations = request.getGenerations();
        String username = request.getUsername();
        numOfEvents = 0;
        numOfPersons = 0;

        Boolean commit = false;
        Database db = new Database();
        FillResult result = new FillResult();

        try {
            Connection connection = db.getConnection();
            Generator generator = new Generator(connection);
            UserDAO uDao = new UserDAO(connection);
            PersonDAO pDao = new PersonDAO(connection);
            EventDAO eDao = new EventDAO(connection);
            User user = uDao.find(username);

            if (generations <= 0) {
                result.setMessage("Error: The number of generations is an invalid parameter.");
                result.setSuccess(false);
            }
            else {
                if (user != null) {
                    //we clear all persons (including user's) related to the username
                    pDao.clearRelatedPersons(username);
                    //we clear all events (including user's) related to the username
                    eDao.clearRelatedEvents(username);
                    /*
                    Since all persons including the user's have been deleted we create a new person for the user with the
                    information that we find with the username.
                    */
                    Person userPerson = new Person();
                    //we generate a unique personID for the user's person
                    String personID = UUID.randomUUID().toString();
                    userPerson.setPersonID(personID);
                    //this personID has to be in the user as well
                    user.setPersonID(personID);
                    userPerson.setAssociatedUsername(user.getUsername());
                    userPerson.setFirstName(user.getFirstName());
                    userPerson.setLastName(user.getLastName());
                    userPerson.setGender(user.getGender());
                    userPerson.setFatherID(null);
                    userPerson.setMotherID(null);
                    userPerson.setSpouseID(null);
                    //we set the files up
                    generator.setUp();
                    //we call our method for the Fill service to fill the Database with data
                    generator.ancestorGenerator(userPerson, username, generations);
                    //we now have a father and mother for the user's person
                    pDao.insert(userPerson);
                    result.setSuccess(true);
                    numOfPersons = generator.getNumPeople();
                    numOfEvents = generator.getNumEvents();
                    result.setMessage("Successfully added " +  numOfPersons + " persons and " +
                            numOfEvents + " events in the Database.");
                    commit = true;
                }
                else {//find method could not find user which means it is not in the DB
                    result.setMessage("Error: The username is not in the Database.");
                    result.setSuccess(false);
                }
            }
            //we close connection
            db.closeConnection(commit);
        }
        catch (DataAccessException dbExceptions) {
            dbExceptions.printStackTrace();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }

    /**
     * Gets the number of people generated based on the generations number
     * @param numOfGenerations an integer with the number of generations desired
     * @return an int with the number of people generated
     */
    private int numOfPeople(int numOfGenerations) {
        int result = 0;
        for(int i = 1; i <= numOfGenerations; ++i) {
            result = result + (int)Math.pow(2, i);
        }
        return result;
    }
}
