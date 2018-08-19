package com.trilokynath.tnpcontacts;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

//we need to extend the ArrayAdapter class as we are building an adapter
public class MyListAdapter extends ArrayAdapter<HR> {

    //the list values in the List of type hero
    List<HR> hrList;

    //activity context
    Context context;

    //the layout resource file for the list items
    int resource;

    //constructor initializing the values
    public MyListAdapter(Context context, int resource, List<HR> hrList) {
        super(context, resource, hrList);
        this.context = context;
        this.resource = resource;
        this.hrList = hrList;
    }

    //this will return the ListView Item as a View
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //we need to get the view of the xml for our list item
        //And for this we need a layoutinflater
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        //getting the view
        View view = layoutInflater.inflate(resource, null, false);

        //getting the view elements of the list from the view
     //   ImageView imageView = view.findViewById(R.id.contact_tmb);
        TextView textViewName = view.findViewById(R.id.contact_name);
        TextView textViewTeam = view.findViewById(R.id.contact_city);

        //getting the hero of the specified position
        HR hr = hrList.get(position);

        textViewName.setText(hr.getName());
        textViewTeam.setText(hr.getCity());

        //adding a click listener to the button to remove item from the list
        /*buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //we will call this method to remove the selected value from the list
                //we are passing the position which is to be removed in the method
                removeHero(position);
            }
        });*/

        //finally returning the view
        return view;
    }

    //this method will remove the item from the list
    /*private void removeHero(final int position) {
        //Creating an alert dialog to confirm the deletion
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Are you sure you want to delete this?");

        //if the response is positive in the alert
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //removing the item
                heroList.remove(position);

                //reloading the list
                notifyDataSetChanged();
            }
        });

        //if response is negative nothing is being done
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        //creating and displaying the alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }*/
}