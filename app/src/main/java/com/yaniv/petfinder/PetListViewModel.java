package com.yaniv.petfinder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.yaniv.petfinder.model.Pet;
import com.yaniv.petfinder.model.PetModel;
import com.yaniv.petfinder.model.User;
import com.yaniv.petfinder.model.UserModel;

import java.util.List;

public class PetListViewModel extends ViewModel {
    LiveData<List<Pet>> petsLiveData;
    LiveData<User> userLiveData;

    public LiveData<List<Pet>> getPetsData() {
        if (petsLiveData == null) {
            petsLiveData = PetModel.instance.getAllPets();
        }
        return petsLiveData;
    }

    public LiveData<User> getUserData() {
        if (userLiveData == null) {

        }
        return userLiveData;
    }

    public void refresh(PetModel.CompListener listener) {
        PetModel.instance.refreshPetList(listener);
    }
}
