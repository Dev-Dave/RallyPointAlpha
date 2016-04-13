package com.example.dcaouette.rallypointalpha;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

/**
 * Created by dcaouette on 3/21/16.
 */
public class User {

    private String email;
    private Map<String, Object> groups;
    private Map<String, Object> members;
    private String provider;
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

    public Map<String, Object> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, Object> groups) {
        this.groups = groups;
    }

    public Map<String, Object> getMembers() {
        return members;
    }

    public void setMembers(Map<String, Object> members) {
        this.members = members;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("User: Key: " + key + " Email: " + email);
        return buffer.toString();
    }

}

// JsonProperty("name") // use if you want to vary the name of the value from the database representation
