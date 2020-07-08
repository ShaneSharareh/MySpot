package com.example.MySpot.models;

public class Spot {

    private int mId;
    private String mTitel;
    private String mImage;
    private String mDescription;
    private String mDate;
    private String mLocation;
    private double mLatitude;
    private double mLongitude;

    public Spot(int id, String titel, String image, String description, String date, String location, double latitude, double longitude ){

        mId = id;
        mTitel = titel;
        mImage = image;
        mDescription = description;
        mDate = date;
        mLocation = location;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getTitel() {
        return mTitel;
    }

    public void setTitel(String titel) {
        mTitel = titel;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }
}
