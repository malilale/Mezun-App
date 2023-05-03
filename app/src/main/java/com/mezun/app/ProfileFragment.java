package com.mezun.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {

    private View view;
    TextView tv_fullname, tv_job, tv_edu, tv_years, tv_jobTitle,tv_joblocation, tv_firm, tv_socialTitle,tv_social,tv_telTitle, tv_tel, tv_email;
    ImageView img_profile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        tv_fullname = view.findViewById(R.id.tv_fullname);
        tv_job = view.findViewById(R.id.tv_job);
        tv_edu = view.findViewById(R.id.tv_edu);
        tv_years = view.findViewById(R.id.tv_years);
        tv_jobTitle = view.findViewById(R.id.tv_jobTitle);
        tv_joblocation = view.findViewById(R.id.tv_jobLocation);
        tv_firm = view.findViewById(R.id.tv_firm);
        tv_socialTitle = view.findViewById(R.id.tv_socialTitle);
        tv_social = view.findViewById(R.id.tv_social);
        tv_telTitle = view.findViewById(R.id.tv_telTitle);
        tv_tel = view.findViewById(R.id.tv_tel);
        tv_email = view.findViewById(R.id.tv_email);
        img_profile = view.findViewById(R.id.img_profile);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firestore.collection("Users").document(currentUserId);

        documentReference.get().addOnCompleteListener(task -> {
            if(task.getResult().exists()){
                getData(task);
            }else{
                //toast
            }
        });
    }

    private void getData(Task<DocumentSnapshot> task) {
        String name = task.getResult().getString("name");
        String  lastname= task.getResult().getString("lastname");
        String  startyear = task.getResult().getString("startyear");
        String  endyear = task.getResult().getString("endyear");
        String  email = task.getResult().getString("email");
        String  education = task.getResult().getString("education");
        String  country = task.getResult().getString("country");
        String  city = task.getResult().getString("city");
        String  firm = task.getResult().getString("firm");
        String  job = task.getResult().getString("job");
        String  social = task.getResult().getString("social");
        String  tel = task.getResult().getString("tel");
        String  imgUrl= task.getResult().getString("imgUrl");

        if(!imgUrl.isEmpty()){
            Log.d("00000",imgUrl);
            Picasso.get().load(imgUrl).into(img_profile);
        }

        tv_fullname.setText(name+" "+lastname);
        tv_job.setText(job);
        tv_edu.setText(education);
        tv_years.setText(startyear+"-"+endyear);
        tv_email.setText(email);
        if(country.isEmpty()) {
            tv_joblocation.setText(city);
            if(city.isEmpty())
                tv_joblocation.setVisibility(View.GONE);
        }else if(city.isEmpty())
            tv_joblocation.setText(country);
        else
            tv_joblocation.setText(city+"/"+country);
        tv_firm.setText(firm);
        if(country.isEmpty() && city.isEmpty() && firm.isEmpty()) {
            tv_jobTitle.setVisibility(View.GONE);
            tv_firm.setVisibility(View.GONE);
        }


        if(social.isEmpty()) {
            tv_socialTitle.setVisibility(View.GONE);
            tv_social.setVisibility(View.GONE);
        }else
            tv_social.setText(social);
        if(tel.isEmpty()){
            tv_telTitle.setVisibility(View.GONE);
            tv_tel.setVisibility(View.GONE);
        }else
            tv_tel.setText(tel);

    }
}