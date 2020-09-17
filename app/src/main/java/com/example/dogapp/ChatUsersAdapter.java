package com.example.dogapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogapp.Enteties.User;

import java.util.ArrayList;
import java.util.List;

public class ChatUsersAdapter extends RecyclerView.Adapter<ChatUsersAdapter.ChatUserViewHolder> implements Filterable {

    private List<User> users;
    private List<User> usersFull;
    private MyChatUserListener listener;
    private Context context;

    public interface MyChatUserListener {
        void onChatUserClicked(int pos, View v);
    }

    public void setMyChatUserListener(MyChatUserListener listener) {
        this.listener = listener;
    }

    public ChatUsersAdapter(List<User> users,Context context) {
        this.users = users;
        this.context = context;
        usersFull = new ArrayList<>(users); //for filtering (copy of list)
    }

    //inner class
    public class ChatUserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTv;
        TextView messageTv;
        TextView timeTv;
        ImageView profileIv;
        ImageView onlineIv, offlineIv;

        //constructor
        public ChatUserViewHolder(@NonNull View itemView) {
            super(itemView);

            usernameTv = itemView.findViewById(R.id.chat_cell_username);
            messageTv = itemView.findViewById(R.id.chat_cell_msg);
            timeTv = itemView.findViewById(R.id.chat_cell_time);
            profileIv = itemView.findViewById(R.id.chat_cell_image);
            onlineIv = itemView.findViewById(R.id.chat_cell_status_online);
            offlineIv = itemView.findViewById(R.id.chat_cell_status_offline);

            //on on cell click event
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onChatUserClicked(getAdapterPosition(), v);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ChatUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_cell_card_view, parent, false);
        ChatUserViewHolder chatUserViewHolder = new ChatUserViewHolder(view);
        return chatUserViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatUserViewHolder holder, int position) {
        User user = users.get(position);
        holder.usernameTv.setText(user.getFullName());
        try {
            Glide.with(holder.itemView).asBitmap().load(user.getPhotoUri()).placeholder(R.drawable.account_icon).into(holder.profileIv);
        } catch (Exception ex) {

        }

        //set status symbol
        if(user.getStatus().equals(context.getString(R.string.online))) {
            holder.onlineIv.setVisibility(View.VISIBLE);
            holder.offlineIv.setVisibility(View.GONE);
        }
        else {
            holder.onlineIv.setVisibility(View.GONE);
            holder.offlineIv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public Filter getFilter() {
        return userFilter;
    }

    private Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) { //back thread automatically

            List<User> filteredList = new ArrayList<>(); //only filtered items

            System.out.println(usersFull.toString() + " !!!!!!!!!!!!!!!");
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(usersFull); //return full list if has no filter!
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim(); //the filter
                for (User user : usersFull) { //adding matching items to the filtered list
                    if (user.getFullName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(user);
                    }
                }
            }
            //assign the final filtered list to the result and return them
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override //publish results on the UI
        protected void publishResults(CharSequence constraint, FilterResults results) {
            users.clear();
            users.addAll((List) results.values); //changing original list
            notifyDataSetChanged();
        }
    };

}
