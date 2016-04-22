package com.example.dcaouette.rallypointalpha;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class RallyPointDetailsActivityFragment extends Fragment {

    private Firebase rootRef;
    private TextView rallyPointNameTextView;
    private TextView rallyPointDescriptionTextView;
    private String rallyPointKey = "";
    private String mainUserKey;
    private RallyPointDetailsActivity activity;

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
        }
        activity = (RallyPointDetailsActivity)getActivity();


        rallyPointNameTextView = (TextView)rootView.findViewById(R.id.rally_point_details_name_text);
        rallyPointDescriptionTextView = (TextView)rootView.findViewById(R.id.rally_point_details_description_text);

        rootRef.child(QuickRefs.RALLYPOINTS).child(rallyPointKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RallyPoint rallyPoint = dataSnapshot.getValue(RallyPoint.class);
                rallyPoint.setKey(dataSnapshot.getKey());
                if (activity != null && activity.getSupportActionBar() != null)
                    activity.setTitle(rallyPoint.getName());
                rallyPointNameTextView.setText(rallyPoint.getName());
                rallyPointDescriptionTextView.setText((rallyPoint.getDescription()));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return rootView;
    }
}
