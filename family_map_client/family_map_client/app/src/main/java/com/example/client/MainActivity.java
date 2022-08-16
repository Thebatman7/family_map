package com.example.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

//AppCompatActivity adds capabilities
public class MainActivity extends AppCompatActivity {//AppCompatActivity adds backwards compatibility
    boolean allSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setTitle("Family Map");

        /*
        The FragmentManager is responsible for managing fragment transactions and adding their
        view to the activity's view hierarchy.
        FragmentManagers handles a list of fragments and a back stack of fragment transactions.
        Fragment transactions are used to add, remove, attach, detach, or replace fragments in the fragment
        list.
        This is what is needed in order to do a transaction
        */
        FragmentManager fragmentManager = getSupportFragmentManager();
        //we get a reference to our Frame Layout fragmentContainer
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);

        Handler uiThreadMessageHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                Bundle bundle = message.getData();
                allSuccess = bundle.getBoolean("All was successful");
                if (allSuccess == true) {
                    Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
                    fragment = new MapFragment(false, null);
                    fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
                }
            }
        };

        if (fragment == null) {
            if (allSuccess == false) {
                fragment = new LoginFragment(uiThreadMessageHandler);
                /*
                 FragmentManager .beginTransaction() method crates and returns an instance of FragmentTransaction.
                 The FragmentTransaction class uses a fluent interface-method that configure FragmentTransaction
                 return a FragmentTransaction instead of void, which allow us to chain them together.
                 It basically says, "create a new fragment transaction, include one add operation in it,
                 then commit it.
                */
                fragmentManager.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
            }
        }
    }
}