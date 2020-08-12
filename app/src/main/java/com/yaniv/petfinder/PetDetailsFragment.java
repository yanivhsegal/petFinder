package com.yaniv.petfinder;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageButton;
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
    int currentImageIndex = 0;


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

        AppCompatImageButton backButton = view.findViewById(R.id.pet_detail_image_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pet != null && pet.imgUrl != null) {
                    if (currentImageIndex == 0) {
                        currentImageIndex = pet.imgUrl.size() - 1;
                    } else {
                        currentImageIndex--;
                    }
                    if (!pet.imgUrl.get(currentImageIndex).equals("")) {
                        Picasso.get().load(pet.imgUrl.get(currentImageIndex)).placeholder(R.drawable.avatar).into(petPhoto);
                    }
                }
            }
        });

        AppCompatImageButton nextButton = view.findViewById(R.id.pet_detail_image_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pet != null && pet.imgUrl != null) {
                    if (currentImageIndex == pet.imgUrl.size() - 1) {
                        currentImageIndex = 0;
                    } else {
                        currentImageIndex++;
                    }
                    if (!pet.imgUrl.get(currentImageIndex).equals("")) {
                        Picasso.get().load(pet.imgUrl.get(currentImageIndex)).placeholder(R.drawable.avatar).into(petPhoto);
                    }
                }
            }
        });

        pet = PetDetailsFragmentArgs.fromBundle(getArguments()).getPet();
        if (pet != null) {
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
        if (pet.imgUrl != null && pet.imgUrl.get(0) != "") {
            Picasso.get().load(pet.imgUrl.get(0)).placeholder(R.drawable.avatar).into(petPhoto);
        } else {
            petPhoto.setImageResource(R.drawable.avatar);
        }
    }
}
