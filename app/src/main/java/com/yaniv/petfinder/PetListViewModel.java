package com.yaniv.petfinder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.yaniv.petfinder.model.Pet;
import com.yaniv.petfinder.model.PetModel;

import java.util.List;

public class PetListViewModel extends ViewModel {
    LiveData<List<Pet>> liveData;

    public LiveData<List<Pet>> getData() {
        if (liveData == null) {
            liveData = PetModel.instance.getAllPets();
        }
        return liveData;
    }

    public void refresh(PetModel.CompListener listener) {
        PetModel.instance.refreshPetList(listener);
    }
}
