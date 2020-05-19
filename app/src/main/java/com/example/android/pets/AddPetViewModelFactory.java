package com.example.android.pets;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.android.pets.data.PetsDatabase;

public class AddPetViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final PetsDatabase database;
    private final int petId;

    public AddPetViewModelFactory(PetsDatabase database, int petId){
        this.database = database;
        this.petId = petId;
    }

    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass){
        return (T) new AddPetViewModel(database, petId);
    }
}
