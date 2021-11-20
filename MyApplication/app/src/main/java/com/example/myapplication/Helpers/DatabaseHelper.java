package com.example.myapplication.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.myapplication.Models.AreaModel;
import com.example.myapplication.Models.MarkerModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "MapAppDatabase";

    // Table Names
    private static final String TABLE_ITEM = "markers";
    private static final String TABLE_AREA = "areasTable";


    // Common column names
    private static final String KEY_ID = "id";


    // Item Table - column nmaes
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_POSITION_LONG = "positionLong";
    private static final String KEY_POSITION_LAT = "positionLat";
    private static final String KEY_ICON = "icon";
    private static final String KEY_COLOR = "color";


    // Item Table - column nmaes
    private static final String KEY_COORDINATES = "coordinates";


    // Table Create Statements
    // Item table create statement
    private static final String CREATE_TABLE_ITEM = "CREATE TABLE " + TABLE_ITEM +
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_TITLE + " TEXT," +
            KEY_DESCRIPTION + " TEXT," +
            KEY_POSITION_LONG + " LONG," +
            KEY_POSITION_LAT + " LONG," +
            KEY_ICON + " INTEGER," +
            KEY_COLOR + " INTEGER" +
            ")";

    // Item table create statement
    private static final String CREATE_TABLE_AREA = "CREATE TABLE " + TABLE_AREA +
            "(" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_TITLE + " TEXT," +
            KEY_DESCRIPTION + " TEXT," +
            KEY_COORDINATES + " TEXT"  +
            ")";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_ITEM);

        db.execSQL(CREATE_TABLE_AREA);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AREA);

        // create new tables
        onCreate(db);
    }


    //Creating a marker
    public long createMarker(MarkerModel marker) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, marker.getTitle());
        values.put(KEY_DESCRIPTION, marker.getDescription());
        values.put(KEY_POSITION_LONG, marker.getLongitude());
        values.put(KEY_POSITION_LAT, marker.getLatidude());
        values.put(KEY_ICON, marker.getIcon());
        values.put(KEY_COLOR, marker.getColor());


        Log.i("responsebody", marker.getLongitude() + " " + marker.getLatidude());

        // insert row
        long item_id = db.insert(TABLE_ITEM, null, values);

        return item_id;

    }
    //#Creating a marker


    //Creating a area
    public long createArea(AreaModel area) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, area.getTitle());
        values.put(KEY_DESCRIPTION, area.getDescription());
        values.put(KEY_COORDINATES, area.getCoordinates());

        // insert row
        long area_id = db.insert(TABLE_AREA, null, values);

        return area_id;

    }
    //#Creating a area








    //get single marker
    public MarkerModel getMarker(long item_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_ITEM + " WHERE "
                + KEY_ID + " = " + item_id;


        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        MarkerModel item = new MarkerModel();
        item.setMarkerId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
        item.setTitle(c.getString(c.getColumnIndexOrThrow(KEY_TITLE)));
        item.setDescription(c.getString(c.getColumnIndexOrThrow(KEY_DESCRIPTION)));
        item.setLongitude(c.getDouble(c.getColumnIndexOrThrow(KEY_POSITION_LONG)));
        item.setLatidude(c.getDouble(c.getColumnIndexOrThrow(KEY_POSITION_LAT)));
        item.setIcon(c.getInt(c.getColumnIndexOrThrow(KEY_ICON)));
        item.setColor(c.getInt(c.getColumnIndexOrThrow(KEY_COLOR)));

        return item;
    }
    //#get single marker


    //get single marker
    public AreaModel getArea(long item_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_AREA + " WHERE "
                + KEY_ID + " = " + item_id;


        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        AreaModel item = new AreaModel();
        item.setAreaId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
        item.setTitle(c.getString(c.getColumnIndexOrThrow(KEY_TITLE)));
        item.setDescription(c.getString(c.getColumnIndexOrThrow(KEY_DESCRIPTION)));
        item.setCoordinates(c.getString(c.getColumnIndexOrThrow(KEY_COORDINATES)));

        return item;
    }
    //#get single marker



    //getting all markers
    public List<MarkerModel> getAllMarkers() {
        List<MarkerModel> items = new ArrayList<MarkerModel>();
        String selectQuery = "SELECT * FROM " + TABLE_ITEM;


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                MarkerModel td = getMarker(c.getInt((c.getColumnIndex(KEY_ID))));
                // adding to item list
                items.add(td);
            } while (c.moveToNext());
        }

        return items;
    }
    //#getting all markers


    //getting all area
    public List<AreaModel> getAllAreas() {
        List<AreaModel> items = new ArrayList<AreaModel>();
        String selectQuery = "SELECT * FROM " + TABLE_AREA;


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                AreaModel td = getArea(c.getInt((c.getColumnIndex(KEY_ID))));
                // adding to item list
                items.add(td);
            } while (c.moveToNext());
        }

        return items;
    }
    //#getting all areas



    //getting marker count
    public int getMarkerCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ITEM;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }
    //#getting marker count



    //Updating marker
    public int updateMarker(MarkerModel marker) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, marker.getTitle());
        values.put(KEY_DESCRIPTION, marker.getDescription());
        values.put(KEY_POSITION_LONG, marker.getLongitude());
        values.put(KEY_POSITION_LAT, marker.getLatidude());
        values.put(KEY_ICON, marker.getIcon());
        values.put(KEY_COLOR, marker.getColor());

        // updating row
        return db.update(TABLE_ITEM, values, KEY_ID + " = ?",
                new String[] { String.valueOf(marker.getMarkerId()) });
    }
    //#Updating Item


    //Updating marker
    public int updateArea(AreaModel areaModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, areaModel.getTitle());
        values.put(KEY_DESCRIPTION, areaModel.getDescription());
        values.put(KEY_COORDINATES, areaModel.getCoordinates());


        // updating row
        return db.update(TABLE_AREA, values, KEY_ID + " = ?",
                new String[] { String.valueOf(areaModel.getAreaId()) });
    }
    //#Updating Item



    //Deleting item
    public void deleteMarker(long item_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEM, KEY_ID + " = ?",
                new String[] { String.valueOf(item_id) });

    }
    //Deleting item

    //Deleting area
    public void deleteArea(long item_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_AREA, KEY_ID + " = ?",
                new String[] { String.valueOf(item_id) });

    }
    //Deleting area


    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    // closing database
    public void clearDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from "+ TABLE_ITEM);
        db.execSQL("delete from "+ TABLE_AREA);

    }



}