package com.example.dcaouette.rallypointalpha;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

/**
 * Created by dcaouette on 4/2/16.
 */
public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.ViewHolder>{

    ArrayList<Team> teamList;
    String userKey;
    Firebase mRef;
    Firebase userGroupsRef;
    Firebase teamsRef;


    public TeamAdapter(Context context) {
        Firebase.setAndroidContext(context);
        mRef = new Firebase(QuickRefs.ROOT_URL);
        userKey = mRef.getAuth().getUid();
        userGroupsRef = mRef.child("/users/" + userKey + "/groups");
        teamsRef = mRef.child("/teams");
        teamsRef.addChildEventListener(new TeamsChildEventListener());
        userGroupsRef.addChildEventListener(new UserGroupsChildEventListener());

        teamList = new ArrayList<Team>();
        //Log.d("SMALL", );

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_team, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Team team = teamList.get(position);
        holder.teamTextView.setText(team.getTeamName());
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView teamTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            teamTextView = (TextView)itemView.findViewById(R.id.list_item_team_text);
        }
    }

    class TeamsChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if (userGroupsRef.child(dataSnapshot.getKey())!= null) {
                Team team = dataSnapshot.getValue(Team.class);
                team.setKey(dataSnapshot.getKey());
                teamList.add(0, team);
                //Log.d("BIG", "Child added to teams");
                notifyDataSetChanged();
            }
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

    class UserGroupsChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            //Log.d("BIG", "Child added to user");

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            //Log.d("BIG", "Im about to delete");

            if (teamsRef.child(dataSnapshot.getKey())!= null) {
                //Team team = teamsRef.child(dataSnapshot.getKey()).child("/");
                teamsRef.child(dataSnapshot.getKey()).child("/members/" + userKey).removeValue();
                //Log.d("BIG", "Im deleting");
                for (Team team: teamList) {
                    if (team.getKey().equals(dataSnapshot.getKey())) {
                        teamList.remove(team);
                        break;
                    }
                }
                notifyDataSetChanged();
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }

}
