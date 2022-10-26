package com.parvesh.imagepickerlistview;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Configuration;
import androidx.work.WorkManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//import com.cloudinary.android.MediaManager;


import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView textview, status;
    Button pick;
    RecyclerAdapter adapter;
    ArrayList<Uri> uri = new ArrayList<>();
    private static final int Read_Permission = 101;
    Map config = new HashMap();

    private void configCloudinary() {

        // provide custom configuration
        Configuration myConfig = new Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .build();
        //initialize WorkManager
        WorkManager.initialize(this, myConfig);

        config.put("cloud_name", "YOUR CLOUD NAME");
        config.put("api_key", "YOUR API KEY");
        config.put("api_secret", "YOUR API SECRET");
        config.put("secure", true);
        MediaManager.init(this, config);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textview = findViewById(R.id.test);
        status = findViewById(R.id.status);
        recyclerView = findViewById(R.id.imageview);
        pick = findViewById(R.id.button);
        adapter = new RecyclerAdapter(uri);
        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 4));
        recyclerView.setAdapter(adapter);

        configCloudinary();

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Read_Permission);
        }

        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");


                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Image"),1 );


            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ArrayList<String> filepaths = new ArrayList<>();

        if(requestCode == 1 && resultCode == Activity.RESULT_OK)
        {

            if(data.getClipData() != null)
            {
                uri.clear();
                filepaths.clear();
                int x = data.getClipData().getItemCount();

                for(int i = 0; i < x; i++)
                {
                    uri.add(data.getClipData().getItemAt(i).getUri());
                    String path = getImageFilePath(data.getClipData().getItemAt(i).getUri());
                    Log.d("a", path);
                    filepaths.add(path);
                    //uploadToCloudinary(path);
                }
                adapter.notifyDataSetChanged();
                textview.setText(String.valueOf(uri.size()));


            }

            else if(data.getData() != null)
            {
                uri.clear();
                filepaths.clear();
                Uri imageURL = data.getData();
                Log.d("URL", String.valueOf(imageURL));
                uri.add(imageURL);
                String path = getImageFilePath(imageURL);
                filepaths.add(path);
                adapter.notifyDataSetChanged();
            }

            for (int counter = 0; counter  < filepaths.size(); counter++)
            {
                Log.d("test", "Entered");
                uploadToCloudinary(filepaths.get(counter));
            }
        }

    }

    private void uploadToCloudinary(String path) {


        Log.d("A", "sign up uploadToCloudinary- ");

        MediaManager.get().upload(path)
                .callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {
                status.setText("start");
            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {
                status.setText("Uploading... ");
            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                //status.setText("image URL: "+resultData.get("url").toString());
                status.setText("Uploaded");
                Log.d("abc","image URL:" +resultData.get("url").toString());
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                status.setText("error "+ error.getDescription());
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
                status.setText("Reshedule "+error.getDescription());
            }
        }
        ).option("folder", "folder").dispatch();

    }


    private String getImageFilePath(Uri uri) {
        File file = new File(uri.getPath());
        String[] filePath = file.getPath().split(":");
        String image_id = filePath[filePath.length - 1];

        Cursor cursor = getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);
        if (cursor != null) {
            cursor.moveToFirst();
            @SuppressLint("Range") String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

            cursor.close();
            return imagePath;
        }
        return null;

    }
}