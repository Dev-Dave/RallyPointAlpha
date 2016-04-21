package com.example.dcaouette.rallypointalpha;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddTeamMemberActivityFragment extends Fragment implements View.OnClickListener {

    private String teamKey = "";
    private String mainUserKey;
    private Firebase rootRef;
    private AddTeamMembersAdaptor addTeamMembersAdaptor;
    private Button addTeamMembersButton;
    private Button cancelButton;

    public AddTeamMemberActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_team_member, container, false);
        Firebase.setAndroidContext(getActivity());
        rootRef = new Firebase(QuickRefs.ROOT_URL);
        mainUserKey = rootRef.getAuth().getUid();
        Bundle teamBundle = getActivity().getIntent().getExtras();
        if (teamBundle != null) {
            teamKey = teamBundle.getString("TEAM_KEY");
        }
        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view_add_team_members);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        addTeamMembersAdaptor = new AddTeamMembersAdaptor(getActivity(), teamKey);
        recyclerView.setAdapter(addTeamMembersAdaptor);

        addTeamMembersButton = (Button)rootView.findViewById(R.id.addTeamMembersButton);
        cancelButton = (Button)rootView.findViewById(R.id.cancelAddTeamMembersButton);

        addTeamMembersButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        switch (viewID) {
            case R.id.addTeamMembersButton:
                saveData();
                launchBack();
                break;
            case R.id.cancelAddTeamMembersButton:
                launchBack();
                break;
        }
    }

    private void saveData() {
        Map<String, Object> userKeyMap = new HashMap<>();
        for (User user: addTeamMembersAdaptor.getAddedMemberList()) {
            // Add user to team members
            userKeyMap.put(user.getKey(), 100);

            // Add team to user's groups
            Map<String, Object> userGroupsMap = new HashMap<>();
            userGroupsMap.put(teamKey, true);
            rootRef.child("users").child(mainUserKey).child("groups").updateChildren(userGroupsMap);
        }
        if (!userKeyMap.isEmpty())
            rootRef.child("teams").child(teamKey).child("members").updateChildren(userKeyMap);
    }

    private void launchBack() {
        Intent teamDetailsIntent = new Intent(getActivity(), TeamDetailsActivity.class);;
        teamDetailsIntent.putExtra("TEAM_KEY", teamKey);
        getActivity().startActivity(teamDetailsIntent);
    }
}

class AddTeamMembersAdaptor extends RecyclerView.Adapter<AddTeamMembersAdaptor.ViewHolder> {

    private String mainUserKey;
    private Firebase rootRef;
    private Firebase usersRef;
    private Firebase teamRef;
    private Firebase mainUserRef;
    private ArrayList<User> memberList;
    private ArrayList<String> currentMemberKeys;
    private ArrayList<User> addedMemberList;

    public AddTeamMembersAdaptor(Context context, String newTeamKey) {
        Firebase.setAndroidContext(context);
        rootRef = new Firebase(QuickRefs.ROOT_URL);
        usersRef = rootRef.child("users");
        mainUserKey = rootRef.getAuth().getUid();
        teamRef = rootRef.child("teams").child(newTeamKey);
        mainUserRef = rootRef.child("users").child(mainUserKey);
        memberList = new ArrayList<>();
        currentMemberKeys = new ArrayList<>();
        addedMemberList = new ArrayList<>();
        teamRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Team team = dataSnapshot.getValue(Team.class);
                team.setKey(dataSnapshot.getKey());
                Map<String, Object> currentMembers = team.getTeamMembers();
                currentMemberKeys = new ArrayList<>();
                for (String userKey : currentMembers.keySet()) {
                    currentMemberKeys.add(userKey);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        mainUserRef.child("members").addChildEventListener(new MembersChildEventListener());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_team_add_members, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        User user = memberList.get(position);
        holder.emailTextView.setText(user.getEmail());
        holder.addedMembersCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User selectedUser = memberList.get(position);
                if (addedMemberList.contains(selectedUser)) {
                    addedMemberList.remove(selectedUser);
                } else {
                    addedMemberList.add(0, selectedUser);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public ArrayList<User> getAddedMemberList() {
        return addedMemberList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView emailTextView;
        private CheckBox addedMembersCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            emailTextView = (TextView)itemView.findViewById(R.id.list_item_team_add_member_text);
            addedMembersCheckBox = (CheckBox)itemView.findViewById(R.id.add_team_member_checkbox);

            emailTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User selectedUser = memberList.get(getAdapterPosition());
                    if (addedMemberList.contains(selectedUser)) {
                        addedMemberList.remove(selectedUser);
                        addedMembersCheckBox.setChecked(false);
                    } else {
                        addedMemberList.add(0, selectedUser);
                        addedMembersCheckBox.setChecked(true);
                    }
                }
            });
        }
    }
    /**
     * Inner class for updating the view from changes in the database
     */
    class MembersChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            // Find user class for each member
            Query memberClassRef = usersRef.orderByKey().equalTo(dataSnapshot.getKey());
            memberClassRef.addChildEventListener(new WholeMembersChildEventListener());
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            // Remove from members list if not already removed
            Firebase membersMember = usersRef.child(dataSnapshot.getKey() + "/members/" + usersRef.getAuth().getUid());
            if (membersMember != null) {
                membersMember.removeValue();
            }

            // Remove from list when item is removed from the database
            for (User user: memberList) {
                if (user.getKey().equals(dataSnapshot.getKey())) {
                    memberList.remove(user);
                    notifyDataSetChanged();
                    break;
                }
            }

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }

    class WholeMembersChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            User user = dataSnapshot.getValue(User.class);
            user.setKey(dataSnapshot.getKey());
            if (!currentMemberKeys.contains(user.getKey())) {
                memberList.add(0, user);
            }
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            User user = dataSnapshot.getValue(User.class);
            user.setKey(dataSnapshot.getKey());
            Map<String, Object> userMembers = user.getMembers();
            if (userMembers == null || !userMembers.containsKey(mainUserKey)) {
                //System.out.println("Removed user from user");
                mainUserRef.child("members").child(user.getKey()).removeValue();
            }
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
