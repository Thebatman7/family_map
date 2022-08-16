package com.example.client;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;

import Model.AuthToken;
import Model.Person;
import Service.Request.EventRequest;
import Service.Request.PersonRequest;
import Service.Result.EventsResult;
import Service.Result.PersonsResult;
import Service.Result.SinglePersonResult;


public class GetDataTask implements Runnable {

    private final Handler messageHandler;
    private PersonsResult personsResult;
    private EventsResult eventsResult;
    private SinglePersonResult thePerson;
    private boolean allSuccess;

    public GetDataTask(Handler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {
        ServerProxy serverProxy = new ServerProxy();

        AuthToken authToken = DataCache.getInstance().getAuthToken();

        try {
            personsResult = serverProxy.getPeople(authToken);
            eventsResult = serverProxy.getEvents(authToken);

            //maybe
            thePerson = serverProxy.getPerson(authToken);

        }
        catch (IOException exception) {
            exception.getMessage();
            exception.printStackTrace();
        }

        if (personsResult.getSuccess() == true &&
                eventsResult.getSuccess() == true &&
                thePerson.getSuccess() == true) {
            if (DataCache.getInstance().getEventPerson()) {
                DataCache.getInstance().getEventsByGender();
                DataCache.getInstance().setMapPersonToEvents();
                DataCache.getInstance().getFatherSide();
                DataCache.getInstance().getMotherSide();
                allSuccess = true;
            }
        }
        else {
            allSuccess = false;
        }

        sendMessage(allSuccess);
    }

    private void sendMessage(boolean success) {
        Message message = Message.obtain();

        Bundle messageBundle = new Bundle();
        messageBundle.putBoolean("All was successful", success);

        message.setData(messageBundle);

        messageHandler.sendMessage(message);
    }
}
