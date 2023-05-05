package com.mezun.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AnnouncementsFragment extends Fragment implements SelectListener{
    RecyclerView recyclerView;
    AnnouncementAdapter announcementAdapter;
    ArrayList<Announcement> list;
    CollectionReference reference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_announcements, container, false);

        FloatingActionButton btn_addpost = view.findViewById(R.id.btn_addpost);

        btn_addpost.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(),AddAnnouncementActivity.class);
            startActivity(intent);
        });

        recyclerView = view.findViewById(R.id.announcements_view);
        reference = FirebaseFirestore.getInstance().collection("Posts");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        list = new ArrayList<>();
        announcementAdapter = new AnnouncementAdapter(this,getActivity(),list);
        recyclerView.setAdapter(announcementAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getData();
        list.clear();
    }

    private void getData() {

       reference.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(DocumentSnapshot d : queryDocumentSnapshots.getDocuments()){
                list.add(d.toObject(Announcement.class));
            }
            announcementAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onItemClicked(int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String  currentUid = user.getUid();
        Announcement announcement = list.get(position);
        if(announcement.getUid().matches(currentUid))
            showDeleteDialog(announcement);

    }

    private void showDeleteDialog(Announcement announcement) {
        Toast.makeText(getActivity(), announcement.getPostId(), Toast.LENGTH_SHORT).show();
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Duyuru Sil");
        alert.setMessage("Bu duyuruyu silmek istediğinize emin misiniz?");
        alert.setPositiveButton("Evet", (dialog, which) -> {
            deletePost(announcement);
            dialog.dismiss();
        });
        alert.setNegativeButton("Hayır", (dialog, which) -> dialog.dismiss());
        alert.show();
    }

    private void deletePost(Announcement announcement) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firestore.collection("Posts").document(announcement.getPostId());

        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                list.remove(announcement);
                announcementAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "Başarıyla Silindi", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Silme İşlemi Başarısız!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}