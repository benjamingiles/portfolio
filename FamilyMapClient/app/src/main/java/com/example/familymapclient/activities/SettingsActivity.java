package com.example.familymapclient.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.familymapclient.DataCache;
import com.example.familymapclient.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        DataCache dataCache = DataCache.getInstance();

        TextView logoutView = findViewById(R.id.logout);
        TextView showLogoutView = findViewById(R.id.showLogout);

        Switch storyLineSwitch = findViewById(R.id.storyLinesSwitch);
        Switch familyTreeLinesSwitch = findViewById(R.id.familyTreeLinesSwitch);
        Switch spouseLinesSwitch = findViewById(R.id.spouseLinesSwitch);
        Switch fatherLinesSwitch = findViewById(R.id.fatherLinesSwitch);
        Switch motherLinesSwitch = findViewById(R.id.motherLinesSwitch);
        Switch maleLinesSwitch = findViewById(R.id.maleLinesSwitch);
        Switch femaleLinesSwitch = findViewById(R.id.femaleLinesSwitch);

        storyLineSwitch.setChecked(dataCache.isShowLifeStoryLines());
        familyTreeLinesSwitch.setChecked(dataCache.isShowFamilyTreeLines());
        spouseLinesSwitch.setChecked(dataCache.isShowSpouseLines());
        fatherLinesSwitch.setChecked(dataCache.isShowFathersSide());
        motherLinesSwitch.setChecked(dataCache.isShowMothersSide());
        maleLinesSwitch.setChecked(dataCache.isShowMaleEvents());
        femaleLinesSwitch.setChecked(dataCache.isShowFemaleEvents());

        storyLineSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dataCache.setShowLifeStoryLines(b);
            }
        });

        familyTreeLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dataCache.setShowFamilyTreeLines(b);
            }
        });

        spouseLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dataCache.setShowSpouseLines(b);
            }
        });

        fatherLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dataCache.setShowFathersSide(b);
            }
        });

        motherLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dataCache.setShowMothersSide(b);
            }
        });

        maleLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dataCache.setShowMaleEvents(b);
            }
        });

        femaleLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dataCache.setShowFemaleEvents(b);
            }
        });

        logoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        showLogoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
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
}