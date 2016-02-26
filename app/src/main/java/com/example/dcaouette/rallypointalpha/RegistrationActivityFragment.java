package com.example.dcaouette.rallypointalpha;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A placeholder fragment containing a simple view.
 */
public class RegistrationActivityFragment extends Fragment implements View.OnClickListener {

    private Button signupRegButton;
    private Button cancelRegButton;

    public RegistrationActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_registration, container, false);

        signupRegButton = (Button) rootView.findViewById(R.id.signupRegButton);
        cancelRegButton = (Button) rootView.findViewById(R.id.cancelRegButton);

        signupRegButton.setOnClickListener(this);
        cancelRegButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int viewID = view.getId();
        switch (viewID) {
            case R.id.signupRegButton:
                
                break;
            case R.id.cancelRegButton:
                startActivity(new Intent(getActivity(), MainActivity.class));
                break;
        }
    }
}
