package com.example.client;

import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import Model.AuthToken;
import Model.Event;
import Model.Person;
import Service.Result.ClearResult;

/*
This class is a SingletonPattern.
With Singleton design pattern we can always ensure that there will only ever be one instance of it
and that one instance can be accessed from anywhere, any of the activities(screens).
*/
public class DataCache {
    private static DataCache instance;//we create an instance that will keep "track" of DataCache

    //server port and server
    private String servePort;
    private String serverHost;

    private AuthToken authToken = new AuthToken();

    //user's person
    private Person userPerson = new Person();
    //user's personID
    private String personID;

    //user's first name
    private String userFirstName;
    private String userLastName;

    //user's persons array
    private Person[] persons;
    //user's events array
    private Event[] events;

    //array that contains events information with the person's information
    private PersonEvent[] personEvents;

    //event's by gender
    private PersonEvent[] malePersonEvent;
    private PersonEvent[] femalePersonEvent;

    //ancestors all were final
    private Set<Person> fatherSideMales = new HashSet<>();
    private Set<Person> fatherSideFemales = new HashSet<>();
    private Set<Person> fatherSide = new HashSet<>();
    private Set<Person> motherSideMales = new HashSet<>();
    private Set<Person> motherSideFemales = new HashSet<>();
    private Set<Person> motherSide = new HashSet<>();

    //set of types of events
    private Map<String, Float> mapEventColors = new HashMap<>();
    private Set<String> eventType = new HashSet<>();

    //a structure that maps a personID to all the events of that person
    private Map<String, ArrayList<PersonEvent>> mapPersonToEvents = new HashMap<String, ArrayList<PersonEvent>>();

    //a structure that maps an eventId to a PersonEvent
    private Map<String, PersonEvent> mapEventIdToEvent = new HashMap<>();

    //a structure that maps a personId to a Person object
    private Map<String, Person> mapPeople = new HashMap<>();


    /*
    Our constructor is private to PREVENT the CREATION of an DataCache instance other than in this
    class where we only create a SINGLE instance.
    */
    private DataCache() {}

    //we create this method so we can have ACCESS to the single instance anywhere we desire so
    public static DataCache getInstance(){
        //we check if the instance is null. If it is we initiate it and return it.
        if (instance == null) {
            instance = new DataCache();
        }
        /*
        If it is not null we return the instance created so every time we call geInstance we will
        get the SAME object
        */
        return instance;
    }

    public Person getUserPerson() { return userPerson; }
    public void setUserPerson(Person userPerson) { this.userPerson = userPerson; }
    public String getUserFirstName() { return userFirstName; }
    public void setUserFirstName(String userFirstName) { this.userFirstName = userFirstName; }
    public String getUserLastName() { return userLastName; }
    public void setUserLastName(String userLastName) { this.userLastName = userLastName; }

    public String getPersonID() { return personID; }
    public void setPersonID(String personID) { this.personID = personID; }

    public AuthToken getAuthToken() { return authToken; }
    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getServePort() { return servePort; }
    public void setServePort(String servePort) { this.servePort = servePort; }

    public String getServerHost() { return serverHost; }
    public void setServerHost(String serverHost) { this.serverHost = serverHost; }

    public Person[] getPersons() { return persons; }
    public void setPersons(Person[] persons) {
        this.persons = persons;
        sortPeople();//we call method to create mapPeople
    }

    public Event[] getEvents() { return events; }
    public void setEvents(Event[] events) {
        this.events = events;
        sortColor();//we call method to create map eventColors
    }

    //variables for lines in settings
    private boolean lifeStorySwitch = true;
    private boolean familyTreeSwitch = true;
    private boolean spouseLines = true;
    public boolean isLifeStorySwitch() { return lifeStorySwitch; }
    public void setLifeStorySwitch(boolean lifeStorySwitch) { this.lifeStorySwitch = lifeStorySwitch; }
    public boolean isFamilyTreeSwitch() { return familyTreeSwitch; }
    public void setFamilyTreeSwitch(boolean familyTreeSwitch) { this.familyTreeSwitch = familyTreeSwitch; }
    public boolean isSpouseLines() { return spouseLines; }
    public void setSpouseLines(boolean spouseLines) { this.spouseLines = spouseLines;  }
    //variable for filters in settings
    private boolean fatherSideSwitch = true;
    public boolean isFatherSideSwitch() { return fatherSideSwitch; }
    public void setFatherSideSwitch(boolean fatherSideSwitch) { this.fatherSideSwitch = fatherSideSwitch; }
    private boolean motherSideSwitch = true;
    public boolean isMotherSideSwitch() { return motherSideSwitch; }
    public void setMotherSideSwitch(boolean motherSideSwitch) { this.motherSideSwitch = motherSideSwitch; }
    private boolean femaleEventsSwitch = true;
    public boolean isFemaleEventsSwitch() { return femaleEventsSwitch; }
    public void setFemaleEventsSwitch(boolean femaleEventsSwitch) { this.femaleEventsSwitch = femaleEventsSwitch; }
    private boolean maleEventsSwitch = true;
    public boolean isMaleEventsSwitch() { return maleEventsSwitch; }
    public void setMaleEventsSwitch(boolean maleEventsSwitch) { this.maleEventsSwitch = maleEventsSwitch; }
    public PersonEvent[] getPersonEvents() {
        List<PersonEvent> personEventList = new ArrayList<>();
        if (fatherSideSwitch && motherSideSwitch && femaleEventsSwitch && maleEventsSwitch) {
            return personEvents;//if all are true that means that all the filters are on
        }
        else {
            if(fatherSideSwitch) {//we add both male and female of father side depending on switches
                if(maleEventsSwitch) {
                    for(Person person : fatherSideMales){
                        //we get the array list of events of the person
                        ArrayList<PersonEvent> fatherSideMalesEvents = mapPersonToEvents.get(person.getPersonID());
                        /*
                        This method appends all of the elements in the specified collection to the end of this list,
                        in the order that they are returned by the specified collection’s iterator.
                        */
                        personEventList.addAll(fatherSideMalesEvents);
                    }
                }
                if(femaleEventsSwitch) {
                    for(Person person : fatherSideFemales) {
                        ArrayList<PersonEvent> fatherSideFemalesEvents = mapPersonToEvents.get(person.getPersonID());
                        personEventList.addAll(fatherSideFemalesEvents);
                    }
                }
            }
            if(motherSideSwitch) {//we add both male and female of mother side depending on switches
                if(maleEventsSwitch) {
                    for(Person person : motherSideMales) {
                        ArrayList<PersonEvent> motherSideMalesEvents = mapPersonToEvents.get(person.getPersonID());
                        personEventList.addAll(motherSideMalesEvents);
                    }
                }
                if(femaleEventsSwitch) {
                    for(Person person : motherSideFemales) {
                        ArrayList<PersonEvent> motherSideFemalesEvents = mapPersonToEvents.get(person.getPersonID());
                        personEventList.addAll(motherSideFemalesEvents);
                    }
                }
            }
            if(maleEventsSwitch) {//we add events of male in both mother and father side
               if (userPerson.getGender().equals("M")) {
                   ArrayList<PersonEvent> userEvents = mapPersonToEvents.get(userPerson.getPersonID());
                   personEventList.addAll(userEvents);
               }
            }
            if (femaleEventsSwitch) {//we add events of female in both mother and father side
                if (userPerson.getGender().equals("F")) {
                    ArrayList<PersonEvent> userEvents = mapPersonToEvents.get(userPerson.getPersonID());
                    personEventList.addAll(userEvents);
                }
            }
            PersonEvent[] filteredPersonEvent = new PersonEvent[personEventList.size()];
            filteredPersonEvent = personEventList.toArray(filteredPersonEvent);
            return filteredPersonEvent;
        }
    }
    public void setPersonEvents(PersonEvent[] personEvents) { this.personEvents = personEvents; }

    public Map<String, Float> getMapEventColors() { return mapEventColors; }

    public Map<String, PersonEvent> getMapEventIdToEvent() { return mapEventIdToEvent; }

    public Map<String, ArrayList<PersonEvent>> getMapPersonToEvents() { return mapPersonToEvents; }

    public Map<String, Person> getMapPeople() { return mapPeople; }

    public Set<Person> getFatherSideMales() { return fatherSideMales; }

    public void setFatherSideMales(Set<Person> persons) { this.fatherSideMales = persons; }

    public Set<Person> getFatherSideFemales() { return fatherSideFemales; }

    public void setFatherSideFemales (Set<Person> persons) { this.fatherSideFemales = persons; }

    public Set<Person> getMotherSideMales() { return motherSideMales; }

    public void setMotherSideMales(Set<Person> persons) { this.motherSideMales = persons; }

    public Set<Person> getMotherSideFemales() { return motherSideFemales; }

    public void setMotherSideFemales(Set<Person> persons) { this.motherSideFemales = persons; }

    public boolean getEventPerson() {
        PersonEvent[] personEventsArray = createEventPerson();
        if (personEventsArray.length != 0 && personEventsArray != null) {
            setPersonEvents(personEventsArray);
            setMapEventID();
            return true;
        }
        else { return false; }
    }
    public PersonEvent[] createEventPerson() {
        ArrayList<PersonEvent> personEventsList = new ArrayList<>();
        PersonEvent personEvent;
        if (events != null && persons != null){
            for (int i = 0; i < events.length; ++i){
                personEvent = new PersonEvent();
                personEvent.setEventID(events[i].getEventID());
                personEvent.setUsername(events[i].getUsername());
                personEvent.setPersonID(events[i].getPersonID());
                personEvent.setLatitude(events[i].getLatitude());
                personEvent.setLongitude(events[i].getLongitude());
                personEvent.setCountry(events[i].getCountry());
                personEvent.setCity(events[i].getCity());
                personEvent.setEventType(events[i].getEventType());
                personEvent.setYear(events[i].getYear());

                for (int j = 0; j < persons.length; ++j) {
                    if (events[i].getPersonID().equals(persons[j].getPersonID())) {
                        personEvent.setFirstName(persons[j].getFirstName());
                        personEvent.setLastName(persons[j].getLastName());
                        personEvent.setGender(persons[j].getGender());
                        personEventsList.add(personEvent);
                        break;
                    }
                }
            }
            PersonEvent[] personEventsArray = new PersonEvent[personEventsList.size()];
            personEventsList.toArray(personEventsArray);
            return personEventsArray;
        }
        else { return null; }
    }

    private void setMapEventID() {
        for (int i = 0; i < personEvents.length; ++i) {
            mapEventIdToEvent.put(personEvents[i].getEventID(), personEvents[i]);
        }
    }

    //method that gets an ArrayList of PersonEvent and sorts the event's year in ascending order
    public ArrayList<PersonEvent> sortEvents(ArrayList<PersonEvent> events){
        if(events != null) {
            for (int i = 0; i < events.size(); ++i) {
                for (int j = i + 1; j < events.size(); ++j) {
                    PersonEvent personEvent = new PersonEvent();
                    if (events.get(i).getYear() > events.get(j).getYear()) {//we swap events
                        personEvent = events.get(i);
                        events.set(i, events.get(j));//we replace the event
                        events.set(j, personEvent);
                    }
                }
            }
            return events;
        }
        else {
            events = new ArrayList<>();
            return events;
        }
    }

    public void getEventsByGender() {
        femalePersonEvent = setFemaleEvents();
        malePersonEvent = setMaleEvents();
    }
    private PersonEvent[] setFemaleEvents() {
        ArrayList<PersonEvent> femaleEventList = new ArrayList<>();
        PersonEvent femaleEvent;
        for(int i = 0; i < events.length; ++i) {
            for(int j = 0; j < persons.length; ++j) {
                if (events[i].getPersonID().equals(persons[j].getPersonID())) {
                    if (persons[j].getGender().equals("F")) {
                        femaleEvent = new PersonEvent();
                        femaleEvent.setEventID(events[i].getEventID());
                        femaleEvent.setUsername(events[i].getUsername());
                        femaleEvent.setUsername(events[i].getPersonID());
                        femaleEvent.setLatitude(events[i].getLatitude());
                        femaleEvent.setLongitude(events[i].getLongitude());
                        femaleEvent.setCountry(events[i].getCountry());
                        femaleEvent.setCity(events[i].getCity());
                        femaleEvent.setEventID(events[i].getEventID());
                        femaleEvent.setYear(events[i].getYear());
                        femaleEvent.setFirstName(persons[j].getFirstName());
                        femaleEvent.setLastName(persons[j].getLastName());
                        femaleEventList.add(femaleEvent);
                        break;
                    }
                }
            }
        }
        PersonEvent[] femaleEvents = new PersonEvent[femaleEventList.size()];
        femaleEventList.toArray(femaleEvents);
        return femaleEvents;
    }
    private PersonEvent[] setMaleEvents() {
        ArrayList<PersonEvent> maleEventList = new ArrayList<>();
        PersonEvent maleEvent;
        for(int i = 0; i < events.length; ++i) {
            for (int j = 0; j < persons.length; ++j) {
                if (events[i].getPersonID().equals(persons[j].getPersonID())) {
                    if (persons[j].getGender().equals("M")) {
                        maleEvent = new PersonEvent();
                        maleEvent.setEventID(events[i].getEventID());
                        maleEvent.setUsername(events[i].getUsername());
                        maleEvent.setUsername(events[i].getPersonID());
                        maleEvent.setLatitude(events[i].getLatitude());
                        maleEvent.setLongitude(events[i].getLongitude());
                        maleEvent.setCountry(events[i].getCountry());
                        maleEvent.setCity(events[i].getCity());
                        maleEvent.setEventID(events[i].getEventID());
                        maleEvent.setYear(events[i].getYear());
                        maleEvent.setFirstName(persons[j].getFirstName());
                        maleEvent.setLastName(persons[j].getLastName());
                        maleEventList.add(maleEvent);
                        break;
                    }
                }
            }
        }
        PersonEvent[] maleEvents = new PersonEvent[maleEventList.size()];
        maleEventList.toArray(maleEvents);
        return maleEvents;
    }

    //a method that maps a personID to all the events of that person
    public void setMapPersonToEvents() {
        ArrayList<PersonEvent> eventsOfPerson;
        for(int i = 0; i < persons.length; ++i) {
            eventsOfPerson = new ArrayList<>();
            for(int j = 0; j < events.length; ++j) {
                if(persons[i].getPersonID().equals(events[j].getPersonID())) {
                    PersonEvent personEvent = new PersonEvent();
                    personEvent.setEventID(events[j].getEventID());
                    personEvent.setUsername(events[j].getUsername());
                    personEvent.setPersonID(events[j].getPersonID());
                    personEvent.setLatitude(events[j].getLatitude());
                    personEvent.setLongitude(events[j].getLongitude());
                    personEvent.setCountry(events[j].getCountry());
                    personEvent.setCity(events[j].getCity());
                    personEvent.setEventType(events[j].getEventType());
                    personEvent.setYear(events[j].getYear());
                    personEvent.setFirstName(persons[i].getFirstName());
                    personEvent.setLastName(persons[i].getLastName());
                    personEvent.setGender(persons[i].getGender());
                    eventsOfPerson.add(personEvent);
                }
            }
            mapPersonToEvents.put(persons[i].getPersonID(), eventsOfPerson);
        }
    }

    //map eventType to color for the markers in map
    private void sortColor() {
        for(int i = 0; i < events.length; ++i) {
            eventType.add(events[i].getEventType());
        }
        SetEventColor();
    }

    private void SetEventColor() {
        Random random = new Random();
        float colorNumber = 37;
        for(String type : eventType){
            colorNumber = (colorNumber + 127) % 360;
            float color = (float)colorNumber;
            mapEventColors.put(type, color);
        }
        //we make the the keys case insensitive
        for(String eventType : mapEventColors.keySet()){
            float color = mapEventColors.get(eventType);
            if(mapEventColors.containsKey(eventType.toLowerCase())){
                String lowercase = eventType.toLowerCase();
                mapEventColors.put(lowercase, color);
            }
        }
    }

    //map personID to person
    public void sortPeople() {
        for (int i = 0; i < persons.length; ++i) {
            mapPeople.put(persons[i].getPersonID(), persons[i]);
        }
    }
    //methods to organize by side of family using mapPeople. Father's side
    public void getFatherSide(){
        if (userPerson.getFatherID() != null) {
            addFatherSide(mapPeople.get(userPerson.getFatherID()));
        }
    }
    private void addFatherSide(Person person) {
        //depending on the gender we add to fatherSideMales or fatherSideFemales
        if (person.getGender().equals("M")) {
            fatherSideMales.add(person);
        }
        if(person.getGender().equals("F")) {
            fatherSideFemales.add(person);
        }
        //we check that person has parents
        if (person.getFatherID() != null) {
            Person father = (Person)mapPeople.get(person.getFatherID());// value = map.get(key)
            addFatherSide(father);//recursive call
            fatherSide.add(father);
        }
        if (person.getMotherID() != null) {
            Person mother = (Person)mapPeople.get(person.getMotherID());
            addFatherSide(mother);
            fatherSide.add(mother);
        }
    }
    //methods to organize by side of family using mapPeople. Mother's side
    public void getMotherSide() {
        if (userPerson.getMotherID() != null) {
            addMotherSide(mapPeople.get(userPerson.getMotherID()));
        }
    }
    private void addMotherSide(Person person){
        //depending on the gender we add to a specific group
        if (person.getGender().equals("M")){ motherSideMales.add(person); }
        if (person.getGender().equals("F")) { motherSideFemales.add(person); }
        //we check that person has parents
        if (person.getFatherID() != null) {
            Person father = (Person)mapPeople.get(person.getFatherID());//value = map.get(key)
            addMotherSide(father);//recursive call
            motherSide.add(father);
        }
        if (person.getMotherID() != null) {
            Person mother = (Person)mapPeople.get(person.getMotherID());
            addMotherSide(mother);
            motherSide.add(mother);
        }
    }

    public boolean findEvent(PersonEvent event) {
        PersonEvent[] filteredEvents = getPersonEvents();
        if (filteredEvents != null) {
            for (int i = 0; i < filteredEvents.length; ++i) {
                if (event.getEventID().equals(filteredEvents[i].getEventID())) {
                    return true;
                }
            }
        }
        return false;
    }

    public ArrayList<Person> findChildren(Person parent) {
        ArrayList<Person> children = new ArrayList<>();
        if (parent.getGender().equals("M")){
            for(int i = 0; i < persons.length; ++i){
                if(parent.getPersonID().equals(persons[i].getFatherID())){
                    children.add(persons[i]);
                }
            }
        }
        if(parent.getGender().equals("F")){
            for(int i = 0; i < persons.length; ++i) {
                if (parent.getPersonID().equals(persons[i].getMotherID())) {
                    children.add(persons[i]);
                }
            }
        }
        return children;
    }

    public boolean clearServer() {
        ServerProxy serverProxy = new ServerProxy();
        ClearResult result = new ClearResult();
        result = serverProxy.clearServer();
        if (result.getSuccess()) { return true; }
        else { return false; }
    }


    //filtering user inputs
    public List<Person> filterPeople(String stringTyped) {
        /*
        We convert the string to lower case, using toLowerCase using the rules defined by the
        specified Locale.
        */
        String stringText = stringTyped.toLowerCase(Locale.getDefault());
        List<Person> personsFiltered = new ArrayList<>();

        if (stringText.length() == 0) { return null; }
        for (Person person : persons) {
            if (person.getFirstName().toLowerCase(Locale.getDefault()).contains(stringText) ||
                    person.getLastName().toLowerCase(Locale.getDefault()).contains(stringText)) {
                personsFiltered.add(person);
            }
        }
        if (personsFiltered.isEmpty()) { return null; }
        else { return personsFiltered; }
    }
    public List<PersonEvent> filterEvent(String stringTyped) {
        String stringText = stringTyped.toLowerCase(Locale.getDefault());
        List<PersonEvent> personEventsFiltered = new ArrayList<>();

        if (stringText.length() == 0) { return null; }
        for (PersonEvent personEvent : personEvents) {
            if (personEvent.getUsername().toLowerCase(Locale.getDefault()).contains(stringText) ||
                    personEvent.getEventID().toLowerCase(Locale.getDefault()).contains(stringText) ||
                    personEvent.getCity().toLowerCase(Locale.getDefault()).contains(stringText) ||
                    personEvent.getCountry().toLowerCase(Locale.getDefault()).contains(stringText) ||
                    personEvent.getFirstName().toLowerCase(Locale.getDefault()).contains(stringText) ||
                    personEvent.getLastName().toLowerCase(Locale.getDefault()).contains(stringText) ||
                    Integer.toString(personEvent.getYear()).contains(stringText)) {
                personEventsFiltered.add(personEvent);
            }
        }
        if (personEventsFiltered.isEmpty()) { return null; }
        else { return personEventsFiltered; }
    }

}
