package com.example.dcaouette.rallypointalpha;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dcaouette on 3/21/16.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> mUsers;
    //private Callback mCallback;
    private static final String USERS_PATH = QuickRefs.ROOT_URL + "/users";

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

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView itemTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemTextView = (TextView)itemView.findViewById(R.id.list_item_member_text);
        }
    }

    public UserAdapter() {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_member, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String memberName = memberList.get(position);
        holder.itemTextView.setText(memberName);
    }

    @Override
    public int getItemCount() {
        // Change for data
        return memberList.size();
    }

    public void addItem() {
        memberList.add(0, "test");
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        memberList.remove(position);
        notifyDataSetChanged();
    }
}
