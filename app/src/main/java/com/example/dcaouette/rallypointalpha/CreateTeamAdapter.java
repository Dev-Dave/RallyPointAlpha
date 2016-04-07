package com.example.dcaouette.rallypointalpha;

import android.content.Context;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dcaouette on 3/27/16.
 */
public class CreateTeamAdapter {

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
