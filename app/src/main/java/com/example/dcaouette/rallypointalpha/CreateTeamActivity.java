package com.example.dcaouette.rallypointalpha;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.Firebase;

public class CreateTeamActivity extends AppCompatActivity {

    FloatingActionButton confirm_fab;
    private Firebase rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        rootRef = new Firebase(QuickRefs.ROOT_URL);
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

    @Override
    public  boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_team, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* navigate up on item selected */
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.create_team_logout:
                rootRef.unauth();
                startActivity(new Intent(this, MainActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
