package com.yaniv.petfinder;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.squareup.picasso.Picasso;
import com.yaniv.petfinder.model.Pet;


/**
 * A simple {@link Fragment} subclass.
 */
public class PetDetailsFragment extends Fragment {
    private Pet pet;
    TextView name;
    TextView description;
    ImageView petPhoto;

    public PetDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pet_details, container, false);

        name = view.findViewById(R.id.pet_details_name);
        description = view.findViewById(R.id.pet_details_description);
        petPhoto = view.findViewById(R.id.pet_photo);

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
        description.setText(pet.description);
        if (pet.imgUrl != null && pet.imgUrl != "") {
            Picasso.get().load(pet.imgUrl).placeholder(R.drawable.avatar).into(petPhoto);
        } else {
            petPhoto.setImageResource(R.drawable.avatar);
        }
    }
}
