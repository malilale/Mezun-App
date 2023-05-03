package com.mezun.app;

import static androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_name, et_lastname,et_starty, et_endy, et_email, et_password;
    private Button btn_register;
    private ImageView addPhoto;
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<Intent> CamActivityResultLauncher,galleryActivityResultLauncher;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int GALLERY_PERM_CODE = 102;

    private Uri image_uri;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;
    private DocumentReference documentReference;
    private StorageReference storageRef;

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
        builder.setItems(options, (dialogInterface, i) -> {
            if(i == 0){
                //Camera
                pickFromCamera();
            }else{
                //Gallery
                pickFromGallery();
            }
        });
        builder.create().show();
    }

    public Uri getImageUri( Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(RegisterActivity.this.getContentResolver(), inImage, UUID.randomUUID().toString() + ".png", "drawing");
        return Uri.parse(path);
    }

    private void setCameraIntent() {
        CamActivityResultLauncher = registerForActivityResult(new StartActivityForResult(), result -> {
            if(result.getResultCode() == RESULT_OK && result.getData() != null) {
               Bundle bundle = result.getData().getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");
                //addPhoto.setImageBitmap(bitmap);
                image_uri = getImageUri(bitmap);
                addPhoto.setImageURI(image_uri);

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
                    image_uri = result.getData().getData();
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
                pickFromGallery();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                uploadData();
                sendToMain();
            }else {
                String error = Objects.requireNonNull(task.getException()).getMessage();
                Toast.makeText(RegisterActivity.this,"Hata"+error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadData() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("Profile Images");
        if(fUser!=null)
            documentReference = db.collection("Users").document(fUser.getUid());

        Map<String, String> user = new HashMap<>();
        user.put("name", et_name.getText().toString());
        user.put("lastname", et_lastname.getText().toString());
        user.put("startyear", et_starty.getText().toString());
        user.put("endyear", et_endy.getText().toString());
        user.put("email", et_email.getText().toString());
        user.put("password", et_password.getText().toString());
        user.put("education", "Lisans");
        user.put("country", "");
        user.put("city", "");
        user.put("firm", "");
        user.put("social", "");
        user.put("job", "");
        user.put("tel", "");
        if(image_uri!=null){
            StorageReference filePath = storageRef.child(System.currentTimeMillis() + ".jpg");
            filePath.putFile(image_uri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Task<Uri> downloadUri = filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            user.put("imgUrl", task.getResult().toString());
                            documentReference.set(user)
                                    .addOnSuccessListener(unused ->
                                            Toast.makeText(RegisterActivity.this,"Başarıyla Kaydedildi", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(RegisterActivity.this,"Kaydedilemedi", Toast.LENGTH_SHORT).show());
                        }
                    });


                }
            });
        }else{
            user.put("imgUrl", "");
            documentReference.set(user)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(RegisterActivity.this,"Başarıyla Kaydedildi", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(RegisterActivity.this,"Kaydedilemedi", Toast.LENGTH_SHORT).show());
        }
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