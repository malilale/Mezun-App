package com.mezun.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    MainPageFragment mainPageFragment = new MainPageFragment();
    UsersFragment usersFragment = new UsersFragment();
    AnnouncementsFragment announcementsFragment = new AnnouncementsFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle(R.string.mainpage);
        if(getCallingActivity()!=null)
            getSupportFragmentManager().beginTransaction().replace(R.id.container,profileFragment).commit();
        else
            getSupportFragmentManager().beginTransaction().replace(R.id.container,mainPageFragment).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selected=null;
            switch (item.getItemId()){
                case R.id.mainpage_menu:
                    selected = mainPageFragment;
                    getSupportActionBar().setTitle(R.string.mainpage);
                    break;
                case R.id.users_menu:
                    selected = usersFragment;
                    getSupportActionBar().setTitle(R.string.users);
                    break;
                case R.id.announcements_menu:
                    selected = announcementsFragment;
                    getSupportActionBar().setTitle(R.string.announcements);
                    break;
                case R.id.profile_menu:
                    selected = profileFragment;
                    getSupportActionBar().setTitle(R.string.profile);
                    break;
            }
            if(selected!=null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
                return true;
            }else
                return false;
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
            sendToLoginPage();
    }

    private void sendToLoginPage() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}