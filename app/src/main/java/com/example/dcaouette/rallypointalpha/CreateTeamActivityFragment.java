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
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class CreateTeamActivityFragment extends Fragment implements View.OnClickListener {

    private EditText teamNameEditText;
    private EditText teamDescriptionEditText;
    private Firebase teamsRef;
    private Firebase usersRef;
    private String mainUserKey;
    private AddMembersAdapter addMemberAdapter;

    public CreateTeamActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_team, container, false);
        if (savedInstanceState == null) {
            Firebase.setAndroidContext(getContext());
        }
        teamsRef = new Firebase(QuickRefs.TEAMS_URL);
        usersRef = new Firebase(QuickRefs.USERS_URL);
        mainUserKey = usersRef.getAuth().getUid();

        teamNameEditText = (EditText)rootView.findViewById(R.id.team_name_edit_text);
        teamDescriptionEditText = (EditText)rootView.findViewById(R.id.team_description_edit_text);

        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view_add_members);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        addMemberAdapter = new AddMembersAdapter(getActivity());
        recyclerView.setAdapter(addMemberAdapter);

        Button createTeamButton = (Button)rootView.findViewById(R.id.createTeamButton);
        Button cancelTeamButton = (Button)rootView.findViewById(R.id.cancelCreateTeamButton);

        createTeamButton.setOnClickListener(this);
        cancelTeamButton.setOnClickListener(this);

        //String myKey = usersRef.getAuth().getUid();
        //Log.d("myKey", myKey);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int viewID = view.getId();
        switch (viewID) {
            case R.id.createTeamButton:
                if (teamNameEditText.getText().toString().isEmpty()||teamDescriptionEditText.getText().toString().isEmpty())
                    return;
                saveData();
                launchBack();
                break;
            case R.id.cancelCreateTeamButton:
                launchBack();
                break;
        }

    }

    // Create Team and update members
    private void saveData() {
        String teamName = teamNameEditText.getText().toString();
        String teamDescription = teamDescriptionEditText.getText().toString();
        Map<String, Object> initialMembers = new HashMap<>();
        // Add user as team leader
        initialMembers.put(mainUserKey, 1000);
        // Add added members
        for (User user: addMemberAdapter.getAddedMemberList()) {
            initialMembers.put(user.getKey(), 100);
        }
        Team team = new Team(teamName, teamDescription, initialMembers);
        //CreateTeamAdapter adapter = new CreateTeamAdapter(getContext());
        //adapter.add(team);

        String teamKey = teamsRef.push().getKey();
        //System.out.println("Team Key " + key);
        teamsRef.child(teamKey).setValue(team);
        ArrayList<String> teamMembersKeys = new ArrayList<>(team.getTeamMembers().keySet());
        for (String userKey: teamMembersKeys) {
            Map<String, Object> userGroup = new HashMap<>();
            userGroup.put(teamKey, true);
            usersRef.child(userKey).child("groups").updateChildren(userGroup);
        }

        //Log.d("Fab", "Fab found");
    }

    // Go back to Home menu with focus on Teams tab
    private void launchBack() {
        final Intent backToHomeIntent = new Intent(getActivity(), HomeActivity.class);
        backToHomeIntent.putExtra("START_POS", 2);
        startActivity(backToHomeIntent);
    }

}

class AddMembersAdapter extends RecyclerView.Adapter<AddMembersAdapter.ViewHolder> {

    private Firebase usersRef;
    private Firebase userMembersRef;
    private List<User> memberList;
    private String mainUser;
    private List<User> addedMemberList;

    public AddMembersAdapter(Context context) {
        Firebase.setAndroidContext(context);
        memberList = new ArrayList<>();
        addedMemberList = new ArrayList<>();
        usersRef = new Firebase(QuickRefs.USERS_URL);
        mainUser = usersRef.getAuth().getUid();
        userMembersRef = usersRef.child(mainUser + "/members");
        userMembersRef.addChildEventListener(new MembersChildEventListener());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_add_member, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // Change the text for the view
        final User user = memberList.get(position);
        holder.emailTextView.setText(user.getEmail());
        holder.addMemberCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User selectedUser = memberList.get(position);
                if (addedMemberList.contains(selectedUser)) {
                    addedMemberList.remove(selectedUser);
                } else {
                    addedMemberList.add(0, selectedUser);
                }
                //System.out.println("Users " + addedMemberList.toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public List<User> getAddedMemberList() {
        return addedMemberList;
    }

    // Creates a View Holder
    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView emailTextView;
        private CheckBox addMemberCheckBox;
        // Set TextViews and other view infor for displaying
        public ViewHolder(View itemView) {
            super(itemView);
            emailTextView = (TextView)itemView.findViewById(R.id.list_item_add_member_text);
            addMemberCheckBox = (CheckBox)itemView.findViewById(R.id.add_member_checkbox);

            emailTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User selectedUser = memberList.get(getAdapterPosition());
                    if (addedMemberList.contains(selectedUser)) {
                        addedMemberList.remove(selectedUser);
                        addMemberCheckBox.setChecked(false);
                    } else {
                        addedMemberList.add(0, selectedUser);
                        addMemberCheckBox.setChecked(true);
                    }
                    //System.out.println("Users " + addedMemberList.toString());
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
            memberList.add(0, user);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            User user = dataSnapshot.getValue(User.class);
            user.setKey(dataSnapshot.getKey());
            Map<String, Object> userMembers = user.getMembers();
            if (userMembers == null || !userMembers.containsKey(mainUser)) {
                //System.out.println("Removed user from user");
                userMembersRef.child(user.getKey()).removeValue();
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
