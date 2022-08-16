package com.example.client;

import android.provider.ContactsContract;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import Model.AuthToken;
import Model.Person;
import Service.Request.LoginRequest;
import Service.Request.RegisterRequest;
import Service.Result.ClearResult;
import Service.Result.EventsResult;
import Service.Result.LoginResult;
import Service.Result.PersonsResult;
import Service.Result.RegisterResult;
import Service.Result.SinglePersonResult;


public class ServerProxy {
    private LoginResult loginResult;
    private RegisterResult registerResult;
    private PersonsResult personsResult;
    private EventsResult eventsResult;
    private SinglePersonResult singlePersonResult;
    private ClearResult clearResult;

    //default constructor
    public ServerProxy() {}


    public LoginResult login(LoginRequest request) {
        String urlString = "http://" + DataCache.getInstance().getServerHost() + ":" +
                DataCache.getInstance().getServePort() + "/user/login";

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //the time that we will wait before we cancel, it usually is in milliseconds
            connection.setReadTimeout(5000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            //we get a connection
            connection.connect();


            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(request);
            OutputStream requestBody = connection.getOutputStream();
            writeString(jsonString, requestBody);
            requestBody.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream responseBody = connection.getInputStream();
                String responseData = readString(responseBody);
                loginResult = JsonSerializer.deserialize(responseData, LoginResult.class);
                //we save the authToken and username in our data cache
                String authToken = loginResult.getAuthToken();
                String username = loginResult.getUsername();
                DataCache.getInstance().getAuthToken().setAuthToken(authToken);
                DataCache.getInstance().getAuthToken().setUsername(username);
            } else {
                InputStream responseBody = connection.getErrorStream();
                String responseData = readString(responseBody);
                loginResult = JsonSerializer.deserialize(responseData, LoginResult.class);
            }
        } catch (IOException exception) {
            exception.getStackTrace();
        }
        return loginResult;
    }


    public RegisterResult register(RegisterRequest request) {
        String urlString = "http://" + DataCache.getInstance().getServerHost() + ":" +
                DataCache.getInstance().getServePort() + "/user/register";

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(5000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            connection.connect();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(request);
            OutputStream requestBody = connection.getOutputStream();
            writeString(jsonString, requestBody);
            requestBody.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream responseBody = connection.getInputStream();
                String responseData = readString(responseBody);
                registerResult = JsonSerializer.deserialize(responseData, RegisterResult.class);
                //we save the authToken and username in our data cache
                String authToken = registerResult.getAuthToken();
                String username = registerResult.getUsername();
                DataCache.getInstance().getAuthToken().setAuthToken(authToken);
                DataCache.getInstance().getAuthToken().setUsername(username);
            } else {
                InputStream responseBody = connection.getErrorStream();
                String responseData = readString(responseBody);
                registerResult = JsonSerializer.deserialize(responseData, RegisterResult.class);
            }
        } catch (IOException exception) {
            exception.getMessage();
            exception.getStackTrace();
        }
        return registerResult;
    }


    public PersonsResult getPeople(AuthToken authTokenObj) throws IOException {//GET
        String urlString = "http://" + DataCache.getInstance().getServerHost() + ":" +
                DataCache.getInstance().getServePort() + "/person";
        try {
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(5000);
            connection.setRequestMethod("GET");
            //we get the AuthToken object from our dataCache that contains the authToken and username
            String authToken = authTokenObj.getAuthToken();
            connection.addRequestProperty("Authorization", authToken);

            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream responseBody = connection.getInputStream();
                String responseData = readString(responseBody);
                personsResult = JsonSerializer.deserialize(responseData, PersonsResult.class);
                //we add the array to the dataCache
                DataCache.getInstance().setPersons(personsResult.getData());
            }
            else {
                InputStream responseBody = connection.getErrorStream();
                String responseData = readString(responseBody);
                personsResult = JsonSerializer.deserialize(responseData, PersonsResult.class);
            }
        } catch (IOException exception) {
            exception.getMessage();
            exception.getStackTrace();
        }
        return personsResult;
    }


    public EventsResult getEvents(AuthToken authTokenObj) {//GET
        String urlString = "http://" + DataCache.getInstance().getServerHost() + ":" +
                DataCache.getInstance().getServePort() + "/event";
        try {
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(5000);
            connection.setRequestMethod("GET");
            //we get the AuthToken object from our dataCache that contains the authToken and username
            String authToken = authTokenObj.getAuthToken();
            connection.addRequestProperty("Authorization", authToken);

            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream responseBody = connection.getInputStream();
                String responseData = readString(responseBody);
                eventsResult = JsonSerializer.deserialize(responseData, EventsResult.class);
                DataCache.getInstance().setEvents(eventsResult.getData());
            }
            else {
                InputStream responseBody = connection.getErrorStream();
                String responseData = readString(responseBody);
                eventsResult = JsonSerializer.deserialize(responseData, EventsResult.class);
            }
        }
        catch (IOException exception) {
            exception.getMessage();
            exception.getStackTrace();
        }
        return eventsResult;
    }

    public SinglePersonResult getPerson(AuthToken authTokenObj) {
        String urlString = "http://" + DataCache.getInstance().getServerHost() + ":" +
                DataCache.getInstance().getServePort() + "/person/" + DataCache.getInstance().getPersonID();
        try {
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(5000);
            connection.setRequestMethod("GET");
            //we get the AuthToken object from our dataCache that contains the authToken and username
            String authToken = authTokenObj.getAuthToken();
            connection.addRequestProperty("Authorization", authToken);

            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream responseBody = connection.getInputStream();
                String responseData = readString(responseBody);
                singlePersonResult = JsonSerializer.deserialize(responseData, SinglePersonResult.class);
                //maybe
                Person userPerson = new Person(singlePersonResult.getPersonID(),
                        singlePersonResult.getAssociatedUsername(), singlePersonResult.getFirstName(),
                        singlePersonResult.getLastName(), singlePersonResult.getGender(),
                        singlePersonResult.getFatherID(), singlePersonResult.getMotherID(),
                        singlePersonResult.getSpouseID());
                DataCache.getInstance().setUserPerson(userPerson);
                DataCache.getInstance().setUserFirstName(singlePersonResult.getFirstName());
                DataCache.getInstance().setUserLastName(singlePersonResult.getLastName());
            }
            else {
                InputStream responseBody = connection.getErrorStream();
                String responseData = readString(responseBody);
                singlePersonResult = JsonSerializer.deserialize(responseData, SinglePersonResult.class);
            }
        }
        catch(IOException exception) {
            exception.getMessage();
            exception.getStackTrace();
        }
        return singlePersonResult;
    }


    /*
    Apps should not be able to do this because Servers are used by many users and they should not
    be able to clear all data in the Server but for this project it makes sense since there is not
    way to clear everything when needed
    */
    public ClearResult clearServer() {
        String urlString = "http://" + DataCache.getInstance().getServerHost() + ":" +
                        DataCache.getInstance().getServePort() + "/clear";
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(5000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            //we get a connection
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream responseBody = connection.getInputStream();
                String responseData = readString(responseBody);
                clearResult = JsonSerializer.deserialize(responseData, ClearResult.class);
            } else {
                InputStream responseBody = connection.getErrorStream();
                String responseData = readString(responseBody);
                clearResult = JsonSerializer.deserialize(responseData, ClearResult.class);
            }

        } catch (IOException exception) {
            exception.getMessage();
            exception.getStackTrace();
        }
        return clearResult;
    }

    //this method writes a String to an OutputStream.
    private void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(sw);
        bw.write(str);
        bw.flush();
    }

    //this method reads a String from a InputStream
    private String readString(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        char[] buffer = new char[1024];
        int length;
        while ((length = streamReader.read(buffer)) > 0) {
            stringBuilder.append(buffer, 0, length);
        }
        return stringBuilder.toString();
    }
}
