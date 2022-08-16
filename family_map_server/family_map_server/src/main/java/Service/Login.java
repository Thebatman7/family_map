package Service;

import DAO.AuthTokenDAO;
import DAO.DataAccessException;
import DAO.Database;
import DAO.UserDAO;
import Model.AuthToken;
import Model.User;
import Service.Request.LoginRequest;
import Service.Result.LoginResult;

import java.util.UUID;

public class Login {

    /**
     * Logs the user with the username and password specified in the service.request.
     * @param request which is an LoginRequest object containing username and password
     * @return a result object (LoginResult object if successful)
     */
    public LoginResult loginUser(LoginRequest request) {
        boolean commit = false;
        Database db = new Database();
        LoginResult result = new LoginResult();

        try {
            db.openConnection();//this whole thing db.openConnection() is the connection that openConnection returns
            UserDAO uDao = new UserDAO(db.getConnection());
            User foundUser = uDao.find(request.getUsername());
            AuthTokenDAO aDao = new AuthTokenDAO(db.getConnection());
            //logging in a user represents that the user is already in our database, we check that that is true
            if (foundUser != null) {
                //we check the password and the user are the right pair
                if (foundUser.getPassword().equals(request.getPassword())) {
                    if (request.getUsername() != null && request.getPassword() != null) {
                        result.setSuccess(true);
                        //we use UUID (Universal Unique Identifier) to generate random unique strings
                        String authTokenGenerated = UUID.randomUUID().toString();
                        //we set our result object with the authToken generated
                        result.setAuthToken(authTokenGenerated);
                        //we create an authToken object to pass it to a AuthTokenDAO object so we can insert into the DB
                        AuthToken authToken = new AuthToken(authTokenGenerated, request.getUsername());
                        aDao.insert(authToken);
                        result.setUsername(request.getUsername());
                        result.setPersonID(foundUser.getPersonID());
                        commit = true;
                    }
                    else {
                        result.setSuccess(false);
                        result.setMessage("Error: Request property missing.");
                    }
                }
                else {
                    result.setSuccess(false);
                    result.setMessage("Error: The password is not the correct password.");
                }
            }
            else {
                result.setSuccess(false);
                result.setMessage("Error: Username " + request.getUsername() + " is not registered in the database.");
            }
            //we close connection
            db.closeConnection(commit);
        }
        catch (DataAccessException dbException) {
            dbException.printStackTrace();
        }
        return result;
    }
}
