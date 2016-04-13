package com.example.dcaouette.rallypointalpha;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class CreateTeamActivityFragment extends Fragment implements View.OnClickListener {

    private EditText teamNameEditText;
    private EditText teamDescriptionEditText;
    private Firebase mRef;
    private Firebase userGroupsRef;
    private static final String URL = QuickRefs.ROOT_URL;
    String myGroups;

    public CreateTeamActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_team, container, false);
        Firebase.setAndroidContext(getContext());
        mRef = new Firebase(QuickRefs.ROOT_URL);
        userGroupsRef = mRef.child("users/" + mRef.getAuth().getUid() + "/groups");
        //userGroupsRef.addChildEventListener();

        teamNameEditText = (EditText)rootView.findViewById(R.id.team_name_edit_text);
        teamDescriptionEditText = (EditText)rootView.findViewById(R.id.team_description_edit_text);

        Button createTeamButton = (Button)rootView.findViewById(R.id.createTeamButton);
        Button cancelTeamButton = (Button)rootView.findViewById(R.id.cancelCreateTeamButton);

        createTeamButton.setOnClickListener(this);
        cancelTeamButton.setOnClickListener(this);

        //String myKey = mRef.getAuth().getUid();
        //Log.d("myKey", myKey);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int viewID = view.getId();
        switch (viewID) {
            case R.id.createTeamButton:
                saveData();
                launchBack();
                break;
            case R.id.cancelCreateTeamButton:
                launchBack();
                break;
        }

    }

    // Create Team
    private void saveData() {
        String teamName = teamNameEditText.getText().toString();
        String teamDescription = teamDescriptionEditText.getText().toString();
        String myKey = mRef.getAuth().getUid();
        Map<String, Object> initialMembers = new HashMap();
        initialMembers.put(myKey, new Integer(100));
        Team team = new Team(teamName, teamDescription, initialMembers);
        CreateTeamAdapter adapter = new CreateTeamAdapter(getContext());
        adapter.add(team);
        //String teamKey = team.getKey();
        //Map<String, Object> addTeam = new HashMap();
        //addTeam.put(teamKey, true);
        //mRef.child("users").child("groups").updateChildren(addTeam);
        //Log.d("Fab", "Fab found");
    }

    // Go back to Home menu with focus on Teams tab
    private void launchBack() {
        final Intent backToHomeIntent = new Intent(getActivity(), HomeActivity.class);
        backToHomeIntent.putExtra("START_POS", 2);
        startActivity(backToHomeIntent);
    }

}

/**
 * Created by dcaouette on 3/27/16.
 */
class CreateTeamAdapter {

    private Firebase userRef;
    private Firebase teamsRef;
    private String userKey;

    public CreateTeamAdapter(Context context) {
        Firebase.setAndroidContext(context);
        teamsRef = new Firebase(QuickRefs.TEAMS_URL);
        //teamsRef.removeEventListener();
        teamsRef.addChildEventListener(new CreateTeamChildEventListener());
        userKey = teamsRef.getAuth().getUid();
        userRef = new Firebase(QuickRefs.USERS_URL).child(userKey + "/groups");
    }

    public void add(Team team) {
        Map<String, Object> teamLeader = new HashMap();
        teamLeader.put(userKey, new Integer(1000));
        team.setTeamMembers(teamLeader);
        teamsRef.push().setValue(team);
    }

    class CreateTeamChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Map<String, Object> map = new HashMap<>();
            map.put(dataSnapshot.getKey(), true);
            userRef.updateChildren(map);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }

}
