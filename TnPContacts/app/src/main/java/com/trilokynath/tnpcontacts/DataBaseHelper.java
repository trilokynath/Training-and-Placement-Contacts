package com.trilokynath.tnpcontacts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

class DataBaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "HR_CONTACTS";

    Context context;
    DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    /**
     * This runs once after the installation and creates a database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // todo text
        db.execSQL("CREATE TABLE " + "hrcontact" + " (" +
                "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Name" + " TEXT, " +
                "City" + " TEXT, " +
                "Mobile" + " TEXT unique, " +
                "Email" + " TEXT, " +
                "Company" + " TEXT, " +
                "Note" + " TEXT" +
                ")");

        db.execSQL("CREATE TABLE " + "alumnicontact" + " (" +
                "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Name" + " TEXT, " +
                "City" + " TEXT, " +
                "Mobile" + " TEXT unique, " +
                "Email" + " TEXT, " +
                "Company" + " TEXT, " +
                "Note" + " TEXT" +
                ")");

        //db.execSQL("create table hrcontact(ID integer primary key autoincrement, Name text, City text, Mobile text, Email, text, Note text)");
    }

    /**
     * This would run after the user updates the app. This is in case you want
     * to modify the database
     */
    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
    }
    boolean addRecords(HR hr){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("Name", hr.getName());
        cv.put("City", hr.getCity());
        cv.put("Mobile", hr.getMobile());
        cv.put("Email", hr.getEmail());
        cv.put("Company", hr.getCompany());
        cv.put("Note", hr.getNote());

        Long id  = db.insert("hrcontact", null, cv);

        Log.d("DATABASE INSERTING" , id+"");

        return true;
    }

    boolean addRecordsalumni(HR hr){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("Name", hr.getName());
        cv.put("City", hr.getCity());
        cv.put("Mobile", hr.getMobile());
        cv.put("Email", hr.getEmail());
        cv.put("Company", hr.getCompany());
        cv.put("Note", hr.getNote());

        Long id  = db.insert("alumnicontact", null, cv);

        Log.d("DATABASE INSERTING" , id+"");

        return true;
    }

    boolean updateRecords(HR hr, Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("Name", hr.getName());
        cv.put("City", hr.getCity());
        cv.put("Mobile", hr.getMobile());
        cv.put("Email", hr.getEmail());
        cv.put("Company", hr.getCompany());
        cv.put("Note", hr.getNote());

        db.update("hrcontact", cv, "ID="+id, null);

        return true;
    }
    boolean updateRecordsalumni(HR hr, Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("Name", hr.getName());
        cv.put("City", hr.getCity());
        cv.put("Mobile", hr.getMobile());
        cv.put("Email", hr.getEmail());
        cv.put("Company", hr.getCompany());
        cv.put("Note", hr.getNote());

        db.update("alumnicontact", cv, "ID="+id, null);

        return true;
    }
    public ArrayList<HR> getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<HR> listItems = new ArrayList<HR>();

        Cursor cursor = db.rawQuery("SELECT * from " + "hrcontact order by Name asc",
                new String[]{});

        if (cursor.moveToFirst()) {
            do {
                HR hr = new HR();

                hr.ID = cursor.getInt(cursor.getColumnIndex("ID"));
                hr.name = cursor.getString(cursor.getColumnIndex("Name"));
                hr.city = cursor.getString(cursor.getColumnIndex("City"));
                hr.mobile = cursor.getString(cursor.getColumnIndex("Mobile"));
                hr.email = cursor.getString(cursor.getColumnIndex("Email"));
                hr.company = cursor.getString(cursor.getColumnIndex("Company"));
                hr.note = cursor.getString(cursor.getColumnIndex("Note"));
                listItems.add(hr);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return listItems;
    }

    public ArrayList<HR> getDataalumni() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<HR> listItems = new ArrayList<HR>();

        Cursor cursor = db.rawQuery("SELECT * from " + "alumnicontact order by Name asc",
                new String[]{});

        if (cursor.moveToFirst()) {
            do {
                HR hr = new HR();

                hr.ID = cursor.getInt(cursor.getColumnIndex("ID"));
                hr.name = cursor.getString(cursor.getColumnIndex("Name"));
                hr.city = cursor.getString(cursor.getColumnIndex("City"));
                hr.mobile = cursor.getString(cursor.getColumnIndex("Mobile"));
                hr.email = cursor.getString(cursor.getColumnIndex("Email"));
                hr.company = cursor.getString(cursor.getColumnIndex("Company"));
                hr.note = cursor.getString(cursor.getColumnIndex("Note"));
                listItems.add(hr);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return listItems;
    }

    public void deleteContact(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + "hrcontact where ID="+id);
    }

    public void deleteContactalumni(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + "alumnicontact where ID="+id);
    }

    public HR getRecord(Integer id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        HR hr = new HR();
        try {
            cursor = db.rawQuery("SELECT * FROM hrcontact WHERE ID="+id,null);
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();

                hr.ID = cursor.getInt(cursor.getColumnIndex("ID"));
                hr.name = cursor.getString(cursor.getColumnIndex("Name"));
                hr.city = cursor.getString(cursor.getColumnIndex("City"));
                hr.mobile = cursor.getString(cursor.getColumnIndex("Mobile"));
                hr.email = cursor.getString(cursor.getColumnIndex("Email"));
                hr.company = cursor.getString(cursor.getColumnIndex("Company"));
                hr.note = cursor.getString(cursor.getColumnIndex("Note"));
            }
            return hr;
        }finally {
            cursor.close();
        }
    }

    public long getHRCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, "hrcontact");
        db.close();
        return count;
    }
    public long getAlumniCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, "alumnicontact");
        db.close();
        return count;
    }

    public HR getRecordalumni(Integer id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        HR hr = new HR();
        try {
            cursor = db.rawQuery("SELECT * FROM alumnicontact WHERE ID="+id,null);
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();

                hr.ID = cursor.getInt(cursor.getColumnIndex("ID"));
                hr.name = cursor.getString(cursor.getColumnIndex("Name"));
                hr.city = cursor.getString(cursor.getColumnIndex("City"));
                hr.mobile = cursor.getString(cursor.getColumnIndex("Mobile"));
                hr.email = cursor.getString(cursor.getColumnIndex("Email"));
                hr.company = cursor.getString(cursor.getColumnIndex("Company"));
                hr.note = cursor.getString(cursor.getColumnIndex("Note"));
            }
            return hr;
        }finally {
            cursor.close();
        }
    }

    public int insertData(CSVReader br, boolean importhr){
        String[] line;
        SQLiteDatabase db = this.getWritableDatabase();
        String str1;
        if(importhr)
            str1 = "INSERT OR REPLACE INTO " + "hrcontact" + " (" + "Name,Email,Mobile,Company,City,Note" + ") values(";
        else
            str1 = "INSERT OR REPLACE INTO " + "alumnicontact" + " (" + "Name,Email,Mobile,Company,City,Note" + ") values(";

        String str2 = ");";
        try {
            db.beginTransaction();
            while ((line = br.readNext()) != null) {
                StringBuilder sb = new StringBuilder(str1);
                sb.append("'" + line[0] + "','");
                sb.append(line[1] + "','");
                sb.append(line[2] + "','");
                sb.append(line[3] + "','");
                sb.append(line[4] + "','");
                sb.append(line[5] + "'");
                sb.append(str2);
                db.execSQL(sb.toString());
            }
            db.setTransactionSuccessful();
            db.endTransaction();
//            Toast.makeText(context,"success",Toast.LENGTH_SHORT).show();
        }catch (IOException ee){
            Log.d("EXCEPTION", ee.toString());
        }
        return 100;
    }

    public ArrayList<HR> getSearchData(String search) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<HR> listItems = new ArrayList<HR>();

    //    Cursor cursor1 = db.rawQuery("SELECT * from " + "hrcontact where City LIKE %"+search+"%",
    //            new String[]{});

        Cursor cursor = db.query(true, "hrcontact", new String[] {}, "City" + " LIKE ?",
                new String[] { "%"+search+"%" }, null, null, "Name",
                null);

        if (cursor.moveToFirst()) {
            do {
                HR hr = new HR();

                hr.ID = cursor.getInt(cursor.getColumnIndex("ID"));
                hr.name = cursor.getString(cursor.getColumnIndex("Name"));
                hr.city = cursor.getString(cursor.getColumnIndex("City"));
                hr.mobile = cursor.getString(cursor.getColumnIndex("Mobile"));
                hr.email = cursor.getString(cursor.getColumnIndex("Email"));
                hr.company = cursor.getString(cursor.getColumnIndex("Company"));
                hr.note = cursor.getString(cursor.getColumnIndex("Note"));
                listItems.add(hr);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return listItems;
    }


}