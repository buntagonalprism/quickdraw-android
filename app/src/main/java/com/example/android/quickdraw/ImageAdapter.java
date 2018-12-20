package com.example.android.quickdraw;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * The default ArrayAdapter implementation only returns a string. If we want our array adapter
 * to populate a listview with more complex views we need to create our own array adapter
 * The getView function is called for each item in the arrayList supplying data for the adapter,
 * and getView should return the full custom view structure desired.
 * Created by Alex on 22/1/15.
 */
public class ImageAdapter extends ArrayAdapter<Integer> {
    Context context;

    // When a child class is instantiated all the constructors of the parent or super classes must
    // be called in order to initialise the data members inherited from the super classes.
    // By default the default constructors of the super classes are called. However if there are
    // no default constructors or we wish to initialise the super class in some way, we must call
    // the appropriate constructor of the super class using the super(args ...) method.
    // Note that we will never need to do this for more than one level up because the constructors
    // should chain through, with the super constructor calling its super constructor etc.
    public ImageAdapter(Context context, int resourceId, ArrayList<Integer> imageIds) {
        super(context, resourceId, imageIds);
        this.context = context;
    }

    // The @Override is an annotation (like anything prefixed with @ in java), which tells the coder
    // the following function is an override of one in the super class. It also tells the compiler
    // to check for a matching function in the super class to override.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get an inflater to inflate the view for a single element
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.constraint_button, null);

        // Get the image part of the view (the only part in this case, but if the view we
        // had just inflated was more complicated we might have other parts to get)
        ImageView image = (ImageView) convertView.findViewById(R.id.constraintImage);

        // Use the position in the array adapter to get the corresponding picture ID to display
        Integer imageId = getItem(position);
        image.setImageResource(imageId);
        return convertView;
    }
}
