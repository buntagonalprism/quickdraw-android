package com.example.android.quickdraw;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by t on 27/4/15.
 */
public class FileOpenDialog extends DialogFragment{

    private ListView mFileList;

    private FileOpenDialogListener mFileOpenDialogListener = null;

    private ArrayAdapter<String> mFilesAdapter = null;

    private String mFileToOpen = null;

    private String mFileRoot = null;

    private TextView mFileSelectedView = null;

    public interface FileOpenDialogListener {
        public void onFileOpenSelected(final String filename);
        public void onFileOpenCancelled(String filename);
    }

    // Allows any parent activities creating this dialog to define their own listener
    // classes which will be called when buttons are selected here.
    public void setListener(FileOpenDialogListener listener) {
        mFileOpenDialogListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mFileRoot = bundle.getString("fileRoot");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Builder creates the alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflater is required to inflate XML layout for the dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate the XML into the contents view of the dialog
        View view = inflater.inflate(R.layout.dialog_fileopen, null);

        // Get a reference to the EditText from the parent view
        mFileList = (ListView) view.findViewById(R.id.filebrowser);

        // Get the root directory to search from the bundle passed to the dialog on creation
        //String fileRoot = getArguments().getString("fileRoot");

        // Get the file list and put them in an array adapter
        File[] files = new File(mFileRoot).listFiles();
        mFilesAdapter = new ArrayAdapter<String>(getActivity(), R.layout.openfile_entry, new ArrayList<String>());
        for (File file : files) {
            mFilesAdapter.add(file.getName().toString());
        }

        // Give the array adapter to the list view to display the file list
        mFileList.setAdapter(mFilesAdapter);

        // Get the view which displays the selected file
        mFileSelectedView = (TextView) view.findViewById(R.id.file_to_open);

        // Implement the onItem click listener on the array list
        // We want onItemSelectedListener, not onSelectedListener. The first fires when a certain
        // option was selected, and tells us which one. The second only tells us the entire list has
        // been clicked, which may be useful if we want to refresh the list when it is touched.
        // We use itemClick rather than itemSelected because onItem fires every time the same item
        // is pressed. itemSelected only occurs when the new position is different to the old.
        // Generally onItem is used for ListViews, and onSelected for Spinners
        mFileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mFileToOpen = mFilesAdapter.getItem(position);
                mFileSelectedView.setText(mFileToOpen);
            }
        });
        // Set the dialog builder to contain the view we have inflated
        builder.setView(view);

        // Title and bottom buttons of the dialog are specified by the builder rather than xml
        builder.setMessage("Select file");

        // Pass data back to the main activity by calling the save file function
        builder.setPositiveButton("Open", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (mFileOpenDialogListener != null)
                    mFileOpenDialogListener.onFileOpenSelected(mFileToOpen);
            }
        });
        // Pass data back to activity using the interface created in the main function
        // This is more robust as it does not require the test for main activity
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (mFileOpenDialogListener != null)
                    mFileOpenDialogListener.onFileOpenCancelled("Yep, cancelled all right");
            }
        });
        return builder.create();
    }
}
