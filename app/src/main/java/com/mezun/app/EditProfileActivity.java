package com.mezun.app;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {
    private String name,lastname,startyear,endyear,email,education,country,city,firm,job,social,tel,imgUrl;
    private String new_name,new_lastname,new_startyear,new_endyear,new_email,new_education,new_country,new_city,new_firm,new_job,new_social,new_tel;
    private EditText et_name,et_lastname,et_startyear,et_endyear,et_email,et_education,et_country,et_city,et_firm,et_job,et_social,et_tel;
    private ImageView img_profile;
    private Button btn_save;
    private ActivityResultLauncher<Intent> CamActivityResultLauncher,galleryActivityResultLauncher;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int GALLERY_PERM_CODE = 102;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private Uri image_uri;
    private boolean isPfpChanged;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setCameraIntent();
        setPickFromGalleryIntent();
        getExtras();
        matchComponents();
        fillComponents();


        img_profile.setOnClickListener(view -> showImagePickDialog());
        
        btn_save.setOnClickListener(view -> {
            getNewdata();
            loadDatasToDb();
        });
        
    }

    @Override
    protected void onStart() {
        super.onStart();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        documentReference = db.collection("Users").document(userID);
    }

    private void showImagePickDialog() {
        String options[] = {"Kamera","Galeri"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle("Profil Fotoğrafı");
        builder.setItems(options, (dialogInterface, i) -> {
            if(i == 0){
                //Camera
                askCameraPermissions();
            }else{
                //Gallery
                askGalleryPermissions();
            }
        });
        builder.create().show();
    }

    public Uri getImageUri( Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(EditProfileActivity.this.getContentResolver(), inImage, UUID.randomUUID().toString() + ".png", "drawing");
        return Uri.parse(path);
    }

    private void setCameraIntent() {
        CamActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                Bundle bundle = result.getData().getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");
                image_uri = getImageUri(bitmap);
                img_profile.setImageURI(image_uri);
                isPfpChanged = true;
            }
        });

    }

    private void pickFromCamera() {
        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try{
            CamActivityResultLauncher.launch(camIntent);
        }catch (ActivityNotFoundException e){
            Toast.makeText(EditProfileActivity.this,"Uygulama yok!",Toast.LENGTH_SHORT).show();
        }
    }

    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }
    }

    private void setPickFromGalleryIntent(){
        galleryActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                        image_uri = result.getData().getData();
                        img_profile.setImageURI(image_uri);
                        isPfpChanged = true;
                    }
                });
    }

    private void pickFromGallery() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try{
            galleryActivityResultLauncher.launch(pickIntent);
        }catch (ActivityNotFoundException e){
            Toast.makeText(EditProfileActivity.this,"Uygulama yok!",Toast.LENGTH_SHORT).show();
        }
    }

    private void askGalleryPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERM_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                pickFromCamera();
            }else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == GALLERY_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickFromGallery();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void loadDatasToDb() {
        Map<String, String> user = new HashMap<>();
        user.put("name", new_name);
        user.put("lastname", new_lastname);
        user.put("startyear", new_startyear);
        user.put("endyear", new_endyear);
        user.put("education", new_education);
        user.put("country", new_country);
        user.put("city", new_city);
        user.put("firm", new_firm);
        user.put("social", new_social);
        user.put("job", new_job);
        user.put("tel", new_tel);
        user.put("email", new_email);
        user.put("imgUrl","");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference storageRef;
        if(isPfpChanged){
            storageRef = FirebaseStorage.getInstance().getReference("Profile Images");
            StorageReference filePath = storageRef.child(System.currentTimeMillis() + ".jpg");
            filePath.putFile(image_uri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    filePath.getDownloadUrl().addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){
                            user.put("imgUrl", task1.getResult().toString());
                            documentReference.set(user)
                                    .addOnSuccessListener(unused ->{
                                            Toast.makeText(EditProfileActivity.this,"Başarıyla Kaydedildi", Toast.LENGTH_SHORT).show();
                                        finish();})
                                    .addOnFailureListener(e ->
                                            Toast.makeText(EditProfileActivity.this,"Kaydedilemedi", Toast.LENGTH_SHORT).show());
                        }
                    });


                }
            });
        }else{
            user.put("imgUrl", imgUrl);
            documentReference.set(user)
                    .addOnSuccessListener(unused ->{
                        Toast.makeText(EditProfileActivity.this,"Başarıyla Kaydedildi", Toast.LENGTH_SHORT).show();
                        finish();})
                    .addOnFailureListener(e ->
                            Toast.makeText(EditProfileActivity.this,"Kaydedilemedi", Toast.LENGTH_SHORT).show());
        }

        if(!email.matches(new_email)) {
            currentUser.updateEmail(new_email).addOnCompleteListener(task -> {
                if(task.isSuccessful())
                    Toast.makeText(EditProfileActivity.this,"E-Posta Başarıyla değişttirildi", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(EditProfileActivity.this,"KE-Posta Değiştirilemedi", Toast.LENGTH_SHORT).show();
            });
        }


    }

    private void getNewdata() {
        new_name = et_name.getText().toString().trim();
        new_lastname = et_lastname.getText().toString().trim();
        new_startyear = et_startyear.getText().toString().trim();
        new_endyear = et_endyear.getText().toString().trim();
        new_email = et_email.getText().toString().trim();
        new_education = et_education.getText().toString().trim();
        new_country = et_country.getText().toString().trim();
        new_city = et_city.getText().toString().trim();
        new_firm = et_firm.getText().toString().trim();
        new_job = et_job.getText().toString().trim();
        new_social = et_social.getText().toString().trim();
        new_tel = et_tel.getText().toString().trim();
    }

    private void getExtras() {
        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name","");
        lastname = bundle.getString("lastname","");
        startyear = bundle.getString("startyear","");
        endyear = bundle.getString("endyear","");
        email = bundle.getString("email","");
        education = bundle.getString("education","");
        country = bundle.getString("country","");
        city = bundle.getString("city","");
        firm = bundle.getString("firm","");
        job = bundle.getString("job","");
        social = bundle.getString("social","");
        tel = bundle.getString("tel","");
        imgUrl = bundle.getString("imgUrl","");
    }
    private void matchComponents() {
        et_name = findViewById(R.id.et_edit_name);
        et_lastname = findViewById(R.id.et_edit_lastname);
        et_startyear = findViewById(R.id.et_edit_starty);
        et_endyear = findViewById(R.id.et_edit_endy);
        et_email = findViewById(R.id.et_edit_email);
        et_education = findViewById(R.id.et_edit_education);
        et_country = findViewById(R.id.et_edit_country);
        et_city = findViewById(R.id.et_edit_city);
        et_firm = findViewById(R.id.et_edit_firm);
        et_job = findViewById(R.id.et_edit_job);
        et_social = findViewById(R.id.et_edit_social);
        et_tel = findViewById(R.id.et_edit_tel);
        img_profile = findViewById(R.id.img_edit_addPhoto);
        btn_save = findViewById(R.id.btn_save);
    }
    private void fillComponents() {
        isPfpChanged=false;

        et_name.setText(name);
        et_lastname.setText(lastname);
        et_startyear.setText(startyear);
        et_endyear.setText(endyear);
        et_email.setText(email);
        et_education.setText(education);
        et_country.setText(country);
        et_city.setText(city);
        et_firm.setText(firm);
        et_job.setText(job);
        et_social.setText(social);
        et_tel.setText(tel);

        Picasso.get().load(imgUrl).into(img_profile);
    }
}