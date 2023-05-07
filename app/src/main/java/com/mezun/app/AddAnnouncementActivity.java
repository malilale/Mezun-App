package com.mezun.app;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddAnnouncementActivity extends AppCompatActivity {
    private EditText et_post;
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    String post,  currentUid, date;
    private long  time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_announcement);
        getSupportActionBar().setTitle("Duyuru Ekle");
        initDatePicker();

        et_post = findViewById(R.id.et_post);
        Button btn_sendpost = findViewById(R.id.btn_sendpost);
        dateButton = findViewById(R.id.datePickerButton);
        dateButton.setText(getTodaysDate());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUid = user.getUid();

        btn_sendpost.setOnClickListener(view -> {
            post = et_post.getText().toString().trim();
            loadData();
        });
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH)+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                date = makeDateString(day, month+1, year);
                dateButton.setText(date);
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, month);
                c.set(Calendar.DAY_OF_MONTH, day);
                time = c.getTimeInMillis();
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);

    }

    private String makeDateString(int day, int month, int year)
    {
        return day + "  "+getMonthFormat(month)  + "  " + year;
    }

    private String getMonthFormat(int month) {
        if(month == 1)      return "Ocak";
        if(month == 2)      return "Şubat";
        if(month == 3)      return "Mart";
        if(month == 4)      return "Nisan";
        if(month == 5)      return "Mayıs";
        if(month == 6)      return "Haziran";
        if(month == 7)      return "Temmuz";
        if(month == 8)      return "Ağustos";
        if(month == 9)      return "Eylül";
        if(month == 10)     return "Ekim";
        if(month == 11)     return "Kasım";
        if(month == 12)     return "Aralık";

        return "Ocak";
    }


    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }

    private void loadData() {
        DocumentReference reference;
        String postId = "post-"+ System.currentTimeMillis();

        Map<String, Object> postMap = new HashMap<>();
        postMap.put("post",post);
        postMap.put("uid",currentUid);
        postMap.put("postId", postId);
        postMap.put("date",date);
        postMap.put("time",time);
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