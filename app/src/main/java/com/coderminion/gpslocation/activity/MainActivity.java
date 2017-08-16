package com.coderminion.gpslocation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.coderminion.gpslocation.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button android_location = (Button)findViewById(R.id.btn_androids_location);
        Button google_location = (Button)findViewById(R.id.btn_google_location);
        Button iplocation = (Button)findViewById(R.id.iplocation);



        android_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AndroidLocationActivity.class);
                startActivity(intent);
            }
        });
        google_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),GoogleLocationActivity.class);
                startActivity(intent);
            }
        });
        iplocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),IPLocationActivity.class);
                startActivity(intent);
            }
        });

    }

}
