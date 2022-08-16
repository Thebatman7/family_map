package com.example.client;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import Service.Request.LoginRequest;
import Service.Result.LoginResult;

public class LoginTask implements Runnable {

    private final Handler messageHandler;
    private LoginRequest request;
    private LoginResult result;

    public LoginTask(Handler messageHandler, LoginRequest request) {
        this.messageHandler = messageHandler;
        this.request = request;
    }

    @Override
    public void run() {
        ServerProxy serverProxy = new ServerProxy();

        result = serverProxy.login(request);

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
