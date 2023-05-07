package com.mezun.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UsersFragment extends Fragment implements SelectListener{

    RecyclerView recyclerView;
    UserAdapter userAdapter;
    ArrayList<User> list;
    CollectionReference reference;
    private Button btn_menu,btn_filter;
    private EditText et_filter;
    private String option, filter;


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
        userAdapter = new UserAdapter(getActivity(),list,this);
        recyclerView.setAdapter(userAdapter);

        et_filter = view.findViewById(R.id.et_filter);
        btn_menu = view.findViewById(R.id.btn_menu_filter);
        btn_filter = view.findViewById(R.id.btn_filter);

        et_filter.setText("");
        option = "";

        btn_menu.setOnClickListener(view1 ->
                showPopupMenu());

        btn_filter.setOnClickListener(view1 ->
                setFilters());


        getData();
        return view;
    }

    private void setFilters() {
        filter = et_filter.getText().toString().trim();
        if(filter.isEmpty() || option.isEmpty()){
            Toast.makeText(getActivity(), "Bir filtre seçiniz", Toast.LENGTH_SHORT).show();
            return;
        }
        list.clear();
        Query filterQuery = reference.whereEqualTo(option,filter);

        filterQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(DocumentSnapshot d : queryDocumentSnapshots.getDocuments()){
                list.add(d.toObject(User.class));
            }
            userAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "Failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }

    private void showPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(getActivity(), btn_menu);
        popupMenu.getMenuInflater().inflate(R.menu.filter_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                option = getOption(menuItem.getTitle().toString());
                et_filter.setText("");
                Toast.makeText(getActivity(), option, Toast.LENGTH_SHORT).show();
                btn_menu.setText(menuItem.getTitle().toString());
                return true;
            }
        });
        popupMenu.show();
    }

    private String getOption(String item) {

        if (item.contains("Ülke")){
            et_filter.setInputType(InputType.TYPE_CLASS_TEXT);
            return "country";
        }if (item.contains("Şehir")) {
            et_filter.setInputType(InputType.TYPE_CLASS_TEXT);
            return "city";
        }if (item.contains("Giriş Yılı")) {
            et_filter.setInputType(InputType.TYPE_CLASS_NUMBER);
            return "startyear";
        }if (item.contains("Mezun Yılı")) {
            et_filter.setInputType(InputType.TYPE_CLASS_NUMBER);
            return "endyear";
        }

        return null;
    }


    private void getData(){
        reference.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(DocumentSnapshot d : queryDocumentSnapshots.getDocuments()){
                list.add(d.toObject(User.class));
            }
            userAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onItemClicked(int position) {
        User currentUser = list.get(position);
        sendToUserProfile(currentUser);
    }

    private void sendToUserProfile(User user) {
        Intent intent = new Intent(getActivity(),UserProfileActivity.class);
        intent.putExtra("fullname", user.getName()+" "+user.getLastname());
        intent.putExtra("lastname", user.getLastname());
        intent.putExtra("years", user.getYears());
        intent.putExtra("education", user.getEducation());
        intent.putExtra("location", user.getLocation());
        intent.putExtra("firm", user.getFirm());
        intent.putExtra("job", user.getJob());
        intent.putExtra("social", user.getSocial());
        intent.putExtra("tel", user.getTel());
        intent.putExtra("imgUrl", user.getImgUrl());
        intent.putExtra("email", user.getEmail());
        startActivity(intent);
    }
}