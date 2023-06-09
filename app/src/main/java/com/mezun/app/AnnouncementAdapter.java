package com.mezun.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.MyViewHolder>{
    final SelectListener listener;
    private Context context;
    private ArrayList<Announcement> list;

    public AnnouncementAdapter(SelectListener listener, Context context, ArrayList<Announcement> list) {
        this.listener = listener;
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.announcement_item,parent,false);
        return new AnnouncementAdapter.MyViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Announcement post = list.get(position);

        String uid = post.getUid();

        DocumentReference reference = FirebaseFirestore.getInstance().collection("Users").document(uid);

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String name,lastname, imgUrl, email;
                name = task.getResult().getString("name");
                lastname = task.getResult().getString("lastname");
                imgUrl = task.getResult().getString("imgUrl");
                email = task.getResult().getString("email");

                holder.tv_name.setText(name+" "+lastname);
                holder.tv_email.setText(email);
                holder.tv_post.setText(post.getPost());
                holder.tv_date.setText(post.getDate());

                if(!imgUrl.isEmpty())
                    Picasso.get().load(imgUrl).into(holder.img_profile);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_name, tv_email, tv_post, tv_date;
        private ImageView img_profile;
        public MyViewHolder(@NonNull View itemView, SelectListener listener) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.anoun_fullname);
            tv_email = itemView.findViewById(R.id.anoun_email);
            tv_post = itemView.findViewById(R.id.anoun);
            tv_date = itemView.findViewById(R.id.tv_date);
            img_profile = itemView.findViewById(R.id.img_anoun_profile);


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
