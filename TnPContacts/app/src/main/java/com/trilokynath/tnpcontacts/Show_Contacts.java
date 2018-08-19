package com.trilokynath.tnpcontacts;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Show_Contacts extends AppCompatActivity {

    TextView c_name,c_mobile,c_email,c_company,c_city,c_note;
    FloatingActionButton fab;
    String spinnerItem;
    ImageView email_view,company_location_view,city_location_view,edit_note;

    HR hr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showcontact);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        hr = new HR();
        if (b != null){
            hr.ID = (Integer) b.get("ID");
            spinnerItem = b.get("spinerItem").toString();
        }

        if (spinnerItem.equalsIgnoreCase("HR")) {
            hr = new DataBaseHelper(this).getRecord(hr.getID());
        } else {
            hr = new DataBaseHelper(this).getRecordalumni(hr.getID());
        }

        getSupportActionBar().setTitle(hr.name);



        c_name = (TextView) findViewById(R.id.contact_name);
        c_mobile = (TextView) findViewById(R.id.contact_mobile);
        c_email = (TextView) findViewById(R.id.contact_email);
        c_company = (TextView) findViewById(R.id.contact_company);
        c_city = (TextView) findViewById(R.id.contact_city);
        c_note = (TextView) findViewById(R.id.contact_note);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        email_view = (ImageView) findViewById(R.id.email_send);
        company_location_view = (ImageView) findViewById(R.id.company_location);
        city_location_view = (ImageView) findViewById(R.id.city_location);
        edit_note = (ImageView) findViewById(R.id.edit_note);



        c_name.setText(hr.getName());
        c_mobile.setText(hr.getMobile());
        c_email.setText(hr.getEmail());
        c_company.setText(hr.getCompany());
        c_city.setText(hr.getCity());
        c_note.setText(hr.getNote());

        ViewGroup.LayoutParams params = c_note.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        c_note.setLayoutParams(params);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validatePhoneNumber(hr.getMobile())) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "+91" + hr.getMobile(), null));
                    startActivity(intent);
                }else
                    Toast.makeText(Show_Contacts.this,"Invalid Mobile Number",Toast.LENGTH_SHORT).show();
            }
        });

        email_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEmailValid(hr.getEmail())) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", hr.getEmail(), null));
                    startActivity(Intent.createChooser(emailIntent, null));
                }else{
                    Toast.makeText(getApplicationContext(),"Invalid Email Address", Toast.LENGTH_SHORT).show();
                }
            }
        });

        company_location_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/dir/?api=1&origin=&destination="+hr.getCompany()+"&travelmode=car"));
                startActivity(intent);
            }
        });

        city_location_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/dir/?api=1&origin=&destination="+hr.getCity()+"&travelmode=car"));
                startActivity(intent);
            }
        });

        edit_note.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(getApplicationContext());
                View promptsView = li.inflate(R.layout.prompt_edit_note, null);


                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                userInput.setText(hr.getNote());
                userInput.setTextColor(Color.BLACK);

                new AlertDialog.Builder(new ContextThemeWrapper(Show_Contacts.this, R.style.myDialog))

                        // set prompts.xml to alertdialog builder
                .setView(promptsView)

                        .setCancelable(false)
                        .setIcon(android.R.drawable.ic_menu_edit)
                        .setTitle("Note")
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
                                        hr.note = userInput.getText().toString().trim();
                                        if(spinnerItem.equalsIgnoreCase("HR"))
                                            new DataBaseHelper(getBaseContext()).updateRecords(hr,hr.getID());
                                        else
                                            new DataBaseHelper(getBaseContext()).updateRecordsalumni(hr,hr.getID());

                                        c_note.setText(hr.getNote().trim());
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                }).show();

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.editbtn, menu);
        // Associate searchable configuration with the SearchView
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // do something based on first item click
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.action_edit:
                // do something based on first item click

                Intent myIntent = new Intent(getApplicationContext(), Add_hr.class);
                myIntent.putExtra("ID",hr.getID());
                myIntent.putExtra("Name", hr.getName());
                myIntent.putExtra("Mobile", hr.getMobile());
                myIntent.putExtra("Email", hr.getEmail());
                myIntent.putExtra("Company", hr.getCompany());
                myIntent.putExtra("City", hr.getCity());
                myIntent.putExtra("Note", hr.getNote());
                myIntent.putExtra("spinerItem", spinnerItem);
                startActivityForResult(myIntent, 0);
                return true;

            case R.id.action_delete:
                // do something based on first item click

                new AlertDialog.Builder(this)
                        .setTitle(hr.getName())
                        .setMessage("Do you really want to delete this contact?")
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (spinnerItem.equalsIgnoreCase("HR")) {
                                    new DataBaseHelper(Show_Contacts.this).deleteContact(hr.ID);
                                } else {
                                    new DataBaseHelper(Show_Contacts.this).deleteContactalumni(hr.ID);
                                }

                                Toast.makeText(Show_Contacts.this,"Contact Deleted Successfully",Toast.LENGTH_SHORT).show();
                                finish();
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (spinnerItem.equalsIgnoreCase("HR")) {
            hr = new DataBaseHelper(this).getRecord(hr.ID);
        } else {
            hr = new DataBaseHelper(this).getRecordalumni(hr.ID);
        }

        c_name.setText(hr.getName());
        c_mobile.setText(hr.getMobile());
        c_email.setText(hr.getEmail());
        c_company.setText(hr.getCompany().trim());
        c_city.setText(hr.getCity().trim());
        c_note.setText(hr.getNote());

    }

    static boolean validatePhoneNumber(String phoneNo) {
        //validate phone numbers of format "1234567890"
        if (phoneNo.matches("\\d{10}")) return true;
            //validating phone number with -, . or spaces
        else if(phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
            //validating phone number with extension length from 3 to 5
        else if(phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
            //validating phone number where area code is in braces ()
        else if(phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true;
            //return false if nothing matches the input
        else return false;

    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
