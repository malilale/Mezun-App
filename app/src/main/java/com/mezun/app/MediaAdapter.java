package com.mezun.app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MyViewHolder>{

    final SelectListener listener;
    Context context;
    ArrayList<Media> list;
    String name,lastname,imgUrl;

    public MediaAdapter(SelectListener listener, Context context, ArrayList<Media> list) {
        this.listener = listener;
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MediaAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.media_item,parent,false);
        return new MediaAdapter.MyViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaAdapter.MyViewHolder holder, int position) {
        Media media = list.get(position);
        String uid = media.getUid();

        DocumentReference reference = FirebaseFirestore.getInstance().collection("Users").document(uid);
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                name = task.getResult().getString("name");
                lastname = task.getResult().getString("lastname");
                imgUrl = task.getResult().getString("imgUrl");
                holder.tv_name.setText(name+" "+lastname);
                holder.tv_title.setText(media.getTitle());
                if(!imgUrl.isEmpty())
                    Picasso.get().load(imgUrl).into(holder.img_profile);
                if(!media.getThumbUrl().isEmpty())
                    Picasso.get().load(media.thumbUrl).into(holder.img_media);
                if(media.getType().contains("video"))
                    holder.play_button.setVisibility(View.VISIBLE);
                else
                    holder.play_button.setVisibility(View.GONE);
            }
        });

        holder.img_media.setOnClickListener(view -> {
            String mediaUrl = media.getMediaUrl();
            String type = media.getType();
            Intent intent = new Intent(context,PlayMediaActivity.class);
            intent.putExtra("mediaUrl",mediaUrl);
            intent.putExtra("type",type);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView img_profile, img_media, play_button;
        private TextView tv_name, tv_title;
        private ImageButton btn_delete;

        public MyViewHolder(@NonNull View itemView, SelectListener listener) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_mediaName);
            tv_title = itemView.findViewById(R.id.tv_title);
            img_profile = itemView.findViewById(R.id.img_media_profile);
            img_media = itemView.findViewById(R.id.img_media);
            play_button = itemView.findViewById(R.id.play_button);
            btn_delete = itemView.findViewById(R.id.btn_delete);


            btn_delete.setOnClickListener(view -> {
                if (listener != null){
                    int posisiton = getAdapterPosition();
                    if(posisiton != RecyclerView.NO_POSITION)
                        listener.onItemClicked(posisiton);
                }
            });
        }
    }


}
