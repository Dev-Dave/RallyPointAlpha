package com.example.dcaouette.rallypointalpha;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;


public class TeamDetailsActivity extends AppCompatActivity {

    private FloatingActionButton addTeamMemberFab;
    private FloatingActionButton addRallyFab;
    private String teamKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }
        Bundle teamBundle = getIntent().getExtras();
        if (teamBundle != null) {
            teamKey = teamBundle.getString("TEAM_KEY");
        } else {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            teamKey = sharedPreferences.getString("TEAM_KEY", "");
        }

        addTeamMemberFab = (FloatingActionButton) findViewById(R.id.add_team_member_fab);
        final Intent addTeamMemberIntent = new Intent(this, AddTeamMemberActivity.class);
        addTeamMemberFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTeamMemberIntent.putExtra("TEAM_KEY", teamKey);
                startActivity(addTeamMemberIntent);
            }
        });
        addTeamMemberFab.hide();

        addRallyFab = (FloatingActionButton) findViewById(R.id.add_rally_fab);
        final Intent createRallyIntent = new Intent(this, CreateRallyActivity.class);
        addRallyFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRallyIntent.putExtra("TEAM_KEY", teamKey);
                startActivity(createRallyIntent);
            }
        });
        addRallyFab.hide();
    }

    public void showTeamLeaderActions() {
        addRallyFab.show();
        addTeamMemberFab.show();
    }

    public void showTeamSponsorActions() {
        addRallyFab.hide();
        addTeamMemberFab.show();
    }

    public void showTeamMemberActions() {
        addRallyFab.hide();
        addTeamMemberFab.hide();
    }

    public void setNewActionBarTitle(String newTitle) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(newTitle);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("TEAM_KEY", teamKey);
        //editor.putString("HOME_TITLE", pageTitle);
        editor.apply();
    }

}
