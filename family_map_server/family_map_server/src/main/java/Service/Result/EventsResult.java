package Service.Result;

import Model.Event;

public class EventsResult {
    private Event[] data;
    private boolean success;
    private String message;

    //default constructor
    public EventsResult() {}
    //parameterized constructor if unsuccessful
    public EventsResult(String message, boolean success){
        this.message = message;
        this.success = success;
    }
    //parameterized constructor if successful
    public EventsResult(Event[] data, boolean success) {
        this.data = data;
        this.success = success;
    }

    public Event[] getData() { return data; }

    public void setData(Event[] data) { this.data = data; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public boolean getSuccess() { return success; }

    public void setSuccess(boolean success) { this.success = success; }
}
