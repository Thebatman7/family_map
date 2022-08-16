package Model;//This package contains the classes to represent the tables in the database


/*this is a Model class to represent the Users table in the database for the family server*/
public class User {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String gender;
    private String personID;

    //default constructor
    public User() {}
    //parameterized constructor
    public User(String username, String password, String email, String firstName, String lastName, String gender, String personID) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.personID = personID;
    }

    /**
     * Get the user's name
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the user's name
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Get last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Get gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Set gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Get the person ID
     */
    public String getPersonID() {
        return personID;
    }

    /**
     * Set the person ID
     */
    public void setPersonID(String personID) {
        this.personID = personID;
    }



    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o instanceof User) {//instanceof is a marco
            User oUser = (User)o;//we can't use it as an event object until we cast it even though it is of the Event type
            return oUser.getUsername().equals(getUsername()) &&
                    oUser.getPassword().equals(getPassword()) &&
                    oUser.getEmail().equals(getEmail()) &&
                    oUser.getFirstName().equals(getFirstName()) &&
                    oUser.getLastName().equals(getLastName()) &&
                    oUser.getGender().equals(getGender()) &&
                    oUser.getPersonID().equals(getPersonID());
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        return  "Username: " + username + "\n" +
                "Password: " + password + "\n" +
                "Email: " + email + "\n" +
                "First Name:" + firstName + "\n" +
                "Last Name: " + lastName + "\n" +
                "Gender: " + gender + "\n" +
                "Person ID: " + personID;
    }
}