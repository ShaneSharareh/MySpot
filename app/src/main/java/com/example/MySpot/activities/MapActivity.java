package com.example.MySpot.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;

import com.example.MySpot.Adapters.MySpotAdapter;
import com.example.MySpot.R;
import com.example.MySpot.models.Spot;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Spot spotDetail = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if(getIntent().hasExtra(MySpotAdapter.SPOT_DETAIL)){
            spotDetail = (Spot) getIntent().getSerializableExtra(MySpotAdapter.SPOT_DETAIL);
        }

        if(spotDetail != null){
            Toolbar toolbarMap = (Toolbar) findViewById(R.id.toolbar_map_spot);
            setSupportActionBar(toolbarMap);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(spotDetail.getTitel());
            toolbarMap.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            supportMapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(spotDetail.getLatitude(),spotDetail.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(latLng).title(spotDetail.getLocation()));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
    }
}