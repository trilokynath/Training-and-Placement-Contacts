package com.trilokynath.tnpcontacts;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class About extends Activity implements View.OnClickListener{

    TextView name,email,mobile,college,social,bio;
    RelativeLayout lname,lemail,lmobile,lcollege,lsocial;
    ImageView iname,iemail,imobile,icollege,isocial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_developer);


        name = findViewById(R.id.contact_name);
        email = findViewById(R.id.contact_email);
        mobile = findViewById(R.id.contact_mobile);
        college = findViewById(R.id.contact_college);
        social = findViewById(R.id.contact_social);
        bio = findViewById(R.id.contact_note);

        lname = findViewById(R.id.contact_name_holder);
        lemail = findViewById(R.id.contact_email_holder);
        lmobile = findViewById(R.id.contact_mobile_holder);
        lcollege = findViewById(R.id.contact_college_holder);
        lsocial = findViewById(R.id.contact_social_holder);

        iname = findViewById(R.id.contact_tmb);
        iemail = findViewById(R.id.contact_email_icon);
        imobile = findViewById(R.id.contact_mobile_icon);
        icollege = findViewById(R.id.contact_comapny_icon);
        isocial = findViewById(R.id.contact_social_icon);

        iname.setImageResource(R.drawable.designation);
        iemail.setImageResource(R.drawable.ic_email_big);
        imobile.setImageResource(R.drawable.ic_phone);
        icollege.setImageResource(R.drawable.ic_company);
        isocial.setImageResource(R.drawable.fb);
//        back.setImageResource(R.drawable.ic_back);


        name.setText("Developer (BE-COMP)");
        mobile.setText("8390861559");
        email.setText("trilokynathwagh@gmail.com");
        college.setText("RCPIT, Shirpur");
        social.setText("fb/waghtrilokynath");
        bio.setText(Html.fromHtml("<font color='black'>This app is Created by trilokynath dedicated to the RCPIT Training and Placement Department to maintain HR and Alumni Contacts. \uD83D\uDE0A</font>"));


        name.setTextColor(Color.BLACK);
        email.setTextColor(Color.BLACK);
        mobile.setTextColor(Color.BLACK);
        college.setTextColor(Color.BLACK);
        social.setTextColor(Color.BLACK);
        bio.setTextColor(Color.BLACK);


        lmobile.setOnClickListener(this);
        lemail.setOnClickListener(this);
        lcollege.setOnClickListener(this);
        lsocial.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId() /*to get clicked view id**/) {
            case R.id.contact_mobile_holder:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "+918390861559", null));
                startActivity(intent);
                break;
            case R.id.contact_email_holder:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "trilokynathwagh@gmail.com", null));
                startActivity(Intent.createChooser(emailIntent, null));
                break;
            case R.id.contact_college_holder:
                Intent collegeintent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://rcpit.ac.in"));
                startActivity(collegeintent);
                break;
            case R.id.contact_social_holder:
                Intent socialintent;
                socialintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/waghtrilokynath"));
                startActivity(socialintent);
                break;
            default:
                break;
        }
    }
}