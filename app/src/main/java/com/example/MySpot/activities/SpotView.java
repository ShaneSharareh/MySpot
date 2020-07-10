package com.example.MySpot.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.MySpot.Adapters.MySpotAdapter;
import com.example.MySpot.R;
import com.example.MySpot.database.DatabaseHandler;
import com.example.MySpot.models.Spot;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SpotView extends AppCompatActivity {
    public static final String EDIT_SPOT_DETAIL = "Edit_Spot_Detail";
    ImageView ivSpotImage;
    TextView tvDescription;
    FloatingActionButton btnEditSpot;
    FloatingActionButton btnDeleteSpot;
    Button btnMap;
    Spot spotDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_view);
        spotDetail = null;

        ivSpotImage = (ImageView) findViewById(R.id.iv_view_spot_image);
        tvDescription = (TextView) findViewById(R.id.tv_view_description);
        btnEditSpot = (FloatingActionButton) findViewById(R.id.editSpot);
        btnDeleteSpot = (FloatingActionButton) findViewById(R.id.deleteSpot);
        btnMap = (Button) findViewById(R.id.btn_map);
        btnEditSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddSpot.class);
                intent.putExtra(EDIT_SPOT_DETAIL, spotDetail);
                startActivity(intent);
                finish();
            }
        });
        btnDeleteSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSpot();
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewMap();
            }
        });

        if (getIntent().hasExtra(MySpotAdapter.SPOT_DETAIL)) {
            spotDetail = (Spot) getIntent().getSerializableExtra(MySpotAdapter.SPOT_DETAIL);
        }

        if (spotDetail != null) {
            androidx.appcompat.widget.Toolbar mToolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar_spot_detail);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(spotDetail.getTitel());
            ivSpotImage.setImageURI(Uri.parse(spotDetail.getImage()));
            tvDescription.setText(spotDetail.getDescription());
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });




        }
    }

    private void viewMap() {
        Intent intent = new Intent(this,MapActivity.class);
        intent.putExtra(MySpotAdapter.SPOT_DETAIL, spotDetail);
        startActivity(intent);
    }

    private void deleteSpot() {
        DatabaseHandler databaseHandler = new DatabaseHandler(this,"SpotDatabase",null,2);
        int sucess = databaseHandler.deleteSpot(spotDetail);
        finish();
    }

}