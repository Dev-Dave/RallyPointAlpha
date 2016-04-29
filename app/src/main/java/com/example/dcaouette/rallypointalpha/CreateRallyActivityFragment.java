package com.example.dcaouette.rallypointalpha;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;

import org.w3c.dom.Text;

import java.util.Calendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class CreateRallyActivityFragment extends Fragment implements View.OnClickListener {

    private String teamKey = "";
    private Firebase rootRef;
    private EditText rallyName;
    private EditText rallyDescription;
    private TextView rallyDate;
    private TextView rallyTime;
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
        rallyDate = (TextView)rootView.findViewById(R.id.rally_point_date_text);
        rallyTime = (TextView)rootView.findViewById(R.id.rally_point_time_text);

        // Initialize date and time
        Calendar calendar = Calendar.getInstance();
        rallyDate.setText((calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR));
        String amPm = "";
        String hourString = "";
        String minuteString = "";
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if (hourOfDay > 12) {
            hourOfDay = hourOfDay - 12;
            amPm = "PM";
        } else {
            amPm = "AM";
        }
        if (hourOfDay<10) {
            hourString = new String("0" + hourOfDay);
        } else {
            hourString = new String("" + hourOfDay);
        }
        if (minute<10) {
            minuteString = new String("0" + minute);
        } else {
            minuteString = new String("" + minute);
            //System.out.println(minute + ":" + minuteString);
        }
        rallyTime.setText(hourString + ":"  + minuteString + " " + amPm);

        //setRallyDateButton = (Button)rootView.findViewById(R.id.rally_point_date_button);
        createRallyButton = (Button)rootView.findViewById(R.id.create_rally_button);
        cancelRallyButton = (Button)rootView.findViewById(R.id.cancel_create_rally_button);

        rallyDate.setOnClickListener(this);
        rallyTime.setOnClickListener(this);
        createRallyButton.setOnClickListener(this);
        cancelRallyButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        switch (viewID) {
            case R.id.rally_point_date_text:
                showDatePickerDialog(v);
                break;
            case R.id.rally_point_time_text:
                showTimePickerDialog(v);
                break;
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
        RallyPoint rallyPoint = new RallyPoint(rallyName.getText().toString(), rallyDescription.getText().toString(), rallyDate.getText().toString(), rallyTime.getText().toString(), teamKey);
        String rallyKey = rootRef.child(QuickRefs.RALLYPOINTS).push().getKey();
        rootRef.child(QuickRefs.RALLYPOINTS).child(rallyKey).setValue(rallyPoint);
    }

    private void launchBack() {
        Intent teamDetailsIntent = new Intent(getActivity(), TeamDetailsActivity.class);;
        teamDetailsIntent.putExtra("TEAM_KEY", teamKey);
        getActivity().startActivity(teamDetailsIntent);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getActivity().getFragmentManager(), "timePicker");
    }
}

