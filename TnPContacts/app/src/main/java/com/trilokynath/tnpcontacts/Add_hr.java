package com.trilokynath.tnpcontacts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Add_hr extends AppCompatActivity {

    EditText input_name,input_mobile,input_email,input_company,input_city,input_note;
    HR hr;
    String spinnerItem;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_hr);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        hr = new HR();

        if(b!=null)
        {
            hr.ID = (Integer) b.get("ID");
            hr.name =(String) b.get("Name");
            hr.mobile =(String) b.get("Mobile");
            hr.email =(String) b.get("Email");
            hr.company =(String) b.get("Company");
            hr.city =(String) b.get("City");
            hr.note =(String) b.get("Note");
            spinnerItem = b.get("spinerItem").toString();
        }

        //Toast.makeText(this,""+hr.ID,Toast.LENGTH_SHORT).show();

        input_name = (EditText) findViewById(R.id.input_name);
        input_mobile = (EditText) findViewById(R.id.input_mobile);
        input_email = (EditText) findViewById(R.id.input_email);
        input_company = (EditText) findViewById(R.id.input_company);
        input_city = (EditText) findViewById(R.id.input_city);
        input_note = (EditText) findViewById(R.id.input_note);
        input_note.setMovementMethod(new ScrollingMovementMethod());

        input_name.setText(hr.getName());
        input_mobile.setText(hr.getMobile());
        input_email.setText(hr.getEmail());
        input_company.setText(hr.getCompany());
        input_city.setText(hr.getCity());
        input_note.setText(hr.getNote());

        input_company.setMovementMethod(new ScrollingMovementMethod());
        input_city.setMovementMethod(new ScrollingMovementMethod());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.savebtn, menu);
        // Associate searchable configuration with the SearchView
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                // do something based on first item click

                if(input_name.getText()!=null&&!input_name.getText().toString().isEmpty()) {
                    hr.name = input_name.getText().toString();
                    hr.email = input_email.getText().toString();
                    hr.mobile = input_mobile.getText().toString();
                    hr.company = input_company.getText().toString();
                    hr.city = input_city.getText().toString();
                    hr.note = input_note.getText().toString().trim();

                    if (hr.getID() == null) {
                        if (spinnerItem.equalsIgnoreCase("HR")) {
                            new DataBaseHelper(this).addRecords(hr);
                        } else {
                            new DataBaseHelper(this).addRecordsalumni(hr);
                        }
                        Toast.makeText(getApplicationContext(), "Contact Saved", Toast.LENGTH_SHORT).show();
                    } else {
                        if (spinnerItem.equalsIgnoreCase("HR")) {
                            new DataBaseHelper(this).updateRecords(hr,hr.getID());
                        } else {
                            new DataBaseHelper(this).updateRecordsalumni(hr,hr.getID());
                        }
                        Toast.makeText(getApplicationContext(), "Contact Updated", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    Toast.makeText(getApplicationContext(), "Contact Not Saved", Toast.LENGTH_SHORT).show();

                finish();

            case android.R.id.home:
                // do something based on first item click
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
