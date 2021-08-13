package com.example.musicplayer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @RequiresApi(api = Build.VERSION_CODES.R)
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                    ArrayList<File> mySongs = fetchMp3(Environment.getExternalStorageDirectory());
                    ArrayList<File> cardSongs = fetchMp3(Environment.getStorageDirectory());
                    mySongs.addAll(cardSongs);
                    String[] item = new String[mySongs.size()];

                    for(int i = 0;i<mySongs.size();i++)
                    {
                        item[i] = mySongs.get(i).getName().replace(".mp3", "");
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, item);
                    listView.setAdapter(arrayAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(MainActivity.this,PlaySong.class);
                            String songName = listView.getItemAtPosition(position).toString();
                            intent.putExtra("name",songName);
                            intent.putExtra("position",position);
                            intent.putExtra("songs",mySongs);
                            startActivity(intent);
                        }
                    });
                }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }
    public ArrayList<File> fetchMp3(File folder){
        ArrayList arraylist = new ArrayList();
        File[] mp3 = folder.listFiles();
        if(mp3 != null){
            for(File m : mp3){
                if(!m.isHidden() && m.isDirectory() && m.getName()!="Android"){
                    arraylist.addAll(fetchMp3(m));
                }
                else {
                    if (m.getName().endsWith(".mp3") && !m.getName().startsWith(".mp3") && !m.getName().startsWith(".")) {
                        arraylist.add(m);
                    }
                }
                }

        }
        return arraylist;
    }
}