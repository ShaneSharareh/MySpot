package com.example.MySpot.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.example.MySpot.Adapters.MySpotAdapter;
import com.example.MySpot.activities.AddSpot;
import com.example.MySpot.R;
import com.example.MySpot.database.DatabaseHandler;
import com.example.MySpot.models.Spot;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int ADD_SPOT_REQUEST_CODE = 1;
    FloatingActionButton btnAddPlace;
RecyclerView rvSpotsView;
MySpotAdapter spotsAdapter;
TextView emptyListGreeting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emptyListGreeting = (TextView) findViewById(R.id.tv_emptyspots);
        btnAddPlace = (FloatingActionButton) findViewById(R.id.addfavoriteplace);
        btnAddPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddPage();
            }
        });
        rvSpotsView = (RecyclerView) findViewById(R.id.rv_spots_view);
        getSpotsListFromDB();
    }
    private void setUpRecyclerView(ArrayList<Spot> spotsList){
        rvSpotsView.setLayoutManager(new LinearLayoutManager(this));
        rvSpotsView.setHasFixedSize(true);
        spotsAdapter = new MySpotAdapter(this,spotsList);
        rvSpotsView.setAdapter(spotsAdapter);

        /*spotsAdapter.setOnClickListener(new MySpotAdapter.OnClickListener){

        };*/

    }
    private void startAddPage(){
       Intent intent = new Intent(this, AddSpot.class);
        startActivityForResult(intent, ADD_SPOT_REQUEST_CODE);
    }

    private void  getSpotsListFromDB(){
        DatabaseHandler databaseHandler = new DatabaseHandler(this,"SpotDatabase",null,2);
        ArrayList<Spot> spotList = new ArrayList<Spot>();
        spotList = databaseHandler.getSpots();
        if(spotList.size()>0){
            rvSpotsView.setVisibility(View.VISIBLE);
            emptyListGreeting.setVisibility(View.GONE);
            setUpRecyclerView(spotList);
        }else{
            rvSpotsView.setVisibility(View.GONE);
            emptyListGreeting.setVisibility(View.VISIBLE);
        }
        Log.v("MyTag", "Title : MOO");



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ADD_SPOT_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                //getSpotsListFromDB();
            }
        }
    }
    @Override protected void onResume() {
        super.onResume();
        getSpotsListFromDB();
    }
}