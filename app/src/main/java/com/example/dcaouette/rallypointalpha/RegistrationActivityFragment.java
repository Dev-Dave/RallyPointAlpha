package com.example.dcaouette.rallypointalpha;

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
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.w3c.dom.Text;

import java.util.Map;

/**
 * Registration Fragment
 */
public class RegistrationActivityFragment extends Fragment implements View.OnClickListener {

    private Button signupRegButton;
    private Button cancelRegButton;

    private EditText emailText;
    private EditText passwordTextOne;
    private EditText passwordTextTwo;

    private Firebase mRef;


    public RegistrationActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Firebase.setAndroidContext(getActivity());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_registration, container, false);

        mRef = new Firebase(QuickRefs.ROOT_URL);

        signupRegButton = (Button) rootView.findViewById(R.id.signupRegButton);
        cancelRegButton = (Button) rootView.findViewById(R.id.cancelRegButton);


        emailText = (EditText) rootView.findViewById(R.id.usernameEditText);
        passwordTextOne = (EditText) rootView.findViewById(R.id.passwordEditText);
        passwordTextTwo = (EditText) rootView.findViewById(R.id.passwordConfirmedEditText);


        signupRegButton.setOnClickListener(this);
        cancelRegButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int viewID = view.getId();
        switch (viewID) {
            case R.id.signupRegButton:
                String email = (String) emailText.getText().toString();
                String passwordOne = (String) passwordTextOne.getText().toString();
                String passwordTwo = (String) passwordTextTwo.getText().toString();
                if (passwordConfirmed(passwordOne, passwordTwo)) {
                    // passwords good
                    Log.d("PasswordCheck", "Good");
                    attempCreateUser(email, passwordOne);
                } else {
                    // passwords missmatched
                    Log.d("PasswordCheck", "Missmatch");
                }
                break;
            case R.id.cancelRegButton:
                startActivity(new Intent(getActivity(), MainActivity.class));
                break;
        }
    }

    // Check to see that passwords are good
    private boolean passwordConfirmed(String passwordOne, String passwordTwo) {
        if (passwordOne.equals(passwordTwo)) {
            // Test for validity
            return true;
        } else {
            return false;
        }
    }

    private void attempCreateUser(final String userEmail, final String userPassword) {
        mRef.createUser(userEmail, userPassword, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                System.out.println("Successfully created user account with uid: " + result.get("uid"));
                attemptLogin(userEmail, userPassword);
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                // there was an error
                Log.d("LOGIN", "Registration error");
            }
        });
    }

    private void attemptLogin(String userEmail, String userPassword) {
        mRef.authWithPassword(userEmail, userPassword, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
               // System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                startActivity(new Intent(getActivity(), HomeActivity.class));
            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // there was an error
                Log.d("LOGIN", "Error logging in after creating user");
            }
        });
    }
}
