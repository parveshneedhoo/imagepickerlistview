package com.parvesh.imagepickerlistview;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView textview;
    Button pick;

    ArrayList<Uri> uri = new ArrayList<>();

    RecyclerAdapter adapter;

    private static final int Read_Permission = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textview = findViewById(R.id.test);
        recyclerView = findViewById(R.id.imageview);
        pick = findViewById(R.id.button);

        adapter = new RecyclerAdapter(uri);

        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 4));

        recyclerView.setAdapter(adapter);

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


        if(requestCode == 1 && resultCode == Activity.RESULT_OK)
        {

            if(data.getClipData() != null)
            {

                int x = data.getClipData().getItemCount();

                for(int i = 0; i < x; i++)
                {
                    uri.add(data.getClipData().getItemAt(i).getUri());
                }
                adapter.notifyDataSetChanged();
                textview.setText(String.valueOf(uri.size()));

            }

            else if(data.getData() != null)
            {
                Uri imageURL = data.getData();
                Log.d("URL", String.valueOf(imageURL));
                uri.add(imageURL);
                adapter.notifyDataSetChanged();
            }
        }
    }
}