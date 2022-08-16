package Model;//This package contains the classes to represent the tables in the database


/*this is a Model class to represent the Events table in the database for the family server*/
public class Event {
    private String eventID;
    private String associatedUsername;
    private String personID;
    private float latitude;
    private float longitude;
    private String country;
    private String city;
    private String eventType;
    private int year;

    //default constructor
    public Event() {}
    //parameterized constructor
    public Event(String eventID, String associatedUsername, String personID, float latitude,
                 float longitude, String country, String city, String eventType, int year) {
        this.eventID = eventID;
        this.associatedUsername = associatedUsername;
        this.personID = personID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
    }

    /**
     * Get the Event ID
     */
    public String getEventID() {
        return eventID;
    }

    /**
     * Set the Event ID
     */
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    /**
     * Get the user's name
     */
    public String getUsername() {//associated username???
        return associatedUsername;
    }

    /**
     * Set the user's name
     */
    public void setUsername(String associatedUsername) {//associated username???
        this.associatedUsername = associatedUsername;
    }

    /**
     * Get the Person's ID
     */
    public String getPersonID() {
        return personID;
    }

    /**
     * Set the Person's ID
     */
    public void setPersonID(String personID) {
        this.personID = personID;
    }

    /**
     * Get the Latitude
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * Set the Latitude
     */
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    /**
     * Get the Longitude
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * Set the Longitude
     */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    /**
     * Get the Country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Set the Country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Get the City
     */
    public String getCity() {
        return city;
    }

    /**
     * Set the City
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Get Event Type
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Set the Event Type
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * Get the year
     */
    public int getYear() {
        return year;
    }

    /**
     * Set the year
     */
    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o instanceof Event) {//instanceof is a marco
            Event oEvent = (Event)o;//we can't use it as an event object until we cast it even though it is of the Event type
            return oEvent.getEventID().equals(getEventID()) &&
                    oEvent.getUsername().equals(getUsername()) &&
                    oEvent.getPersonID().equals(getPersonID()) &&
                    oEvent.getLatitude() == (getLatitude()) && //notice we use == instead of equals() for the integer values
                    oEvent.getLongitude() == (getLongitude()) &&
                    oEvent.getCountry().equals(getCountry()) &&
                    oEvent.getCity().equals(getCity()) &&
                    oEvent.getEventType().equals(getEventType()) &&
                    oEvent.getYear() == (getYear());
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        return  "EventID: " + eventID + "\n" +
                "Associated Username: " + associatedUsername +"\n" +
                "Person ID: " + personID + "\n" +
                "Latitude: " + latitude + "\n" +
                "Longitude: " + longitude + "\n" +
                "Country: " + country + "\n" +
                "City: " + city + "\n" +
                "EventType: " + eventType + "\n" +
                "Year: " + year;
    }
}