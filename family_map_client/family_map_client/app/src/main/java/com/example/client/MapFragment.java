package com.example.client;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.Feature;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Model.Event;
import Model.Person;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap map;
    //view that can be accessed everywhere after inflated
    private View view;
    //textView variables
    private TextView eventInf;
    //imageView variable
    private ImageView imageView;
    //map of event to color
    private Map<String, Float> eventColor = new HashMap<>();

    private ArrayList<PersonEvent> personEvents;
    private TextView mapTextView;
    private Set<PolylineOptions> setOfLines;
    //container of lines between markers
    private ArrayList<Polyline> lines = new ArrayList<>();
    //boolean to determine if we display bar tools or back button
    private boolean isEvent;
    private String theEventID;
    //variables to draw lines
    private PersonEvent markerEvent = new PersonEvent();
    private Marker marker;
    LatLng latitudeLongitude;

    //default constructor
    public MapFragment(boolean event, String eventID) {
        this.isEvent = event;
        this.theEventID = eventID;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isEvent){
            /*
            We add back button. DON'T FORGET to state the parent in android:parentActivityName="" in
            AndroidManifest.xml file. Because we are inside of a fragment, adding the back button is
            a little different, we need ((AppCompatActivity)getActivity()).
            */
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        else {
            /*
            Because this is a fragment we need this line of code so we can display the menu that is in
            the activity hosting this fragment. Without it, we cannot display the menu.
            */
            setHasOptionsMenu(true);
        }
        //to use the fond awesome library we need to initialize it
        Iconify.with(new FontAwesomeModule());


    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        view = layoutInflater.inflate(R.layout.fragment_map, container, false);
        //we get a reference to the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        /*
        This will asynchronously load the map.
        Anything that we do in our UI that takes more than just a very short amount of time should
        be asynchronously. this, which is the class MapFragment, is what be pas which is something
        that has implemented OnMapReadyCallback interface, then OnMapReady will be called when map
        is ready.
        */
        mapFragment.getMapAsync(this);

        //we convert the array of PersonEvent to an arrayList
        personEvents = new ArrayList<PersonEvent>(Arrays.asList(DataCache.getInstance().getPersonEvents()));

        return view;
    }


    //because we are implementing OnMapReadyCallBack we must write this method
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //we get a reference to our map and place it as an instance variable
        map = googleMap;
        //we probably do not need this. It is here for the case that is described in OnMapLoaded, read it.
        map.setOnMapLoadedCallback(this);
        //get the map of event type to color from data base
        eventColor = DataCache.getInstance().getMapEventColors();

        //set marker listener
        map.setOnMarkerClickListener(this);

        for(int i = 0; i < personEvents.size(); ++i){
            //we add a marker for each location and event
            LatLng location = new LatLng(personEvents.get(i).getLatitude(), personEvents.get(i).getLongitude());
            Float color = eventColor.get(personEvents.get(i).getEventType());
            Marker marker = map.addMarker(new MarkerOptions().position(location).
                    icon(BitmapDescriptorFactory.defaultMarker(color)));//for color (BitmapDescriptorFactory.HUE_BLUE)
            marker.setTag(personEvents.get(i));
        }


        if(isEvent) {
            //when we get the call from the event activity we need to set the LinearLayout with the proper event
            PersonEvent theEvent = DataCache.getInstance().getMapEventIdToEvent().get(theEventID);
            eventInf = view.findViewById(R.id.eventData);
            imageView = view.findViewById(R.id.imageView);
            LinearLayout linearLayout;
            //setting the Layout
            if (theEvent.getGender().equals("M")) {
                eventInf.setText(theEvent.getFirstName() + " " + theEvent.getLastName() + "\n" +
                        theEvent.getEventType().toUpperCase() + ": " + theEvent.getCity() + ", " +
                        theEvent.getCountry() + " (" + theEvent.getYear() + ")");
                eventInf.setTextColor(this.getResources().getColor(R.color.blue));
                Drawable theGender = new IconDrawable(getContext(), FontAwesomeIcons.fa_male).
                        colorRes(R.color.blue).sizeDp(40);
                imageView.setImageDrawable(theGender);
            }
            if (theEvent.getGender().equals("F")) {
                eventInf.setText(theEvent.getFirstName() + " " + theEvent.getLastName() + "\n" +
                        theEvent.getEventType().toUpperCase() + ": " + theEvent.getCity() + ", " +
                        theEvent.getCountry() + " (" + theEvent.getYear() + ")");
                eventInf.setTextColor(this.getResources().getColor(R.color.purple_700));
                Drawable theGender = new IconDrawable(getContext(), FontAwesomeIcons.fa_female).
                        colorRes(R.color.purple_700).sizeDp(40);
                imageView.setImageDrawable(theGender);
            }

            //we draw the lines of the event activity
            markerEvent = theEvent;
            LatLng theLatLng = new LatLng(theEvent.getLatitude(), theEvent.getLongitude());
            latitudeLongitude = theLatLng;
            drawLines();

            //we center the camera
            LatLng eventLatLng = new LatLng(theEvent.getLatitude(), theEvent.getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(eventLatLng, 1));
            //we make the linear layout clickable
            linearLayout = view.findViewById(R.id.eventInfo);
            String personID = theEvent.getPersonID();
            //linearLayout.setClickable(true);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent personIntent = new Intent(getActivity().getApplicationContext(), PersonActivity.class);
                    personIntent.putExtra("PersonID", personID);
                    getActivity().startActivity(personIntent);
                }
            });
        }
    }


    @Override
    public void onMapLoaded() {
        /*
        We probably don't need this callback. It occurs after onMapReady and there have been cases where
        you get an error when adding markers or otherwise interacting with the map in onMapReady(...) because
        the map isn't really all the way ready. If you see that, just move all code where you interact
        with the map (everything after map.setonMapLoadedCallBack(...) above to here.
        */
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        /*
        This method is different for fragments than it is for activities, we get a MenuInflater inflater
        parameter that we don't get in activities so we need to inflate it with the right menu reference
        */
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.map_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        switch (menu.getItemId()) {
            case R.id.searchMenuItem:
                Intent searchIntent = new Intent(getActivity().getApplicationContext(), SearchActivity.class);
                /*
                When we go to another activity and we get back to this activity, we do not want a
                new instance oc the activity/fragment. We want the same one that we had before we
                changed activities.
                Intent.FLAG_ACTIVITY_CLEAR_TOP: if there's a stack of activities and one of them is
                the activity we just asked for, pop everything that's on top of it off until we to that
                activity, instead of sticking a new activity on top of the stack go to the one that's
                already in the stack.
                Intent.FLAG_ACTIVITY_SINGLE_TOP: if the activity we just asked for is already on the
                top (there should only be one) don't stack another one on the top of it, use the one
                that is already on top.
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                */
                getActivity().startActivity(searchIntent);
                return true;
            case R.id.settingMenuItem:
                Intent settingsIntent = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
                getActivity().startActivity(settingsIntent);
                return true;
            default:
                /*
                If it is anything else apart from the items we implemented we must do this because there
                are other menu items
                */
                return super.onOptionsItemSelected(menu);
        }
    }

    //we must override this method because of GoogleMap.OnMarkerClickListener
    @Override
    public boolean onMarkerClick(Marker marker) {
        this.marker = marker;
        latitudeLongitude = marker.getPosition();
        clearLines();
        LinearLayout linearLayout;
        eventInf = view.findViewById(R.id.eventData);
        imageView = view.findViewById(R.id.imageView);
        //PersonEvent markerEvent = new PersonEvent();
        markerEvent = (PersonEvent)marker.getTag();


        //setting the Layout
        if (markerEvent.getGender().equals("M")) {
            eventInf.setText(markerEvent.getFirstName() + " " + markerEvent.getLastName() + "\n" +
                    markerEvent.getEventType().toUpperCase() + ": " + markerEvent.getCity() + ", " +
                    markerEvent.getCountry() + " (" + markerEvent.getYear() + ")");
            eventInf.setTextColor(this.getResources().getColor(R.color.blue));
            Drawable gender = new IconDrawable(getContext(), FontAwesomeIcons.fa_male).
                    colorRes(R.color.blue).sizeDp(40);
            imageView.setImageDrawable(gender);
        }
        if (markerEvent.getGender().equals("F")) {
            eventInf.setText(markerEvent.getFirstName() + " " + markerEvent.getLastName() + "\n" +
                    markerEvent.getEventType().toUpperCase() + ": " + markerEvent.getCity() + ", " +
                    markerEvent.getCountry() + " (" + markerEvent.getYear() + ")");
            eventInf.setTextColor(this.getResources().getColor(R.color.purple_700));
            Drawable gender = new IconDrawable(getContext(), FontAwesomeIcons.fa_female).
                    colorRes(R.color.purple_700).sizeDp(40);
            imageView.setImageDrawable(gender);
        }

        //we draw lines connecting markers
        drawLines();

        //we make the layout clickable
        linearLayout = view.findViewById(R.id.eventInfo);
        String personID = markerEvent.getPersonID();
        //linearLayout.setClickable(true);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent personIntent = new Intent(getActivity().getApplicationContext(), PersonActivity.class);
                personIntent.putExtra("PersonID", personID);
                getActivity().startActivity(personIntent);
            }
        });
        //this will center the location or marker in the screen
        map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        //makes the default information on marker not to be shown else(false) it will show on marker
        return true;
    }

    //lines are added to the set of lines recursively including all generations
    private void getFamilyTreeLines(Person child, PersonEvent event, float width) {
        Person person = new Person();
        person = child;
        //we check that person has parents
        if (person.getFatherID() != null && DataCache.getInstance().isMaleEventsSwitch()) {
            Person father = DataCache.getInstance().getMapPeople().get(person.getFatherID());//value = map.get(key)
            //we get events of father
            ArrayList<PersonEvent> eventsOfFather = new ArrayList<>();
            eventsOfFather = DataCache.getInstance().getMapPersonToEvents().get(father.getPersonID());
            eventsOfFather = DataCache.getInstance().sortEvents(eventsOfFather);
            if (eventsOfFather.size() != 0) {
                PersonEvent firstFatherEvent = new PersonEvent();
                firstFatherEvent = eventsOfFather.get(0);
                boolean eventIsNotFiltered = DataCache.getInstance().findEvent(firstFatherEvent);
                if (eventIsNotFiltered) {
                    LatLng eventLocation = new LatLng(event.getLatitude(), event.getLongitude());
                    LatLng fatherLocation = new LatLng(firstFatherEvent.getLatitude(),
                            firstFatherEvent.getLongitude());
                    PolylineOptions polyline = new PolylineOptions();
                    polyline.add(eventLocation, fatherLocation);
                    width = width - 4;
                    polyline.width(width);
                    polyline.color(Color.BLUE);
                    setOfLines.add(polyline);
                    getFamilyTreeLines(father, firstFatherEvent, width);//recursive call
                }
            }
        }
        if (person.getMotherID() != null && DataCache.getInstance().isFemaleEventsSwitch()) {
            Person mother = DataCache.getInstance().getMapPeople().get(person.getMotherID());
            ArrayList<PersonEvent> eventsOfMother = new ArrayList<>();
            eventsOfMother = DataCache.getInstance().getMapPersonToEvents().get(mother.getPersonID());
            eventsOfMother = DataCache.getInstance().sortEvents(eventsOfMother);
            if (eventsOfMother.size() != 0) {
                PersonEvent firstMotherEvent = new PersonEvent();
                firstMotherEvent = eventsOfMother.get(0);
                boolean eventIsNotFiltered = DataCache.getInstance().findEvent(firstMotherEvent);
                if (eventIsNotFiltered) {
                    LatLng eventLocation = new LatLng(event.getLatitude(), event.getLongitude());
                    LatLng fatherLocation = new LatLng(firstMotherEvent.getLatitude(),
                            firstMotherEvent.getLongitude());
                    PolylineOptions polyline = new PolylineOptions();
                    polyline.add(eventLocation, fatherLocation);
                    width = width + 4;
                    polyline.width(width);
                    width = width - 4;
                    polyline.color(Color.BLUE);
                    setOfLines.add(polyline);
                    getFamilyTreeLines(mother, firstMotherEvent, width);//recursive call
                }
            }
        }
    }

    private void clearLines() {
        if (lines.size() != 0){
            for(int i = 0; i < lines.size(); ++i){
                lines.get(i).remove();
            }
        }
        lines.clear();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (map != null) {
            //we clear the map
            map.clear();
            //get the map of event type to color from data base
            eventColor = DataCache.getInstance().getMapEventColors();
            //we get the data from
            personEvents = new ArrayList<PersonEvent>(Arrays.asList(DataCache.getInstance().getPersonEvents()));
            for(int i = 0; i < personEvents.size(); ++i){
                //we add a marker for each location and event
                LatLng location = new LatLng(personEvents.get(i).getLatitude(), personEvents.get(i).getLongitude());
                Float color = eventColor.get(personEvents.get(i).getEventType());
                Marker marker = map.addMarker(new MarkerOptions().position(location).
                        icon(BitmapDescriptorFactory.defaultMarker(color)));//for color (BitmapDescriptorFactory.HUE_BLUE)
                marker.setTag(personEvents.get(i));
            }
            //we draw the proper lines based on the filters
            if(marker != null && eventInf != null & imageView != null &&
                    markerEvent.getPersonID() != null){
                drawLines();
            }
        }
    }

    protected void drawLines() {
        //life story lines
        if(DataCache.getInstance().isLifeStorySwitch()) {
            //we get all the events of the person of the marker
            ArrayList<PersonEvent> eventsOfPerson = new ArrayList<>();
            eventsOfPerson = DataCache.getInstance().getMapPersonToEvents().get(markerEvent.getPersonID());
            //we sort the events in chronological order
            eventsOfPerson = DataCache.getInstance().sortEvents(eventsOfPerson);
            //LatLng latLngMarker = marker.getPosition();
            LatLng latLngMarker = latitudeLongitude;
            for (int i = 0; i < eventsOfPerson.size(); ++i) {
                LatLng latLngEvent = new LatLng(eventsOfPerson.get(i).getLatitude(),
                        eventsOfPerson.get(i).getLongitude());
                if(!eventsOfPerson.get(i).getEventID().equals(markerEvent.getEventID())){//find
                    Polyline line = map.addPolyline(new PolylineOptions().clickable(false).add(
                            latLngMarker,
                            latLngEvent).color(Color.GREEN));
                    lines.add(line);
                    latLngMarker = latLngEvent;
                }
            }
        }
        //family tree lines
        if(DataCache.getInstance().isFamilyTreeSwitch()) {
            Person personOfMarker = new Person();
            personOfMarker = DataCache.getInstance().getMapPeople().get(markerEvent.getPersonID());
            //we initialize the variable
            setOfLines = new HashSet<>();
            float width = 14;
            getFamilyTreeLines(personOfMarker, markerEvent, width);
            //we add the lines in the map
            if (setOfLines.size() != 0){
                for(PolylineOptions line : setOfLines){
                    Polyline familyLine = map.addPolyline(line);
                    lines.add(familyLine);
                }
            }
        }
        //spouse lines
        if(DataCache.getInstance().isSpouseLines()){
            //we get all the events of spouse
            ArrayList<PersonEvent> eventsOfSpouse = new ArrayList<>();
            //husband to wife
            if (markerEvent.getGender().equals("M") && DataCache.getInstance().isFemaleEventsSwitch()){
                Person personOfMarker = new Person();
                personOfMarker = DataCache.getInstance().getMapPeople().get(markerEvent.getPersonID());
                if (personOfMarker.getSpouseID() != null) {
                    Person spouse = new Person();
                    spouse = DataCache.getInstance().getMapPeople().get(personOfMarker.getSpouseID());
                    eventsOfSpouse = DataCache.getInstance().getMapPersonToEvents().get(spouse.getPersonID());
                    eventsOfSpouse = DataCache.getInstance().sortEvents(eventsOfSpouse);
                    if (eventsOfSpouse.size() != 0){ //there is at least one event for the spouse
                        PersonEvent firstEventOfSpouse = new PersonEvent();
                        firstEventOfSpouse = eventsOfSpouse.get(0);
                        boolean eventIsNotFiltered = DataCache.getInstance().findEvent(firstEventOfSpouse);
                        if(eventIsNotFiltered) {
                            //LatLng latLngMarker = marker.getPosition();
                            LatLng latLngMarker = latitudeLongitude;
                            LatLng latLngSpouse = new LatLng(firstEventOfSpouse.getLatitude(),
                                    firstEventOfSpouse.getLongitude());
                            Polyline line = map.addPolyline(new PolylineOptions().clickable(false).add(
                                    latLngMarker,
                                    latLngSpouse).color(Color.MAGENTA));
                            lines.add(line);
                        }
                    }
                }
            }
            //wife to husband
            if (markerEvent.getGender().equals("F") && DataCache.getInstance().isMaleEventsSwitch()){
                Person personOfMarker = new Person();
                personOfMarker = DataCache.getInstance().getMapPeople().get(markerEvent.getPersonID());
                if (personOfMarker.getSpouseID() != null){
                    Person spouse = new Person();
                    spouse = DataCache.getInstance().getMapPeople().get(personOfMarker.getSpouseID());
                    eventsOfSpouse = DataCache.getInstance().getMapPersonToEvents().get(spouse.getPersonID());
                    eventsOfSpouse = DataCache.getInstance().sortEvents(eventsOfSpouse);
                    if (eventsOfSpouse.size() != 0){ //there is at least one event for the spouse
                        PersonEvent firstEventOfSpouse = new PersonEvent();
                        firstEventOfSpouse = eventsOfSpouse.get(0);
                        boolean eventIsNotFiltered = DataCache.getInstance().findEvent(firstEventOfSpouse);
                        if(eventIsNotFiltered) {
                            //LatLng latLngMarker = marker.getPosition();
                            LatLng latLngMarker = latitudeLongitude;
                            LatLng latLngSpouse = new LatLng(firstEventOfSpouse.getLatitude(),
                                    firstEventOfSpouse.getLongitude());
                            Polyline line = map.addPolyline(new PolylineOptions().clickable(false).add(
                                    latLngMarker,
                                    latLngSpouse).color(Color.MAGENTA));
                            lines.add(line);
                        }
                    }
                }
            }
        }
    }



    /*
    Some useful Map related Methods:
    When people go to setting and they change things (turn of a line, filter, etc.) when that happens
    we need to clear the map because there is no easy way to delete certain lines. Then we add back what
    we want (user modification of the settings). Clear wipes out the lines and also the markers:
    GoogleMap.clear()
    GoogleMap.addMarker(...)
    This will add lines like the ones needed for the map assignment. We can specify the locations that
    the line should be drawn between them. We can control the color, thickness, etc.
    GoogleMap.addPolyLine(...)
    GoogleMap.moveCamera(...)
    GoogleMap.animateCamera(...)
    GoogleMap.setMapType(...)
    We do not have multiple listeners for all the markers. We only have one listener. Notice we pass
    a marker, so we will get a reference to the marker that was clicked.
    GoogleMap.setOnMarkerClickListener(Marker)
    We need to know some information in the marker when we place it on the map. Before we add any marker
    to the map we need to set a tag for them. The tag is any serializable (a string, or an object that
    we associate with that marker. This could be eventIDs or event objects
    Markers.setTag(...)
    Marker.getTag(...)
    */
}