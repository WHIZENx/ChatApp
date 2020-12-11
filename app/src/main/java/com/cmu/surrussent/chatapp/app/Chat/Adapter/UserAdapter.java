package com.cmu.surrussent.chatapp.app.Chat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cmu.surrussent.chatapp.R;
import com.cmu.surrussent.chatapp.app.Chat.MessageActivity;
import com.cmu.surrussent.chatapp.app.Chat.Model.Chat;
import com.cmu.surrussent.chatapp.app.Chat.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    public static final int USER_TYPE_S = 0;
    public static final int USER_TYPE_M = 1;

    private Context mContext;
    private List<User> mUsers;
    private boolean ischat;

    String theLastMessage;
    String showDate;

    public UserAdapter(Context mContext, List<User> mUsers, boolean ischat){
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == USER_TYPE_S) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
            return new UserAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.user_item_m, parent, false);
            return new UserAdapter.ViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (ischat){
            return USER_TYPE_M;
        } else {
            return USER_TYPE_S;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user = mUsers.get(position);
        holder.username.setText(user.getUsername());
        if (user.getImageURL().equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }

        if (ischat){
            lastMessage(user.getId(), holder.last_msg, holder.date, holder.username, holder.img_seen);
        } else {
            holder.last_msg.setVisibility(View.GONE);
        }

        if (ischat){
            if (user.getStatus().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", user.getId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView date;
        private TextView last_msg;
        ImageView img_seen;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            date = itemView.findViewById(R.id.date);
            last_msg = itemView.findViewById(R.id.last_msg);
            img_seen = itemView.findViewById(R.id.img_seen);
        }
    }

    //check for last message
    private void lastMessage(final String userid, final TextView last_msg, final TextView date, final TextView username, final ImageView img_seen){
        theLastMessage = "default";
        showDate = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                            if (firebaseUser.getUid().equals(userid) == chat.getSender().equals(userid)) {
                                theLastMessage = "Me: " + chat.getMessage();
                            } else {
                                theLastMessage = chat.getMessage();

                                if (!chat.isIsseen()) {
                                    last_msg.setTextColor(Color.parseColor("#0080ff"));
                                    last_msg.setTypeface(last_msg.getTypeface(), Typeface.BOLD);
                                    username.setTypeface(username.getTypeface(), Typeface.BOLD);
                                    img_seen.setVisibility(View.VISIBLE);
                                }

                            }
                            showDate = chat.getDate();


                        }
                    }
                }

                switch (theLastMessage){
                    case  "default":
                        last_msg.setText("No Message");
                        date.setText("No Date");
                        break;

                    default:
                        last_msg.setText(theLastMessage);
                        date.setText(showDate);
                        break;
                }

                theLastMessage = "default";
                showDate = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
