package com.yaniv.petfinder;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public MyDatePickerFragment() {
        // Required empty public constructor
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        DatePickerDialog picker = new DatePickerDialog(getContext(), this, 2020, 1, 1);

        return picker;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.d("TAG", "" + year +"/" + month + "/" + dayOfMonth);
    }
}
