package Service.Result;

import Model.Person;

public class PersonsResult {
    private Person[] data;
    private boolean success;
    private String message;

    //default constructor
    public PersonsResult() {}
    //parameterized constructor if unsuccessful
    public PersonsResult(String message, boolean success){
        this.message = message;
        this.success = success;
    }
    //parameterized constructor if successful
    public PersonsResult(Person[] data, boolean success){
        this.data = data;
        this.success = success;
    }

    public Person[] getData() { return data; }

    public void setData(Person[] data) { this.data = data; }

    public boolean getSuccess() { return success; }

    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }
}

