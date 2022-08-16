package com.example.client;

import android.util.Log;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import Model.AuthToken;
import Service.Request.LoginRequest;
import Service.Request.RegisterRequest;
import Service.Result.ClearResult;
import Service.Result.EventsResult;
import Service.Result.LoginResult;
import Service.Result.PersonsResult;
import Service.Result.RegisterResult;

public class ServerProxyTest {
    private ServerProxy serverProxy = new ServerProxy();
    private LoginRequest loginRequest = new LoginRequest();
    private LoginResult loginResult = new LoginResult();
    private RegisterRequest registerRequest = new RegisterRequest();
    private RegisterResult registerResult = new RegisterResult();
    private AuthToken authToken = new AuthToken();
    private PersonsResult personsResult = new PersonsResult();
    private EventsResult eventsResult = new EventsResult();


    @Before
    public void setUp() {
        DataCache.getInstance().setServerHost("localhost");//the IP address of my laptop:192.168.1.4
        DataCache.getInstance().setServePort("8080");
        loginRequest = new LoginRequest("Batman", "Dark777");
        registerRequest = new RegisterRequest("Ba7Man", "CaballeroOscuro", "Batman1@wayne.com",
                "Rembrand", "Pardo","M");
        DataCache.getInstance().getAuthToken().setAuthToken(null);
        DataCache.getInstance().getAuthToken().setUsername(null);
    }

    @After
    public void cleanServer(){
        //we clean the server after each test so we can use the same register request
        serverProxy.clearServer();
    }

    @Test
    public void testRegister() {
        DataCache dataCache = DataCache.getInstance();
        //we check that the authtoken and username of our AuthToken object in our data cache are clean
        Assert.assertNull(dataCache.getAuthToken().getAuthToken());
        Assert.assertNull(dataCache.getAuthToken().getUsername());
        registerResult = serverProxy.register(registerRequest);
        //we check that we have an AuthToken for the user just registered
        Assert.assertNotNull(dataCache.getAuthToken().getAuthToken());
        //we check that the user name in AuthToken is not null either
        Assert.assertNotNull(dataCache.getAuthToken().getUsername());
        //we confirm that the we have true for the success boolean variable
        Assert.assertEquals(true, registerResult.getSuccess());

        //Log.i("TEST", registerResult.getAuthToken());
        //we check that the username the same as the one that was just registered
        Assert.assertEquals("Ba7Man", registerResult.getUsername());
    }

    @Test
    public void testRegisterFail() {
        DataCache dataCache = DataCache.getInstance();
        //we check that the authtoken and username of our AuthToken object in our data cache are clean
        Assert.assertNull(dataCache.getAuthToken().getAuthToken());
        Assert.assertNull(dataCache.getAuthToken().getUsername());
        registerResult = serverProxy.register(registerRequest);
        //we confirm that the we have true for the success boolean variable
        Assert.assertEquals(true, registerResult.getSuccess());
        //we try to register the same user again. We should not be able to
        registerResult = serverProxy.register(registerRequest);
        //we confirm we that registering was not successful
        Assert.assertEquals(false, registerResult.getSuccess());
    }

    @Test
    public void testLogin() {
        DataCache dataCache = DataCache.getInstance();
        //we check that the authtoken and username of our AuthToken object in our data cache are clean
        Assert.assertNull(dataCache.getAuthToken().getAuthToken());
        Assert.assertNull(dataCache.getAuthToken().getUsername());
        //we register the user before logging in
        RegisterRequest request = new RegisterRequest("Batman", "Dark777",
                "Batman1@wayne.com","Rembrand", "Pardo","M");
        //we register the user
        serverProxy.register(request);
        //we clear tha AuthToken because it got populated with the registration
        dataCache.getAuthToken().setAuthToken(null);
        dataCache.getAuthToken().setUsername(null);
        //we check that the authtoken and username of our AuthToken object in our data cache are clean
        Assert.assertNull(dataCache.getAuthToken().getAuthToken());
        Assert.assertNull(dataCache.getAuthToken().getUsername());
        //we log in the user again
        loginResult = serverProxy.login(loginRequest);
        //we confirm that the we have true for the success boolean variable
        Assert.assertEquals(true, loginResult.getSuccess());
        //we check that the username the same as the one that was just registered
        Assert.assertEquals("Batman", loginResult.getUsername());
    }

    @Test
    public void testLoginFail() {
        DataCache dataCache = DataCache.getInstance();
        //we check that the authtoken and username of our AuthToken object in our data cache are clean
        Assert.assertNull(dataCache.getAuthToken().getAuthToken());
        Assert.assertNull(dataCache.getAuthToken().getUsername());
        //we register the user before logging in
        RegisterRequest request = new RegisterRequest("Batman", "Dark777",
                "Batman1@wayne.com","Rembrand", "Pardo","M");
        //we register the user
        serverProxy.register(request);
        //we clear tha AuthToken because it got populated with the registration
        dataCache.getAuthToken().setAuthToken(null);
        dataCache.getAuthToken().setUsername(null);
        //we check that the authtoken and username of our AuthToken object in our data cache are clean
        Assert.assertNull(dataCache.getAuthToken().getAuthToken());
        Assert.assertNull(dataCache.getAuthToken().getUsername());
        //we try to login a user that is not register. We should not be able to do that
        LoginRequest wrongLogin = new LoginRequest("Robin", "NightWing");
        loginResult = serverProxy.login(wrongLogin);
        //we confirm that the we have true for the success boolean variable
        Assert.assertEquals(false, loginResult.getSuccess());
    }

    @Test
    public void testGetPeople() throws IOException {
        DataCache dataCache = DataCache.getInstance();
        //we check that the authtoken and username of our AuthToken object in our data cache are clean
        Assert.assertNull(dataCache.getAuthToken().getAuthToken());
        Assert.assertNull(dataCache.getAuthToken().getUsername());
        registerResult = serverProxy.register(registerRequest);
        //we confirm that the we have true for the success boolean variable
        Assert.assertEquals(true, registerResult.getSuccess());
        //we use the authtoken object that we got from a successful registration
        personsResult = serverProxy.getPeople(dataCache.getAuthToken());
        //we check that we got a successful task
        Assert.assertEquals(true, personsResult.getSuccess());
        //we check that the array with data is not null
        Assert.assertNotNull(personsResult.getData());
        //we  make sure that the number of people inside the result is the correct one
        Assert.assertEquals(31, personsResult.getData().length);

    }

    @Test
    public void testGetPeopleFail() throws IOException {
        DataCache dataCache = DataCache.getInstance();
        //we check that the authtoken and username of our AuthToken object in our data cache are clean
        Assert.assertNull(dataCache.getAuthToken().getAuthToken());
        Assert.assertNull(dataCache.getAuthToken().getUsername());
        registerResult = serverProxy.register(registerRequest);
        //we confirm that the we have true for the success boolean variable
        Assert.assertEquals(true, registerResult.getSuccess());
        //we create an AuthToken object that has the wrong information so we should fail the request
        AuthToken wrongAuthToken = new AuthToken("thisisthewrongauthtoken", "wrong user");
        personsResult = serverProxy.getPeople(wrongAuthToken);
        //we check that we got a successful task
        Assert.assertEquals(false, personsResult.getSuccess());
    }

    @Test
    public void testGetEvents() {
        DataCache dataCache = DataCache.getInstance();
        //we check that the authtoken and username of our AuthToken object in our data cache are clean
        Assert.assertNull(dataCache.getAuthToken().getAuthToken());
        Assert.assertNull(dataCache.getAuthToken().getUsername());
        registerResult = serverProxy.register(registerRequest);
        //we confirm that the we have true for the success boolean variable
        Assert.assertEquals(true, registerResult.getSuccess());
        //we create an AuthToken object to pass it to the getEvents method
        AuthToken authToken = new AuthToken();
        authToken = dataCache.getAuthToken();
        //we use the authtoken object that we got from a successful registration
        eventsResult = serverProxy.getEvents(authToken);
        //we check that we had a successful boolean
        Assert.assertEquals(true, eventsResult.getSuccess());
        //we//we check that the array with data is not null
        Assert.assertNotNull(eventsResult.getData());
        //we make sure that the number of people inside the result is the correct one
        Assert.assertEquals(91, eventsResult.getData().length);
    }

    @Test
    public void testGetEventsFail() {
        DataCache dataCache = DataCache.getInstance();
        //we check that the authtoken and username of our AuthToken object in our data cache are clean
        Assert.assertNull(dataCache.getAuthToken().getAuthToken());
        Assert.assertNull(dataCache.getAuthToken().getUsername());
        registerResult = serverProxy.register(registerRequest);
        //we confirm that the we have true for the success boolean variable
        Assert.assertEquals(true, registerResult.getSuccess());
        //we create an AuthToken object that has the wrong information so we should fail the request
        AuthToken wrongAuthToken = new AuthToken("thisisthewrongauthtoken", "wrong user");
        //we use the authtoken object that we got from a successful registration
        eventsResult = serverProxy.getEvents(wrongAuthToken);
        //we check that we had a successful boolean
        Assert.assertEquals(false, eventsResult.getSuccess());
    }

    @Test
    public void clearTest(){
        //we register a user
        registerResult = serverProxy.register(registerRequest);
        //we confirm that the we have true for the success boolean variable
        Assert.assertEquals(true, registerResult.getSuccess());
        //we clear the server
        ClearResult result = new ClearResult();
        result = serverProxy.clearServer();
        //we check we got a success result
        Assert.assertEquals(true, result.getSuccess());
        //if we were really able to clear the server we can register the user again
        registerResult = serverProxy.register(registerRequest);
        //we confirm that the we have true for the success boolean variable
        Assert.assertEquals(true, registerResult.getSuccess());
    }
}