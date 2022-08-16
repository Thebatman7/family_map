package Service;

import DAO.*;
import Model.AuthToken;
import Model.Person;
import Model.User;
import Service.Request.PersonRequest;
import Service.Result.PersonsResult;
import Service.Result.SingleEventResult;
import Service.Result.SinglePersonResult;

import java.util.ArrayList;
import java.util.List;

public class PersonService {

    /**
     * Gets a single Person object with the ID specified in the service.request.
     * @param request which is a PersonRequest object containing personID and authToken
     * @return a result object (SinglePersonResult object if successful)
     */
    public SinglePersonResult getSinglePerson(PersonRequest request) {
        Boolean commit = false;
        Database db = new Database();
        SinglePersonResult result = new SinglePersonResult();
        String authToken = request.getAuthToken();
        String personID = request.getPersonID();

        //CODE
        try {
            db.getConnection();
            PersonDAO pDao = new PersonDAO(db.getConnection());
            UserDAO uDao = new UserDAO(db.getConnection());
            AuthTokenDAO aDao = new AuthTokenDAO(db.getConnection());
            AuthToken authorization = aDao.findWithToken(authToken);
            if (authToken != null && authorization != null) {
                User user = uDao.find(authorization.getUsername());
                if (user != null){
                    Person person = pDao.find(personID);
                    if (personID != null && person != null) {
                        if (person.getPersonID().equals(user.getPersonID())) {
                            result.setAssociatedUsername(person.getAssociatedUsername());
                            result.setPersonID(person.getPersonID());
                            result.setFirstName(person.getFirstName());
                            result.setLastName(person.getLastName());
                            result.setGender(person.getGender());
                            result.setFatherID(person.getFatherID());
                            result.setMotherID(person.getMotherID());
                            result.setSpouseID(person.getSpouseID());
                            result.setSuccess(true);
                            commit = true;
                        }
                        else {//if the user's personId and the person's ID aren't the same; requested person does not belong to user
                            result.setSuccess(false);
                            result.setMessage("Error: Requested person does not belong to the user");
                        }
                    }
                    else {//if the person is null that means the person is not in the DB which means personID is invalid
                        result.setSuccess(false);
                        result.setMessage("Error: The personId,  " + personID + ", is invalid");
                    }
                }
                else {//we got an invalid authToken because the user result from the find method user the AuthToken object's username was null
                    result.setSuccess(false);
                    result.setMessage("Error: The auth token, " + authToken + ", is invalid");
                }
            }
            else {
                result.setSuccess(false);
                result.setMessage("Error: The auth token, " + authToken + ", is invalid");
            }
            //we close connection
            db.closeConnection(commit);
        }
        catch(DataAccessException dbException) {
            dbException.printStackTrace();
            //FIX ME...
        }
        return result;
    }


    /**
     * Gets all family members of the user found with the authToken
     * @param request which is a PersonRequest object containing the authToken
     * @return a result object (PersonsResult object if successful)
     */
    public PersonsResult getAllPersons(PersonRequest request) {
        Boolean commit = false;
        Database db = new Database();
        PersonsResult result = new PersonsResult();
        String authToken = request.getAuthToken();

        try{
            db.getConnection();
            PersonDAO pDao = new PersonDAO(db.getConnection());
            AuthTokenDAO aDao = new AuthTokenDAO(db.getConnection());
            AuthToken authorization = aDao.findWithToken(authToken);
            if (authorization != null) {
                List<Person> personsList = new ArrayList<>();
                personsList = pDao.findAllFamily(authorization.getUsername());
                if (personsList != null && personsList.size() != 0) {
                    //converting the List of persons to an Array of persons
                    Person[] personsArray = new Person[personsList.size()];
                    personsArray = personsList.toArray(personsArray);
                    result.setData(personsArray);
                    result.setSuccess(true);
                    commit = true;
                }
                else {
                    result.setSuccess(false);
                    result.setMessage("Error: The user does not have family members in the Database.");
                }
            }
            else{
                result.setSuccess(false);
                result.setMessage("Error: The auth token is invalid.");
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
