package com.example.dcaouette.rallypointalpha;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class CreateTeamActivity extends AppCompatActivity {

    FloatingActionButton confirm_fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


//        final CreateTeamActivityFragment frag = (CreateTeamActivityFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_create_team);
//
//        confirm_fab = (FloatingActionButton)findViewById(R.id.finalize_team_fab);
//        final Intent backToHomeIntent = new Intent(this, HomeActivity.class);
//        // Initialize focus to teams page
//        backToHomeIntent.putExtra("START_POS", 2);
//        confirm_fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                // Write changes to database
//                frag.saveData();
//                startActivity(backToHomeIntent);
//            }
//        });

    }

}
