package Service.Request;

public class PersonRequest {
    private String personID;
    private String authToken;

    //constructor
    public PersonRequest() {}
    //parameterized constructor to get a multiple persons
    public PersonRequest(String authToken) {
        this.authToken = authToken;
    }
    //parameterized constructor to get a single Person
    public PersonRequest(String personID, String authToken) {
        this.personID = personID;
        this.authToken = authToken;
    }

    public String getPersonID() { return personID; }

    public void setPersonID(String personID) { this.personID = personID; }

    public String getAuthToken() { return authToken; }

    public void setAuthToken(String authToken) { this.authToken = authToken; }
}
