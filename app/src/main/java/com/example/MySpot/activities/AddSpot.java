package com.example.MySpot.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.MySpot.database.DatabaseHandler;
import com.example.MySpot.models.Spot;
import com.example.MySpot.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddSpot extends AppCompatActivity implements View.OnClickListener {
    private static final int GALLERY = 1;
    private static final int CAMERA = 2;
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
    private Button saveSpot;
    private double latitude = 0.0;
    private double longtitude = 0.0;
    private Uri savedImage = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spot);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_add_spot);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        tvAddImage = findViewById(R.id.tv_add_image);
        ivImage =  findViewById(R.id.iv_place_image);
        saveSpot = findViewById(R.id.btn_save);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mOnDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(mCalendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setDateInTextView();
            }


        };
        etDate.setOnClickListener(this);
        tvAddImage.setOnClickListener(this);
        saveSpot.setOnClickListener(this);
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
                        0,
                        etTitle.getText().toString(),
                        savedImage.toString(),
                        etDescription.getText().toString(),
                        etDate.getText().toString(),
                        etLocation.getText().toString(),
                        latitude,
                        longtitude
                );

                DatabaseHandler databaseHandler = new DatabaseHandler(this,"SpotDatabase",null,2);
                Long mySpotSuccess = databaseHandler.addSpot(spot);

                if(mySpotSuccess>0){
                    setResult(Activity.RESULT_OK);
                    finish();
                }

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
                saveImagesToStorage(thumbnail);

                ivImage.setImageBitmap(thumbnail);
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


    private void setDateInTextView(){
        String dateFromat = "dd-MM-yyyy";
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(dateFromat, Locale.getDefault());
        etDate.setText(mSimpleDateFormat.format(mCalendar.getTime()).toString());
    }

    private Uri saveImagesToStorage(Bitmap bitmap){
        ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
        File file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE);
        file = new File(file,"${UUID.randomUUID()}.jpg");

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