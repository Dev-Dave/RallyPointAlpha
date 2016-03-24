package com.example.dcaouette.rallypointalpha;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


/**
 * Members Fragment
 */
public class MembersFragment extends Fragment {

    //private ArrayAdapter<String> mMemberAdapter;
    private MemberAdapter mMemberAdapter;

    private FloatingActionButton addMemberFab;
    private FloatingActionButton addTeamFab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_members, container, false);


        /*
        //Log.d("List Test", memberList.get(1));
        mMemberAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_member,
                R.id.list_item_member_text,
                memberList
        );


        ListView listView = (ListView)rootView.findViewById(R.id.list_view_members);
        listView.setAdapter(mMemberAdapter);
        */

        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view_members);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        mMemberAdapter = new MemberAdapter();
        recyclerView.setAdapter(mMemberAdapter);

        addMemberFab = (FloatingActionButton) getActivity().findViewById(R.id.add_member_fab);
        addMemberFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //         .setAction("Action", null).show();
                //MembersFragment membersFragment = (MembersFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_members_r);
                mMemberAdapter.addItem();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        //fab.show();
    }

    public static MembersFragment newInstance() {
        MembersFragment fragment = new MembersFragment();
        //Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        //fragment.setArguments(args);
        return fragment;
    }

}
