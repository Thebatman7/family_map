package Service.Request;

public class EventRequest {
    private String eventID;
    private String authToken;

    //default constructor
    public EventRequest() {}
    //parameterized constructor for a multiple events
    public EventRequest(String authToken) {
        this.authToken = authToken;
    }
    //parameterized constructor for a single event
    public EventRequest(String eventID, String authToken) {
        this.eventID = eventID;
        this.authToken = authToken;
    }

    public String getEventID() { return eventID; }

    public void setEventID(String eventID) { this.eventID = eventID; }

    public String getAuthToken() { return authToken; }

    public void setAuthToken(String authToken) { this.authToken = authToken; }
}
