/**
 * AddPetViewModelFactory.java
 * @author Aavash Sthapit
 * As you can see in the PetDao when we want to update a Pet, we need to call the
 * loadPetById method, which requires the id of the pet of a parameter. Therefore,
 * the ViewModel for the EditorActivity will need the id of the Pet, so we need to pass this value
 * to the ViewModel. For that, we need to create a ViewModel factory.
 * We can not create ViewModel on our own. We need ViewModelProviders utility provided by Android to create ViewModels.
 *
 * But ViewModelProviders can only instantiate ViewModels with no arg constructor.
 *
 * So if I have a ViewModel with multiple arguments, then I need to use a Factory that I can pass to
 * ViewModelProviders to use when an instance of MyViewModel is required.
 *
 * Argument is sent here.
 */
package com.example.android.pets;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.android.pets.data.PetsDatabase;

public class AddPetViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final PetsDatabase database;
    private final int petId;

    /**
     * Constructor to initialize those member variables
     * @param database instance
     * @param petId to be edited
     */
    public AddPetViewModelFactory(PetsDatabase database, int petId){
        this.database = database;
        this.petId = petId;
    }

    /**
     * @param modelClass to be used.
     * @param <T> required class
     * @return AddTaskViewModel that uses our parameters in the constructor.
     */
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass){
        return (T) new AddPetViewModel(database, petId);
    }
}
