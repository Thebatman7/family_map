package com.example.client;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import java.io.IOException;

import Service.Request.RegisterRequest;
import Service.Result.EventsResult;
import Service.Result.PersonsResult;
import Service.Result.RegisterResult;
import Service.Result.SinglePersonResult;

public class RegisterTask implements Runnable {

    private final Handler messageHandler;
    private RegisterRequest request;
    private RegisterResult result;



    public RegisterTask(Handler messageHandler, RegisterRequest request) {
        this.messageHandler = messageHandler;
        this.request =  request;
    }

    @Override
    public void run() {
        ServerProxy serverProxy = new ServerProxy();

        result = serverProxy.register(request);

        if (result.getSuccess()) { DataCache.getInstance().setPersonID(result.getPersonID()); }

        sendMessage(result.getSuccess());
    }

    private void sendMessage(boolean success) {
        Message message = Message.obtain();

        Bundle messageBundle = new Bundle();
        messageBundle.putBoolean("Success", success);

        message.setData(messageBundle);

        messageHandler.sendMessage(message);
    }
}
