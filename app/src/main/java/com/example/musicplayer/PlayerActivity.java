package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
             mediaPlayer.stop();
             mediaPlayer.release();
             updateSeek.interrupt();
    }

    Button pause, previous, next;
    TextView songTextLabel;
    SeekBar songSeekbar;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    int position;
    Thread updateSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        next = findViewById(R.id.next);
        pause = findViewById(R.id.pause);
        previous = findViewById(R.id.previous);
        songSeekbar = findViewById(R.id.seekBar);
        songTextLabel = findViewById(R.id.songLabel);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("songList");
        textContent = intent.getStringExtra("currentSong");
        songTextLabel.setText(textContent);
        songTextLabel.setSelected(true);
        position = intent.getIntExtra("position", 0);
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();
        songSeekbar.setMax(mediaPlayer.getDuration());
        songSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songSeekbar.setMax(mediaPlayer.getDuration());
                if (mediaPlayer.isPlaying()) {
                    pause.setBackgroundResource(R.drawable.icon_play);
                    mediaPlayer.pause();
                } else {
                    pause.setBackgroundResource(R.drawable.icon_pause);
                    mediaPlayer.start();
                }
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position != 0) {
                    position = position - 1;
                } else {
                    position = songs.size() - 1;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                pause.setBackgroundResource(R.drawable.icon_pause);
                songSeekbar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName().toString();
                songTextLabel.setText(textContent);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position != songs.size() - 1) {
                    position = position + 1;
                } else {
                    position = 0;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                pause.setBackgroundResource(R.drawable.icon_pause);
                songSeekbar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName().toString();
                songTextLabel.setText(textContent);
            }
        });


        updateSeek = new Thread() {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;
                while(currentPosition < totalDuration){
                    try{
                        sleep(800);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        songSeekbar.setProgress(currentPosition);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        updateSeek.start();
    }
}

