package com.example.familymapclient.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.familymapclient.activities.PersonActivity;
import com.example.familymapclient.activities.SearchActivity;
import com.example.familymapclient.activities.SettingsActivity;
import com.example.familymapclient.DataCache;
import com.example.familymapclient.R;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import model.Event;
import model.Person;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private GoogleMap map;
    private List<Polyline> polylines = new ArrayList<>();
    private TextView mapTextView;
    private ImageView genderView;
    private String activity;

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        View view = layoutInflater.inflate(R.layout.fragment_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        genderView = view.findViewById(R.id.genderIcon);
        Drawable icon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_map_marker).sizeDp(40);

        genderView.setImageDrawable(icon);
        mapTextView = view.findViewById(R.id.mapTextView);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Iconify.with(new FontAwesomeModule());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);


        Bundle arguments = getArguments();
        assert arguments != null;
        activity = arguments.getString("Activity");

        if (activity.equals("main")) {
            inflater.inflate(R.menu.menu, menu);

            MenuItem searchMenuItem = menu.findItem(R.id.searchMenuItem);
            searchMenuItem.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_search).
                    colorRes(R.color.white).
                    actionBarSize());

            MenuItem settingsMenuItem = menu.findItem(R.id.settingsMenuItem);
            settingsMenuItem.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_gear).
                    colorRes(R.color.white).
                    actionBarSize());

            settingsMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Intent intent = new Intent(getActivity(), SettingsActivity.class);
                    startActivity(intent);
                    return false;
                }
            });

            searchMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Intent intent = new Intent(getActivity(), SearchActivity.class);
                    startActivity(intent);
                    return false;
                }
            });
        }
    }

    @Override
    public void onMapLoaded() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) {
            map.clear();
            fillMap();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);
        fillMap();
    }

    private void fillMap() {
        DataCache dataCache = DataCache.getInstance();

        Map<String, Float> eventColors = new HashMap<>();
        Float[] colors = {
                BitmapDescriptorFactory.HUE_BLUE,
                BitmapDescriptorFactory.HUE_RED,
                BitmapDescriptorFactory.HUE_VIOLET,
                BitmapDescriptorFactory.HUE_AZURE,
                BitmapDescriptorFactory.HUE_ORANGE,
                BitmapDescriptorFactory.HUE_ROSE,
                BitmapDescriptorFactory.HUE_CYAN,
                BitmapDescriptorFactory.HUE_GREEN,
                BitmapDescriptorFactory.HUE_MAGENTA,
                BitmapDescriptorFactory.HUE_YELLOW };
        int arrayPosition = 0;

        Bundle arguments = getArguments();
        assert arguments != null;
        activity = arguments.getString("Activity");

        for (Event event : dataCache.getEvents()) {
            Person person = dataCache.getPerson(event.getPersonID());

            if (!dataCache.isShowMaleEvents() && person.getGender().toLowerCase(Locale.ROOT).equals("m")) {
                continue;
            }
            if (!dataCache.isShowFemaleEvents() && person.getGender().toLowerCase(Locale.ROOT).equals("f")) {
                continue;
            }

            if (dataCache.canMarkFromFamilyFilter(person)) {
                float googleColor;
                String eventType = event.getEventType().toLowerCase(Locale.ROOT);

                if (eventColors.containsKey(eventType)) {
                    googleColor = eventColors.get(eventType);
                } else {
                    eventColors.put(eventType, colors[arrayPosition % colors.length]);
                    googleColor = colors[arrayPosition % colors.length];
                    ++arrayPosition;
                }

                Marker marker = map.addMarker(new MarkerOptions().
                        position(new LatLng(event.getLatitude(), event.getLongitude())).
                        title(event.getCity() + ", " + event.getCountry()).
                        icon(BitmapDescriptorFactory.defaultMarker(googleColor)));

                assert marker != null;
                marker.setTag(event);
            }
        }

        if (activity.equals("event")) {
            String eventID = arguments.getString("eventID");
            Event event = dataCache.getEvent(eventID);

            populateBottomText(event);
            if (dataCache.isShowFamilyTreeLines()) {
                drawFamilyLines(event);
            }
            if (dataCache.isShowSpouseLines()) {
                drawSpouseLines(event);
            }
            if (dataCache.isShowLifeStoryLines()) {
                drawLifeStoryLines(event);
            }

            LatLng startPoint = new LatLng(event.getLatitude(), event.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLng(startPoint));

            mapTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), PersonActivity.class);
                    intent.putExtra("personID", event.getPersonID());
                    startActivity(intent);
                }
            });

        }

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                clearPolylines();
                Event markerEvent = (Event) marker.getTag();
                assert markerEvent != null;
                populateBottomText(markerEvent);
                if (dataCache.isShowLifeStoryLines()) {
                    drawLifeStoryLines(markerEvent);
                }
                if (dataCache.isShowFamilyTreeLines()) {
                    drawFamilyLines(markerEvent);
                }
                if (dataCache.isShowSpouseLines()) {
                    drawSpouseLines(markerEvent);
                }

                mapTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), PersonActivity.class);
                        intent.putExtra("personID", markerEvent.getPersonID());
                        startActivity(intent);
                    }
                });

                return false;
            }
        });
    }

    private void drawLine(Event startEvent, Event endEvent, float googleColor, float width) {
        LatLng startPoint = new LatLng(startEvent.getLatitude(), startEvent.getLongitude());
        LatLng endPoint = new LatLng(endEvent.getLatitude(), endEvent.getLongitude());

        PolylineOptions options = new PolylineOptions()
                .add(startPoint)
                .add(endPoint)
                .color((int) googleColor)
                .width(width);
        Polyline line = map.addPolyline(options);
        polylines.add(line);
    }

    private void drawLifeStoryLines(Event markerEvent) {
        DataCache dataCache = DataCache.getInstance();
        Person person = dataCache.getPerson(markerEvent.getPersonID());

        if (!dataCache.isShowMaleEvents() && person.getGender().toLowerCase(Locale.ROOT).equals("m")) {
            return;
        }
        if (!dataCache.isShowFemaleEvents() && person.getGender().toLowerCase(Locale.ROOT).equals("f")) {
            return;
        }

        List<Event> lifeEvents = dataCache.getPersonEvents(person.getPersonID());

        if (lifeEvents.size() > 0) {
            Event temp = lifeEvents.get(0);
            for (Event event : lifeEvents) {
                drawLine(temp, event, Color.RED, 15);
                temp = event;
            }
        }
    }

    private void drawSpouseLines(Event markerEvent) {
        DataCache dataCache = DataCache.getInstance();
        Person person = dataCache.getPerson(markerEvent.getPersonID());
        if (person.getSpouseID() == null) {
            return;
        }
        Person spouse = dataCache.getPerson(person.getSpouseID());

        if (!dataCache.isShowMaleEvents() && spouse.getGender().toLowerCase(Locale.ROOT).equals("m")) {
            return;
        }
        if (!dataCache.isShowFemaleEvents() && spouse.getGender().toLowerCase(Locale.ROOT).equals("f")) {
            return;
        }

        if ((dataCache.canMarkFromFamilyFilter(person) && dataCache.canMarkFromFamilyFilter(spouse))
                || person.getPersonID().equals(dataCache.getRootPersonID())) {
            List<Event> spouseEvents = dataCache.getPersonEvents(person.getSpouseID());
            if (spouseEvents.size() > 0) {
                drawLine(markerEvent, spouseEvents.get(0), Color.BLUE, 5);
            }
        }
    }

    private void drawFamilyLines(Event markerEvent) {
        DataCache dataCache = DataCache.getInstance();
        Person person = dataCache.getPerson(markerEvent.getPersonID());

        drawFamilyLinesHelper(person, markerEvent, 35);
    }

    public void drawFamilyLinesHelper(Person person, Event event, float width) {
        DataCache dataCache = DataCache.getInstance();

        if (person.getFatherID() != null && dataCache.isShowMaleEvents()) {
            Person father = dataCache.getPerson(person.getFatherID());
            if (dataCache.canMarkFromFamilyFilter(father)) {
                List<Event> fatherEvents = dataCache.getPersonEvents(father.getPersonID());
                if (fatherEvents.size() > 0) {
                    drawLine(event, fatherEvents.get(0), Color.GREEN, width);
                    drawFamilyLinesHelper(father, fatherEvents.get(0), width - 10);
                }
            }
        }
        if (person.getMotherID() != null && dataCache.isShowFemaleEvents()) {
            Person mother = dataCache.getPerson(person.getMotherID());
            if (dataCache.canMarkFromFamilyFilter(mother)) {
                List<Event> motherEvents = dataCache.getPersonEvents(mother.getPersonID());
                if (motherEvents.size() > 0) {
                    drawLine(event, motherEvents.get(0), Color.GREEN, width);
                    drawFamilyLinesHelper(mother, motherEvents.get(0), width - 10);
                }
            }
        }
    }

    private void clearPolylines() {
        for (Polyline line : polylines) {
            line.remove();
        }
        polylines.clear();
    }

    private void populateBottomText(Event event) {
        DataCache dataCache = DataCache.getInstance();
        Person person = dataCache.getPerson(event.getPersonID());

        String name = person.getFirstName() + " " + person.getLastName();
        String eventType = event.getEventType().toUpperCase(Locale.ROOT);
        String city = event.getCity();
        String country = event.getCountry();
        String gender = person.getGender();
        int year = event.getYear();

        String fullData = name + "\n" + eventType + ": " + city + ", " + country + " (" + year + ")";

        mapTextView.setText(fullData);

        Drawable genderIcon;
        if (gender.toLowerCase(Locale.ROOT).equals("m")) {
            genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).
                    colorRes(R.color.blue).sizeDp(40);
        } else {
            genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).
                    colorRes(R.color.pink).sizeDp(40);
        }
        genderView.setImageDrawable(genderIcon);

    }

}