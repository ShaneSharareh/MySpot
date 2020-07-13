package com.example.MySpot.utilities;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.example.MySpot.models.KeyConstants;
import com.google.android.gms.location.LocationCallback;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class AddressCalculation extends IntentService {
    private ResultReceiver mResultReceiver;
    public AddressCalculation() {
        super("AddressCalculation");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null){
            String err = "";
            mResultReceiver = intent.getParcelableExtra(KeyConstants.RECIEVER);
            Location location = intent.getParcelableExtra(KeyConstants.LOCATION_EXTRA);
            if(location == null){
                return;
            }
            Geocoder geocoder = new Geocoder(this,Locale.getDefault());
            List<Address> addressList = null;
            try{
                addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);

            }catch (Exception exception){
                err= exception.getMessage();
            }
            if(addressList == null || addressList.isEmpty()){
                sendResultToReceiver(KeyConstants.FAILURE_RESULT, err);
            }
            else{
                Address address = addressList.get(0);
                ArrayList<String> addressFragments = new ArrayList<>();
                for(int i = 0; i<=address.getMaxAddressLineIndex();i++){
                    addressFragments.add(address.getAddressLine(i));
                }
                sendResultToReceiver(KeyConstants.SUCCESS_RESULT, TextUtils.join(
                        Objects.requireNonNull(System.getProperty("line.separator")),
                        addressFragments));
            }
        }
    }

    private void sendResultToReceiver(int result, String addressMessage){
        Bundle bundle = new Bundle();
        bundle.putString(KeyConstants.RESULT_KEY, addressMessage);
        mResultReceiver.send(result, bundle);
    }

   /* private Context mContext;
    private double latitude;
    private double longitude;

    public AddressCalculation(){

    }
    public  AddressCalculation(Context context, double newLatitude, double newLongitude){
        mContext = context;


        latitude = newLatitude;
        longitude = newLongitude;
    }
    private Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
    AddressListener addressListener;

    @Override
    protected String doInBackground(Void... voids) {
        try {
            List<Address> addressList= geocoder.getFromLocation(latitude, longitude,1);
            if(addressList != null && !addressList.isEmpty()){
                Address address = addressList.get(0);
                StringBuilder stringBuilder = new StringBuilder();
                for(int i=0; i<address.getMaxAddressLineIndex(); i++){
                    stringBuilder.append(address.getAddressLine(i)).append(" ");
                }
                stringBuilder.deleteCharAt(stringBuilder.length()-1);
                return stringBuilder.toString();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        if(result==null){
            addressListener.onError();
        }
        else{
            addressListener.onAddressFound(result);
        }
        super.onPostExecute(result);
    }

    public void setAddressListener(AddressListener addressListener){
        this.addressListener = addressListener;

    }

    void fetchAddress(){
        execute();
    }
*/

}
