package com.example.dcaouette.rallypointalpha;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class TeamDetailsActivityFragment extends Fragment {

    private String teamKey = "";
    private Firebase rootRef;
    private Firebase teamsRef;
    private TextView teamNameTextView;
    private TextView teamDescriptionTextView;
    private TeamDetailsAdapter teamDetailsAdapter;

    public TeamDetailsActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(getActivity());

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_team_details, container, false);

        teamNameTextView = (TextView)rootView.findViewById(R.id.details_team_name_text_view);
        teamDescriptionTextView = (TextView)rootView.findViewById(R.id.details_team_description_text_view);

        Bundle teamBundle = getActivity().getIntent().getExtras();
        if (teamBundle != null) {
            teamKey = teamBundle.getString("TEAM_KEY");
        }
        Log.d("(Team Key: )", teamKey);
        rootRef = new Firebase(QuickRefs.ROOT_URL);
        teamsRef = rootRef.child("teams/" + teamKey);
        teamsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.equals(null)) {
                    Team team = dataSnapshot.getValue(Team.class);
                    team.setKey(dataSnapshot.getKey());
                    //System.out.println("TESTSTESTES");
                    getActivity().setTitle(team.getTeamName());
                    teamNameTextView.setText(team.getTeamName());
                    teamDescriptionTextView.setText(team.getTeamDescription());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        //

        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view_teams);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        teamDetailsAdapter = new TeamDetailsAdapter(getActivity());
        recyclerView.setAdapter(teamDetailsAdapter);

        return rootView;
    }
}

/**
 * Class for displaying team members
 */
class TeamDetailsAdapter extends RecyclerView.Adapter<TeamDetailsAdapter.ViewHolder>{

    public TeamDetailsAdapter(Context context) {

    }

    @Override
    public TeamDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(TeamDetailsAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

}



