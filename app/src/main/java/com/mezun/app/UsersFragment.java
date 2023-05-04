package com.mezun.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UsersFragment extends Fragment {

    RecyclerView recyclerView;
    UserAdapter userAdapter;
    ArrayList<User> list;
    CollectionReference reference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = view.findViewById(R.id.user_view);
        reference = FirebaseFirestore.getInstance().collection("Users");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        list = new ArrayList<>();
        userAdapter = new UserAdapter(getActivity(),list);
        recyclerView.setAdapter(userAdapter);

        getData();


        return view;
    }


    private void getData(){
        reference.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(DocumentSnapshot d : queryDocumentSnapshots.getDocuments()){
                list.add(d.toObject(User.class));
            }
            userAdapter.notifyDataSetChanged();
        });
    }
}