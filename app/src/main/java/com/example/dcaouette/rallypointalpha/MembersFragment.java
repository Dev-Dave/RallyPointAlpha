package com.example.dcaouette.rallypointalpha;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Members Fragment
 */
public class MembersFragment extends Fragment {

    private ArrayAdapter<String> mMemberAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_members, container, false);

        String[] memberArray = {
                "Joe",
                "Mike",
                "Dan",
                "Angelica",
                "Kevin",
                "Brittany",
                "Dave",
                "Tom",
                "Jessica",
                "Tiffany"
        };

        ArrayList<String> memberList = new ArrayList<String>(Arrays.asList(memberArray));
        //Log.d("List Test", memberList.get(1));
        mMemberAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_member,
                R.id.list_item_member_text,
                memberList
        );


        ListView listView = (ListView)rootView.findViewById(R.id.list_view_members);
        listView.setAdapter(mMemberAdapter);

        return rootView;
    }

    public static MembersFragment newInstance() {
        MembersFragment fragment = new MembersFragment();
        //Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        //fragment.setArguments(args);
        return fragment;
    }

}
