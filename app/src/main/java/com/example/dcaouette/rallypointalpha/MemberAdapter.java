package com.example.dcaouette.rallypointalpha;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by dcaouette on 3/22/16.
 *
 * Adapter for filling out members
 */
public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
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
                    removeItem(getAdapterPosition());
                    return false;
                }
            });
        }
    }

    public MemberAdapter() {

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

