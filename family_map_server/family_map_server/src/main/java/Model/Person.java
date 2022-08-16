package Model;//This package contains the classes to represent the tables in the database


/*this is a Model class to represent the Persons table in the database for the family server*/
public class Person {
    private String personID;
    private String associatedUsername;
    private String firstName;
    private String lastName;
    private String gender;
    private String fatherID;
    private String motherID;
    private String spouseID;

    //constructor
    public Person() {}
    //parameterized constructor
    public Person(String personID, String associatedUsername, String firstName, String lastName, String gender, String fatherID, String motherID, String spouseID) {
        this.personID = personID;
        this.associatedUsername = associatedUsername;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.fatherID = fatherID;
        this.motherID = motherID;
        this.spouseID = spouseID;
    }

    /**
     * Get the Person ID
     */
    public String getPersonID() {
        return personID;
    }

    /**
     * Set the Person ID
     */
    public void setPersonID(String personID) {
        this.personID = personID;
    }

    /**
     * Get the user's name
     */
    public String getAssociatedUsername() {
        return associatedUsername;
    }

    /**
     * Set the user's name
     */
    public void setAssociatedUsername(String associatedUsername) {//associated username???
        this.associatedUsername = associatedUsername;
    }

    /**
     * Get the person's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set the person's first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Get the person's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set the person's last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Get person's gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Set person's gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Get person's father ID
     */
    public String getFatherID() {
        return fatherID;
    }

    /**
     * Set the person's father ID
     */
    public void setFatherID(String fatherID) {
        this.fatherID = fatherID;
    }

    /**
     * Get the person's mother ID
     */
    public String getMotherID() {
        return motherID;
    }

    /**
     * Set peron's mother ID
     */
    public void setMotherID(String motherID) {
        this.motherID = motherID;
    }

    /**
     * Get the person' spouse ID
     */
    public String getSpouseID() {
        return spouseID;
    }

    /**
     * Set the person' spouse ID
     */
    public void setSpouseID(String spouseID) {
        this.spouseID = spouseID;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o instanceof Person) {//instanceof is a marco
            Person oPerson = (Person)o;//we can't use it as an event object until we cast it even though it is of the Person type
            return oPerson.getPersonID().equals(getPersonID()) &&
                    oPerson.getAssociatedUsername().equals(getAssociatedUsername()) &&
                    oPerson.getFirstName().equals(getFirstName()) &&
                    oPerson.getLastName().equals(getLastName()) &&
                    oPerson.getGender().equals(getGender()) &&
                    (oPerson.getFatherID() == null ? getFatherID() == null : oPerson.getFatherID().equals(getFatherID())) &&
                    (oPerson.getMotherID() == null ? getMotherID() == null : oPerson.getMotherID().equals(getMotherID())) &&
                    (oPerson.getSpouseID() == null ? getSpouseID() == null : oPerson.getSpouseID().equals(getSpouseID()));
        }
        else {
            return false;
        }
    }

    public String toString() {
        String person;
        return person = "Person ID: " + personID + "\n" + "Associated Username: " + associatedUsername + "\n" +
                "First Name: " +firstName + "\n" + "Last Name: " + lastName + "\n" + "Gender: " + gender + "\n" +
                "Father ID: " + fatherID + "\n" + "Mother ID: " + motherID + "\n" + "Spouse: " + spouseID;
    }
}