package com.example.client;

import android.util.Log;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Model.AuthToken;
import Model.Person;
import Service.Request.LoginRequest;
import Service.Request.RegisterRequest;
import Service.Result.EventsResult;
import Service.Result.LoginResult;
import Service.Result.PersonsResult;
import Service.Result.RegisterResult;
import Service.Result.SinglePersonResult;

public class
DataCacheTest {
    private ServerProxy serverProxy = new ServerProxy();
    private RegisterRequest registerRequest = new RegisterRequest();
    private RegisterResult registerResult = new RegisterResult();
    private PersonsResult personsResult = new PersonsResult();
    private EventsResult eventsResult = new EventsResult();
    private SinglePersonResult singlePersonResult = new SinglePersonResult();
    private final int SIZE_ALL_FEMALE = 45;
    private final int DEFAULT_NUM_EVENTS = 91;
    private final int SIZE_ALL_MALE = 46;
    private final int NUM_EVENTS_FATHER_SIDE = 46;
    private final int NUM_EVENTS_MOTHER_SIDE = 46;
    private final int USER = 1;

    @Before
    public void setUp() {
        DataCache.getInstance().setServerHost("localhost");//the IP address of my laptop:192.168.1.4
        DataCache.getInstance().setServePort("8080");
        registerRequest = new RegisterRequest("Ba7Man", "CaballeroOscuro", "Batman1@wayne.com",
                "Rembrand", "Pardo","M");
        DataCache.getInstance().getAuthToken().setAuthToken(null);
        DataCache.getInstance().getAuthToken().setUsername(null);
    }

    @After
    public void cleanUp(){
        //we clean the server after each test so we can use the same register request
        serverProxy.clearServer();
        //PersonEvent[] cleanArray = new PersonEvent[0];
        DataCache.getInstance().setPersonEvents(null);
        DataCache.getInstance().getFatherSideMales().clear();
        DataCache.getInstance().getFatherSideFemales().clear();
        DataCache.getInstance().getMotherSideMales().clear();
        DataCache.getInstance().getMotherSideFemales().clear();
        DataCache.getInstance().setMaleEventsSwitch(true);
        DataCache.getInstance().setFemaleEventsSwitch(true);
        DataCache.getInstance().setFatherSideSwitch(true);
        DataCache.getInstance().setMotherSideSwitch(true);
    }

    @Test
    public void familyRelationsTest() throws IOException {
        DataCache dataCache = DataCache.getInstance();
        //we register a user
        registerResult = serverProxy.register(registerRequest);
        //we check we registered them correctly
        Assert.assertEquals(true, registerResult.getSuccess());
        dataCache.setPersonID(registerResult.getPersonID());
        //we set the user's person
        singlePersonResult = serverProxy.getPerson(dataCache.getAuthToken());
        //we check we saved person in data cache correctly
        Assert.assertEquals(true, singlePersonResult.getSuccess());
        //we get people and events data
        personsResult = serverProxy.getPeople(dataCache.getAuthToken());
        eventsResult = serverProxy.getEvents(dataCache.getAuthToken());
        Assert.assertEquals(true, personsResult.getSuccess());
        Assert.assertEquals(true, eventsResult.getSuccess());
        //we map people to get family sorted
        dataCache.sortPeople();
        //we get mother and father side. All people by gender
        dataCache.getFatherSide();
        dataCache.getMotherSide();
        //we check that all containers are not empty
        Assert.assertNotEquals(0, dataCache.getFatherSideMales().size());
        Assert.assertNotEquals(0, dataCache.getFatherSideFemales().size());
        Assert.assertNotEquals(0, dataCache.getMotherSideMales().size());
        Assert.assertNotEquals(0, dataCache.getMotherSideFemales().size());
    }

    @Test
    public void familyRelationsTestFail() throws IOException {
        DataCache dataCache = DataCache.getInstance();
        //we register a user
        registerResult = serverProxy.register(registerRequest);
        //we check we registered them correctly
        Assert.assertEquals(true, registerResult.getSuccess());
        dataCache.setPersonID(registerResult.getPersonID());
        //we set the user's person
        singlePersonResult = serverProxy.getPerson(dataCache.getAuthToken());
        //we check we saved person in data cache correctly
        Assert.assertEquals(true, singlePersonResult.getSuccess());
        personsResult = serverProxy.getPeople(dataCache.getAuthToken());
        //we map people to get family sorted
        dataCache.sortPeople();
        //bad setting method for sort people should not populate family relationships
        Assert.assertEquals(0, dataCache.getFatherSideFemales().size());
        Assert.assertEquals(0, dataCache.getMotherSideMales().size());
        Assert.assertEquals(0, dataCache.getMotherSideFemales().size());
    }

    @Test
    public void filterEvents() throws IOException {
        DataCache dataCache = DataCache.getInstance();
        //we set variables to test filters
        registerResult = serverProxy.register(registerRequest);
        Assert.assertEquals(true, registerResult.getSuccess());
        dataCache.setPersonID(registerResult.getPersonID());
        singlePersonResult = serverProxy.getPerson(dataCache.getAuthToken());
        Assert.assertEquals(true, singlePersonResult.getSuccess());
        personsResult = serverProxy.getPeople(dataCache.getAuthToken());
        eventsResult = serverProxy.getEvents(dataCache.getAuthToken());
        dataCache.setMapPersonToEvents();
        Assert.assertEquals(true, personsResult.getSuccess());
        Assert.assertEquals(true, eventsResult.getSuccess());
        //we map people to get family sorted
        dataCache.sortPeople();
        dataCache.getFatherSide();
        dataCache.getMotherSide();
        //we check that all containers are not empty
        Assert.assertNotEquals(0, dataCache.getFatherSideMales().size());
        Assert.assertNotEquals(0, dataCache.getFatherSideFemales().size());
        Assert.assertNotEquals(0, dataCache.getMotherSideMales().size());
        Assert.assertNotEquals(0, dataCache.getMotherSideFemales().size());
        //we check that number of events with no filters is 91 default
        Assert.assertEquals(DEFAULT_NUM_EVENTS, eventsResult.getData().length);
        //we set male filter
        dataCache.setMaleEventsSwitch(false);
        //we confirm that we filtered the array of events
        Assert.assertEquals(SIZE_ALL_FEMALE, dataCache.getPersonEvents().length);
        //we set female filter
        dataCache.setFemaleEventsSwitch(false);
        dataCache.setMaleEventsSwitch(true);
        //we confirm that we filtered the array of events
        Assert.assertEquals(SIZE_ALL_MALE, dataCache.getPersonEvents().length);
        //we filter both female and male
        dataCache.setFemaleEventsSwitch(false);
        dataCache.setMaleEventsSwitch(false);
        //we confirm that the size is 0
        Assert.assertEquals(0, dataCache.getPersonEvents().length);
        //we filter mother size
        dataCache.setMotherSideSwitch(false);
        dataCache.setFemaleEventsSwitch(true);
        dataCache.setMaleEventsSwitch(true);
        //we confirm events were filtered correctly
        Assert.assertEquals(NUM_EVENTS_FATHER_SIDE, dataCache.getPersonEvents().length);
        //we filter father size
        dataCache.setFatherSideSwitch(false);
        dataCache.setMotherSideSwitch(true);
        dataCache.setFemaleEventsSwitch(true);
        dataCache.setMaleEventsSwitch(true);
        //we confirm events were filtered correctly
        Assert.assertEquals(NUM_EVENTS_MOTHER_SIDE, dataCache.getPersonEvents().length);
    }

    @Test
    public void filterEventsFail() throws IOException {
        DataCache dataCache = DataCache.getInstance();
        //we set variables to test filters
        registerResult = serverProxy.register(registerRequest);
        Assert.assertEquals(true, registerResult.getSuccess());
        dataCache.setPersonID(registerResult.getPersonID());
        singlePersonResult = serverProxy.getPerson(dataCache.getAuthToken());
        Assert.assertEquals(true, singlePersonResult.getSuccess());
        personsResult = serverProxy.getPeople(dataCache.getAuthToken());
        eventsResult = serverProxy.getEvents(dataCache.getAuthToken());
        dataCache.setMapPersonToEvents();
        Assert.assertEquals(true, personsResult.getSuccess());
        Assert.assertEquals(true, eventsResult.getSuccess());
        //we map people to get family sorted
        dataCache.sortPeople();
        //we set male filter
        dataCache.setMaleEventsSwitch(false);
        //if filter method does not have data set it should fail and return the size of events to 0
        Assert.assertEquals(0, dataCache.getPersonEvents().length);
        //we set female filter
        dataCache.setFemaleEventsSwitch(false);
        dataCache.setMaleEventsSwitch(true);
        //if filter method does not have data set it should fail and return more than 1(user)
        Assert.assertFalse(USER < dataCache.getPersonEvents().length);
        //we filter mother size
        dataCache.setMotherSideSwitch(false);
        dataCache.setFemaleEventsSwitch(true);
        dataCache.setMaleEventsSwitch(true);
        //if filter method does not have data set it should fail and return more than 1(user)
        Assert.assertFalse(USER < dataCache.getPersonEvents().length);
        //we filter father size
        dataCache.setFatherSideSwitch(false);
        dataCache.setMotherSideSwitch(true);
        dataCache.setFemaleEventsSwitch(true);
        dataCache.setMaleEventsSwitch(true);
        //if filter method does not have data set it should fail and return more than 1(user)
        Assert.assertFalse(USER < dataCache.getPersonEvents().length);
    }

    @Test
    public void orderEvents() throws IOException {
        DataCache dataCache = DataCache.getInstance();
        //we set variables to test order of events
        registerResult = serverProxy.register(registerRequest);
        Assert.assertEquals(true, registerResult.getSuccess());
        dataCache.setPersonID(registerResult.getPersonID());
        singlePersonResult = serverProxy.getPerson(dataCache.getAuthToken());
        Assert.assertEquals(true, singlePersonResult.getSuccess());
        personsResult = serverProxy.getPeople(dataCache.getAuthToken());
        eventsResult = serverProxy.getEvents(dataCache.getAuthToken());
        dataCache.setMapPersonToEvents();
        Assert.assertEquals(true, personsResult.getSuccess());
        Assert.assertEquals(true, eventsResult.getSuccess());
        dataCache.sortPeople();
        dataCache.getFatherSide();
        dataCache.getMotherSide();
        //we get the events of user
        ArrayList<PersonEvent> userEvents = dataCache.getMapPersonToEvents().get(dataCache.getPersonID());
        //we reorder the events in chronological order
        userEvents = dataCache.sortEvents(userEvents);
        //we test that the order of events is in chronological order
        for(int i = 0; i < userEvents.size(); ++i){
            if(i != userEvents.size() -1){
                int current = userEvents.get(i).getYear();
                int next = userEvents.get(i + 1).getYear();
                Assert.assertTrue(current < next);
            }
        }
    }

    @Test
    public void orderEventsFail() throws IOException {
        DataCache dataCache = DataCache.getInstance();
        //we set variables to test order of events
        registerResult = serverProxy.register(registerRequest);
        Assert.assertEquals(true, registerResult.getSuccess());
        dataCache.setPersonID(registerResult.getPersonID());
        singlePersonResult = serverProxy.getPerson(dataCache.getAuthToken());
        Assert.assertEquals(true, singlePersonResult.getSuccess());
        personsResult = serverProxy.getPeople(dataCache.getAuthToken());
        eventsResult = serverProxy.getEvents(dataCache.getAuthToken());
        dataCache.setMapPersonToEvents();
        Assert.assertEquals(true, personsResult.getSuccess());
        Assert.assertEquals(true, eventsResult.getSuccess());
        dataCache.sortPeople();
        dataCache.getFatherSide();
        dataCache.getMotherSide();
        //we get the events of user
        ArrayList<PersonEvent> userEvents = new ArrayList<>();
        ArrayList<PersonEvent> orderEvents = new ArrayList<>();
        //we reorder the events in chronological order
        orderEvents = dataCache.sortEvents(userEvents);
        //we confirm that sorting event failed
        Assert.assertEquals(0, orderEvents.size());
    }


    @Test
    public void searchesTest() throws IOException {
        DataCache dataCache = DataCache.getInstance();
        //we set variables to test searches
        registerResult = serverProxy.register(registerRequest);
        Assert.assertEquals(true, registerResult.getSuccess());
        dataCache.setPersonID(registerResult.getPersonID());
        singlePersonResult = serverProxy.getPerson(dataCache.getAuthToken());
        Assert.assertEquals(true, singlePersonResult.getSuccess());
        personsResult = serverProxy.getPeople(dataCache.getAuthToken());
        eventsResult = serverProxy.getEvents(dataCache.getAuthToken());
        dataCache.setMapPersonToEvents();
        dataCache.getEventPerson();
        Assert.assertEquals(true, personsResult.getSuccess());
        Assert.assertEquals(true, eventsResult.getSuccess());
        dataCache.sortPeople();
        dataCache.getFatherSide();
        dataCache.getMotherSide();
        //we create a holder for the persons returned from search
        List<Person> persons = new ArrayList<>();
        //we use part of the user's name as the string that will be searched in all people
        persons = dataCache.filterPeople("Rembra");
        //we check that persons size is not 0
        Assert.assertNotEquals(0, persons.size());
        //we check that the person inside the list has the name of user
        Assert.assertEquals("Rembrand", persons.get(0).getFirstName());
        //we create a holder for the events returned from search
        List<PersonEvent> events = new ArrayList<>();
        //we use part of the user's name as the string that will be searched in all events
        events = dataCache.filterEvent("Rembra");
        //we check that events size is not 0
        Assert.assertNotEquals(0, events.size());
        //we check that event actually belongs to the user typed
        Assert.assertEquals("Rembrand", events.get(0).getFirstName());
    }

    @Test
    public void searchesTestFail() throws IOException {
        DataCache dataCache = DataCache.getInstance();
        //we set variables to test searches
        registerResult = serverProxy.register(registerRequest);
        Assert.assertEquals(true, registerResult.getSuccess());
        dataCache.setPersonID(registerResult.getPersonID());
        singlePersonResult = serverProxy.getPerson(dataCache.getAuthToken());
        Assert.assertEquals(true, singlePersonResult.getSuccess());
        personsResult = serverProxy.getPeople(dataCache.getAuthToken());
        eventsResult = serverProxy.getEvents(dataCache.getAuthToken());
        dataCache.setMapPersonToEvents();
        dataCache.getEventPerson();
        Assert.assertEquals(true, personsResult.getSuccess());
        Assert.assertEquals(true, eventsResult.getSuccess());
        dataCache.sortPeople();
        dataCache.getFatherSide();
        dataCache.getMotherSide();
        //we create a holder for the persons returned from search
        List<Person> persons = new ArrayList<>();
        //we first test the empty string
        persons = dataCache.filterPeople("");
        //we check that the search person method returned null
        Assert.assertNull(persons);
        //we test the method with a string that we know it won't work
        persons = dataCache.filterPeople("@#$*&^%$&*@(");
        //we check that the search person failed
        Assert.assertNull(persons);
        //we create a holder for the events returned from search
        List<PersonEvent> events = new ArrayList<>();
        //we first test the empty string
        events = dataCache.filterEvent("");
        //we check that events is null
        Assert.assertNull(events);
        //we test the method with a string that we know it won't work
        events = dataCache.filterEvent("@#$*&^%$&*@(");
        //we check that the search event failed
        Assert.assertNull(events);
    }
}
