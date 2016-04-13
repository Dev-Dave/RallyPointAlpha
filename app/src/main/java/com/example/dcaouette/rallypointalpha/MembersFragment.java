package com.example.dcaouette.rallypointalpha;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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

/**
 * Created by dcaouette on 3/22/16.
 *
 * Adapter for filling out members
 */
class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {

    private Firebase mRef;
    private Firebase userMembersRef;
    private List<User> memberList;
    private String mainUser;

    public MemberAdapter(Context context) {
        Firebase.setAndroidContext(context);
        memberList = new ArrayList<>();
        mRef = new Firebase(QuickRefs.USERS_URL);
        mainUser = (String) mRef.getAuth().getUid();
        userMembersRef = mRef.child(mainUser + "/members");
        userMembersRef.addChildEventListener(new MembersChildEventListener());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_member, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Change the text for the view
        final User user = memberList.get(position);
        holder.itemTextView.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public void add(User user) {
        mRef.push().setValue(user);
        notifyDataSetChanged();
    }

    public void remove(User user) {
        //mRef.child(user.getKey()).removeValue();
        //memberList.remove(user);
        userMembersRef.child(user.getKey()).removeValue();
        mRef.child(user.getKey() + "/members/" + mainUser).removeValue();
        memberList.remove(user);
        notifyDataSetChanged();
    }

    // Creates a View Holder
    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView itemTextView;
        // Set TextViews and other view infor for displaying
        public ViewHolder(View itemView) {
            super(itemView);
            itemTextView = (TextView)itemView.findViewById(R.id.list_item_member_text);
            // Set long click delete action
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    remove(memberList.get(getAdapterPosition()));
                    return false;
                }
            });
        }
    }

    /**
     * Inner class for updating the view from changes in the database
     */
    class MembersChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            /*
            User user = dataSnapshot.getValue(User.class);
            user.setKey(dataSnapshot.getKey());
            //Log.d("Data snapshot test", user.getEmail());
            memberList.add(0, user);
            notifyDataSetChanged();
            */
            // Find user class for each member
            Query memberClassRef = mRef.orderByKey().equalTo(dataSnapshot.getKey());
            memberClassRef.addChildEventListener(new WholeMembersChildEventListener());
            notifyDataSetChanged();
            //System.out.println("Data Snapshot: " + dataSnapshot.getKey() + "; " + dataSnapshot.getValue());
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            // Remove from members list if not already removed
            Firebase membersMember = mRef.child(dataSnapshot.getKey() + "/members/" + mRef.getAuth().getUid());
            if (membersMember != null) {
                membersMember.removeValue();
            }

            // Remove from list when item is removed from the database
            for (User user: memberList) {
                if (user.getKey().equals(dataSnapshot.getKey())) {
                    memberList.remove(user);
                    notifyDataSetChanged();
                    break;
                }
            }

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }

    class WholeMembersChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            User user = dataSnapshot.getValue(User.class);
            user.setKey(dataSnapshot.getKey());
            memberList.add(0, user);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            User user = dataSnapshot.getValue(User.class);
            user.setKey(dataSnapshot.getKey());
            Map<String, Object> userMembers = user.getMembers();
            if (userMembers == null || !userMembers.containsKey(mainUser)) {
                //System.out.println("Removed user from user");
                userMembersRef.child(user.getKey()).removeValue();
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }
}


