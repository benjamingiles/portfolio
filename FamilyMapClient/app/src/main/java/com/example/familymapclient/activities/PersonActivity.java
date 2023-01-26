package com.example.familymapclient.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.familymapclient.DataCache;
import com.example.familymapclient.R;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import model.Event;
import model.Person;

public class PersonActivity extends AppCompatActivity {

    private Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);
        DataCache dataCache = DataCache.getInstance();

        Intent intent = getIntent();
        person = dataCache.getPerson(intent.getStringExtra("personID"));

        TextView firstNameView = findViewById(R.id.personFirstName);
        firstNameView.setText(person.getFirstName());

        TextView lastNameView = findViewById(R.id.personLastName);
        lastNameView.setText(person.getLastName());

        TextView genderView = findViewById(R.id.personGender);

        if (person.getGender().equals("m")) {
            genderView.setText(R.string.male);
        } else {
            genderView.setText(R.string.female);
        }

        boolean canGetEvents = true;
        if (!dataCache.isShowMaleEvents() && person.getGender().toLowerCase(Locale.ROOT).equals("m")) {
            canGetEvents = false;
        }
        if (!dataCache.isShowFemaleEvents() && person.getGender().toLowerCase(Locale.ROOT).equals("f")) {
            canGetEvents = false;
        }
        if (!dataCache.canMarkFromFamilyFilter(person)) {
            canGetEvents = false;
        }

        List<Event> personEvents = new ArrayList<>();

        if (canGetEvents) {
            personEvents = dataCache.getPersonEvents(person.getPersonID());
        }

        List<Person> family = new ArrayList<>();

        if (person.getFatherID() != null) {
            family.add(dataCache.getPerson(person.getFatherID()));
        }
        if (person.getMotherID() != null) {
            family.add(dataCache.getPerson(person.getMotherID()));
        }
        if (person.getSpouseID() != null) {
            family.add(dataCache.getPerson(person.getSpouseID()));
        }

        family.addAll(dataCache.getChildren(person.getPersonID()));

        expandableListView.setAdapter(new ExpandableListAdapter(personEvents, family));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        return true;
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final int EVENT_GROUP_POSITION = 0;
        private static final int FAMILY_GROUP_POSITION = 1;

        private final List<Event> events;
        private final List<Person> family;

        ExpandableListAdapter(List<Event> events, List<Person> family) {
            this.events = events;
            this.family = family;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    return events.size();
                case FAMILY_GROUP_POSITION:
                    return family.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    titleView.setText(R.string.eventTitle);
                    break;
                case FAMILY_GROUP_POSITION:
                    titleView.setText(R.string.familyTitle);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            View itemView;

            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.family_item, parent, false);
                    initializeEventView(itemView, childPosition);
                    break;
                case FAMILY_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.family_item, parent, false);
                    initializeFamilyView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        private void initializeEventView(View eventView, final int childPosition) {
            Event event = events.get(childPosition);

            String eventInfo = event.getEventType().toUpperCase(Locale.ROOT) + ": "
                    + event.getCity() + ", " + event.getCountry() + "(" + event.getYear() + ")";

            TextView eventInfoView = eventView.findViewById(R.id.personName);
            eventInfoView.setText(eventInfo);

            String fullName = person.getFirstName() + " " + person.getLastName();

            TextView personNameView = eventView.findViewById(R.id.familyRelation);
            personNameView.setText(fullName);

            ImageView eventImage = eventView.findViewById(R.id.personGenderIcon);
            Drawable icon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).sizeDp(40);

            eventImage.setImageDrawable(icon);

            eventView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PersonActivity.this, EventActivity.class);
                    intent.putExtra("eventID", event.getEventID());
                    startActivity(intent);
                }
            });
        }

        private void initializeFamilyView(View familyView, final int childPosition) {
            Person familyMember = family.get(childPosition);

            String fullName = familyMember.getFirstName() + " " + familyMember.getLastName();

            TextView nameTextView = familyView.findViewById(R.id.personName);
            nameTextView.setText(fullName);

            String relation = "Child";

            if (person.getFatherID() != null &&
                    person.getFatherID().equals(familyMember.getPersonID())) {
                relation = "Father";
            }

            if (person.getMotherID() != null &&
                    person.getMotherID().equals(familyMember.getPersonID())) {
                relation = "Mother";
            }

            if (person.getSpouseID() != null &&
                    person.getSpouseID().equals(familyMember.getPersonID())) {
                relation = "Spouse";
            }

            TextView relationView = familyView.findViewById(R.id.familyRelation);
            relationView.setText(relation);

            ImageView genderIcon = familyView.findViewById(R.id.personGenderIcon);
            Drawable icon;

            if (familyMember.getGender().toLowerCase(Locale.ROOT).equals("m")) {
                icon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_male)
                        .colorRes(R.color.blue).sizeDp(40);
            } else {
                icon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_female)
                        .colorRes(R.color.pink).sizeDp(40);
            }

            genderIcon.setImageDrawable(icon);

            familyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
                    intent.putExtra("personID", familyMember.getPersonID());
                    startActivity(intent);
                }
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

}