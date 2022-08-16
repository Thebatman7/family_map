package Service;

import DAO.*;
import Model.Event;
import Model.Person;
import Model.User;
import Service.Request.LoadRequest;
import Service.Result.LoadResult;


public class Load {

    /**
     * Clears all data from the Database and then loads users, persons and events data into the Database
     * @param request which is an LoadRequest object containing users, persons, and events arrays
     * @return a result object (LoadResult object if successful)
     */
    public LoadResult loadData(LoadRequest request) {
        int numOfUsers = 0;
        int numOfPersons = 0;
        int numOfEvents = 0;
        Boolean commit = false;
        Database db = new Database();
        LoadResult result = new LoadResult();
        User[] users = request.getUsers();
        Person[] persons = request.getPersons();
        Event[] events = request.getEvents();

        try{
            db.getConnection();
            UserDAO uDao = new UserDAO(db.getConnection());
            PersonDAO pDao = new PersonDAO(db.getConnection());
            EventDAO eDao = new EventDAO(db.getConnection());

            //we clear all data from the database
            db.clearTables();
            //we load the users data
            for(int i = 0; i < users.length; ++i) {
                User tempUser = new User();
                if(users[i].getUsername() != null && users[i].getPassword() != null && users[i].getEmail() != null &&
                        users[i].getFirstName() != null && users[i].getLastName() != null && users[i].getGender() != null
                        && users[i].getPersonID() != null) {
                    tempUser.setUsername(users[i].getUsername());
                    tempUser.setPassword(users[i].getPassword());
                    tempUser.setEmail(users[i].getEmail());
                    tempUser.setFirstName(users[i].getFirstName());
                    tempUser.setLastName(users[i].getLastName());
                    tempUser.setGender(users[i].getGender());
                    tempUser.setPersonID(users[i].getPersonID());
                    uDao.insert(tempUser);
                    ++numOfUsers;
                }
                else {
                    result.setMessage("Error: User's invalid or missing value.");
                    result.setSuccess(false);
                    break;
                }
            }
            //we load the persons data
            for(int i = 0; i < persons.length; ++i) {
                Person tempPerson = new Person();
                if (persons[i].getPersonID() != null && persons[i].getAssociatedUsername() != null &&
                        persons[i].getFirstName() != null && persons[i].getLastName() != null &&
                        persons[i].getGender() != null) {
                    tempPerson.setPersonID(persons[i].getPersonID());
                    tempPerson.setAssociatedUsername(persons[i].getAssociatedUsername());
                    tempPerson.setFirstName(persons[i].getFirstName());
                    tempPerson.setLastName(persons[i].getLastName());
                    tempPerson.setGender(persons[i].getGender());
                    tempPerson.setFatherID(persons[i].getFatherID());
                    tempPerson.setMotherID(persons[i].getMotherID());
                    tempPerson.setSpouseID(persons[i].getSpouseID());
                    pDao.insert(tempPerson);
                    ++numOfPersons;
                }
                else {
                    result.setMessage("Error: Person's invalid or missing value.");
                    result.setSuccess(false);
                    break;
                }
            }
            //we load the events data
            for(int i = 0; i < events.length; ++i) {
                Event tempEvent = new Event();
                if (events[i].getEventID() != null && events[i].getUsername() != null &&
                        events[i].getPersonID() != null && events[i].getCountry() != null &&
                        events[i].getCity() != null && events[i].getEventType() != null && events[i].getYear() != 0) {
                    tempEvent.setEventID(events[i].getEventID());
                    tempEvent.setUsername(events[i].getUsername());
                    tempEvent.setPersonID(events[i].getPersonID());
                    tempEvent.setLatitude(events[i].getLatitude());
                    tempEvent.setLongitude(events[i].getLongitude());
                    tempEvent.setCountry(events[i].getCountry());
                    tempEvent.setCity(events[i].getCity());
                    tempEvent.setEventType(events[i].getEventType());
                    tempEvent.setYear(events[i].getYear());
                    eDao.insert(tempEvent);
                    ++numOfEvents;
                }
                else {
                    result.setMessage("Error: Person's invalid or missing value.");
                    result.setSuccess(false);
                    break;
                }
            }
            if (result.getMessage() == null) {
                commit = true;
                result.setSuccess(true);
                result.setMessage("Successfully added " + numOfUsers + " users, " + numOfPersons + " persons, and " +
                        numOfEvents + " events to the Database.");
            }
            //we close connection
            db.closeConnection(commit);
        }
        catch(DataAccessException dbException) {
            dbException.printStackTrace();
        }
        return result;
    }
}
