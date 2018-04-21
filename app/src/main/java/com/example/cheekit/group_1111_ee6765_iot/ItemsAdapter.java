package com.example.cheekit.group_1111_ee6765_iot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class ItemsAdapter extends ArrayAdapter {
    ArrayList<String> items = new ArrayList<String>();

    public ItemsAdapter(Context context, ArrayList<String> items) {
        super(context, 0, items);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.items_list_view, parent, false);
        // Lookup view for data population
        LinearLayout itemsHolder = (LinearLayout) convertView.findViewById(R.id.items);
        // Populate the data into the template view using the data object
        for (int i = 0; i < items.size(); i++) {
            CheckBox checkBox = new CheckBox(getContext()); // Creating the checkbox
            checkBox.setText(items.get(i)); // Adding the item name to it
            itemsHolder.addView(checkBox); // Appending it to the layout
        }
        // Return the completed view to render on screen
        return convertView;
    }
}