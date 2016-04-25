package com.example.dcaouette.rallypointalpha;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public class RallyPointDetailsActivityFragment extends Fragment implements View.OnClickListener {

    private Firebase rootRef;

    private TextView rallyPointNameTextView;
    private TextView rallyPointDescriptionTextView;
    private Button bailButton;
    private Button rallyUpButton;

    private String rallyPointKey = "";
    private String teamKey = "";
    private String mainUserKey;
    private RallyPointDetailsActivity activity;
    private Map<String, Object> rallyPointAttendeesList;
    private RallyPointAttendeesAdapter rallyPointAttendeesAdapter;

    public RallyPointDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rally_point_details, container, false);
        Firebase.setAndroidContext(getActivity());
        rootRef = new Firebase(QuickRefs.ROOT_URL);
        mainUserKey = rootRef.getAuth().getUid();

        Bundle teamBundle = getActivity().getIntent().getExtras();
        if (teamBundle != null) {
            rallyPointKey = teamBundle.getString("RALLY_KEY");
            teamKey = teamBundle.getString("TEAM_KEY");
        }
        activity = (RallyPointDetailsActivity)getActivity();
        rallyPointAttendeesList = new HashMap<>();


        rallyPointNameTextView = (TextView)rootView.findViewById(R.id.rally_point_details_name_text);
        rallyPointDescriptionTextView = (TextView)rootView.findViewById(R.id.rally_point_details_description_text);

        bailButton = (Button)rootView.findViewById(R.id.bailButton);
        bailButton.setVisibility(View.GONE);
        bailButton.setOnClickListener(this);

        rallyUpButton = (Button)rootView.findViewById(R.id.rallyUpButton);
        rallyUpButton.setVisibility(View.GONE);
        rallyUpButton.setOnClickListener(this);

        rootRef.child(QuickRefs.RALLYPOINTS).child(rallyPointKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RallyPoint rallyPoint = dataSnapshot.getValue(RallyPoint.class);
                rallyPoint.setKey(dataSnapshot.getKey());
                if (activity != null && activity.getSupportActionBar() != null)
                    activity.setTitle(rallyPoint.getName());
                rallyPointNameTextView.setText(rallyPoint.getName());
                rallyPointDescriptionTextView.setText((rallyPoint.getDescription()));
                if (rallyPoint.getAttendees() == null || !rallyPoint.getAttendees().containsKey(mainUserKey)) {
                    bailButton.setVisibility(View.GONE);
                    rallyUpButton.setVisibility(View.VISIBLE);
                } else if (rallyPoint.getAttendees().get(mainUserKey).equals(QuickRefs.UNRANKED)) {
                    rallyUpButton.setVisibility(View.GONE);
                    bailButton.setVisibility(View.VISIBLE);
                } else {
                    rallyUpButton.setVisibility(View.GONE);
                    bailButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view_details_attendees);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        rallyPointAttendeesAdapter = new RallyPointAttendeesAdapter(rootRef, teamKey, rallyPointKey, mainUserKey, this);
        recyclerView.setAdapter(rallyPointAttendeesAdapter);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        switch (viewID) {
            case R.id.rallyUpButton:
                rallyUp();
                break;
            case R.id.bailButton:
                bail();
                break;
        }
    }

    public void rallyUp() {
        Map<String, Object> map = new HashMap<>();
        map.put(mainUserKey, QuickRefs.UNRANKED);
        rootRef.child(QuickRefs.RALLYPOINTS).child(rallyPointKey).child(QuickRefs.ATTENDEES).updateChildren(map);
        rallyUpButton.setVisibility(View.GONE);
        bailButton.setVisibility(View.VISIBLE);
    }

    public void bail() {
        rootRef.child(QuickRefs.RALLYPOINTS).child(rallyPointKey).child(QuickRefs.ATTENDEES).child(mainUserKey).removeValue();
        bailButton.setVisibility(View.GONE);
        rallyUpButton.setVisibility(View.VISIBLE);
    }

}

class RallyPointAttendeesAdapter extends RecyclerView.Adapter<RallyPointAttendeesAdapter.ViewHolder> {

    private Firebase rootRef;
    private ArrayList<MemberRallyGroup> attendeeUserList;
    private RallyPointDetailsActivityFragment fragment;
    private String mainUserKey;
    private String teamKey;
    private String teamLeaderKey = "";
    private String rallyPointKey;

    public RallyPointAttendeesAdapter(Firebase newRootRef, String newTeamKey, String newRallyPointKey, String newMainUserKey, RallyPointDetailsActivityFragment newFragment) {
        rootRef = newRootRef;
        attendeeUserList = new ArrayList<>();
        mainUserKey = newMainUserKey;
        teamKey = newTeamKey;
        fragment = newFragment;
        rallyPointKey = newRallyPointKey;
        Firebase attendeeRef = rootRef.child(QuickRefs.RALLYPOINTS).child(rallyPointKey).child(QuickRefs.ATTENDEES);
        attendeeRef.addChildEventListener(new AttendeeEventListener());
        // Set listener for teamLeader
        Firebase teamLeaderRef = rootRef.child(QuickRefs.TEAMS).child(teamKey).child(QuickRefs.MEMBERS);
        Query teamLeaderQuery = teamLeaderRef.orderByValue().limitToLast(1);
        teamLeaderQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                System.out.println("TeamLeader: " + dataSnapshot.getKey());
                teamLeaderKey = dataSnapshot.getKey();
                for (MemberRallyGroup grouping: attendeeUserList) {
                    if (grouping.getUser().getKey().equals(teamLeaderKey)) {
                        grouping.setLeaderStatus(true);
                    } else {
                        grouping.setLeaderStatus(false);
                    }
                }
                notifyDataSetChanged();
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
        });
        // Set listener for user points
        rootRef.child(QuickRefs.TEAMS).child(teamKey).child(QuickRefs.MEMBERS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Integer points = dataSnapshot.getValue(Integer.class);
                for (MemberRallyGroup grouping: attendeeUserList) {
                    if (grouping.getUser().getKey().equals(dataSnapshot.getKey())) {
                        grouping.setUserPoints(points);
                        notifyDataSetChanged();
                        break;
                    }
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
        });

    }

    @Override
    public RallyPointAttendeesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_details_rally_points_details, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RallyPointAttendeesAdapter.ViewHolder holder, int position) {
        MemberRallyGroup grouping = attendeeUserList.get(position);
        User user = grouping.getUser();
        holder.attendeeTextView.setText(user.getEmail());
        holder.pointsTextView.setText(grouping.getUserPoints().toString());
        if (!teamLeaderKey.equals(mainUserKey) || grouping.getAttendValue().equals(QuickRefs.ATTENDED) || grouping.getAttendValue().equals(QuickRefs.BAILED)) {
            holder.bailedButton.setVisibility(View.GONE);
            holder.attendedButton.setVisibility(View.GONE);
            if (grouping.getAttendValue().equals(QuickRefs.ATTENDED)) {
                holder.cardView.setBackgroundColor(QuickRefs.LIGHT_GREEN);
            } else if (grouping.getAttendValue().equals(QuickRefs.BAILED)) {
                holder.cardView.setBackgroundColor(QuickRefs.LIGHT_RED);
            } else if (grouping.getAttendValue().equals(QuickRefs.UNRANKED)) {
                holder.cardView.setBackgroundColor(QuickRefs.PLAIN);
            }
        } else {
            holder.bailedButton.setVisibility(View.VISIBLE);
            holder.attendedButton.setVisibility(View.VISIBLE);
            holder.cardView.setBackgroundColor(QuickRefs.PLAIN);
        }
    }

    @Override
    public int getItemCount() {
        return attendeeUserList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView attendeeTextView;
        private TextView pointsTextView;
        private CardView cardView;

        private Button attendedButton;
        private Button bailedButton;

        public ViewHolder(View itemView) {
            super(itemView);
            attendeeTextView = (TextView)itemView.findViewById(R.id.list_item_details_rally_points_details_text);
            pointsTextView = (TextView)itemView.findViewById(R.id.list_item_details_rally_points_details_text_points);
            cardView = (CardView)itemView.findViewById(R.id.rally_card);
            attendedButton = (Button)itemView.findViewById(R.id.rally_card_attended_button);
            bailedButton = (Button)itemView.findViewById(R.id.rally_card_bailed_button);
            bailedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MemberRallyGroup grouping = attendeeUserList.get(getAdapterPosition());
                    User user = grouping.getUser();
                    Integer newPoints = grouping.getUserPoints() - QuickRefs.USER_BAIL;
                    if (newPoints < 0) {
                        newPoints = 0;
                    }
                    rootRef.child(QuickRefs.TEAMS).child(teamKey).child(QuickRefs.MEMBERS).child(user.getKey()).setValue(newPoints);
                    rootRef.child(QuickRefs.RALLYPOINTS).child(rallyPointKey).child(QuickRefs.ATTENDEES).child(user.getKey()).setValue(QuickRefs.BAILED);
                }
            });
        }
    }

    class AttendeeEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            final String userKey = dataSnapshot.getKey();
            final Integer userValue = dataSnapshot.getValue(Integer.class); // Current member value
            Query userQuery = rootRef.child(QuickRefs.USERS).child(userKey);
            userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final User user = dataSnapshot.getValue(User.class); // User info
                    user.setKey(dataSnapshot.getKey());
                    final Boolean teamLeaderState = teamLeaderKey.equals(user.getKey());
                    Query userPointsQuery = rootRef.child(QuickRefs.TEAMS).child(teamKey).child(QuickRefs.MEMBERS).child(user.getKey());
                    userPointsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Integer userPoints = dataSnapshot.getValue(Integer.class); // current member points
                            MemberRallyGroup pair = new MemberRallyGroup(user, userValue, userPoints, teamLeaderState);
                            attendeeUserList.add(pair);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            Integer newAttendValue = dataSnapshot.getValue(Integer.class);
            String newUserKey = dataSnapshot.getKey();
            for (MemberRallyGroup grouping: attendeeUserList) {
                if (grouping.getUser().getKey().equals(newUserKey)) {
                    grouping.setAttendValue(newAttendValue);
                    notifyDataSetChanged();
                    break;
                }
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            for (MemberRallyGroup pair: attendeeUserList) {
                User user = pair.getUser();
                if (user.getKey().equals(dataSnapshot.getKey())) {
                    attendeeUserList.remove(pair);
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

    class MemberRallyGroup {

        private User user;
        private Integer attendValue;
        private Integer userPoints;
        private Boolean leaderStatus;

        public MemberRallyGroup(User newUser, Integer newAttendValue, Integer newUserPoints, Boolean newLeaderStatus) {
            user = newUser;
            attendValue = newAttendValue;
            userPoints = newUserPoints;
            leaderStatus = newLeaderStatus;
        }

        public User getUser() {
            return user;
        }

        public void setAttendValue(Integer newAttendValue) {
            attendValue = newAttendValue;
        }

        public Integer getAttendValue() {
            return attendValue;
        }

        public void setUserPoints(Integer newUserPoints) {
            userPoints = newUserPoints;
        }

        public Integer getUserPoints() {
            return userPoints;
        }

        public void setLeaderStatus(Boolean newLeaderStatus) {
            leaderStatus = newLeaderStatus;
        }

        public Boolean getLeaderStatus() {
            return leaderStatus;
        }

        public String toString() {
            return "MemberRallyGroup: " + "User: " + user + " AttendValue: " + attendValue + " UserPoints: " + userPoints + " LeaderStatus: " + leaderStatus;
        }
    }
}

