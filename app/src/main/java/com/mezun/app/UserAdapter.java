package com.mezun.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    final SelectListener listener;
    Context context;
    ArrayList<User> list;

    public UserAdapter(Context context, ArrayList<User> list, SelectListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new MyViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = list.get(position);
        holder.tv_fullname.setText(user.getName()+" "+user.getLastname());
        holder.tv_job.setText(user.getJob());
        holder.tv_location.setText(user.getLocation());
        holder.tv_years.setText(user.getYears());
        if(!user.getImgUrl().isEmpty())
            Picasso.get().load(user.getImgUrl()).into(holder.img_profile);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_fullname, tv_job, tv_years, tv_location;
        private ImageView img_profile;

        public MyViewHolder(@NonNull View itemView, SelectListener listener) {
            super(itemView);

            tv_fullname = itemView.findViewById(R.id.card_fullname);
            tv_job = itemView.findViewById(R.id.card_job);
            tv_years = itemView.findViewById(R.id.card_years);
            tv_location = itemView.findViewById(R.id.card_location);
            img_profile = itemView.findViewById(R.id.img_card_profile);

            itemView.setOnClickListener(view -> {
                if (listener != null){
                    int posisiton = getAdapterPosition();
                    if(posisiton != RecyclerView.NO_POSITION)
                        listener.onItemClicked(posisiton);

                }
            });
        }
    }
}
