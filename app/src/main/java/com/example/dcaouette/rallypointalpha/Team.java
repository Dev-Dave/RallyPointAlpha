package com.example.dcaouette.rallypointalpha;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Created by dcaouette on 3/27/16.
 */
public class Team {

    @JsonProperty("name")
    private String teamName;

    @JsonProperty("description")
    private String teamDescription;

    @JsonProperty("members")
    private Map<String, Object> teamMembers;

    @JsonIgnore
    private String key;

    public Team() {

    }

    public Team(String teamName, String teamDescription, Map<String, Object> teamMembers) {
        this.teamName = teamName;
        this.teamDescription = teamDescription;
        this.teamMembers = teamMembers;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamDescription(String teamDescription) {
        this.teamDescription = teamDescription;
    }

    public String getTeamDescription() {
        return teamDescription;
    }

    public void setTeamMembers(Map<String, Object> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public Map<String, Object> getTeamMembers() {
        return teamMembers;
    }

}
