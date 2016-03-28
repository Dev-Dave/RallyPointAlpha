package com.example.dcaouette.rallypointalpha;

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
        Log.d("Fab", "Fab found");




    }

    // Go back to Home menue with focus on Teams tab
    private void launchBack() {
        final Intent backToHomeIntent = new Intent(getActivity(), HomeActivity.class);
        backToHomeIntent.putExtra("START_POS", 2);
        startActivity(backToHomeIntent);
    }

    class GroupChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

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
