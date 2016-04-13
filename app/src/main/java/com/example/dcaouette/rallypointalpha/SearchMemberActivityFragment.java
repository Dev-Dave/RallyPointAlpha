package com.example.dcaouette.rallypointalpha;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchMemberActivityFragment extends Fragment {

    private EditText searchText;
    private SearchMemberAdapter mSearchMemberAdapter;

    public SearchMemberActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_member, container, false);

        searchText = (EditText)rootView.findViewById(R.id.member_search_edit_text);
        searchText.addTextChangedListener(new MemberSearchWatcher());

        // RecyclerView and adapter
        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view_search_members);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        mSearchMemberAdapter = new SearchMemberAdapter(getActivity());
        recyclerView.setAdapter(mSearchMemberAdapter);

        return rootView;
    }

    class MemberSearchWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            mSearchMemberAdapter.searchForMember(searchText.getText().toString());
        }
    }
}

/* Adapter for listing found users */
class SearchMemberAdapter extends RecyclerView.Adapter<SearchMemberAdapter.ViewHolder> {

    private Firebase mRef;
    private Query mEmailQuery;
    private ArrayList<User> foundMemberList;
    private Context androidContext;
    private String mainUser;

    public SearchMemberAdapter(Context context) {
        Firebase.setAndroidContext(context);
        androidContext = context;
        mRef = new Firebase(QuickRefs.USERS_URL);
        foundMemberList = new ArrayList<User>();
        mainUser = (String) mRef.getAuth().getUid();
        System.out.println("user 1: " + mainUser);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_search_member, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User user = foundMemberList.get(position);
        holder.itemTextView.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return foundMemberList.size();
    }

    public void searchForMember(String textInput) {
        for (User user: foundMemberList) {
            foundMemberList.remove(user);
        }
        mEmailQuery = mRef.orderByChild("email").equalTo(textInput);
        mEmailQuery.addChildEventListener(new SearchMemberEventListener());
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView itemTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemTextView = (TextView)itemView.findViewById(R.id.list_item_search_member_text);

            itemTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = foundMemberList.get(getAdapterPosition());
                    // Set user to be a member
                    Firebase selectedUserRef = mRef.child(user.getKey() + "/members");
                    Firebase mainUserRef = mRef.child(mainUser + "/members");
                    // Add member to selected user
                    Map<String, Object> selectedUserMember = new HashMap<String, Object>();
                    selectedUserMember.put(mainUser, true);
                    // Add member to main user
                    Map<String, Object> mainUserMember = new HashMap<String, Object>();
                    mainUserMember.put(user.getKey(), true);

                    selectedUserRef.updateChildren(selectedUserMember);
                    mainUserRef.updateChildren(mainUserMember);
                    Intent intent = new Intent(androidContext, HomeActivity.class);
                    androidContext.startActivity(intent);
                }
            });
        }
    }

    class SearchMemberEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            User user = dataSnapshot.getValue(User.class);
            user.setKey(dataSnapshot.getKey());
            if (user.getMembers() != null && (mainUser.equals(user.getKey()) || user.getMembers().containsKey(mainUser)))
                return;
            foundMemberList.add(0, user);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
