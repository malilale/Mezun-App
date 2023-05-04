package com.mezun.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {

    TextView tv_fullname, tv_job, tv_edu, tv_years, tv_jobTitle,tv_joblocation, tv_firm, tv_socialTitle,tv_social,tv_telTitle, tv_tel, tv_email;
    ImageView img_profile;
    private String name,lastname,startyear,endyear,email,education,country,city,firm,job,social,tel,imgUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setHasOptionsMenu(true);

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
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.act_logout:
                FirebaseAuth.getInstance().signOut();
                sendToLoginPage();
                break;
            case R.id.act_edit:
                sendToEditProfilePage();
                break;
            case R.id.act_updatepassword:
                updatePasswordDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updatePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LinearLayout layout = new LinearLayout(getActivity());
        final EditText et_password = new EditText(getActivity());
        et_password.setHint("Yeni Şifre");
        et_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        layout.addView(et_password);
        layout.setPadding(20,20,10,10);

        builder.setView(layout);

        builder.setPositiveButton("Değiştir", (dialogInterface, i) -> {
            String password = et_password.getText().toString().trim();
            updatePassword(password);
        });
        builder.setNegativeButton("İptal", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.create().show();
    }

    private void updatePassword(String password) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.updatePassword(password).addOnCompleteListener(task -> {
            if(task.isSuccessful())
                Toast.makeText(getActivity(), "Şifre Başarıyla Değiştirildi", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
        });
    }

    private void sendToEditProfilePage() {
        Intent intent = new Intent(getActivity(),EditProfileActivity.class);
        intent.putExtra("name",name);
        intent.putExtra("lastname",lastname);
        intent.putExtra("startyear",startyear);
        intent.putExtra("endyear",endyear);
        intent.putExtra("education",education);
        intent.putExtra("country",country);
        intent.putExtra("city",city);
        intent.putExtra("firm",firm);
        intent.putExtra("job",job);
        intent.putExtra("social",social);
        intent.putExtra("tel",tel);
        intent.putExtra("imgUrl",imgUrl);
        intent.putExtra("email",email);
        startActivity(intent);
    }

    private void sendToLoginPage() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void getData(Task<DocumentSnapshot> task) {
        name = task.getResult().getString("name");
        lastname= task.getResult().getString("lastname");
        startyear = task.getResult().getString("startyear");
        endyear = task.getResult().getString("endyear");
        email = task.getResult().getString("email");
        education = task.getResult().getString("education");
        country = task.getResult().getString("country");
        city = task.getResult().getString("city");
        firm = task.getResult().getString("firm");
        job = task.getResult().getString("job");
        social = task.getResult().getString("social");
        tel = task.getResult().getString("tel");
        imgUrl= task.getResult().getString("imgUrl");

        if(!imgUrl.isEmpty()){
            Picasso.get().load(imgUrl).into(img_profile);
        }

        tv_fullname.setText(name+" "+lastname);
        tv_job.setText(job);
        tv_edu.setText(education);
        tv_years.setText(startyear+"-"+endyear);
        tv_email.setText(email);
        tv_firm.setText(firm);

        if(!country.isEmpty() || !city.isEmpty() || !firm.isEmpty())
            tv_jobTitle.setVisibility(View.VISIBLE);

        if(!country.isEmpty() || !city.isEmpty())
            tv_joblocation.setVisibility(View.VISIBLE);

        if(country.isEmpty()) {
            tv_joblocation.setText(city);
        }else if(city.isEmpty())
            tv_joblocation.setText(country);
        else
            tv_joblocation.setText(city+"/"+country);

        if(!social.isEmpty()) {
            tv_social.setText(social);
            tv_socialTitle.setVisibility(View.VISIBLE);
            tv_social.setVisibility(View.VISIBLE);
        }
        if(!tel.isEmpty()){
            tv_tel.setText(tel);
            tv_telTitle.setVisibility(View.VISIBLE);
            tv_tel.setVisibility(View.VISIBLE);
        }


    }
}