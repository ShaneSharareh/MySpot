package com.example.spotty.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.spotty.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
FloatingActionButton btnAddPlace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAddPlace = (FloatingActionButton) findViewById(R.id.addfavoriteplace);
        btnAddPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddPage();
            }
        });
    }

    private void startAddPage(){
       Intent intent = new Intent(this, AddSpot.class);
        startActivity(intent);
    }
}