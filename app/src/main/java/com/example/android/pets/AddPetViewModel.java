/**
 * AddPetViewModel.java
 * @author Aavash Sthapit
 * Now we can create our AddPetViewModel, and since we are using a factory, we won't extend from
 * AndroidViewModel but from ViewModel instead, with a member variable that represents the pet that
 * will be updated, a constructor with the appropriate call to the database to initialize it, and a
 * getter to have access to it.
 */
package com.example.android.pets;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.pets.data.PetEntry;
import com.example.android.pets.data.PetsDatabase;

public class AddPetViewModel extends ViewModel {

    private LiveData<PetEntry> pet;

    public AddPetViewModel(PetsDatabase database, int petId){
        pet = database.petDao().loadPetById(petId);
    }

    public LiveData<PetEntry> getPet(){
        return pet;
    }
}
