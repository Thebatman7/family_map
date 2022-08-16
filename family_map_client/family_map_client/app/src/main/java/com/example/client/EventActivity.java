package com.example.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        String eventID = getIntent().getStringExtra("EventID");

        /*
        We can name the activity here or we can do it inside the AndroidManifest.xml. Inside
        <activity android:name=".EventActivity"> </activity> we add this, android:label="Event" line
        of code inside the first <>
        */
        this.setTitle("Event");

        /*
        The FragmentManager is responsible for managing fragment transactions and adding their
        view to the activity's view hierarchy.
        FragmentManagers handles a list of fragments and a back stack of fragment transactions.
        Fragment transactions are used to add, remove, attach, detach, or replace fragments in the fragment
        list. This is what is needed in order to do a transaction.
        */
        FragmentManager fragmentManager = getSupportFragmentManager();
        //we get a reference to our Frame Layout fragmentContainer
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentMapContainer);
        fragment = new MapFragment(true, eventID);
        fragmentManager.beginTransaction().replace(R.id.fragmentMapContainer, fragment).commit();
    }
}