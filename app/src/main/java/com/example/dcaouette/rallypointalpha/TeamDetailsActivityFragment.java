package com.example.dcaouette.rallypointalpha;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
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
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class TeamDetailsActivityFragment extends Fragment {

    private String teamKey = "";
    private Firebase rootRef;
    private Firebase teamsRef;
    private TextView teamNameTextView;
    private TextView teamDescriptionTextView;
    private TextView userStatus;
    private TeamDetailsAdapter teamDetailsAdapter;
    private RallyAdapter rallyAdapter;

    private int currentStatus = 0;

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

        final TeamDetailsActivity activity = (TeamDetailsActivity)getActivity();

        teamNameTextView = (TextView)rootView.findViewById(R.id.details_team_name_text_view);
        teamDescriptionTextView = (TextView)rootView.findViewById(R.id.details_team_description_text_view);
        userStatus = (TextView)rootView.findViewById(R.id.details_status_text_view);

        Bundle teamBundle = getActivity().getIntent().getExtras();
        if (teamBundle != null) {
            teamKey = teamBundle.getString("TEAM_KEY");
        } else {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            teamKey = sharedPreferences.getString("TEAM_KEY", "");
        }
        //Log.d("(Team Key: )", teamKey);
        rootRef = new Firebase(QuickRefs.ROOT_URL);
        teamsRef = rootRef.child("teams/" + teamKey);
        Query teamsQuery = rootRef.child("teams/" + teamKey);
        teamsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Team team = dataSnapshot.getValue(Team.class);
                    team.setKey(dataSnapshot.getKey());
                    //System.out.println("TESTSTESTES");
                    if (activity!=null)
                        activity.setNewActionBarTitle(team.getTeamName());
                    teamNameTextView.setText(team.getTeamName());
                    teamDescriptionTextView.setText(team.getTeamDescription());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        //

        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view_details_members);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        teamDetailsAdapter = new TeamDetailsAdapter(getActivity(), this, rootRef, teamsRef);
        recyclerView.setAdapter(teamDetailsAdapter);

        RecyclerView rallyRecyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view_details_rally_points);
        rallyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        rallyRecyclerView.setHasFixedSize(true);
        rallyAdapter = new RallyAdapter(getActivity(), this, rootRef, teamKey);
        rallyRecyclerView.setAdapter(rallyAdapter);

        return rootView;
    }

    public void setStatus(String newStatus, int colorInt) {
        userStatus.setText(newStatus);
        userStatus.setTextColor(colorInt);
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("TEAM_KEY", teamKey);
        //editor.putString("HOME_TITLE", pageTitle);
        editor.apply();
    }

    public void setCurrentStatus(int status) {
        currentStatus = status;
    }

    public int getCurrentStatus() {
        return currentStatus;
    }
}

/**
 * Class for displaying team members
 */
class TeamDetailsAdapter extends RecyclerView.Adapter<TeamDetailsAdapter.ViewHolder>{

    private Context context;
    private TeamDetailsActivityFragment detailsFragment;
    private Firebase teamRef;
    private Firebase rootRef;
    private Query memberRank;
    private String mainUserKey;
    private ArrayList<MemberValuePair> memberList;
    private ArrayList<User> sponsorList;
    // constants (for deleting rally points
    private static final int TEAM_LEADER = 1;
    private static final int TEAM_SPONSOR = 2;
    private static final int TEAM_MEMBER = 3;
    private String teamLeaderKey = "";

    public TeamDetailsAdapter(Context context, TeamDetailsActivityFragment detailsFragment, Firebase newRootRef, Firebase newTeamRef) {
        this.context = context;
        this.detailsFragment = detailsFragment;
        memberList = new ArrayList<>();
        sponsorList = new ArrayList<>();
        rootRef = newRootRef;
        mainUserKey = rootRef.getAuth().getUid();
        teamRef = newTeamRef;

        final Firebase myQuery = teamRef.child(QuickRefs.MEMBERS);
        myQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                memberList = new ArrayList<MemberValuePair>();
                System.out.println("Hi!!!!!: " + dataSnapshot.getKey());
                memberRank = teamRef.child("members").orderByValue();
                memberRank.addChildEventListener(new OrderChildEventListener());
                //notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    @Override
    public TeamDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_details_member, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TeamDetailsAdapter.ViewHolder holder, int position) {
        MemberValuePair map = memberList.get(position);
        User user = map.getUser();
        Integer value = map.getValue();
        if (position == 0) {
            System.out.println(memberList.get(position));
            System.out.println(memberList);
            updateSponsorList(sponsorList, memberList);
            if (mainUserKey.equals(user.getKey())) {
                detailsFragment.setCurrentStatus(TEAM_LEADER);
                detailsFragment.setStatus("Team Leader", Color.RED);
                ((TeamDetailsActivity)context).showTeamLeaderActions();
            }
            holder.memberText.setText(user.getEmail() + " Points: " + value + " (Team Leader)");
            holder.memberText.setTextColor(Color.RED);
        } else if (sponsorList.contains(user)) {
            if (mainUserKey.equals(user.getKey())) {
                detailsFragment.setCurrentStatus(TEAM_SPONSOR);
                detailsFragment.setStatus("Sponsor", Color.parseColor("#F5C507"));
                ((TeamDetailsActivity)context).showTeamSponsorActions();
            }
            holder.memberText.setText(user.getEmail() + " Points: " + value + " (Sponsor)");
            holder.memberText.setTextColor(Color.parseColor("#F5C507"));
        }else {
            if (mainUserKey.equals(user.getKey())) {
                detailsFragment.setCurrentStatus(TEAM_MEMBER);
                detailsFragment.setStatus("Member", Color.parseColor("#57D404"));
                ((TeamDetailsActivity)context).showTeamMemberActions();
            }
            holder.memberText.setText(user.getEmail() + " Points: " + value);
            holder.memberText.setTextColor(Color.parseColor("#57D404"));
        }
    }

    // If list contains users that have values over the median, add them to the sponsors list
    private void updateSponsorList(ArrayList<User> newSponsorList, ArrayList<MemberValuePair> newPair) {
        sponsorList = new ArrayList<>();
        if (newPair.size() < 3)
            return;
        ArrayList<MemberValuePair> newMemberList = new ArrayList<>();
        for (MemberValuePair pair: newPair)
            newMemberList.add(pair);
        if (newMemberList.size()>0)
            newMemberList.remove(0);// Remove team leader
        int n = newMemberList.size();
        //for (MemberValuePair m: newMemberList)
            //System.out.print(" MemberList: " + m.getValue());
        //System.out.println();
        // Find median
        double median;
        int nth;
        if (n%2 == 1) {
            nth = (n + 1)/2;
            median = (double) newMemberList.get(nth - 1).getValue();
            //System.out.println("Odd Group Median: " + median);
        } else {
            nth = n/2;
            double upperBound = (double)newMemberList.get(nth - 1).getValue();
            double lowerBound = (double)newMemberList.get(nth).getValue();
            median = ((upperBound - lowerBound)/2) + lowerBound;
            //System.out.println("Upperbound: " + (nth-1) + "  " + upperBound);
            //System.out.println("Lowerbound: " + (nth) + "  " + lowerBound);
        }
        //System.out.println("Median: " + median);
        for (MemberValuePair pair: newMemberList) {
            if (Double.compare(pair.getValue().doubleValue(), median)>0) {
                sponsorList.add(pair.getUser());
            }
            //System.out.println("Compare: " + Double.compare(pair.getValue().doubleValue(), median));
        }
        //System.out.println("Sponsor List: " + sponsorList.toString());
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView memberText;

        public ViewHolder(View itemView) {
            super(itemView);
            memberText = (TextView)itemView.findViewById(R.id.list_item_details_member_text);
        }
    }

    class OrderChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            String memberKey = dataSnapshot.getKey();
            teamLeaderKey = memberKey;
            final Integer memberValue = dataSnapshot.getValue(Integer.class);
            final MemberValuePair memberValuePair = new MemberValuePair(new User(""), memberValue);
            memberList.add(0, memberValuePair);
            // Retrieve user with key
            Firebase userRef = rootRef.child("users/" + memberKey);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    user.setKey(dataSnapshot.getKey());
                    memberValuePair.setUser(user);
                    //memberList.add(0, memberValuePair);
                    //System.out.println("Hey: " + memberValuePair.getUser().getEmail());
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            System.out.println("New Member: " + memberKey);
            //notifyDataSetChanged();
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

/**
 * Class for displaying team members
 */
class RallyAdapter extends RecyclerView.Adapter<RallyAdapter.ViewHolder> {

    private Context context;
    private Firebase rootRef;
    private String teamKey;
    private ArrayList<RallyPoint> rallyPointsList;
    private TeamDetailsActivityFragment teamDetailsActivityFragment;

    public RallyAdapter(Context context, TeamDetailsActivityFragment fragment, Firebase rootRef, String teamKey) {
        Firebase.setAndroidContext(context);
        this.context = context;
        this.rootRef = rootRef;
        this.teamKey = teamKey;
        rallyPointsList = new ArrayList<>();
        Query rallyPointQuery = this.rootRef.child(QuickRefs.RALLYPOINTS);
        rallyPointQuery.orderByChild("teamKey").equalTo(this.teamKey);
        rallyPointQuery.addChildEventListener(new RallyPointChildEventListener());
        teamDetailsActivityFragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.list_item_details_rally_points, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RallyPoint rallyPoint = rallyPointsList.get(position);
        holder.rallyPointName.setText(rallyPoint.getName());
    }

    @Override
    public int getItemCount() {
        return rallyPointsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView rallyPointName;

        public ViewHolder(View itemView) {
            super(itemView);
            rallyPointName = (TextView)itemView.findViewById(R.id.list_item_details_rally_points_text);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    RallyPoint rallyPoint = rallyPointsList.get(getAdapterPosition());
                    if (teamDetailsActivityFragment.getCurrentStatus() == 1) {
                        // remove rally point
                        rootRef.child(QuickRefs.RALLYPOINTS).child(rallyPoint.getKey()).removeValue();
                        rallyPointsList.remove(rallyPoint);
                        notifyDataSetChanged();
                    }
                    return false;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RallyPoint rallyPoint = rallyPointsList.get(getAdapterPosition());
                    Intent rallyPointIntent = new Intent(context, RallyPointDetailsActivity.class);
                    rallyPointIntent.putExtra("RALLY_KEY", rallyPoint.getKey());
                    rallyPointIntent.putExtra("TEAM_KEY", teamKey);
                    context.startActivity(rallyPointIntent);
                }
            });
        }
    }

    class RallyPointChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            RallyPoint rallyPoint = dataSnapshot.getValue(RallyPoint.class);
            rallyPoint.setKey(dataSnapshot.getKey());
            //System.out.println("Team Key: " + teamKey + " dataKey: " + rallyPoint.getTeamKey());
            if (rallyPoint.getTeamKey().equals(teamKey)) {
                rallyPointsList.add(0, rallyPoint);
                notifyDataSetChanged();
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            RallyPoint deletedRP = dataSnapshot.getValue(RallyPoint.class);
            deletedRP.setKey(dataSnapshot.getKey());
            for (RallyPoint rallyPoint: rallyPointsList) {
                if (rallyPoint.equals(deletedRP)) {
                    rallyPointsList.remove(rallyPoint);
                    break;
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }

}


