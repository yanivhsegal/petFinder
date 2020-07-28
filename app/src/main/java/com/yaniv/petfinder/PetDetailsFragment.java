package com.yaniv.petfinder;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.yaniv.petfinder.model.Pet;


/**
 * A simple {@link Fragment} subclass.
 */
public class PetDetailsFragment extends Fragment {
    private Pet pet;
    TextView name;
    TextView id;

    public PetDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pet_details, container, false);

        name = view.findViewById(R.id.pet_details_name_tv);
        id = view.findViewById(R.id.pet_details_id_tv);

        pet = PetDetailsFragmentArgs.fromBundle(getArguments()).getPet();
        if (pet != null){
            update_display();
        }

        View closeBtn = view.findViewById(R.id.pet_details_close_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navCtrl = Navigation.findNavController(v);
                navCtrl.popBackStack();
            }
        });
        return view;
    }

    private void update_display() {
        name.setText(pet.name);
        id.setText(pet.id);
    }
}
