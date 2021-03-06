package com.yaniv.petfinder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.yaniv.petfinder.model.Pet;
import com.yaniv.petfinder.model.PetModel;
import com.yaniv.petfinder.model.PetTypes;
import com.yaniv.petfinder.model.User;
import com.yaniv.petfinder.model.UserModel;

import java.util.List;

public class PetListViewModel extends ViewModel {
    LiveData<List<Pet>> petsLiveData;
    LiveData<List<Pet>> myPetsLiveData;

    public LiveData<List<Pet>> getPetsData(boolean isPetsManagement, String ownerId) {
        if (!isPetsManagement) {
            if (petsLiveData == null) {
                petsLiveData = PetModel.instance.getAllPets();
            }
            return petsLiveData;
        } else {
            if (myPetsLiveData == null) {
                myPetsLiveData = PetModel.instance.getAllMyPets(ownerId);
            }
            return myPetsLiveData;
        }
    }

    public void deleteById(String petId) {
        PetModel.instance.deleteById(petId);
    }

    public void refresh(PetModel.CompListener listener) {
        PetModel.instance.refreshPetList(listener);
    }
}
