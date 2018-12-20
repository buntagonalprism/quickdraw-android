package com.example.android.quickdraw;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by t on 26/4/15.
 */
public class FileSaveDialog extends DialogFragment {

    private EditText mEditText;

    private FileSaveDialogListener mFileSaveDialogListener = null;

    public interface FileSaveDialogListener {
        public void onFileSaveSelected(String filename);
        public void onFileSaveCancelled();
    }

    // Allows any parent activities creating this dialog to define their own listener
    // classes which will be called when buttons are selected here.
    public void setListener(FileSaveDialogListener listener) {
        mFileSaveDialogListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Builder creates the alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflater is required to inflate XML layout for the dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate the XML into the contents view of the dialog
        View view = inflater.inflate(R.layout.dialog_filesave, null);

        // Get a reference to the EditText from the parent view
        mEditText = (EditText) view.findViewById(R.id.filename);

        // Set the dialog builder to contain the view we have inflated
        builder.setView(view);

        // Title and bottom buttons of the dialog are specified by the builder rather than xml
        builder.setMessage("Enter file name");

        // Pass data back to the main activity by calling the save file function
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (mFileSaveDialogListener != null)
                    mFileSaveDialogListener.onFileSaveSelected(mEditText.getText().toString());
            }
        });
        // Pass data back to activity using the interface created in the main function
        // This is more robust as it does not require the test for main activity
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (mFileSaveDialogListener != null)
                mFileSaveDialogListener.onFileSaveCancelled();
            }
        });
        return builder.create();
    }
}
