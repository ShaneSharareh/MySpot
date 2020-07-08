package com.example.MySpot.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.MySpot.Adapters.MySpotAdapter;
import com.example.MySpot.R;
import com.example.MySpot.models.Spot;

public class SpotView extends AppCompatActivity {
    ImageView ivSpotImage;
    TextView tvDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_view);
        ivSpotImage = (ImageView) findViewById(R.id.iv_view_spot_image);
        tvDescription = (TextView) findViewById(R.id.tv_view_description);
        Spot spotDetail = null;

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
}