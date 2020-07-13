package com.example.MySpot.utilities;

import com.example.MySpot.R;

public class AddressListener {
    private String address = "";

    void onAddressFound(String newAddress){
        address = newAddress;
    };
    void  onError(){

    };

    public String getAddress() {
        return address;
    }
}
