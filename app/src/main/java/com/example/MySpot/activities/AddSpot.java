package com.example.MySpot.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.MySpot.database.DatabaseHandler;
import com.example.MySpot.models.KeyConstants;
import com.example.MySpot.models.Spot;
import com.example.MySpot.R;
import com.example.MySpot.utilities.AddressCalculation;
import com.example.MySpot.utilities.AddressListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AddSpot extends AppCompatActivity implements View.OnClickListener {
    private static final int GALLERY = 1;
    private static final int CAMERA = 2;
    private static final int LOCATION_REQUEST_CODE = 3;
    private static final int CURRENT_LOCATION_REQUEST_CODE = 4;
    private static final String IMAGE_DIRECTORY = "SpotImages";
    //@Override
    private EditText etTitle;
    private EditText etDescription;
    private EditText etLocation;
    public EditText etDate;
    private Calendar mCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener mOnDateSetListener;
    private TextView tvAddImage;
    private ImageView ivImage;
    private Button btnCurrentLocation;
    private Button saveSpot;
    private double latitude = 0.0;
    private double longtitude = 0.0;
    private Uri savedImage = null;
    private Spot mSpotDetails;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ResultReceiver mResultReceiver;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spot);
        mResultReceiver = new AddressReciever(new Handler());
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_add_spot);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        btnCurrentLocation = (Button) findViewById(R.id.btn_current_location);
        etDate = findViewById(R.id.etDate);
        tvAddImage = findViewById(R.id.tv_add_image);
        ivImage =  findViewById(R.id.iv_place_image);
        saveSpot = findViewById(R.id.btn_save);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if(!Places.isInitialized()){
            Log.v("Places","You Passed");
            Places.initialize(getApplicationContext(),getString(R.string.google_maps_api_key));
        }
        if(getIntent().hasExtra(SpotView.EDIT_SPOT_DETAIL)){
            mSpotDetails = (Spot) getIntent().getSerializableExtra(SpotView.EDIT_SPOT_DETAIL);

        }
        mOnDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(mCalendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setDateInTextView();
            }


        };
        if(mSpotDetails != null){
            getSupportActionBar().setTitle("EDIT SPOT");
            etTitle.setText(mSpotDetails.getTitel());
            etDescription.setText((mSpotDetails.getDescription()));
            etDate.setText(mSpotDetails.getDate());
            etLocation.setText(mSpotDetails.getLocation());
            latitude = mSpotDetails.getLatitude();
            longtitude = mSpotDetails.getLongitude();

            savedImage = Uri.parse(mSpotDetails.getImage());

            ivImage.setImageURI(savedImage);

            saveSpot.setText("UPDATE");

        }

        etDate.setOnClickListener(this);
        tvAddImage.setOnClickListener(this);
        btnCurrentLocation.setOnClickListener(this);
        saveSpot.setOnClickListener(this);
        etLocation.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.etDate) {
            new DatePickerDialog(this, mOnDateSetListener, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();

        }
        if (view.getId() == R.id.tv_add_image) {
            final CharSequence[] photoOptions = {"Select a photo from Gallery", "Capture a photo from camera"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Action");
            builder.setItems(photoOptions, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (item == 0) {
                        choosePhotoFromGallery();
                    }

                    if (item == 1) {
                        choosePhotoFromCamera();
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        }

        if(view.getId()== R.id.btn_save){
            if(etTitle.getText().length()==0){
                Toast.makeText(this,"Please Enter title",Toast.LENGTH_SHORT).show();
            }
            else if(etDescription.getText().length()==0){
                Toast.makeText(this,"Please Enter Description",Toast.LENGTH_SHORT).show();
            }
            else if(etLocation.getText().length()==0){
                Toast.makeText(this,"Please Enter Location",Toast.LENGTH_SHORT).show();
            }
            else if(savedImage==null){
                Toast.makeText(this,"Please Select an Image",Toast.LENGTH_SHORT).show();
            }
            else {
                Spot spot = new Spot(
                        mSpotDetails==null? 0: mSpotDetails.getId(),
                        etTitle.getText().toString(),
                        savedImage.toString(),
                        etDescription.getText().toString(),
                        etDate.getText().toString(),
                        etLocation.getText().toString(),
                        latitude,
                        longtitude
                );

                DatabaseHandler databaseHandler = new DatabaseHandler(this,"SpotDatabase",null,2);
                if(mSpotDetails ==null) {
                    Long mySpotSuccess = databaseHandler.addSpot(spot);

                    if (mySpotSuccess > 0) {
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                }else{
                    int mySpotSuccess = databaseHandler.updateSpot(spot);

                    if (mySpotSuccess > 0) {
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                }
            }
        }
        if(view.getId() == R.id.btn_current_location) {
            if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(AddSpot.this, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        CURRENT_LOCATION_REQUEST_CODE);
            } else {
                getCurrentLocation();
            }
        }
          /*  if(!checkLocationEnabled()){
                Toast.makeText(
                        this,
                        "Your location provider is turned off. To use it, pleaase turn it off",
                        Toast.LENGTH_SHORT
                ).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }else{
              Dexter.withActivity(this).withPermissions(
                      Manifest.permission.ACCESS_FINE_LOCATION,
                      Manifest.permission.ACCESS_COARSE_LOCATION).withListener(new MultiplePermissionsListener(){

                  @Override
                  public void onPermissionsChecked(MultiplePermissionsReport report) {
                      if(report.areAllPermissionsGranted()){
                          locationDataRequest();
                      }
                  }

                  @Override
                  public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    showRationalDialogForPermissions();
                  }
              }).onSameThread().check();
                }*/
        if(view.getId() == R.id.etLocation){
            try{
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                Intent intentBuilder = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,fields)
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        //.setLocationBias( RectangularBounds.newInstance( new LatLng(-33.880490, 151.184363), new LatLng(-33.858754, 229596)))
                        //.setCountries(Arrays.asList("BR","SR","GY"))
                        .build(this);
                startActivityForResult(intentBuilder,LOCATION_REQUEST_CODE);
            }catch (Exception exception){

            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CURRENT_LOCATION_REQUEST_CODE && grantResults.length>0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            } else{
                Toast.makeText(this, "Permission denied, turn on permissions!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == GALLERY){
                if(data != null){
                    Uri contentURI =data.getData();
                    try{
                        Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                       savedImage = saveImagesToStorage(selectedImageBitmap);
                        ivImage.setImageBitmap(selectedImageBitmap);
                    } catch(IOException e){
                        e.printStackTrace();
                    }

                }
                }
            else if(requestCode == CAMERA){
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                savedImage = saveImagesToStorage(thumbnail);

                ivImage.setImageBitmap(thumbnail);
            }

            else if(requestCode == LOCATION_REQUEST_CODE){
                Place mPlace = Autocomplete.getPlaceFromIntent(data);
                etLocation.setText(mPlace.getAddress());
                latitude= mPlace.getLatLng().latitude;
                longtitude = mPlace.getLatLng().longitude;
            }
        }



    }
    private void choosePhotoFromGallery() {
        Dexter.withActivity(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report)
            {
                if(report.areAllPermissionsGranted()){
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent, GALLERY);
                 }/* ... */}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token)
            {
                showRationalDialogForPermissions();
            }
        }).onSameThread().check();
    }
    public void choosePhotoFromCamera(){
        Dexter.withActivity(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report)
            {
                if(report.areAllPermissionsGranted()){
                    Intent galleryIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(galleryIntent, CAMERA);
                }/* ... */}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token)
            {
                showRationalDialogForPermissions();
            }
        }).onSameThread().check();
    }
    private void showRationalDialogForPermissions() {
        new AlertDialog.Builder(this).setMessage("Looks like you turned off permissions, go to applicaions setttings to change permissions");

    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation(){
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(AddSpot.this)
                .requestLocationUpdates(locationRequest,new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(AddSpot.this)
                                .removeLocationUpdates(this);
                        if(locationResult != null && locationResult.getLocations().size()>0){
                            int locationIndex = locationResult.getLocations().size()-1;
                            latitude = locationResult.getLocations().get(locationIndex).getLatitude();
                            longtitude = locationResult.getLocations().get(locationIndex).getLongitude();
                            //etLocation.setText("Latitude is"+latitude + " and the longitutde is"+longtitude);
                            Location location = new Location("providerNA");
                            location.setLatitude(latitude);
                            location.setLongitude(longtitude);
                            calculateAdressFromLatLong(location);

                        }
                    }
                }, Looper.getMainLooper());
    }
    private void calculateAdressFromLatLong(Location location){
        Intent intent = new Intent(this, AddressCalculation.class);
        intent.putExtra(KeyConstants.RECIEVER, mResultReceiver);
        intent.putExtra(KeyConstants.LOCATION_EXTRA, location);
        startService(intent);

    }
    private class AddressReciever extends ResultReceiver{

        public AddressReciever(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if(resultCode == KeyConstants.SUCCESS_RESULT){

                etLocation.setText(resultData.getString(KeyConstants.RESULT_KEY));
            } else{
                Toast.makeText(AddSpot.this, resultData.getString(KeyConstants.RESULT_KEY), Toast.LENGTH_SHORT).show();

            }
        }
    }
    private void setDateInTextView(){
        String dateFromat = "dd-MM-yyyy";
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(dateFromat, Locale.getDefault());
        etDate.setText(mSimpleDateFormat.format(mCalendar.getTime()).toString());
    }

    private Boolean checkLocationEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    /*
    *For Later
        @SuppressLint("MissingPermission")
        private void locationDataRequest(){
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(1000);
            locationRequest.setNumUpdates(1);

            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallBack, Looper.myLooper());
        }


    private final LocationCallback locationCallBack = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location lastLocation = locationResult.getLastLocation();
            latitude = lastLocation.getLatitude();
            longtitude = lastLocation.getLongitude();
            Log.i("Latlong", "" + latitude + " " + longtitude + "");
            AddressCalculation addressTask = new AddressCalculation(AddSpot.this, latitude, longtitude);
            AddressListener addressListener = new AddressListener();
            addressTask.setAddressListener(addressListener);
            //etLocation.setText(addressListener.getAddress());

        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
        }
    };

     */

    private Uri saveImagesToStorage(Bitmap bitmap){
        ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
        File file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE);
        UUID randomImageID = UUID.randomUUID();
        file = new File(file,randomImageID+".jpg");

        try{
            OutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        }catch (IOException exception){
            exception.printStackTrace();
        }
        return Uri.parse(file.getAbsolutePath());
    }



}