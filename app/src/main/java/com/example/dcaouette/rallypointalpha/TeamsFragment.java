package com.example.dcaouette.rallypointalpha;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;


/**
 *
 */
public class TeamsFragment extends Fragment {

    private ArrayAdapter<String> mTeamAdapter;

    public TeamsFragment() {
        // Required empty public constructor
    }


    public static TeamsFragment newInstance() {
        TeamsFragment fragment = new TeamsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_teams, container, false);

        String[] teamArray = {
                "Tennis",
                "Band",
                "Honor Society",
                "Game Group"
        };

        ArrayList<String> teamList = new ArrayList<String>(Arrays.asList(teamArray));
        //Log.d("List Test", memberList.get(1));
        mTeamAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_team,
                R.id.list_item_team_text,
                teamList
        );


        ListView listView = (ListView)rootView.findViewById(R.id.list_view_teams);
        listView.setAdapter(mTeamAdapter);

        return rootView;
    }

}
