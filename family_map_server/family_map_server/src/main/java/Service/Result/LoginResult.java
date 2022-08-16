package Service.Result;

public class LoginResult {
    private String authtoken;
    private String username;
    private String personID;
    private boolean success;
    private String message;

    //default constructor
    public LoginResult() {}
    //parameterized constructor if unsuccessful
    public LoginResult(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
    //parameterized constructor if successful
    public LoginResult(String authtoken, String username, String personID, boolean success) {
        this.authtoken = authtoken;
        this.username = username;
        this.personID = personID;
        this.success = success;
    }

    public String getAuthToken() { return authtoken; }

    public void setAuthToken(String authtoken) { this.authtoken = authtoken; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPersonID() { return personID; }

    public void setPersonID(String personID) { this.personID = personID; }

    public boolean getSuccess() { return success; }

    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }
}
