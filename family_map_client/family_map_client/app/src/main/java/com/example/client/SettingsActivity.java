package com.example.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {
    private Switch lifeStorySwitch;
    private Switch familyTreeSwitch;
    private Switch spouseLineSwitch;
    private Switch fatherSideSwitch;
    private Switch motherSideSwitch;
    private Switch maleEventSwitch;
    private Switch femaleEventSwitch;

    private RelativeLayout logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DataCache dataCache = DataCache.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        /*
        We add back button. DON'T FORGET to state the parent in android:parentActivityName="" in
        AndroidManifest.xml file.
        */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //we get reference to the switches
        lifeStorySwitch = (Switch)findViewById(R.id.lifeStorySwitch);
        familyTreeSwitch = (Switch)findViewById(R.id.familyTreeSwitch);
        spouseLineSwitch = (Switch)findViewById(R.id.spouseLinesSwitch);
        fatherSideSwitch = (Switch)findViewById(R.id.fatherSideSwitch);
        motherSideSwitch = (Switch)findViewById(R.id.motherSideSwitch);
        maleEventSwitch = (Switch)findViewById(R.id.maleEventSwitch);
        femaleEventSwitch = (Switch)findViewById(R.id.femaleEventSwitch);

        //we get status of switches from data cache
        initialize();

        //we set data cache based on what switches were enabled
        updateSettings();


        //we make the whole Relative layout for logout section clickable
        logout = findViewById(R.id.logoutLayout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //we create an intent to go back to main activity
                Intent settingsIntent = new Intent(SettingsActivity.this, MainActivity.class);
                //clear the back stack, meaning clear all the activities so we can log out
                settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(settingsIntent);
                finish();//let the activity know that it should end itself
            }
        });
    }


    private void initialize() {
        DataCache dataCache = DataCache.getInstance();
        lifeStorySwitch.setChecked(dataCache.isLifeStorySwitch());
        familyTreeSwitch.setChecked(dataCache.isFamilyTreeSwitch());
        spouseLineSwitch.setChecked(dataCache.isSpouseLines());
        fatherSideSwitch.setChecked(dataCache.isFatherSideSwitch());
        motherSideSwitch.setChecked(dataCache.isMotherSideSwitch());
        maleEventSwitch.setChecked(dataCache.isMaleEventsSwitch());
        femaleEventSwitch.setChecked(dataCache.isFemaleEventsSwitch());
    }

    private void updateSettings() {
        DataCache dataCache = DataCache.getInstance();
        lifeStorySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setLifeStorySwitch(isChecked);
            }
        });
        familyTreeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setFamilyTreeSwitch(isChecked);
            }
        });
        spouseLineSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setSpouseLines(isChecked);
            }
        });
        fatherSideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setFatherSideSwitch(isChecked);
            }
        });
        motherSideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setMotherSideSwitch(isChecked);
            }
        });
        maleEventSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setMaleEventsSwitch(isChecked);
            }
        });
        femaleEventSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataCache.setFemaleEventsSwitch(isChecked);
            }
        });
    }
    
}