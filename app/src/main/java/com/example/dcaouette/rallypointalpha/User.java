package com.example.dcaouette.rallypointalpha;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by dcaouette on 3/21/16.
 */
public class User {

    private String email;
    @JsonIgnore
    private String key;

    public User() {
        // Empty Constructor needed
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

// JsonProperty("name") // use if you want to vary the name of the value from the database representation
