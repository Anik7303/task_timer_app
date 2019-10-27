package com.anikmohammad.tasktimerapp;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * Basic Dialog class for the dialogs used in the app.
 */
public class AppDialog extends DialogFragment {
    private static final String TAG = "AppDialog";

    private DialogEvents mDialogListener = null;

    static final String DIALOG_ID = "id";
    static final String DIALOG_TITLE = "title";
    static final String DIALOG_MESSAGE = "message";
    static final String DIALOG_POSITIVE_RID = "positive_rid";
    static final String DIALOG_NEGATIVE_RID = "negative_rid";

    /**
     * The dialogue's callback interface to notify of user selected results (deletion confirmed, etc.).
     */
    public interface DialogEvents {
        void onPositiveDialogResult(int dialogId, Bundle args);
        void onNegativeDialogResult(int dialogId, Bundle args);
        void onDialogCancelled(int dialogId);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: starts with activity: " + context.toString());
        super.onAttach(context);

        // Activities containing this class have to implement the AppDialog.DialogEvents interface
        if(!(context instanceof AppDialog.DialogEvents)) {
            throw new ClassCastException(context.toString() + " has to implement AppDialog.DialogEvents interface.");
        }

        mDialogListener = (DialogEvents) context;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: starts");
        super.onDetach();
        mDialogListener = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog: starts");

        final Bundle arguments = getArguments();
        final int dialogId;
        String messageTitle;
        String messageString;
        int positiveStringId;
        int negativeStringId;

        if(arguments != null) {
            dialogId = arguments.getInt(DIALOG_ID, 0);
            messageTitle = arguments.getString(DIALOG_TITLE, null);
            messageString = arguments.getString(DIALOG_MESSAGE, null);

            if(dialogId == 0 || messageString == null) {
                throw new IllegalArgumentException("DIALOG_ID and/or DIALOG_MESSAGE is not present in the bundle");
            }

            positiveStringId = arguments.getInt(DIALOG_POSITIVE_RID, 0);
            if(positiveStringId == 0) {
                positiveStringId = R.string.ok;
            }
            negativeStringId = arguments.getInt(DIALOG_NEGATIVE_RID, 0);
            if(negativeStringId == 0) {
                negativeStringId = R.string.cancel;
            }
        } else {
            throw new IllegalArgumentException("Must pass DIALOG_ID and DIALOG_MESSAGE in the bundle");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle(messageTitle)
                .setMessage(messageString)
                .setPositiveButton(positiveStringId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // callback positive result method
                        if(mDialogListener != null) {
                            mDialogListener.onPositiveDialogResult(dialogId, arguments);
                        }
                    }
                })
                .setNegativeButton(negativeStringId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // callback negative result method
                        if(mDialogListener != null) {
                            mDialogListener.onNegativeDialogResult(dialogId, arguments);
                        }
                    }
                });
        return builder.create();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        Log.d(TAG, "onCancel: starts");
        if(mDialogListener != null) {
            int dialogId = getArguments().getInt(DIALOG_ID);
            mDialogListener.onDialogCancelled(dialogId);
        }
    }
}
