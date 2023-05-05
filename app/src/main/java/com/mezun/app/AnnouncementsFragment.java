package com.mezun.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AnnouncementsFragment extends Fragment {
    private FloatingActionButton btn_addpost;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_announcements, container, false);

        btn_addpost = view.findViewById(R.id.btn_addpost);

        btn_addpost.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(),AddAnnouncementActivity.class);
            startActivity(intent);
        });

        return view;
    }
}