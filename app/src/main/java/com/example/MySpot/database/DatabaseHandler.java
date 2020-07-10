package com.example.MySpot.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.MySpot.models.Spot;

import java.sql.SQLDataException;
import java.sql.Statement;
import java.util.ArrayList;

import androidx.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {
    public DatabaseHandler(@Nullable Context context, @Nullable String DATABASENAME, @Nullable SQLiteDatabase.CursorFactory factory, int DATABASEVERSION) {
        super(context, DATABASENAME, factory, DATABASEVERSION);
    }
    private static int DATABASEVERSION = 2;
    private static String DATABASENAME = "SpotDatabase";
    private static String TABLE_MY_SPOT = "SpotTable";

    //columns
    private static String ID = "_id";
    private static String TITLE = "title";
    private static String IMAGE = "image";
    private static String DESCRIPTION = "description";
    private static String DATE = "date";
    private static String LOCATION = "location";
    private static String LATITUDE = "latitude";
    private static String LONGITUDE = "longitude";


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT);", TABLE_MY_SPOT, ID, TITLE, IMAGE, DESCRIPTION, DATE, LOCATION, LATITUDE, LONGITUDE);

        sqLiteDatabase.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MY_SPOT);
        onCreate(sqLiteDatabase);
    }

    public Long addSpot(Spot spot){
        SQLiteDatabase database = this.getWritableDatabase();
        Log.v("Check",spot.getTitel().toString());
        Log.v("Check",spot.getDescription().toString());
        Log.v("Check",spot.getDate().toString());
        Log.v("Check",spot.getImage().toString());
        Log.v("Check",spot.getLocation().toString());

        ContentValues contentValues = new ContentValues();
        //add values to columns
        contentValues.put(TITLE, spot.getTitel());
        contentValues.put(IMAGE, spot.getImage());
        contentValues.put(DESCRIPTION, spot.getDescription());
        contentValues.put(DATE, spot.getDate());
        contentValues.put(LOCATION, spot.getLocation());
        contentValues.put(LATITUDE, spot.getLatitude());
        contentValues.put(LONGITUDE, spot.getLongitude());

        //insert the row
        Long result = database.insert(TABLE_MY_SPOT, null, contentValues);
        database.close();
        return result;
    }

    public int updateSpot(Spot spot){
        SQLiteDatabase database = this.getWritableDatabase();
        Log.v("Check",spot.getTitel().toString());
        Log.v("Check",spot.getDescription().toString());
        Log.v("Check",spot.getDate().toString());
        Log.v("Check",spot.getImage().toString());
        Log.v("Check",spot.getLocation().toString());

        ContentValues contentValues = new ContentValues();
        //add values to columns
        contentValues.put(TITLE, spot.getTitel());
        contentValues.put(IMAGE, spot.getImage());
        contentValues.put(DESCRIPTION, spot.getDescription());
        contentValues.put(DATE, spot.getDate());
        contentValues.put(LOCATION, spot.getLocation());
        contentValues.put(LATITUDE, spot.getLatitude());
        contentValues.put(LONGITUDE, spot.getLongitude());

        int sucess = database.update(TABLE_MY_SPOT, contentValues, ID + "=" + spot.getId(),null);
        //insert the row
        database.close();
        return sucess;
    }

    public int deleteSpot(Spot spot){
        SQLiteDatabase database = this.getWritableDatabase();
        int sucesss = database.delete(TABLE_MY_SPOT, ID + "=" + spot.getId(), null);
        database.close();
        return sucesss;
    }

    public ArrayList<Spot> getSpots(){
        ArrayList<Spot> spotsList = new ArrayList<Spot>();
        String query = "SELECT * FROM "+ TABLE_MY_SPOT;
        SQLiteDatabase database = this.getReadableDatabase();
        try{
            Log.v("MyTag", "Title :  DAHDAH eyes");
            Cursor cursor = database.rawQuery(query, null);
            if(cursor.moveToFirst()){
                do {
                    Log.v("MyTag", "Title :  In your eyes");
                            Spot spot = new Spot(
                            cursor.getInt(cursor.getColumnIndex(ID)),
                            cursor.getString(cursor.getColumnIndex(TITLE)),
                            cursor.getString(cursor.getColumnIndex(IMAGE)),
                            cursor.getString(cursor.getColumnIndex(DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndex(DATE)),
                            cursor.getString(cursor.getColumnIndex(LOCATION)),
                            cursor.getDouble(cursor.getColumnIndex(LATITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(LONGITUDE))
                            );
                    spotsList.add(spot);
                }while (cursor.moveToNext());
            }
            cursor.close();
        }catch (SQLiteException exception){
            database.execSQL(query);
            return new ArrayList();
        }

        return spotsList;
    }

}
