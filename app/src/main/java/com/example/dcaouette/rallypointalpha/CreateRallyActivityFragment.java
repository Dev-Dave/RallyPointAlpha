package com.example.dcaouette.rallypointalpha;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;

/**
 * A placeholder fragment containing a simple view.
 */
public class CreateRallyActivityFragment extends Fragment implements View.OnClickListener {

    private String teamKey = "";
    private Firebase rootRef;
    private EditText rallyName;
    private EditText rallyDescription;
    private Button createRallyButton;
    private Button cancelRallyButton;

    public CreateRallyActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_rally, container, false);
        Firebase.setAndroidContext(getActivity());
        rootRef = new Firebase(QuickRefs.ROOT_URL);

        Bundle teamBundle = getActivity().getIntent().getExtras();
        if (teamBundle != null) {
            teamKey = teamBundle.getString("TEAM_KEY");
        }

        rallyName = (EditText)rootView.findViewById(R.id.rally_point_name_text);
        rallyDescription = (EditText)rootView.findViewById(R.id.rally_point_description_text);

        createRallyButton = (Button)rootView.findViewById(R.id.create_rally_button);
        cancelRallyButton = (Button)rootView.findViewById(R.id.cancel_create_rally_button);

        createRallyButton.setOnClickListener(this);
        cancelRallyButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        switch (viewID) {
            case R.id.create_rally_button:
                saveData();
                launchBack();
                break;
            case R.id.cancel_create_rally_button:
                launchBack();
                break;
        }
    }

    private void saveData() {
        RallyPoint rallyPoint = new RallyPoint(rallyName.getText().toString(), rallyDescription.getText().toString(), teamKey);
        String rallyKey = rootRef.child(QuickRefs.RALLYPOINTS).push().getKey();
        rootRef.child(QuickRefs.RALLYPOINTS).child(rallyKey).setValue(rallyPoint);
    }

    private void launchBack() {
        Intent teamDetailsIntent = new Intent(getActivity(), TeamDetailsActivity.class);;
        teamDetailsIntent.putExtra("TEAM_KEY", teamKey);
        getActivity().startActivity(teamDetailsIntent);
    }
}
