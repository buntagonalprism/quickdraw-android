package com.example.android.quickdraw;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * onCreate is called at the start of the activity lifecycle. It is generally responsible for
     * two things: specifying a layout resource for the activity UI using setContentView, and
     * modifying particular views in the UI using findViewById.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add fragment through XML
        setContentView(R.layout.mainactivity_container);

        // Add fragment programatically
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        QuickDrawFragment fragment = new QuickDrawFragment();
        fragmentTransaction.add(R.id.mainactivity_container, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mainactivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}



//
//        // Specify the XML layout file to use for this activity
//        setContentView(R.layout.activity_main);
//
//        /**************************** Constraint Bar Configuration ********************************/
//        // The adapter takes input of the data and a layout file describing how to show each data element
//        ImageAdapter itemAdapter = new ImageAdapter(this, R.layout.constraint_button, new ArrayList<Integer>());
//
//        // We pass the currently empty adapter to the canvas so it can modify the contents later
//        DrawingCanvas drawCanvas = (DrawingCanvas) findViewById(R.id.drawingCanvas);
//        drawCanvas.setImageButtonAdapter(itemAdapter);
//
//        // The adapter is set for the two way view it supplies data tor
//        TwoWayView twoWayView = (TwoWayView) findViewById(R.id.constraintBar);
//        twoWayView.setAdapter(itemAdapter);
//
//        // Use the callback functions defined in this activity below to respond to events
//        twoWayView.setOnItemClickListener(this);
//
//        // Note this is a pixel value manually set to be equal to the 2dp borders used elsewhere
//        twoWayView.setItemMargin(4);
//
//
//        /*************************** Colour Selector Configuration ********************************/
//        // A spinner is a drop-down selection list
//        Spinner colourSelectorSpinner = (Spinner) findViewById(R.id.colour_selector);
//
//        // The array adapter can be constructed an existing arraylist, a new empty array list, or an
//        // array list constructed from a simple array. Modifications to the contents of the array
//        // adapter can be made at any time in the same ways as an array list.
//        ArrayList<CharSequence> colourSelections = new ArrayList<CharSequence>(Arrays.asList(coloursText));
//        ArrayAdapter<CharSequence> colourSelectorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, colourSelections);
//
//        // Drop down view resource is how to draw each spinner item. We use an android default here
//        colourSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        colourSelectorSpinner.setAdapter(colourSelectorAdapter);
//
//        // Anonymous class definition of the listener for clicks on the spinner. We could specify
//        // an activity-scope click listener handling all spinners, but then the click listener must
//        // work out which spinner was clicked. This way only the relevant anonymous class is called
//        // Note spinner items are selected, listview items are clicked
//        colourSelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                paint.setColor(coloursCodes[position]);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//
//        /*************************** Weight Selector Configuration ********************************/
//        ArrayAdapter<CharSequence> weightSelectorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<CharSequence>(Arrays.asList(weightsText)));
//        weightSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        Spinner weightSelectorSpinner = (Spinner) findViewById(R.id.weight_selector);
//        weightSelectorSpinner.setAdapter(weightSelectorAdapter);
//        weightSelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                paint.setStrokeWidth(weightsValues[position]);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//
//        /**************************** Tool Selector Configuration *********************************/
//        ArrayAdapter<CharSequence> toolSelectorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<CharSequence>(Arrays.asList(toolsText)));
//        toolSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        Spinner toolSelectorSpinner = (Spinner) findViewById(R.id.tool_selector);
//        toolSelectorSpinner.setAdapter(toolSelectorAdapter);
//        toolSelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {}
//        });
//
//        /**************************** Specify default paint style *********************************/
//        paint.setAntiAlias(true);
//        paint.setStrokeWidth(6f);
//        paint.setColor(Color.BLACK);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeJoin(Paint.Join.ROUND);
//        drawCanvas.setPaintStyle(paint);


    //
//    /**
//     * A callback function for when an item has been clicked. Note that listView items are clicked
//     * while spinner items are selected. Requires this activity to be registered as a listener to
//     * whatever view it is supposed to respond to using setOnItemClickListener(this)
//     * @param parent
//     * @param view
//     * @param position
//     * @param id
//     */
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        showToast("Button number " + position + " pressed with id " + id);
//    }
//
//    /***********************************************************************************************
//     * Inflate the menu; this adds items to the action bar if it is present.
//     * Note that if the action bar is not present this is probably due to issues with the android
//     * theme selected - check both the android manifest file and the styles.xml file to see what
//     * theme is being applied.
//     **********************************************************************************************/

//
//    /**
//     * Handle selections on menu items here.
//     * @param item
//     * @return
//     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        // Get the ID of the item which was selected then respond appropriately
//        int id = item.getItemId();
//
//        if (id == R.id.action_settings) {
//            showToast("Settings Option Selected");
//            return true;
//        }
//        if (id == R.id.action_new) {
//            showToast("New document selected");
//            DrawingCanvas drawCanvas = (DrawingCanvas) findViewById(R.id.drawingCanvas);
//            drawCanvas.clearCanvas();
//            return true;
//        }
//        if (id == R.id.action_open){
//            showToast("Open document selected");
//
//            FileOpenDialog dialogFragment = new FileOpenDialog();
//            Bundle args = new Bundle();
//            args.putString("fileRoot",getFilesDir().toString());
//            dialogFragment.setArguments(args);
//
//            dialogFragment.setListener(new FileOpenDialog.FileOpenDialogListener() {
//                @Override
//                public void onFileOpenSelected(final String filename) {
//                    fileToOpen = filename;
//                    //showToast(fileToOpen);
//
//                    // As the JSON load is time consuming, we do it off the main thread
//                    new Thread(new Runnable() {
//                        public void run() {
//                            final DrawingCanvas drawCanvas = (DrawingCanvas) findViewById(R.id.drawingCanvas);
//                            try {
//                                ObjectMapper mapper = new ObjectMapper();
//                                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//                                File file = new File(getFilesDir(), filename);
//
//                                if (file == null || !file.exists()) {
//                                    //showToast("null filename");
//                                    return;
//                                }
//                                else
//                                drawCanvas.shapeDB = mapper.readValue(file, ShapeDatabase.class);
//
//                                // Invalidating the canvas actually occurs on the UI thread, so we
//                                // post the request to operate on this view back to the UI thread
//                                // We could also have used Activity.runOnUiThread or View.postDelayed
//                                // which has a delay in milliseconds before the UiThread executes
//                                // the runnable we supply.
//                                drawCanvas.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        drawCanvas.invalidate();
//                                    }
//                                });
//                                // As an aside, we could have also done this using an Aysnc Task -
//                                // an asynchronous task auot-generates worker threads to do work in the
//                                // background and then on completion can update the main thread
//                                // private class MyAsyncTask extends AsyncTask<Input, Output, Intermediate> {
//                                //
//                                //     protected Intermediate doInbackground(Input inputVar) {
//                                //         Do not touch anything on the UI thread here
//                                //         Just do background work and return intermediate results
//                                //         This would take an input filename, read the json and
//                                //         update the shape database, intermediate output void.
//                                //     }
//                                //
//                                //     protected OutPut onPostExecute(Intermediate result) {
//                                //         Use the intermediate results to update UI thread
//                                //         Possibly output something else
//                                //         This would call invalidate on the canvas view
//                                //     }
//                                // }
//                                //
//                                // Call with:
//                                // new MyAsyncTask().execute(myInputVar);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }).start();
//                }
//
//                @Override
//                public void onFileOpenCancelled(String filename) {
//                    fileToOpen = null;
//                }
//            });
//            dialogFragment.show(getFragmentManager(), "fileopenchooser");
//
//            return true;
//        }
//        if (id == R.id.action_save) {
//            showToast("Save document selected");
//            FileSaveDialog dialogFragment = new FileSaveDialog();
//            dialogFragment.setListener(new FileSaveDialog.FileSaveDialogListener() {
//                @Override
//                public void onFileNameCancelled(String filename) {
//                    showToast("Data output from listener: " + filename);
//                }
//            });
//            dialogFragment.show(getFragmentManager(),"filesavechooser");
//
//            return true;
//        }
//        if (id == R.id.action_export_svg) {
//            DrawingCanvas drawCanvas = (DrawingCanvas) findViewById(R.id.drawingCanvas);
//            drawCanvas.writeSvgFile();
//            return true;
//        }
//        if (id == R.id.action_export_bitmap) {
//            DrawingCanvas drawCanvas = (DrawingCanvas) findViewById(R.id.drawingCanvas);
//            drawCanvas.writeBitmap();
//            return true;
//        }
//        if (id == R.id.action_output_points) {
//            DrawingCanvas drawCanvas = (DrawingCanvas) findViewById(R.id.drawingCanvas);
//            drawCanvas.printPtsToFile();
//            return true;
//        }
//        if (id == R.id.action_show_points) {
//            DrawingCanvas drawCanvas = (DrawingCanvas) findViewById(R.id.drawingCanvas);
//            if (!item.isChecked()) {
//                item.setChecked(true);
//                drawCanvas.setShowPoints(true);
//            }
//            else {
//                item.setChecked(false);
//                drawCanvas.setShowPoints(false);
//            }
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    /**
//     * Save a file with a given name to the android application directory. Note the use of 'final'
//     * in the arguments for this function. It declares the string variable will not be modified
//     * once initialised. This means the new thread can access it safely as the value won't change
//     * @param filename
//     */
//    public void SaveFile(final String filename) {
//        showToast(filename);
//
//            new Thread(new Runnable() {
//                public void run() {
//                    try {
//                    DrawingCanvas drawCanvas = (DrawingCanvas) findViewById(R.id.drawingCanvas);
//                    ObjectMapper mapper = new ObjectMapper();
//                    mapper.enableDefaultTyping();
//                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//                    mapper.writeValue(new File(getFilesDir(), filename), drawCanvas.shapeDB);
//                    }
//                    catch (Exception e) { e.printStackTrace(); }
//                }
//            }).start();
//    }
//
//    /**
//     * Helper function to display a toast to the user for debugging.
//     * @param text
//     */
//    private void showToast(CharSequence text) {
//        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
//        toast.show();
//    }