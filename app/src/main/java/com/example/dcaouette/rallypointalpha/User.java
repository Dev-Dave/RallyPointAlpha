package com.example.dcaouette.rallypointalpha;

/**
 * Created by dcaouette on 3/21/16.
 */
public class User {

    private String email;

    private String key;

    public User() {
        // Empty Constructor
    }

    public User(String email) {
        this.email = email;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
