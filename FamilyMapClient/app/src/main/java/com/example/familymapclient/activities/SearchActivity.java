package com.example.familymapclient.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.familymapclient.DataCache;
import com.example.familymapclient.R;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.List;
import java.util.Locale;

import model.Event;
import model.Person;

public class SearchActivity extends AppCompatActivity {

    private static final int EVENT_SEARCH_RESULT = 1;
    private static final int PERSON_SEARCH_RESULT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        SearchView searchView = findViewById(R.id.searchBar);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                DataCache dataCache = DataCache.getInstance();
                List<Person> people = dataCache.searchPeople(s);
                List<Event> events = dataCache.searchEvents(s);

                SearchAdapter adapter = new SearchAdapter(people, events);
                recyclerView.setAdapter(adapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

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

    private class SearchAdapter extends RecyclerView.Adapter<SearchHolder> {
        private final List<Person> people;
        private final List<Event> events;

        SearchAdapter(List<Person> people, List<Event> events) {
            this.people = people;
            this.events = events;
        }

        @Override
        public int getItemViewType(int position) {
            return position < people.size() ? PERSON_SEARCH_RESULT : EVENT_SEARCH_RESULT;
        }

        @NonNull
        @Override
        public SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            view = getLayoutInflater().inflate(R.layout.search_person, parent, false);

            return new SearchHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchHolder holder, int position) {
            if (position < people.size()) {
                holder.bind(people.get(position));
            } else {
                holder.bind(events.get(position));
            }
        }

        @Override
        public int getItemCount() { return people.size() + events.size(); }
    }

    private class SearchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView searchIcon;
        private final TextView searchResult;
        private final TextView searchDetails;

        private final int viewType;
        private Person person;
        private Event event;

        SearchHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            searchIcon = itemView.findViewById(R.id.searchIcon);
            searchResult = itemView.findViewById(R.id.searchResult);
            searchDetails = itemView.findViewById(R.id.searchDetails);
        }

        private void bind(Person person) {
            this.person = person;
            if (person.getGender().toLowerCase(Locale.ROOT).equals("m")) {
                Drawable icon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_male)
                        .colorRes(R.color.blue).sizeDp(40);
                searchIcon.setImageDrawable(icon);
            } else {
                Drawable icon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_female)
                        .colorRes(R.color.pink).sizeDp(40);
                searchIcon.setImageDrawable(icon);
            }

            String fullName = person.getFirstName() + " " + person.getLastName();
            searchResult.setText(fullName);
        }

        private void bind(Event event) {
            this.event = event;
            DataCache dataCache = DataCache.getInstance();
            Person eventPerson = dataCache.getPerson(event.getPersonID());

            String name = eventPerson.getFirstName() + " " + eventPerson.getLastName();
            String eventType = event.getEventType().toUpperCase(Locale.ROOT);
            String city = event.getCity();
            String country = event.getCountry();
            int year = event.getYear();

            String fullData = eventType + ": " + city + ", " + country + " (" + year + ")";
            Drawable icon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker).sizeDp(40);

            searchIcon.setImageDrawable(icon);
            searchResult.setText(fullData);
            searchDetails.setText(name);
        }

        @Override
        public void onClick(View view) {
            if (viewType == PERSON_SEARCH_RESULT) {
                Intent intent = new Intent(SearchActivity.this, PersonActivity.class);
                intent.putExtra("personID", person.getPersonID());
                startActivity(intent);
            } else {
                Intent intent = new Intent(SearchActivity.this, EventActivity.class);
                intent.putExtra("eventID", event.getEventID());
                startActivity(intent);
            }

        }
    }

}