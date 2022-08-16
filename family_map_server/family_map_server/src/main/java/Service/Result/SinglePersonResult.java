package Service.Result;

public class SinglePersonResult {
    private String personID;
    private String associatedUsername;
    private String firstName;
    private String lastName;
    private String gender;
    private String fatherID;//this can be null
    private String motherID;//this can be null
    private String spouseID;//this can be null
    private boolean success;
    private String message;

    //default constructor
    public SinglePersonResult() {}
    //parameterized constructor if unsuccessful
    public SinglePersonResult(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
    //parameterized constructor if successful
    public SinglePersonResult(String personID, String associatedUsername, String firstName, String lastName,
                              String gender, String fatherID, String motherID, String spouseID, boolean success) {
        this.personID = personID;
        this.associatedUsername = associatedUsername;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.fatherID = fatherID;
        this.motherID = motherID;
        this.spouseID = spouseID;
        this.success = success;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getAssociatedUsername() {
        return associatedUsername;
    }

    public void setAssociatedUsername(String associatedUsername) {
        this.associatedUsername = associatedUsername;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFatherID() {
        return fatherID;
    }

    public void setFatherID(String fatherID) {
        this.fatherID = fatherID;
    }

    public String getMotherID() {
        return motherID;
    }

    public void setMotherID(String motherID) {
        this.motherID = motherID;
    }

    public String getSpouseID() {
        return spouseID;
    }

    public void setSpouseID(String spouseID) {
        this.spouseID = spouseID;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message;}
}
