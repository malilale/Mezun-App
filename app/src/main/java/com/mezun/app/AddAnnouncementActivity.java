package com.mezun.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddAnnouncementActivity extends AppCompatActivity {
    private EditText et_post;
    String post, name, lastname, email, imgUrl, currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_announcement);
        getSupportActionBar().setTitle("Duyuru Ekle");

        et_post = findViewById(R.id.et_post);
        Button btn_sendpost = findViewById(R.id.btn_sendpost);

        getCurrentUser();

        btn_sendpost.setOnClickListener(view -> {
            post = et_post.getText().toString().trim();
            loadData();
        });
    }

    private void getCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUid = user.getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firestore.collection("Users").document(currentUid);

        documentReference.get().addOnCompleteListener(task -> {
            if(task.getResult().exists()){
                name = task.getResult().getString("name");
                lastname = task.getResult().getString("lastname");
                email = task.getResult().getString("email");
                imgUrl = task.getResult().getString("imgUrl");
            }else{
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        DocumentReference reference;
        String postId = "post-"+ System.currentTimeMillis();
        Map<String, String> postMap = new HashMap<>();
        postMap.put("post",post);
        postMap.put("uid",currentUid);
        postMap.put("fullname",name+" "+lastname);
        postMap.put("email",email);
        postMap.put("imgUrl",imgUrl);
        postMap.put("postId", postId);
        reference = FirebaseFirestore.getInstance().collection("Posts").document(postId);
        reference.set(postMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(AddAnnouncementActivity.this,"Başarıyla Gönderildi", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddAnnouncementActivity.this,"Gönderilemedi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}