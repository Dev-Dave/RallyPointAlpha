package com.example.dcaouette.rallypointalpha;

class MemberValuePair {

    private User user;
    private Integer value;

    public MemberValuePair(User newUser, Integer newValue) {
        user = newUser;
        value = newValue;
    }

    public void setUser(User newUser) {
        user = newUser;
    }

    public User getUser() {
        return user;
    }

    public Integer getValue() {
        return value;
    }

    public String toString() {
        return "MemberValuePair: User: " + user.getEmail() + " Value: " + value;
    }
}