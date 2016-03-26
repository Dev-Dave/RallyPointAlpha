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

    private MemberAdapter mMemberAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_members, container, false);

        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view_members);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        mMemberAdapter = new MemberAdapter(getActivity());
        recyclerView.setAdapter(mMemberAdapter);

        return rootView;
    }

    // Method for fab click event
    public void addMember() {
        mMemberAdapter.add(new User("test@test1.com"));
    }

    public static MembersFragment newInstance() {
        MembersFragment fragment = new MembersFragment();
        //Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        //fragment.setArguments(args);
        return fragment;
    }


}
