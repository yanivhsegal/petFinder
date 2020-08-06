package com.yaniv.petfinder;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.yaniv.petfinder.model.Pet;
import com.yaniv.petfinder.model.PetModel;


public class AlertDialogFragment extends DialogFragment {
    private static final String ARG_TITLE = "TITLE";
    private static final String ARG_MESSAGE = "MESSAGE";
    private static final String ARG_PET_ID = "PETID";

    private String alertTitle;
    private String alertMessage;
    private String petId;

    public AlertDialogFragment() {
    }

    public static AlertDialogFragment newInstance(String title, String message, String PetID) {
        AlertDialogFragment fragment = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_PET_ID, PetID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            alertTitle = getArguments().getString(ARG_TITLE);
            alertMessage = getArguments().getString(ARG_MESSAGE);
            petId = getArguments().getString(ARG_PET_ID);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (alertTitle != null) {
            builder.setTitle(alertTitle);
        }
        if (alertMessage != null){
            builder.setMessage(alertMessage);
        }
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PetModel.instance.deleteById(petId);
                dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        return builder.create();
    }
}
