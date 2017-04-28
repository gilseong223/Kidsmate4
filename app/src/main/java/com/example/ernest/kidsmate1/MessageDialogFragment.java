package com.example.ernest.kidsmate1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

/**
 * Created by User on 2017-04-28.
 */

public class MessageDialogFragment extends AppCompatDialogFragment {
    public interface Listener {
        void onMessageDialogDismissed();
    }

    private static final String ARG_MESSAGE = "message";

    public static MessageDialogFragment newInstance(String message) {
        final MessageDialogFragment fragment = new MessageDialogFragment();
        final Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setMessage(getArguments().getString(ARG_MESSAGE))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Listener) getActivity()).onMessageDialogDismissed();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        ((Listener) getActivity()).onMessageDialogDismissed();
                    }
                })
                .create();
    }
}
