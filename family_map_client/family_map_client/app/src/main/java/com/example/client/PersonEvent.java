package com.example.client;

import android.util.EventLog;

import Model.Event;

public class PersonEvent extends Event {
    private String firstName;
    private String lastName;
    private String gender;

    //default constructor
    public PersonEvent() {}

    //constructor if we have all the information
    public PersonEvent(String eventID, String associatedUsername, String firstName,
                       String lastName, String personID, float latitude, float longitude,
                       String country, String city, String eventType, int year, String gender) {
        super(eventID, associatedUsername, personID, latitude, longitude, country, city, eventType, year);
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    //constructor if we don't have all information
    public PersonEvent(String eventID, String associatedUsername, String personID, float latitude,
                       float longitude, String country, String city, String eventType, int year) {
        super(eventID, associatedUsername, personID, latitude, longitude, country, city, eventType, year);
    }

    //subclass methods
    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() { return gender; }

    public void setGender(String gender) { this.gender = gender; }
}

