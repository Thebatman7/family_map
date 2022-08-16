package Service;

import DAO.*;
import Model.AuthToken;
import Model.Person;
import Model.User;
import Service.Request.LoginRequest;
import Service.Request.RegisterRequest;
import Service.Result.LoginResult;
import Service.Result.RegisterResult;

import java.util.UUID;

public class Register {


    /**
     * Creates a new user account
     * @param request which is an RegisterRequest object containing username, password, email, first and last name and gender
     * @return a result object (RegisterResult object if successful)
     */
    public RegisterResult newUserAccount(RegisterRequest request) {
        Boolean commit = false;
        Database db = new Database();
        RegisterResult result = new RegisterResult();

        try {
            db.getConnection();
            PersonDAO pDao = new PersonDAO(db.getConnection());
            UserDAO uDao = new UserDAO(db.getConnection());
            AuthTokenDAO aDao = new AuthTokenDAO(db.getConnection());
            User userFound = uDao.find(request.getUsername());
            Generator generator = new Generator(db.getConnection());

            //if we search for the user with username we should get null since we are registering and usernames are primary keys
            if (userFound == null) {
                if (request.getUsername() != null && request.getPassword() != null && request.getEmail() != null &&
                        request.getFirstName() != null && request.getLastName() != null && request.getGender() != null) {
                    //we set success to true
                    result.setSuccess(true);
                    //we crete new person for the new user
                    Person person = new Person();
                    //we use UUID (Universal Unique Identifier) to generate random unique strings
                    String personID = UUID.randomUUID().toString();
                    person.setPersonID(personID);
                    person.setAssociatedUsername(request.getUsername());
                    person.setFirstName(request.getFirstName());
                    person.setLastName(request.getLastName());
                    person.setGender(request.getGender());
                    person.setFatherID(null);
                    person.setMotherID(null);
                    person.setSpouseID(null);


                    //we create new user
                    User newUser = new User();
                    newUser.setUsername(request.getUsername());//we set new user's username
                    result.setUsername(request.getUsername());//we set result's username
                    newUser.setPassword(request.getPassword());//we set new user's password
                    newUser.setEmail(request.getEmail());//we set new user's password
                    newUser.setFirstName(request.getFirstName());
                    newUser.setLastName(request.getLastName());
                    newUser.setGender(request.getGender());
                    newUser.setPersonID(personID);
                    result.setPersonID(personID);
                    uDao.insert(newUser);//we insert the new user in the database

                    //we generate the 4 generations and events for the user
                    generator.setUp();
                    generator.ancestorGenerator(person, request.getUsername(), 4);

                    //once the user's person got parents we add it to the database
                    pDao.insert(person);


                    //we create AuthToken object
                    AuthToken authToken = new AuthToken();
                    String authtoken = UUID.randomUUID().toString();
                    authToken.setAuthToken(authtoken);//we set the new authToken
                    result.setAuthToken(authtoken);//we set the result authToken
                    authToken.setUsername(request.getUsername());
                    aDao.insert(authToken);//we insert the new authToken object in the database
                    commit = true;
                }
                else {//one of the properties of the request body is null
                    result.setMessage("Error: Request property missing");
                    result.setSuccess(false);
                }
            }
            else {//find method found a user with the same username in the request parameter
                result.setMessage("Error: Username is already taken.");
                result.setSuccess(false);
            }
            //we close connection
            db.closeConnection(commit);
        }
        catch(DataAccessException dbException) {
            dbException.printStackTrace();
        }
        catch(Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }
}
