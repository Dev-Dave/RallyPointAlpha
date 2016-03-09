package com.example.dcaouette.rallypointalpha;

import android.app.Fragment;
import android.content.Intent;
import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * Created by dcaouette on 3/2/16.
 */
public class QuickLogin {

    public static void QuickLogin(String userEmail, String userPassword, Firebase mRef, Fragment fragment) {

        mRef.authWithPassword(userEmail, userPassword, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                // System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                //fragment.startActivity(new Intent(fragment.getActivity(), HomeActivity.class));
            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // there was an error
                Log.i("LOGIN", "Error logging in after creating user");
            }
        });
    }
}
