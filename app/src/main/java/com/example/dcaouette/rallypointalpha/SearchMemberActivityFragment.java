package com.example.dcaouette.rallypointalpha;

import android.content.Context;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchMemberActivityFragment extends Fragment {

    private EditText searchText;
    //private TextView searchTextView;
    //private Firebase mRef;
    //private Query mEmailQuery;
    private SearchMemberAdapter mSearchMemberAdapter;

    public SearchMemberActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_member, container, false);

        //Firebase.setAndroidContext(getActivity());
        //mRef = new Firebase(QuickRefs.USERS_URL);


        searchText = (EditText)rootView.findViewById(R.id.member_search_edit_text);
        searchText.addTextChangedListener(new MemberSearchWatcher());
        //searchTextView = (TextView)rootView.findViewById(R.id.mem_text_view);
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
            //Log.d("Hello", searchText.getText().toString());
            //
            //searchTextView.setText("");
            //mEmailQuery = mRef.orderByChild("email").equalTo(searchText.getText().toString());
            //searchTextView.setText(searchText.getText().toString());
            /*mEmailQuery.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //String user = dataSnapshot.getKey();
                    User user = dataSnapshot.getValue(User.class);
                    user.setKey(dataSnapshot.getKey());
                    String email = (String) user.getEmail();
                    searchTextView.setText(email);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    searchTextView.setText("");
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    searchTextView.setText("");
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });*/
        }
    }
}

class SearchMemberAdapter extends RecyclerView.Adapter<SearchMemberAdapter.ViewHolder> {

    private Firebase mRef;
    private Query mEmailQuery;
    private ArrayList<User> foundMemberList;

    public SearchMemberAdapter(Context context) {
        Firebase.setAndroidContext(context);
        mRef = new Firebase(QuickRefs.USERS_URL);
        foundMemberList = new ArrayList<User>();
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
                    //TODO Invite user to be a member
                }
            });
        }
    }

    class SearchMemberEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            User user = dataSnapshot.getValue(User.class);
            user.setKey(dataSnapshot.getKey());
            String email = (String) user.getEmail();
            //searchTextView.setText(email);
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
