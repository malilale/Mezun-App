package com.mezun.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainPageFragment extends Fragment implements SelectListener {
    RecyclerView recyclerView;
    MediaAdapter mediaAdapter;
    ArrayList<Media> list;
    CollectionReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_page, container, false);
        FloatingActionButton btn_addmedia = view.findViewById(R.id.btn_addMedia);



        btn_addmedia.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(),AddMediaActivity.class);
            startActivity(intent);
        });

        recyclerView = view.findViewById(R.id.media_view);
        reference = FirebaseFirestore.getInstance().collection("Media");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        list = new ArrayList<>();
        mediaAdapter = new MediaAdapter(this,getActivity(),list);
        recyclerView.setAdapter(mediaAdapter);

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
                list.add(d.toObject(Media.class));
            }
            mediaAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onItemClicked(int position) {
       FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String  currentUid = user.getUid();
        Media media = list.get(position);

        Toast.makeText(getActivity(), media.getTitle(), Toast.LENGTH_SHORT).show();
        if(media.getUid().matches(currentUid))
            showDeleteDialog(media);

    }

    private void showDeleteDialog(Media media) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Gönderiyi Sil");
        alert.setMessage("Bu Gönderiyi silmek istediğinize emin misiniz?");
        alert.setPositiveButton("Evet", (dialog, which) -> {
            deleteMedia(media);
            dialog.dismiss();
        });
        alert.setNegativeButton("Hayır", (dialog, which) -> dialog.dismiss());
        alert.show();
    }

    private void deleteMedia(Media media) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firestore.collection("Media").document(media.getMediaId());
        Log.i("!!!!",media.mediaId);
        documentReference.delete().addOnSuccessListener(unused -> {
            list.remove(media);
            mediaAdapter.notifyDataSetChanged();
            Toast.makeText(getActivity(), "Başarıyla Silindi", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e ->
                Toast.makeText(getActivity(), "Silme İşlemi Başarısız!", Toast.LENGTH_SHORT).show());
    }
}