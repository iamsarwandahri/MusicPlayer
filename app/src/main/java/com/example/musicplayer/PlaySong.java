package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlaySong extends AppCompatActivity {
    TextView textView,currentTime,totalTime;
    ImageView play,previous,next;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    ArrayList<File> songsList;
    Uri uri;
    int position;
    String textContent;
    Thread updateSeek,updateTime,updateMusic;


    public void play(){

        textContent = songsList.get(position).getName().toString();
        textView.setText(textContent);
        play.setImageResource(R.drawable.pause);
        uri = Uri.parse(songsList.get(position).toString());
        mediaPlayer = MediaPlayer.create(PlaySong.this, uri);
        seekBar.setMax(mediaPlayer.getDuration());
        totalTime.setText(setTime(mediaPlayer.getDuration()));
        mediaPlayer.start();
        autoSeekBarUpdate();
        autoCurrentTimeUpdate();
        autoPlayNext();

    }
    //Thread to playNext Music
    public void autoPlayNext(){
        updateMusic = new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    while (position < songsList.size() - 1) {
                        int totalDuration = mediaPlayer.getDuration()/1000;
                        int currenDuration = mediaPlayer.getCurrentPosition()/1000;
                        if (currenDuration > totalDuration-1) {
                            position = position + 1;
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            play();
                        }
                        sleep(500);
                    }

                } catch (
                        Exception e) {
                    e.printStackTrace();
                }
            }
        };
        updateMusic.start();

    }

    //Thread to update the seekBar every second
    public void autoSeekBarUpdate() {
        updateSeek = new Thread() {
            @Override
            public void run() {
                int currentPosition = 0;
                try {
                    while (currentPosition < mediaPlayer.getDuration()) {
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        };
        updateSeek.start();
    }

    //Thread tp update Time
    public void autoCurrentTimeUpdate() {
        updateTime = new Thread() {
            @Override
            public void run() {
                int cp = mediaPlayer.getCurrentPosition();;
                try {
                    while (cp <= mediaPlayer.getDuration()) {
                        cp = mediaPlayer.getCurrentPosition();
                        String text = setTime(cp);
                        currentTime.setText(text);
                        sleep(1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        };
        updateTime.start();
    }

    //Returing time in string to set on TextView to show Time
    public String setTime(Integer getDurationInMillis){
        int getDurationMillis = getDurationInMillis;

        String convertHours = String.format("%02d", TimeUnit.MILLISECONDS.toHours(getDurationMillis));
        String convertMinutes = String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(getDurationMillis) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(getDurationMillis))); //I needed to add this part.
        String convertSeconds = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(getDurationMillis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getDurationMillis)));


        String getDuration = convertHours + ":" + convertMinutes + ":" + convertSeconds;

        return getDuration;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        textView = findViewById(R.id.textView);
        previous = findViewById(R.id.imageView2);
        play = findViewById(R.id.imageView3);
        next = findViewById(R.id.imageView4);
        seekBar = findViewById(R.id.seekBar);
        currentTime = findViewById(R.id.curentTime);
        totalTime = findViewById(R.id.totalTime);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songsList = (ArrayList) bundle.getParcelableArrayList("songs");
        position = intent.getIntExtra("position",0);
        textView.setSelected(true);
        play();
        autoCurrentTimeUpdate();

        // SeekBar
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
            });

        // Play Button
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();

                }
                else{
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();

                }

            }
        });

        // Previus Button
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position==0){
                    position = songsList.size()-1;
                }
                else{
                    position = position-1;
                }
                mediaPlayer.stop();
                mediaPlayer.release();
                play();
            }
        });


        // NEXT Button
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == songsList.size()-1) {
                    position = 0;
                } else {
                    position = position + 1;
                }
                mediaPlayer.stop();
                mediaPlayer.release();
                play();

            }
            });
    }


    //to Stop music when activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

}