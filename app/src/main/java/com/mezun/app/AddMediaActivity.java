package com.mezun.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddMediaActivity extends AppCompatActivity {
    private ImageView img_media;
    private VideoView vid_media;
    private EditText et_title;
    private TextView tv_addmedia;
    ProgressDialog progressDialog;
    Button btn_sendmedia;
    private String type, title;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Uri selectedUri, thumbUri;
    public static final int GALLERY_PERM_CODE = 102;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_media);

        setPickFromGalleryIntent();

        img_media = findViewById(R.id.img_media);
        vid_media = findViewById(R.id.vid_media);
        et_title = findViewById(R.id.et_title);
        tv_addmedia = findViewById(R.id.tv_addmedia);
        btn_sendmedia = findViewById(R.id.btn_sendmedia);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("İçerik Gönderiliyor...");

        img_media.setOnClickListener(view -> askGalleryPermissions());

        vid_media.setOnClickListener(view -> askGalleryPermissions());

        btn_sendmedia.setOnClickListener(view -> {
            title = et_title.getText().toString().trim();
            if(selectedUri == null)
                Toast.makeText(AddMediaActivity.this,"Lütfen bir fotoğraf veya video seçin",Toast.LENGTH_SHORT).show();
            else{
                loadData();
            }
        });

    }

    private void loadData() {
        progressDialog.show();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference mediaStorageRef, thumbStorageRef;
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Media").document("media-"+System.currentTimeMillis());

        Map<String, String> media = new HashMap<>();
        media.put("title", title);
        media.put("uid", currentUser.getUid());
        media.put("type",type);

        String fileName=System.currentTimeMillis()+"";
        String thumbName=fileName+".png";
        if(type.contains("image"))
            fileName=fileName+".jpg";
        else if(type.contains("video"))
            fileName=fileName+".mp4";
        mediaStorageRef = FirebaseStorage.getInstance().getReference("Media");
        thumbStorageRef = FirebaseStorage.getInstance().getReference("Thumbnails");

        StorageReference mediaFilePath = mediaStorageRef.child(fileName);
        StorageReference thumbFilePath = thumbStorageRef.child(thumbName);

        mediaFilePath.putFile(selectedUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mediaFilePath.getDownloadUrl().addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()){
                        media.put("mediaUrl", task1.getResult().toString());
                        thumbFilePath.putFile(thumbUri).addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()){
                                thumbFilePath.getDownloadUrl().addOnCompleteListener(task21 -> {
                                    if(task21.isSuccessful()){
                                        media.put("thumbUrl", task21.getResult().toString());
                                        documentReference.set(media)
                                            .addOnSuccessListener(unused ->{
                                                Toast.makeText(AddMediaActivity.this,"Başarıyla Kaydedildi", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                                finish();})
                                            .addOnFailureListener(e ->{
                                                Toast.makeText(AddMediaActivity.this,"Kaydedilemedi", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();});
                                    }else
                                        Toast.makeText(AddMediaActivity.this,"FAILED!",Toast.LENGTH_SHORT).show();
                                });
                            }else{
                                Toast.makeText(AddMediaActivity.this,"FAILED!",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }

    @SuppressLint("NewApi")
    private void setPickFromGalleryIntent(){
        activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedUri = result.getData().getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    if(getContentResolver().getType(selectedUri).contains("image")){
                        Picasso.get().load(selectedUri).into(img_media);
                        tv_addmedia.setVisibility(View.GONE);
                        vid_media.setVisibility(View.INVISIBLE);
                        img_media.setVisibility(View.VISIBLE);

                        Bitmap bmap = ThumbnailUtils.createImageThumbnail(picturePath,MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                        try {
                            thumbUri = getImageUri(bmap);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        type="image";
                    }else if(getContentResolver().getType(selectedUri).contains("video")){
                        tv_addmedia.setVisibility(View.GONE);
                        vid_media.setVideoURI(selectedUri);
                        img_media.setVisibility(View.INVISIBLE);
                        vid_media.setVisibility(View.VISIBLE);

                        Bitmap bmap = ThumbnailUtils.createVideoThumbnail(picturePath,MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                        try {
                            thumbUri = getImageUri(bmap);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        type="video";
                        vid_media.start();
                    }
                }
            });
    }

    public Uri getImageUri( Bitmap inImage) throws IOException {
        File tempFile = new File(getCacheDir(), "temp.png");
        try {
            FileOutputStream fos = new FileOutputStream(tempFile);
            inImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
// Handle error
        }
        return Uri.fromFile(tempFile);
    }

    private void pickFromGallery() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/* video/*");
        try{
            activityResultLauncher.launch(pickIntent);
        }catch (ActivityNotFoundException e){
            Toast.makeText(AddMediaActivity.this,"Uygulama yok!",Toast.LENGTH_SHORT).show();
        }
    }

    private void askGalleryPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERM_CODE);
        }else
            pickFromGallery();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == GALLERY_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickFromGallery();
            } else {
                Toast.makeText(this, "External Storage Permission is Required to Use Gallery.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}