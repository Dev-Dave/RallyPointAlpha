package com.example.dcaouette.rallypointalpha;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dcaouette on 3/22/16.
 *
 * Adapter for filling out members
 */
public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {

    private static final String MEMBERS_REF = QuickRefs.MEMBERS_URL;
    private Firebase mRef;
    private List<User> memberList;

    public MemberAdapter(Context context) {
        Firebase.setAndroidContext(context);
        memberList = new ArrayList<>();
        mRef = new Firebase(MEMBERS_REF);
        mRef.addChildEventListener(new MembersChildEventListener());
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
        mRef.child(user.getKey()).removeValue();
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
            User user = dataSnapshot.getValue(User.class);
            user.setKey(dataSnapshot.getKey());
            //Log.d("Data snapshot test", user.getEmail());
            memberList.add(0, user);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
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
}



