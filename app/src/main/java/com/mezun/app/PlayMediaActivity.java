package com.mezun.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class PlayMediaActivity extends AppCompatActivity {
    private ImageView imageViewer;
    private VideoView videoViewer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_media);
        getSupportActionBar().setTitle(R.string.create_media);

        Bundle bundle = getIntent().getExtras();
        String url = bundle.getString("mediaUrl");
        String type = bundle.getString("type");

        imageViewer = findViewById(R.id.imageviewer);
        videoViewer = findViewById(R.id.videoviewer);

        if(type.matches("image"))
            setImage(url);
        else
            setVideo(url);
    }

    private void setVideo(String url) {
        MediaController mediaController = new MediaController(this);
        videoViewer.setMediaController(mediaController);
        mediaController.setAnchorView(videoViewer);
        videoViewer.setVideoPath(url);
        imageViewer.setVisibility(View.GONE);
        videoViewer.setVisibility(View.VISIBLE);
        videoViewer.requestFocus();
        videoViewer.start();
    }

    private void setImage(String url) {
        Picasso.get().load(url).into(imageViewer);
        videoViewer.setVisibility(View.GONE);
        imageViewer.setVisibility(View.VISIBLE);
    }
}