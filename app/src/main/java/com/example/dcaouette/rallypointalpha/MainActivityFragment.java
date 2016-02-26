package com.example.dcaouette.rallypointalpha;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Map;

/**
 * Fragment for login page
 */
public class MainActivityFragment extends Fragment implements View.OnClickListener {

    // Fields
    private EditText usernameText;
    private EditText passwordText;
    private Button loginButton;
    private Button signupButton;

    private Button mFoggyButton;
    private Button mSunnyButton;
    private TextView mTextCondition;
    private TextView mTextAuth;

    /* Firebase code */
    private final static String ROOT_URL = "https://rallypointalpha.firebaseio.com";
    private Firebase mRef;


    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();

        mRef = new Firebase(ROOT_URL).child("condition");

        /*
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String newCondition = (String) dataSnapshot.getValue();
                mTextCondition.setText(newCondition);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        mRef.createUser("bobtony@firebase.com", "correcthorsebatterystaple", new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                System.out.println("Successfully created user account with uid: " + result.get("uid"));
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                // there was an error
            }
        });

        mRef.authWithPassword("bobtony@firebase.com", "correcthorsebatterystaple", new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                mTextAuth.setText(authData.getUid());
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // there was an error
            }
        });

         /* End of Firebase code */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        usernameText = (EditText) rootView.findViewById(R.id.usernameEditText);
        passwordText = (EditText) rootView.findViewById(R.id.passwordEditText);
        loginButton = (Button) rootView.findViewById(R.id.loginButton);
        signupButton = (Button) rootView.findViewById(R.id.signupButton);

        loginButton.setOnClickListener(this);
        signupButton.setOnClickListener(this);
        /*
        mFoggyButton = (Button) rootView.findViewById(R.id.buttonFoggy);
        mSunnyButton = (Button) rootView.findViewById(R.id.buttonSunny);
        mTextCondition = (TextView) rootView.findViewById(R.id.textViewCondition);
        mTextAuth = (TextView) rootView.findViewById(R.id.loginAuth);
        */


        return rootView;
    }

    @Override
    public void onClick(View view) {
        int viewID = view.getId();
        switch (viewID) {

            case R.id.loginButton:
                String username = (String) usernameText.getText().toString();
                String password = (String) passwordText.getText().toString();

                //bobtony@firebase.com
                //correcthorsebatterystaple

                mRef.authWithPassword(username, password, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        //System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                        //mTextAuth.setText(authData.getUid());

                        startActivity(new Intent(getActivity(), HomeActivity.class));
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        // there was an error
                        Log.d("not auth", "bull");
                    }
                });

                break;

            case R.id.signupButton:
                Log.d("sighup button:", "active");
                startActivity(new Intent(getActivity(), RegistrationActivity.class));
                break;
        }
    }
}
