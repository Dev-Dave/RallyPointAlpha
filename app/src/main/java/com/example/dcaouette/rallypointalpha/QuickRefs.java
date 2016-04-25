package com.example.dcaouette.rallypointalpha;

import android.graphics.Color;

/**
 * Created by dcaouette on 2/27/16.
 */
public class QuickRefs {
    /* Firebase References */
    public static final String TEAMS = "teams";
    public static final String MEMBERS = "members";
    public static final String USERS = "users";
    public static final String RALLYPOINTS = "rallypoints";
    public static final String ATTENDEES = "attendees";
    public static final String ROOT_URL =  "https://rallypointalpha.firebaseio.com";
    public static final String MEMBERS_URL = ROOT_URL + "/members";
    public static final String USERS_URL = ROOT_URL + "/users";
    public static final String TEAMS_URL = ROOT_URL +  "/teams";

    /* Member attend/bail status */
    public static final int UNRANKED = 0;
    public static final int ATTENDED = 1;
    public static final int BAILED = 2;

    /* Constant Points */
    public static final int USER_BAIL = 30;
    public static final int USER_ATTEND = 20;
    public static final int ASSIGN_BAIL = 50;
    public static final int ASSIGN_ATTEND = 50;

    /* Color Refs */
    public static final int LIGHT_GREEN = Color.parseColor("#B6E8A5");
    public static final int LIGHT_RED = Color.parseColor("#EDA1A1");
    public static final int DARK_GREEN = Color.parseColor("#57D404");
    public static final int DARK_RED = Color.parseColor("#E64E4E");
    public static final int PLAIN = Color.parseColor("#FFFFFF");
}
