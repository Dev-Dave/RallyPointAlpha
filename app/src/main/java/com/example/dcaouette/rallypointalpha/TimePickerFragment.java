package com.example.dcaouette.rallypointalpha;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by dcaouette on 4/27/16.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private int selectedHour;
    private int selectedMinute;
    private String amPm = "";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();

        selectedHour = calendar.get(Calendar.HOUR);
        selectedMinute = calendar.get(Calendar.MINUTE);

        //return new TimePickerDialog(getActivity(), this, year, month, day);
        return new TimePickerDialog(getActivity(), this, selectedHour, selectedMinute, false);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView timeTextView = (TextView)getActivity().findViewById(R.id.rally_point_time_text);
        String amPm = "";
        String hourString = "";
        String minuteString = "";
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
        if (hourOfDay == 0)
            hourString = new String("12");
        timeTextView.setText(hourString + ":"  + minuteString + " " + amPm);
    }
}
