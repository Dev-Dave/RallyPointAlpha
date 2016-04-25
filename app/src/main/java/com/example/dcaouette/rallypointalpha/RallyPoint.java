package com.example.dcaouette.rallypointalpha;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

/**
 * Created by dcaouette on 4/19/16.
 */
public class RallyPoint {


    private String name;
    private String description;
    private String teamKey;
    private Map<String, Object> attendees;

    @JsonIgnore
    private String key;

    public RallyPoint() {

    }

    public RallyPoint(String name, String description, String teamKey) {
        this.name = name;
        this.description = description;
        this.teamKey = teamKey;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setName(String newName) {
        name = newName;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String newDescription) {
        description = newDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setAttendees(Map<String, Object> newAttendees) {
        attendees = newAttendees;
    }

    public Map<String, Object> getAttendees() {
        return attendees;
    }

    public void setTeamKey(String teamKey) {
        this.teamKey = teamKey;
    }

    public String getTeamKey() {
        return teamKey;
    }

    public String toString() {
        return "Rally Point: Name: " + name + " Description: " + description + " Team Key: " + teamKey;
    }

}
