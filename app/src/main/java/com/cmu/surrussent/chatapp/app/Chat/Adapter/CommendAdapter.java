package com.cmu.surrussent.chatapp.app.Chat.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cmu.surrussent.chatapp.R;
import com.cmu.surrussent.chatapp.app.Chat.Model.Commend;

import java.util.List;

public class CommendAdapter extends RecyclerView.Adapter<CommendAdapter.ViewHolder> {

    private Context mContext;
    private List<Commend> mCommend;

    public CommendAdapter(Context mContext, List<Commend> mCommend){
        this.mContext = mContext;
        this.mCommend = mCommend;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_post_commend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Commend commend = mCommend.get(position);
        holder.username_commend.setText(commend.getUserName());
        Glide.with(mContext).load(commend.getUserImg()).into(holder.profile_image_commend);
        holder.date_commend.setText(commend.getDateId());
        holder.commendid.setText(commend.getCommendId());
    }

    @Override
    public int getItemCount() {
        return mCommend.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        TextView username_commend;
        ImageView profile_image_commend;
        TextView date_commend;
        TextView commendid;

        public ViewHolder(View itemView) {
            super(itemView);

            username_commend = itemView.findViewById(R.id.username_commend);
            profile_image_commend = itemView.findViewById(R.id.profile_image_commend);
            date_commend = itemView.findViewById(R.id.date_commend);
            commendid = itemView.findViewById(R.id.commendid);
        }
    }

}
