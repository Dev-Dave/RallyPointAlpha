package com.example.dcaouette.rallypointalpha;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
 * Fragment for displaying teams
 */
public class TeamsFragment extends Fragment {

    private FloatingActionButton addTeamFab;
    private TeamAdapter mTeamAdapter;

    //private ArrayAdapter<String> mTeamAdapter;

    public TeamsFragment() {
        // Required empty public constructor
    }

    public static TeamsFragment newInstance() {
        return new TeamsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_teams, container, false);

        addTeamFab = (FloatingActionButton) getActivity().findViewById(R.id.add_team_fab);

        //addTeamFab.hide();
        /*
        String[] teamArray = {
                "Tennis",
                "Band",
                "Honor Society",
                "Game Group"
        };

        ArrayList<String> teamList = new ArrayList<String>(Arrays.asList(teamArray));
        //Log.d("List Test", memberList.get(1));
        mTeamAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_team,
                R.id.list_item_team_text,
                teamList
        );


        ListView listView = (ListView)rootView.findViewById(R.id.list_view_teams);
        listView.setAdapter(mTeamAdapter);
        */

        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view_teams);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        mTeamAdapter = new TeamAdapter(getActivity());
        recyclerView.setAdapter(mTeamAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        //fab.hide();
    }

    @Override
    public void onStop() {
        super.onStart();
        addTeamFab.hide();
    }

    public FloatingActionButton getFAb() {
        return addTeamFab;
    }

}

/**
 * Class for displaying user's teams
 * Created by dcaouette on 4/2/16.
 */
class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Team> teamList;
    private String userKey;
    private Firebase usersRef;
    private Firebase userGroupsRef;
    private Firebase teamsRef;


    public TeamAdapter(Context context) {
        this.context = context;
        Firebase.setAndroidContext(context);
        usersRef = new Firebase(QuickRefs.USERS_URL);
        userKey = usersRef.getAuth().getUid();
        userGroupsRef = usersRef.child(userKey).child("groups");
        teamsRef = usersRef.getParent().child("teams");
        teamsRef.addChildEventListener(new TeamsChildEventListener());
        userGroupsRef.addChildEventListener(new UserGroupsChildEventListener());

        teamList = new ArrayList<>();
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

    public void remove(Team team) {
        //System.out.println("Start Removal");
        String teamKey = team.getKey();
        //System.out.println("Team to remove" + teamKey);
        // Determine if team leader left, if so then add points for new leader
        userGroupsRef.child(teamKey).removeValue();
        teamsRef.child(teamKey).child("members").child(userKey).removeValue();
        teamList.remove(team);
        notifyDataSetChanged();
    }

    private void launchTeamDetails(Team team) {
        Intent teamDetailsIntent = new Intent(context, TeamDetailsActivity.class);
        String teamKey = team.getKey();
        teamDetailsIntent.putExtra("TEAM_KEY", teamKey);
        context.startActivity(teamDetailsIntent);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView teamTextView;

        public ViewHolder(final View itemView) {
            super(itemView);
            teamTextView = (TextView)itemView.findViewById(R.id.list_item_team_text);
            teamTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Team team = teamList.get(getAdapterPosition());
                    launchTeamDetails(team);
                }
            });
            teamTextView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Team team = teamList.get(getAdapterPosition());
                    remove(team);
                    return false;
                }
            });
        }
    }

    class TeamsChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Team team = dataSnapshot.getValue(Team.class);
            team.setKey(dataSnapshot.getKey());
            if (team.getTeamMembers() == null)
                return;
            //System.out.println("Team Name: " + team.getTeamName());
            ArrayList<String> members = new ArrayList<>(team.getTeamMembers().keySet());
            //System.out.println("Team Members: " + members.toString());
            if (members.contains(userKey)) {
                teamList.add(0, team);
                //Log.d("BIG", "Child added to teams");
                notifyDataSetChanged();
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            Team team = dataSnapshot.getValue(Team.class);
            team.setKey(dataSnapshot.getKey());
            if (team.getTeamMembers() == null) {
                teamsRef.child(team.getKey()).removeValue();
                notifyDataSetChanged();
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
            /*
            //Log.d("BIG", "Im about to delete");
            String groupKey = (String)dataSnapshot.getKey();
            System.out.println("Team Removed");
            teamsRef.child(groupKey).child("/members/" + userKey).removeValue();
            if (teamsRef.child(groupKey)!= null) {
                //Team team = teamsRef.child(dataSnapshot.getKey()).child("/");

                //Log.d("BIG", "Im deleting");
                for (Team team: teamList) {
                    if (team.getKey().equals(dataSnapshot.getKey())) {
                        teamList.remove(team);
                        break;
                    }
                }
                notifyDataSetChanged();
            }*/
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }

}
