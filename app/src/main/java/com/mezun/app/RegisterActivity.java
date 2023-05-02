package com.mezun.app;

import static androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
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

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_name, et_lastname,et_starty, et_endy, et_email, et_password;
    private Button btn_register;
    private ImageView addPhoto;
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<Intent> CamActivityResultLauncher,galleryActivityResultLauncher;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int GALLERY_PERM_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_name = findViewById(R.id.et_name);
        et_lastname = findViewById(R.id.et_lastname);
        et_starty = findViewById(R.id.et_starty);
        et_endy = findViewById(R.id.et_endy);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_register = findViewById(R.id.btn_register);
        addPhoto = findViewById(R.id.img_addPhoto);
        mAuth = FirebaseAuth.getInstance();

        setCameraIntent();
        setPickFromGalleryIntent();

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog();
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = et_name.getText().toString().trim();
                String lastname = et_lastname.getText().toString().trim();
                String starty = et_starty.getText().toString().trim();
                String endy = et_endy.getText().toString().trim();
                String email = et_email.getText().toString().trim();
                String password = et_password.getText().toString().trim();

                if(!name.isEmpty() || !lastname.isEmpty() ||!starty.isEmpty() ||!endy.isEmpty() ||!email.isEmpty() ||!password.isEmpty()) {
                    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        //error message
                        et_email.setError(getString(R.string.invaild_email));
                        et_email.setFocusable(true);
                    }else {
                        registerUser(email,password);
                    }
                }else {
                    Toast.makeText(RegisterActivity.this,"Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showImagePickDialog() {
        String options[] = {"Kamera","Galeri"};
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Profil Fotoğrafı");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0){
                    //Camera
                    pickFromCamera();
                }else{
                    //Gallery
                    pickFromGallery();
                }
            }
        });
        builder.create().show();
    }

    private void setCameraIntent() {
        CamActivityResultLauncher = registerForActivityResult(new StartActivityForResult(), result -> {
            if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                Bundle bundle = result.getData().getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");
                addPhoto.setImageBitmap(bitmap);
            }
        });
    }

    private void pickFromCamera() {
        askCameraPermissions();
        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try{
            CamActivityResultLauncher.launch(camIntent);
        }catch (ActivityNotFoundException e){
            Toast.makeText(RegisterActivity.this,"Uygulama yok!",Toast.LENGTH_SHORT).show();
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
                    Uri image_uri = result.getData().getData();
                    addPhoto.setImageURI(image_uri);
                }
            });
    }

    private void pickFromGallery() {
        askGalleryPermissions();
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try{
            galleryActivityResultLauncher.launch(pickIntent);
        }catch (ActivityNotFoundException e){
            Toast.makeText(RegisterActivity.this,"Uygulama yok!",Toast.LENGTH_SHORT).show();
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
                pickFromCamera();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                sendToMain();
            }else {
                String error = Objects.requireNonNull(task.getException()).getMessage();
                Toast.makeText(RegisterActivity.this,"Hata"+error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendToMain() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null)
            sendToMain();
    }
}