package com.example.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.Person;

public class PersonActivity extends AppCompatActivity {
    private List<Person> people = new ArrayList<>();
    private List<PersonEvent> personEvents = new ArrayList<>();
    private Map<String, String> mapPersonIDToRelation = new HashMap<>();
    private Person person = new Person();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        //for the icons. This way of getting icons has been deprecated.
        Iconify.with(new FontAwesomeModule());

        //reference to expandable list view (a.k.a collapsible list)
        ExpandableListView expandableListView = findViewById(R.id.expandableListView);

        /*
        We add back button. DON'T FORGET to state the parent in android:parentActivityName="" in
        AndroidManifest.xml file.
        */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String personID = getIntent().getStringExtra("PersonID");
        setPeopleAndEvents(personID);

        TextView personName = findViewById(R.id.personName);
        personName.setText(person.getFirstName());
        TextView personLastName = findViewById(R.id.personLastName);
        personLastName.setText(person.getLastName());
        TextView personGender = findViewById(R.id.personGender);
        if (person.getGender().equals("M")) { personGender.setText("Male"); }
        if (person.getGender().equals("F")) { personGender.setText("Female"); }


        //we set an adapter for the expandable list view by created an instance of the class we created
        expandableListView.setAdapter(new ExpandableListAdapter(personEvents, people));

    }

    private void setPeopleAndEvents(String personID) {
        DataCache dataCache = DataCache.getInstance();
        person = dataCache.getMapPeople().get(personID);
        ArrayList<PersonEvent> tempEvents = dataCache.getMapPersonToEvents().get(personID);
        tempEvents = dataCache.sortEvents(tempEvents);
        //we check that the events are not filtered
        for(int i = 0; i < tempEvents.size(); ++i){
            if (dataCache.findEvent(tempEvents.get(i))){
                //if event is not filtered we add it
                personEvents.add(tempEvents.get(i));
            }
        }
        figureOutRelation(person);
    }
    private void figureOutRelation(Person person){
        DataCache dataCache = DataCache.getInstance();

        if (person.getFatherID() != null) {
            Person father = new Person();
            father = dataCache.getMapPeople().get(person.getFatherID());
            people.add(father);
            mapPersonIDToRelation.put(person.getFatherID(), "Father");
        }
        if (person.getMotherID() != null){
            Person mother = new Person();
            mother = dataCache.getMapPeople().get(person.getMotherID());
            people.add(mother);
            mapPersonIDToRelation.put(person.getMotherID(), "Mother");
        }
        if (person.getSpouseID() != null){
            Person spouse = new Person();
            spouse = dataCache.getMapPeople().get(person.getSpouseID());
            people.add(spouse);
            mapPersonIDToRelation.put(person.getSpouseID(), "Spouse");
        }
        ArrayList<Person> children = new ArrayList<>();
        children = dataCache.findChildren(person);
        if(children.size() != 0){
            for(int i = 0; i < children.size(); ++i) {
                people.add(children.get(i));
                mapPersonIDToRelation.put(children.get(i).getPersonID(), "Child");
            }
        }
    }


    //adapter class we created
    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final int PERSON_EVENTS = 0;
        private static final int PEOPLE = 1;
        private static final int NUM_GROUPS = 2;
        List<Person> people = new ArrayList<>();
        List<PersonEvent> personEvents = new ArrayList<>();

        //constructor
        ExpandableListAdapter(List<PersonEvent> events, List<Person> people){
            this.personEvents = events;
            this.people = people;
        }

        @Override
        public int getGroupCount() {
            //expandable list view needs to know how many groups it's displaying
            return NUM_GROUPS;
        }

        @Override
        public int getChildrenCount(int groupPosition){
            /*
            We do not call these methods, but they are necessary for the expandable list view so it can
            call them when it needs the information.
            Based on the number groupPosition,  it will check and see the number of elements of the type
            */
            switch(groupPosition){
                case PERSON_EVENTS:
                    return personEvents.size();
                case PEOPLE:
                    return people.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition){
            //This is how we get the name that goes on the group
            switch (groupPosition){
                case PERSON_EVENTS:
                    return getString(R.string.eventTitle);
                case PEOPLE:
                    return getString(R.string.peopleTile);
                default:
                    throw new IllegalArgumentException("Unrecognized group position " + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition){
            /*
            We need to return the actual object that should be associated wit a row that displayed in
            a particular positions of a group.
            */
            switch (groupPosition){
                case PERSON_EVENTS:
                    return personEvents.get(childPosition);
                case PEOPLE:
                    return people.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized group position " + groupPosition);
            }
        }

        //in this case we are using the groupPosition as the ID
        @Override
        public long getGroupId(int groupPosition) { return groupPosition; }

        //in this case we are using the childPosition as the ID
        @Override
        public long getChildId(int groupPosition, int childPosition) { return childPosition; }

        /*
        This method asks if the IDs are based on position or on something internal to the object.
        In our case, we are using the positions as IDs. If the list get reorganized the IDs would change
        for every element so they are not stable IDs.
        */
        @Override
        public boolean hasStableIds() { return false; }

        //here is where we instantiate or inflate the views
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent){
            //if parameter is null we inflate the list layout
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            //we get a reference to the text view inside the list layout
            TextView titleView = convertView.findViewById(R.id.listTitle);
            //we check the position to set the tile
            switch (groupPosition){
                case PERSON_EVENTS:
                    titleView.setText(R.string.eventTitle);
                    break;
                case PEOPLE:
                    titleView.setText(R.string.peopleTile);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch (groupPosition){
                case PERSON_EVENTS:
                    itemView = getLayoutInflater().inflate(R.layout.event_expandable_list, parent, false);
                    //we call a method we have created
                    initializeEventView(itemView, childPosition);
                    break;
                case PEOPLE:
                    itemView = getLayoutInflater().inflate(R.layout.person_expandable_list, parent, false);
                    initializePersonView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position " + groupPosition);
            }
            return itemView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
        /*
        Because we do not have a view holder like we have in a recycler view.
        This is a method where we bind similar to what we have in recyclerview where we call the
        view holder
        */
        private void initializeEventView(View eventItemView, final int childPosition) {
            TextView eventInfo = eventItemView.findViewById(R.id.eventText);
            TextView eventPerson = eventItemView.findViewById(R.id.eventPersonName);
            ImageView eventIcon = eventItemView.findViewById(R.id.eventImageView);

            PersonEvent event = new PersonEvent();
            event = personEvents.get(childPosition);
            eventInfo.setText(event.getEventType().toUpperCase() + ": " + event.getCity() + ", " +
                            event.getCountry() + " (" + event.getYear() + ")");
            eventPerson.setText(event.getFirstName() + " " + event.getLastName());

            Drawable eventDrawable = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).
                    colorRes(R.color.dark_green).sizeDp(30);
            eventIcon.setImageDrawable(eventDrawable);

            //we make the view clickable
            eventItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent eventIntent = new Intent(PersonActivity.this, EventActivity.class);
                    eventIntent.putExtra("EventID", personEvents.get(childPosition).getEventID());
                    startActivity(eventIntent);
                }
            });
        }
        private void initializePersonView(View personItemView, final int childPosition) {
            TextView personName = personItemView.findViewById(R.id.personText);
            ImageView personIcon = personItemView.findViewById(R.id.personImageView);

            Person person = people.get(childPosition);
            personName.setText(person.getFirstName() + " " + person.getLastName());
            TextView relationOfPerson = personItemView.findViewById(R.id.relation);
            String relation = mapPersonIDToRelation.get(people.get(childPosition).getPersonID());
            relationOfPerson.setText(relation);

            if (person.getGender().equals("M")){
                Drawable personDrawable = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_male).
                        colorRes(R.color.blue).sizeDp(30);
                personIcon.setImageDrawable(personDrawable);
            }
            if (person.getGender().equals("F")){
                Drawable personDrawable = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_female).
                        colorRes(R.color.purple_700).sizeDp(30);
                personIcon.setImageDrawable(personDrawable);
            }

            personItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent personIntent = new Intent(PersonActivity.this, PersonActivity.class);
                    personIntent.putExtra("PersonID", person.getPersonID());
                    startActivity(personIntent);
                }
            });
        }
    }

    //generic function to convert an Array to List
    private static <T> List<T> convertArrayToList(T array[]){
        //we create the List by passing the Array ad parameter in the constructor
        List<T> list = new ArrayList<>();
        //we add the array to list
        Collections.addAll(list, array);
        //we return the converted List
        return list;
    }
}


