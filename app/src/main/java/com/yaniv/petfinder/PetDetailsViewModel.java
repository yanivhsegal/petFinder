package com.yaniv.petfinder;

import androidx.lifecycle.ViewModel;

import com.yaniv.petfinder.model.Pet;

public class PetDetailsViewModel extends ViewModel {
    private Pet pet;

    PetDetailsViewModel(){

    }
    Pet getPet(){
        return pet;
    }
}
