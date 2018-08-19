package com.trilokynath.tnpcontacts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ListView list;
    ArrayAdapter<HR> adapter;
    ArrayList<HR> hrList;
    DataBaseHelper dataBaseHelper;
    String spinnerItem = "HR";
    boolean importhr = true;
    ProgressDialog progressBar;
    int progressBarStatus = 0;
    Handler progressBarbHandler = new Handler();
    long fileSize = 0;
    boolean isLocationOn = true;
    String city = "";
    String address = "";
    int currentPos = 0;
    boolean isGPSEnabled, isNetworkEnabled;
    android.support.v7.app.ActionBar actionBar;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                handleUncaughtException(thread, new RuntimeException(e));
            }
        });

        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.rcpit);
        actionBar.setDisplayUseLogoEnabled(true);

        SharedPreferences prefs = getSharedPreferences("status", MODE_PRIVATE);
        if (prefs != null)
            spinnerItem = prefs.getString("item", "HR");


        list = (ListView) findViewById(R.id.list);
        dataBaseHelper = new DataBaseHelper(this);

        hrList = new ArrayList<>();


        prefs = getSharedPreferences("status", MODE_PRIVATE);
        if (prefs != null) {
            isLocationOn = prefs.getBoolean("location", false);
            address = prefs.getString("address", "No City");
            city = prefs.getString("lastLocation", "No City");
            actionBar.setSubtitle(city);
        }

        Log.d("LocationStatus", isLocationOn + " ");

        gettingPermissions();

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locListener = new MyLocationListener();

        if (MainActivity.this.spinnerItem.equalsIgnoreCase("HR"))
            actionBar.setTitle("HR Contacts");
        else
            actionBar.setTitle("Alumni Contacts");

        changeList(1);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.bringToFront();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), Add_hr.class);
                myIntent.putExtra("spinerItem", spinnerItem);
                startActivityForResult(myIntent, 0);
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent myIntent = new Intent(getApplicationContext(), Show_Contacts.class);
                myIntent.putExtra("ID", hrList.get(i).getID());
                myIntent.putExtra("spinerItem", spinnerItem);
                startActivityForResult(myIntent, 0);


            }
        });


        list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        list.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

                final int checkedItemCount = list.getCheckedItemCount();
                actionMode.setTitle(checkedItemCount + " Contacts Selected");

                list.getCheckedItemPositions();
                SparseBooleanArray a = list.getCheckedItemPositions();

                StringBuffer sb = new StringBuffer("");
                for (int ii = 0; ii < a.size(); ii++) {

                    if (a.valueAt(ii)) {
                        int idx = a.keyAt(ii);

                        if (sb.length() > 0)
                            sb.append(", ");


                        HR hr = (HR) list.getAdapter().getItem(idx);
                        sb.append(hr.getName());
                    }
                }
                //Toast.makeText(getApplicationContext(), sb + "", Toast.LENGTH_SHORT).show();


            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.getMenuInflater().inflate(R.menu.selectlistview, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_delete:
                        /*delete selected items*/

                        new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.myDialog))
                                .setTitle("Delete Contacts?")
                                .setMessage("Do you really want to delete selected contacts?")
                                .setIcon(android.R.drawable.ic_menu_delete)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        list.getCheckedItemPositions();
                                        SparseBooleanArray a = list.getCheckedItemPositions();
                                        Integer checkedIemsCount = list.getCheckedItemCount();

                                        for (int ii = 0; ii < a.size(); ii++) {
                                            if (a.valueAt(ii)) {
                                                int idx = a.keyAt(ii);
                                                Integer id = hrList.get(idx).getID();
                                                if (spinnerItem.equalsIgnoreCase("HR")) {
                                                    dataBaseHelper.deleteContact(id);
                                                } else {
                                                    dataBaseHelper.deleteContactalumni(id);
                                                }

                                            }
                                        }

                                        if (spinnerItem.equalsIgnoreCase("HR")) {
                                            hrList = dataBaseHelper.getData();
                                        } else {
                                            hrList = dataBaseHelper.getDataalumni();
                                        }

                                        actionMode.finish();
                                        setAdapter(hrList);

                                        Toast.makeText(MainActivity.this, checkedIemsCount + " Items Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null).show();

                        return false;

                    case R.id.action_send_mail:
                        list.getCheckedItemPositions();
                        SparseBooleanArray a = list.getCheckedItemPositions();
                        Integer checkedIemsCount = list.getCheckedItemCount();

                        StringBuffer sb = new StringBuffer("");
                        for (int ii = 0; ii < a.size(); ii++) {

                            if (a.valueAt(ii)) {
                                int idx = a.keyAt(ii);

                                if (sb.length() > 0)
                                    sb.append(", ");


                                HR hr = (HR) list.getAdapter().getItem(idx);
                                sb.append(hr.getEmail());
                            }
                        }

                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", sb.toString(), null));
                        startActivity(Intent.createChooser(emailIntent, null));

                        actionMode.finish();
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                changeList(2);
                return true;

            case R.id.action_import_hr:
                // do something based on first item click
                importhr = true;
                Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
                fileintent.setType("*/*");
                fileintent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(Intent.createChooser(fileintent, "Select File"), 1);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "No app found for importing the file.", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.action_import_alumni:
                // do something based on first item click
                importhr = false;
                Intent fileintent1 = new Intent(Intent.ACTION_GET_CONTENT);
                fileintent1.setType("*/*");
                fileintent1.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    this.startActivityForResult(fileintent1, 1);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "No app found for importing the file.", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.action_export_hr:
                hrList = dataBaseHelper.getData();
                File sdcard0 = Environment.getExternalStoragePublicDirectory("");
                File file = new File(sdcard0, "HR_CONTACTS.csv");
                //    StringBuffer text = new StringBuffer("");

                List<String[]> data = new ArrayList<String[]>();

                for (HR hr : hrList)
                    data.add(new String[]{hr.getName(), hr.getEmail(), hr.getMobile(), hr.getCompany(), hr.getCity(), hr.getNote()});

                try {
                    CSVWriter writer = new CSVWriter(new FileWriter(file));

                    writer.writeAll(data);

                    writer.close();

                    notifyExporting(file);
                } catch (IOException e) {
                    Log.e("Exception", "File write failed: " + e.toString());
                }
                break;

            case R.id.action_export_alumni:
                hrList = dataBaseHelper.getDataalumni();
                File sdcard1 = Environment.getExternalStoragePublicDirectory("");
                File file1 = new File(sdcard1, "ALUMNI_CONTACTS.csv");
                StringBuffer text1 = new StringBuffer("");
                List<String[]> data1 = new ArrayList<String[]>();

                for (HR hr : hrList)
                    data1.add(new String[]{hr.getName(), hr.getEmail(), hr.getMobile(), hr.getCompany(), hr.getCity(), hr.getNote()});

                try {
                    CSVWriter writer = new CSVWriter(new FileWriter(file1));

                    writer.writeAll(data1);

                    writer.close();

                    notifyExporting(file1);
                } catch (IOException e) {
                    Log.e("Exception", "File write failed: " + e.toString());
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    MenuItem item;
    MenuItem filter;
    MenuItem about;
    LocationManager locManager;
    LocationListener locListener;

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchview, menu);

        item = menu.findItem(R.id.spinner);
        filter = menu.findItem(R.id.filter);
        about = menu.findItem(R.id.about);

        about.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent myIntent = new Intent(getApplicationContext(), About.class);
                startActivityForResult(myIntent, 0);

                return false;
            }
        });

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                SharedPreferences.Editor editor =
                        getSharedPreferences("status", MODE_PRIVATE).edit();
                if (spinnerItem.equalsIgnoreCase("HR")) {
                    spinnerItem = "Alumni";
                    actionBar.setTitle("Alumni Contacts");
                    editor.putString("item", "Alumni");
                    editor.apply();
                } else {
                    spinnerItem = "HR";
                    actionBar.setTitle("HR Contacts");
                    editor.putString("item", "HR");
                    editor.apply();
                }
                changeList(3);
                return false;
            }
        });


        isGPSEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled || !isNetworkEnabled)
            isLocationOn = false;

        if (isLocationOn) {
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle("RCPIT T&P");
            actionBar.setSubtitle(Html.fromHtml("<font color='black'>Search by : " + city + "</font>"));
            filter.setIcon(R.drawable.ic_city);
        } else {
            filter.setIcon(R.drawable.ic_city_disable);
            actionBar.setSubtitle("");
        }

        filter.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Log.d("TRACE", "1");
                SharedPreferences.Editor editor = getSharedPreferences("status", MODE_PRIVATE).edit();

                if (isLocationOn) {
                    editor.putBoolean("location", false);
                    editor.apply();
                    filter.setIcon(R.drawable.ic_city_disable);
                    isLocationOn = false;
                    actionBar.setSubtitle("");

                    progressBar = new ProgressDialog(MainActivity.this);
                    progressBar.setCancelable(true);
                    progressBar.setMessage("Please Wait...");
                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressBar.setProgress(0);
                    progressBar.setMax(100);
                    progressBar.show();
                    progressBarStatus = 0;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (MainActivity.this.spinnerItem.equalsIgnoreCase("HR"))
                                hrList = dataBaseHelper.getData();
                            else
                                hrList = dataBaseHelper.getDataalumni();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    adapter = new MyListAdapter(MainActivity.this, R.layout.contactlist, hrList);
                                    MainActivity.this.<ListView>findViewById(R.id.list).setAdapter(adapter);

                                }
                            });

                            progressBar.dismiss();
                        }
                    }).start();

                    if (locManager != null)
                        locManager.removeUpdates(locListener);


                } else {
                    Log.d("TRACE", "2");

                    isGPSEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    isNetworkEnabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                    if (!isGPSEnabled && !isNetworkEnabled) {
                        Intent gpsOptionsIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(gpsOptionsIntent);
                    } else {

                        progressBar = new ProgressDialog(MainActivity.this);
                        progressBar.setCancelable(true);
                        progressBar.setMessage("Searching...");
                        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressBar.setProgress(0);
                        progressBar.setMax(100);
                        progressBar.show();
                        progressBarStatus = 0;

                        isLocationOn = true;
                        editor.putBoolean("location", true);
                        editor.apply();
                        filter.setIcon(R.drawable.ic_city);


                        new Thread(new Runnable() {
                            public void run() {

                                final ArrayList<HR> newHrList;

                                if (MainActivity.this.spinnerItem.equalsIgnoreCase("HR"))
                                    newHrList = new Searching().onSearch(dataBaseHelper.getData(), address);
                                else
                                    newHrList = new Searching().onSearch(dataBaseHelper.getDataalumni(), address);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (newHrList.isEmpty())
                                            Toast.makeText(MainActivity.this, "No Results Found", Toast.LENGTH_SHORT).show();


                                        adapter = new MyListAdapter(MainActivity.this, R.layout.contactlist, newHrList);
                                        hrList = newHrList;
                                        MainActivity.this.<ListView>findViewById(R.id.list).setAdapter(adapter);

                                    }
                                });

                                progressBar.dismiss();

                            }
                        }).start();
                        actionBar.setSubtitle(Html.fromHtml("<font color='black'>Search by : " + city + "</font>"));
                        try {
                            progressBar.setTitle("Fetching New Location..");
                            if (isNetworkEnabled) {
                                locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                        60000, 0, locListener);

                                Log.d("TRACE", "7");
                                //        Toast.makeText(MainActivity.this, "using Network", Toast.LENGTH_SHORT).show();
                            } else if (isGPSEnabled) {
                                locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                        60000, 0, locListener);
                                Log.d("TRACE", "8");
                                //        Toast.makeText(MainActivity.this, "Using GPS", Toast.LENGTH_SHORT).show();
                            } else {
                                //        Toast.makeText(MainActivity.this, "Please Enable GPS", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {

                            Log.d("ERROR-PANIC", e.toString());
                        }
                    }
                }


                return false;
            }
        });

        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(final String s) {
                if (s != null && !s.isEmpty()) {
                    //    setAdapter(dataBaseHelper.getSearchData(s));

                    progressBar = new ProgressDialog(MainActivity.this);
                    progressBar.setCancelable(true);
                    progressBar.setMessage("Searching...");
                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressBar.setProgress(0);
                    progressBar.setMax(100);
                    progressBar.show();
                    progressBarStatus = 0;

                    fileSize = 0;
                    new Thread(new Runnable() {
                        public void run() {

                            final ArrayList<HR> newHrList;

                            if (MainActivity.this.spinnerItem.equalsIgnoreCase("HR"))
                                newHrList = new Searching().onSearch(dataBaseHelper.getData(), s);
                            else
                                newHrList = new Searching().onSearch(dataBaseHelper.getDataalumni(), s);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (newHrList.isEmpty())
                                        Toast.makeText(MainActivity.this, "No Results Found", Toast.LENGTH_SHORT).show();


                                    adapter = new MyListAdapter(MainActivity.this, R.layout.contactlist, newHrList);
                                    hrList = newHrList;
                                    MainActivity.this.<ListView>findViewById(R.id.list).setAdapter(adapter);

                                    progressBar.dismiss();
                                }
                            });


                        }
                    }).start();

                }
                //   Toast.makeText(getApplicationContext(),dataBaseHelper.getAlumniCount()+"",Toast.LENGTH_SHORT).show();
                //   Toast.makeText(getApplicationContext(),hrList.size()+"",Toast.LENGTH_SHORT).show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // do your search on change or save the last string in search

                if (s.isEmpty()) {
                    if (MainActivity.this.spinnerItem.equalsIgnoreCase("HR")) {
                        if (adapter.getCount() != dataBaseHelper.getHRCount()) {
                            hrList = dataBaseHelper.getData();
                            setAdapter(hrList);
                        }
                    } else {
                        if (adapter.getCount() != dataBaseHelper.getAlumniCount()) {
                            hrList = dataBaseHelper.getDataalumni();
                            setAdapter(hrList);
                        }
                    }
                }
                return false;
            }

        });

        // you can get query
        searchView.getQuery();

        return true;
    }


    public void onSearchContact(String s) {


        Log.d("SEARCH TAG", s);

        ArrayList<HR> newHrList = new ArrayList<>();

        if (MainActivity.this.spinnerItem.equalsIgnoreCase("HR"))
            newHrList = new Searching().onSearch(dataBaseHelper.getData(), s);
        else
            newHrList = new Searching().onSearch(dataBaseHelper.getDataalumni(), s);

        progressBarStatus = 100;
        //for (HR hr : hrList) {
        //    if (hr.getName().toLowerCase().contains(s.toLowerCase()) || hr.getCity().toLowerCase().contains(s.toLowerCase())) {
        //        newHrList.add(hr);
        //    }
        //}

        if (newHrList.isEmpty())
            Toast.makeText(MainActivity.this, "No Results Found", Toast.LENGTH_SHORT).show();

        adapter = new MyListAdapter(this, R.layout.contactlist, newHrList);

        hrList = newHrList;
        list.setAdapter(adapter);

    }


    public void setAdapter(ArrayList<HR> hrList) {
        if (adapter != null) adapter.clear();
        adapter = new MyListAdapter(MainActivity.this, R.layout.contactlist, hrList);
        list.setAdapter(adapter);


        //    if(isLocationOn)
        //        onSearchContact(city);

    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        switch (requestCode) {
            case 1:
                try {
//                    String filepath = new File(data.getData().getPath()).getName().split(":")[1];
                    File sdcard0 = Environment.getExternalStoragePublicDirectory("");

                    Uri uri = data.getData();
                    String path = getPath(this, uri);

                    final File file;
//                    file = new File(sdcard0, filepath);
                    file = new File(path);

                    Log.d("FILEPATH0", file.toString());
//                    Log.d("FILEPATH1", filepath);
                    Log.d("FILEPATH2", sdcard0.toString());
                    Log.d("FILEPATH3", "File Uri: " + uri.toString());
                    Log.d("FILEPATH4", "File Path: " + path);

                    String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));

                    if (file.exists()) {
                        if (extension.equalsIgnoreCase(".csv")) {

                            progressBar = new ProgressDialog(MainActivity.this);
                            progressBar.setCancelable(true);
                            progressBar.setMessage("Importing Contacts");
                            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressBar.setProgress(0);
                            progressBar.setMax(100);
                            progressBar.show();
                            progressBarStatus = 0;

                            CSVReader csvReader = new CSVReader(new FileReader(file));
                            if(csvReader.readNext().length==6) {
                                csvReader.close();
                                final CSVReader csvReader1 = new CSVReader(new FileReader(file));
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        progressBarStatus = dataBaseHelper.insertData(csvReader1, importhr);
                                        try {
                                            csvReader1.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.dismiss();
                                                if (spinnerItem.equalsIgnoreCase("HR"))
                                                    hrList = dataBaseHelper.getData();
                                                else
                                                    hrList = dataBaseHelper.getDataalumni();

                                                setAdapter(hrList);
                                            }
                                        });
                                    }
                                }).start();
                            }else {
                                Toast.makeText(getApplicationContext(), "CSV file is not as per format", Toast.LENGTH_SHORT).show();
                                progressBar.dismiss();
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.dismiss();
                                    Toast.makeText(getApplicationContext(), "File Format Not Supported", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Something went wrong...\n please place your file to internal storage then try again", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.d("STATUS", e.toString());
                }
                break;
        }

    }

    public void notifyExporting(File file) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.stat_sys_upload_done)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_success))
                        .setContentTitle("File Exported Successfully!")
                        .setContentText("Check your file in INTERNAL STORAGE")
                        .setAutoCancel(true);

        Intent notificationIntent = new Intent();
        notificationIntent.setAction(android.content.Intent.ACTION_VIEW);
        notificationIntent.setDataAndType(Uri.fromFile(file), getMimeType(file.getAbsolutePath()));

        //Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public boolean gettingPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.v("PERMISSION", "Permission is granted");
            } else {
                Log.v("GETTING PERMISSIONS", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("PERMISSION", "Permission is granted");
            return true;
        }
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 123: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Peform your task here if any
                } else {
                    gettingPermissions();
                }
                return;
            }
        }
    }

    public class MyLocationListener implements LocationListener {

        double lat, lon;

        public void onLocationChanged(Location loc) {
            try {

                Log.d("LOCATION", "Trying to get lattitude and longitude");
                lat = loc.getLatitude();

                lon = loc.getLongitude();

                loc.getAccuracy();

                Log.d("LOCATION", "lattitude and longitude getting success");



                String Text = "My current location is: " + "\nLatitude = "
                        + lat + "\nLongitude = " + lon;

                Log.d("Location", Text);

                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                final String addressLine = addresses.get(0).getAddressLine(0);
                final String cityName = addresses.get(0).getLocality();
                SharedPreferences.Editor editor = getSharedPreferences("status", MODE_PRIVATE).edit();
                editor.putString("lastLocation", cityName);
                editor.putString("address", addressLine);
                editor.apply();

                city = cityName;

                Log.d("City", city);
                Log.d("Address", addressLine);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isLocationOn) {
                            onSearchContact(addressLine);
                            actionBar.setSubtitle(Html.fromHtml("<font color='black'>Search by : " + cityName + "</font>"));
                        }
                    }
                });


                //onSearchLocation(cityName);

                //   Toast.makeText(getApplicationContext(),"city:"+cityName, Toast.LENGTH_SHORT).show();
                progressBarStatus = 100;
                // Display location
                //    Toast.makeText(getApplicationContext(), Text,Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                // TODO: handle exception
                Log.d("ERROR-PANIC-2", e.toString());
            }
        }

        public void onProviderDisabled(String provider) {
            SharedPreferences.Editor editor =
                    getSharedPreferences("status", MODE_PRIVATE).edit();
            editor.putBoolean("location", false);
            editor.apply();
            filter.setIcon(R.drawable.ic_city_disable);
            isLocationOn = false;
            actionBar.setSubtitle("");
            locManager.removeUpdates(locListener);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.dismiss();
                    if (spinnerItem.equalsIgnoreCase("HR"))
                        hrList = dataBaseHelper.getData();
                    else
                        hrList = dataBaseHelper.getDataalumni();

                    setAdapter(hrList);
                }
            });
            Toast.makeText(getApplicationContext(), "Gps Disabled",
                    Toast.LENGTH_SHORT).show();
        }

        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Enabled",
                    Toast.LENGTH_SHORT).show();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        if (locManager != null)
            locManager.removeUpdates(locListener);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        isGPSEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void changeList(int code) {
        isGPSEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        progressBar = new ProgressDialog(MainActivity.this);
        switch (code) {
            case 1:
                progressBar.setMessage("Please Wait...");
                break;
            case 2:
                progressBar.setMessage("Refreshing...");
                break;
            case 3:
                progressBar.setMessage("Changing List...");
                break;
        }
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        new Thread(new Runnable() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {
                if (isLocationOn) {
                    try {
                        if (isNetworkEnabled && isGPSEnabled) {
                            if (MainActivity.this.spinnerItem.equalsIgnoreCase("HR"))
                                hrList = new Searching().onSearch(dataBaseHelper.getData(), address);
                            else
                                hrList = new Searching().onSearch(dataBaseHelper.getDataalumni(), address);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    adapter = new MyListAdapter(MainActivity.this, R.layout.contactlist, hrList);
                                    MainActivity.this.<ListView>findViewById(R.id.list).setAdapter(adapter);

                                    if (isNetworkEnabled) {
                                        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                                60000, 0, locListener);
                                    } else if (isGPSEnabled) {
                                        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                                60000, 0, locListener);
                                    }
                                }
                            });
                            progressBar.dismiss();
                        }
                    } catch (Exception e) {

                        Log.d("ERROR-PANIC", e.toString());
                    }

                } else {
                    if (spinnerItem.equalsIgnoreCase("HR")) {
                        hrList = dataBaseHelper.getData();
                    } else {
                        hrList = dataBaseHelper.getDataalumni();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setAdapter(hrList);
                            progressBar.dismiss();
                        }
                    });
                }
            }
        }).start();
    }

    public void handleUncaughtException(Thread thread, Throwable e) {
        e.printStackTrace(); // not all Android versions will print the stack trace automatically

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "trilokynathwagh@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "T&P Contact App Bugs");
        emailIntent.putExtra(Intent.EXTRA_TEXT, e.toString());
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
        finish();
    }

    private Boolean exit = false;

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }
}
