package com.mezun.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {
    private String fullname,years,email,education,location,firm,job,social,tel,imgUrl;
    TextView tv_fullname, tv_job, tv_edu, tv_years, tv_jobTitle,tv_joblocation, tv_firm, tv_socialTitle,tv_social,tv_telTitle, tv_tel, tv_email;
    private ImageView img_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setTitle("Kullanıcı Bilgileri");
        getExtras();
        matchComponents();
        fillComponents();

    }

    private void getExtras() {
        Bundle bundle = getIntent().getExtras();
        fullname = bundle.getString("fullname","");
        years = bundle.getString("years","");
        email = bundle.getString("email","");
        education = bundle.getString("education","");
        location = bundle.getString("location","");
        firm = bundle.getString("firm","");
        job = bundle.getString("job","");
        social = bundle.getString("social","");
        tel = bundle.getString("tel","");
        imgUrl = bundle.getString("imgUrl","");
    }
    private void matchComponents() {
        tv_fullname = findViewById(R.id.tv_userfullname);
        tv_job = findViewById(R.id.tv_userjob);
        tv_edu = findViewById(R.id.tv_useredu);
        tv_years = findViewById(R.id.tv_useryears);
        tv_jobTitle = findViewById(R.id.tv_userjobTitle);
        tv_joblocation = findViewById(R.id.tv_userjobLocation);
        tv_firm = findViewById(R.id.tv_userfirm);
        tv_socialTitle = findViewById(R.id.tv_usersocialTitle);
        tv_social = findViewById(R.id.tv_usersocial);
        tv_telTitle = findViewById(R.id.tv_usertelTitle);
        tv_tel = findViewById(R.id.tv_usertel);
        tv_email = findViewById(R.id.tv_useremail);
        img_profile = findViewById(R.id.img_userprofile);
    }
    private void fillComponents() {
        tv_fullname.setText(fullname);
        tv_years.setText(years);
        tv_email.setText(email);
        tv_edu.setText(education);
        tv_joblocation.setText(location);
        tv_firm.setText(firm);
        tv_job.setText(job);
        tv_social.setText(social);
        tv_tel.setText(tel);

        if(!imgUrl.isEmpty()){
            Picasso.get().load(imgUrl).into(img_profile);
        }

        if(!location.isEmpty())
            tv_joblocation.setVisibility(View.VISIBLE);
        if(!firm.isEmpty())
            tv_firm.setVisibility(View.VISIBLE);
        if(!location.isEmpty() || !firm.isEmpty())
            tv_jobTitle.setVisibility(View.VISIBLE);

        if(!social.isEmpty()) {
            tv_socialTitle.setVisibility(View.VISIBLE);
            tv_social.setVisibility(View.VISIBLE);
        }
        if(!tel.isEmpty()){
            tv_telTitle.setVisibility(View.VISIBLE);
            tv_tel.setVisibility(View.VISIBLE);
        }
    }
}