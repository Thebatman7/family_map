package com.example.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import Model.Event;
import Model.Person;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final int PEOPLE = 0;
    private static final int PERSON_EVENTS = 1;
    private SearchAdapter adapter;
    List<Person> people = new ArrayList<>();
    List<PersonEvent> personEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //we get a reference to the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.RecyclerView);
        //we always need to set a layoutManager for the RecyclerView to be able to scroll up and down
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        /*
        We add back button. DON'T FORGET to state the parent in android:parentActivityName="" in
        AndroidManifest.xml file.
        */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //for the icons. This way of getting icons has been deprecated.
        Iconify.with(new FontAwesomeModule());

        //binds the Adapter to the RecyclerView
        adapter = new SearchAdapter(people, personEvents);
        recyclerView.setAdapter(adapter);

        //we locate the EditText in the xml file ???MAYBE???
        SearchView editSearch = (SearchView) findViewById(R.id.searchView);
        editSearch.setOnQueryTextListener(this);
    }
    /*
    public boolean onOptionItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
           this.finish();
        }
        return super.onOptionsItemSelected(item);
    }*/

    //we create the class for the Adapter
    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        //adapter has the data that needs to display
        private List<Person> persons;
        private List<PersonEvent> personEvents;
        //private ArrayList<Person> personsArray;
        //private ArrayList<PersonEvent> personEventsArray;

        SearchAdapter(List<Person> persons, List<PersonEvent> personEvents) {
            this.persons = persons;
            this.personEvents = personEvents;

            /*personsArray = new ArrayList<Person>();
            personsArray.addAll(persons);
            personEventsArray = new ArrayList<PersonEvent>();
            personEventsArray.addAll(personEvents);*/
        }

        @Override
        public int getItemViewType(int position) {
            /*
            This method is called by the RecyclerView. As people interact with it RecyclerView will
            decide when it needs this information.
            We return different number for the different types of layout
            */
            return position < persons.size() ? PEOPLE : PERSON_EVENTS;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if(viewType == PEOPLE) {
                /*
                We use the same inflater the Activity uses to inflate any of its UI controls.
                We pass the parent, just like we did for fragments
                We always set the third parameter to false unless we are writing code to instantiate
                the UI controls.
                */
                view = getLayoutInflater().inflate(R.layout.person_item, parent, false);
            }
            else {
                view = getLayoutInflater().inflate(R.layout.event_item, parent, false);
            }
            //once we have a view we pass it to the view holder
            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            if (position < persons.size()) {
                holder.bind(persons.get(position));
            }
            else {
                holder.bind(personEvents.get(position - persons.size()));
            }
        }

        @Override
        public int getItemCount() { return persons.size() + personEvents.size(); }


        //method for filtering our recyclerview items
        public void filterList(List<Person> personsFiltered, List<PersonEvent> personEventsFiltered) {
            persons = personsFiltered;
            personEvents = personEventsFiltered;
            notifyDataSetChanged();
        }
    }

    //we create another class for the ViewHolder
    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView name;
        private final TextView eventInformation;
        private final ImageView imageView;

        private final int viewType;
        private Person person;
        private PersonEvent personEvent;

        SearchViewHolder(View view, int viewType) {
            //we pass to the parent constructor
            super(view);
            //we get this from RecyclerView.ViewHolder, when we call super it is set.
            this.viewType = viewType;

            //we set a listener for the view. this is the view holder.
            itemView.setOnClickListener(this);

            //we get references to the controllers inside the view
            if (viewType == PEOPLE) {
                name = itemView.findViewById(R.id.personTextView);
                //name.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_person, 0,0,0);
                eventInformation = null;

                imageView = itemView.findViewById(R.id.personImageView);
            }
            else {
                name = itemView.findViewById(R.id.eventPersonNameTV);
                eventInformation = itemView.findViewById(R.id.eventTextView);

                imageView = itemView.findViewById(R.id.eventImageView);
            }
        }

        //binders
        private void bind(Person person){
            this.person = person;
            name.setText(person.getFirstName() + " " + person.getLastName());

            if(person.getGender().equals("M")) {
                Drawable personDrawable = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_male).
                        colorRes(R.color.blue).sizeDp(30);
                imageView.setImageDrawable(personDrawable);
            }
            if(person.getGender().equals("F")) {
                Drawable personDrawable = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_female).
                        colorRes(R.color.purple_700).sizeDp(30);
                imageView.setImageDrawable(personDrawable);
            }
        }
        private void bind(PersonEvent personEvent){
            this.personEvent = personEvent;
            eventInformation.setText(personEvent.getEventType().toUpperCase() + ": " +
                    personEvent.getCity() + ",  " +  personEvent.getCountry() + " (" +
                    personEvent.getYear() + ")");

            name.setText(personEvent.getFirstName() + " " + personEvent.getLastName());

            Drawable event = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker).
                    colorRes(R.color.dark_green).sizeDp(30);
            imageView.setImageDrawable(event);
        }

        @Override
        public void onClick(View view){
            if (viewType == PEOPLE) {
                Intent personIntent = new Intent(SearchActivity.this, PersonActivity.class);
                personIntent.putExtra("PersonID", person.getPersonID());
                startActivity(personIntent);
            }
            else {
                Intent eventIntent = new Intent(SearchActivity.this, EventActivity.class);
                eventIntent.putExtra("EventID", personEvent.getEventID());
                startActivity(eventIntent);
                //Toast.makeText(SearchActivity.this, "User clicked an event", Toast.LENGTH_LONG).show();
            }
        }
    }

    //generic function to convert an Array to List
    private static <T> List<T> convertArrayToList(T array[]){
        //we create the List by passing the Array ad parameter in the constructor
        List<T> list = new ArrayList<>();
        if(array.length != 0) {
            //we add the array to list
            Collections.addAll(list, array);
            //we return the converted List
        }
        return list;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        filter(text);
        return false;
    }

    public void filter(String stringText) {
        //we get the data of the user from our DataCache
        people = convertArrayToList(DataCache.getInstance().getPersons());
        personEvents = convertArrayToList(DataCache.getInstance().getPersonEvents());

        /*
        We convert the string to lower case, using toLowerCase using the rules defined by the
        specified Locale.
        */
        stringText = stringText.toLowerCase(Locale.getDefault());
        List<Person> personsFiltered = new ArrayList<>();
        List<PersonEvent> personEventsFiltered = new ArrayList<>();

        if (stringText.length() == 0) {
            people.clear();
            personEvents.clear();
            adapter.filterList(personsFiltered, personEventsFiltered);
        }
        for (Person person : people) {
            if (person.getFirstName().toLowerCase(Locale.getDefault()).contains(stringText) ||
                    person.getLastName().toLowerCase(Locale.getDefault()).contains(stringText)) {
                personsFiltered.add(person);
            }
        }
        for (PersonEvent personEvent : personEvents) {
            if (personEvent.getUsername().toLowerCase(Locale.getDefault()).contains(stringText) ||
                    personEvent.getEventType().toLowerCase(Locale.getDefault()).contains(stringText) ||
                    personEvent.getCity().toLowerCase(Locale.getDefault()).contains(stringText) ||
                    personEvent.getCountry().toLowerCase(Locale.getDefault()).contains(stringText) ||
                    Integer.toString(personEvent.getYear()).contains(stringText)) {
                personEventsFiltered.add(personEvent);
            }
        }
        if (personsFiltered.isEmpty() && personEventsFiltered.isEmpty()) {
            //if no item is added in filtered lists we display a toast message as no data found
            Toast.makeText(this, "No data found...", Toast.LENGTH_LONG);
        } else {
            adapter.filterList(personsFiltered, personEventsFiltered);
        }
    }
}

