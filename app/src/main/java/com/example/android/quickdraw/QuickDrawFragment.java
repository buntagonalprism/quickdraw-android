package com.example.android.quickdraw;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.quickdraw.ObjectDatabase.ShapeDatabase;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.lucasr.twowayview.TwoWayView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by t on 3/5/15.
 */
public class QuickDrawFragment extends Fragment implements AdapterView.OnItemClickListener{
    Paint paint = new Paint();

    String fileToOpen = null;

    DrawingCanvas mDrawCanvas = null;

    private final String[] coloursText = {"Black", "Blue", "Red", "Green"};
    private final int[] coloursCodes = {Color.BLACK, Color.BLUE, Color.RED, Color.GREEN};
    private final String[] weightsText = {"0.5pt","1.0pt", "1.5pt", "2.0pt"};
    private final float[] weightsValues = {4f, 8f, 12f, 16f};
    private final String[] toolsText = {"Draw","Edit","Erase","Move"};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main_quickdraw_actions, menu);
    }

    /**
     * onCreate is called at the start of the activity lifecycle. It is generally responsible for
     * two things: specifying a layout resource for the activity UI using setContentView, and
     * modifying particular views in the UI using findViewById.
     * @param savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Specify the XML layout file to use for this activity
        View mFragmentView = inflater.inflate(R.layout.quickdraw_fragment, container, false);

        /**************************** Constraint Bar Configuration ********************************/
        // The adapter takes input of the data and a layout file describing how to show each data element
        ImageAdapter itemAdapter = new ImageAdapter(getActivity(), R.layout.constraint_button, new ArrayList<Integer>());

        // We pass the currently empty adapter to the canvas so it can modify the contents later
        mDrawCanvas = (DrawingCanvas) mFragmentView.findViewById(R.id.drawingCanvas);
        mDrawCanvas.setImageButtonAdapter(itemAdapter);

        // The adapter is set for the two way view it supplies data tor
        TwoWayView twoWayView = (TwoWayView) mFragmentView.findViewById(R.id.constraintBar);
        twoWayView.setAdapter(itemAdapter);

        // Use the callback functions defined in this activity below to respond to events
        twoWayView.setOnItemClickListener(this);

        // Note this is a pixel value manually set to be equal to the 2dp borders used elsewhere
        twoWayView.setItemMargin(4);


        /*************************** Colour Selector Configuration ********************************/
        // A spinner is a drop-down selection list
        Spinner colourSelectorSpinner = (Spinner) mFragmentView.findViewById(R.id.colour_selector);

        // The array adapter can be constructed an existing arraylist, a new empty array list, or an
        // array list constructed from a simple array. Modifications to the contents of the array
        // adapter can be made at any time in the same ways as an array list.
        ArrayList<CharSequence> colourSelections = new ArrayList<CharSequence>(Arrays.asList(coloursText));
        ArrayAdapter<CharSequence> colourSelectorAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, colourSelections);

        // Drop down view resource is how to draw each spinner item. We use an android default here
        colourSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        colourSelectorSpinner.setAdapter(colourSelectorAdapter);

        // Anonymous class definition of the listener for clicks on the spinner. We could specify
        // an activity-scope click listener handling all spinners, but then the click listener must
        // work out which spinner was clicked. This way only the relevant anonymous class is called
        // Note spinner items are selected, listview items are clicked
        colourSelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                paint.setColor(coloursCodes[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /*************************** Weight Selector Configuration ********************************/
        ArrayAdapter<CharSequence> weightSelectorAdapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item, new ArrayList<CharSequence>(Arrays.asList(weightsText)));
        weightSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner weightSelectorSpinner = (Spinner) mFragmentView.findViewById(R.id.weight_selector);
        weightSelectorSpinner.setAdapter(weightSelectorAdapter);
        weightSelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                paint.setStrokeWidth(weightsValues[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /**************************** Tool Selector Configuration *********************************/
        ArrayAdapter<CharSequence> toolSelectorAdapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item, new ArrayList<CharSequence>(Arrays.asList(toolsText)));
        toolSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner toolSelectorSpinner = (Spinner) mFragmentView.findViewById(R.id.tool_selector);
        toolSelectorSpinner.setAdapter(toolSelectorAdapter);
        toolSelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        /**************************** Specify default paint style *********************************/
        paint.setAntiAlias(true);
        paint.setStrokeWidth(6f);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        mDrawCanvas.setPaintStyle(paint);

        return mFragmentView;
    }


    /**
     * A callback function for when an item has been clicked. Note that listView items are clicked
     * while spinner items are selected. Requires this activity to be registered as a listener to
     * whatever view it is supposed to respond to using setOnItemClickListener(this)
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showToast("Button number " + position + " pressed with id " + id);
    }

    /**
     * Handle selections on menu items here.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Get the ID of the item which was selected then respond appropriately
        int id = item.getItemId();
        DrawingCanvas drawCanvas = (DrawingCanvas) getView().findViewById(R.id.drawingCanvas);

        if (id == R.id.action_settings) {
            showToast("Settings Option Selected");
            return true;
        }
        if (id == R.id.action_new) {
            showToast("New document selected");
            drawCanvas.clearCanvas();
            return true;
        }
        if (id == R.id.action_open){
            showToast("Open document selected");

            FileOpenDialog dialogFragment = new FileOpenDialog();
            Bundle args = new Bundle();
            args.putString("fileRoot",getActivity().getFilesDir().toString());
            dialogFragment.setArguments(args);

            dialogFragment.setListener(new FileOpenDialog.FileOpenDialogListener() {
                @Override
                public void onFileOpenSelected(final String filename) {
                    OpenFile(filename);
                }

                @Override
                public void onFileOpenCancelled(String filename) {
                    fileToOpen = null;
                }
            });
            dialogFragment.show(getFragmentManager(), "fileopenchooser");

            return true;
        }
        if (id == R.id.action_save) {
            FileSaveDialog dialogFragment = new FileSaveDialog();
            dialogFragment.setListener(new FileSaveDialog.FileSaveDialogListener() {
                @Override
                public void onFileSaveSelected(String filename) {
                    SaveFile(filename);
                }

                @Override
                public void onFileSaveCancelled() {}
            });
            dialogFragment.show(getFragmentManager(),"filesavechooser");

            return true;
        }
        if (id == R.id.action_export_svg) {
            drawCanvas.writeSvgFile();
            return true;
        }
        if (id == R.id.action_export_bitmap) {
            drawCanvas.writeBitmap();
            return true;
        }
        if (id == R.id.action_output_points) {
            drawCanvas.printPtsToFile();
            return true;
        }
        if (id == R.id.action_show_points) {
            if (!item.isChecked()) {
                item.setChecked(true);
                drawCanvas.setShowPoints(true);
            }
            else {
                item.setChecked(false);
                drawCanvas.setShowPoints(false);
            }
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Open a file with a given file name
     * @param filename The file name to open
     */
    public void OpenFile(final String filename) {
        fileToOpen = filename;
        DrawingCanvas drawingCanvas = (DrawingCanvas) getView().findViewById(R.id.drawingCanvas);
        if (drawingCanvas == null) {
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        File file = new File(getActivity().getFilesDir(), filename);
        if (!file.exists()) {
            return;
        }
        try {
            ShapeDatabase shapeDB = mapper.readValue(file, ShapeDatabase.class);
            drawingCanvas.shapeDB = shapeDB;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//        if (mDrawCanvas == null) {
//            return;
//        }
//        // As the JSON load is time consuming, we do it off the main thread
//        new Thread(new Runnable() {
//            public void run() {
//
//                try {
//                    final DrawingCanvas drawingCanvas = (DrawingCanvas) getView().findViewById(R.id.drawingCanvas);
//                    ObjectMapper mapper = new ObjectMapper();
//                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//                    File file = new File(getActivity().getFilesDir(), filename);
//
//                    drawingCanvas.shapeDB = mapper.readValue(file, ShapeDatabase.class);
//
//                    // Invalidating the canvas occurs on the UI thread, so we post a request to
//                    // operate on this view back to the UI thread. We could also have used
//                    // Activity.runOnUiThread or View.postDelayed which has a ms delay
//                    // before the UI thread executes the supplied runnable
//                    drawingCanvas.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            drawingCanvas.invalidate();
//                        }
//                    });
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();

    // As an aside, we could have also done this using an Aysnc Task - an asynchronous task auto-
    // generates worker threads to do work in the background and on completion updates the main thread:
    //
    // private class MyAsyncTask extends AsyncTask<InputType, OutputType, IntermediateType> {
    //
    //     protected IntermediateType doInbackground(InputType inputVar) {
    //         Do not touch anything on the UI thread here
    //         Just do background work and return intermediate results
    //         This would take an input filename, read the json and
    //         update the shape database, intermediate output void.
    //     }
    //
    //     protected OutPutType onPostExecute(IntermediateType result) {
    //         Use the intermediate results to update UI thread
    //         Possibly output something else
    //         This would call invalidate on the canvas view
    //     }
    // }
    //
    // Call with:
    // new MyAsyncTask().execute(myInputVar);


    /**
     * Save a file with a given name to the android application directory. Note the use of 'final'
     * in the arguments for this function. It declares the string variable will not be modified
     * once initialised. This means the new thread can access it safely as the value won't change
     * @param filename The filename to save
     */
    public void SaveFile(final String filename) {
        showToast(filename);

        new Thread(new Runnable() {
            public void run() {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.enableDefaultTyping();
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    mapper.writeValue(new File(getActivity().getFilesDir(), filename), mDrawCanvas.shapeDB);
                }
                catch (Exception e) { e.printStackTrace(); }
            }
        }).start();
    }


    private void showToast(CharSequence text) {
        Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        toast.show();
    }



}
