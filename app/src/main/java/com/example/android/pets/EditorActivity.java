/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.PetEntry;
import com.example.android.pets.data.PetsDatabase;

import java.lang.ref.WeakReference;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {

    public static final String EXTRA_PET_ID = "extraPetId";

    public static final String INSTANCE_PET_ID = "instancePetid";

    private static final int DEFAULT_PET_ID = -1;

    private int mPetId = DEFAULT_PET_ID;

    private PetsDatabase mDb;

    //CHecking if the user has saved after changing or not
    private boolean mPetHasChanged = false;

    //Setting an onTouchListener to check if changes have been made
    private View.OnTouchListener mTouchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent){
            mPetHasChanged = true;
            return false;
        }
    };


    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;

    /**
     * Gender of the pet. The possible valid values are in the PetEntry.java file:
     * {@link PetEntry#GENDER_UNKNOWN}, {@link PetEntry#GENDER_MALE}, or
     * {@link PetEntry#GENDER_FEMALE}.
     */
    private int mGender = PetEntry.GENDER_UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //Initializing Database
        mDb = PetsDatabase.getInstance(getApplicationContext());

        initViews();
        setupSpinner();

        //Called if any previous stance of the activity is saved.
        //Previous instance is stored or the default value of 1 is stored.
        if(savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_PET_ID)){
            mPetId = savedInstanceState.getInt(INSTANCE_PET_ID, DEFAULT_PET_ID);
        }

        //Examine the intent that used to launch this activity
        Intent intent = getIntent();

        //If we came from catalog activity
        if(intent != null && intent.hasExtra(EXTRA_PET_ID)){

            setTitle(getString(R.string.editor_activity_title_edit_pet));

            //mPedId is initiated to DEFAULT_PET_ID at onCreate().
            if(mPetId == DEFAULT_PET_ID){
                //Populate the UI
                mPetId = intent.getIntExtra(EXTRA_PET_ID, DEFAULT_PET_ID);
                //Sent argument in factory here
                AddPetViewModelFactory factory = new AddPetViewModelFactory(mDb, mPetId);
                final AddPetViewModel viewModel =
                        ViewModelProviders.of(EditorActivity.this,
                                factory).get(AddPetViewModel.class);
                viewModel.getPet().observe((LifecycleOwner) EditorActivity.this, new Observer<PetEntry>() {
                    @Override
                    public void onChanged(@Nullable PetEntry petEntry) {
                        viewModel.getPet().removeObserver(this);
                        populateUI(petEntry);
                    }
                });
            }
        }else{
            //This is a new pet, so change the app bar to say "Add a Pet"
            setTitle(getString(R.string.editor_activity_title_new_pet));

            invalidateOptionsMenu();
        }


    }

    //Called after/before to save the instance of the activity
    @Override
    protected void onSaveInstanceState(Bundle outState){
        outState.putInt(INSTANCE_PET_ID, mPetId);
        super.onSaveInstanceState(outState);
    }

    private void initViews(){

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        //Setting onTouchListener on editTexts
        mNameEditText.setOnTouchListener(mTouchListener);
        mBreedEditText.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);

    }


    private void populateUI(PetEntry pet){
        if (pet == null){
            return;
        }

        String name  = pet.getName();
        String breed = pet.getBreed();
        int gender = pet.getGender();
        int weight = pet.getWeight();

        mNameEditText.setText(name);
        mBreedEditText.setText(breed);
        mWeightEditText.setText(Integer.toString(weight));

        switch (gender){
            case PetEntry.GENDER_MALE:
                mGenderSpinner.setSelection(1);
                break;
            case PetEntry.GENDER_FEMALE:
                mGenderSpinner.setSelection(2);
                break;
            default:
                mGenderSpinner.setSelection(0);
                break;
        }
    }

    //Sets up a discard dialog box
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        //Create an AlertDialog.Builder and set the message, and click listeners
        //for the positive and negative buttons on the dialog
        AlertDialog.Builder builder  = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int id) {
                //User clicked the "Keep editing" button, so dismiss the dialog
                //and continue editing the pet
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });

        //Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Hooking up the back button by Overriding it
    @Override
    public void onBackPressed(){
        //If the pet hasn't changed, continue with handling back button press
        if(!mPetHasChanged){
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }





    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetEntry.GENDER_MALE;
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetEntry.GENDER_FEMALE;
                    } else {
                        mGender = PetEntry.GENDER_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = PetEntry.GENDER_UNKNOWN;
            }
        });


    }

    /**
     * Get user input from editor and save new pet into database.
     */
    private void savePet() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        int weight = 0;
        String nameString = mNameEditText.getText().toString().trim();
        String breedString = mBreedEditText.getText().toString().trim();
        String weightString = mWeightEditText.getText().toString().trim();

        //Checking if null values are inserted
        if(mPetId == DEFAULT_PET_ID && TextUtils.isEmpty(nameString)&&TextUtils.isEmpty(breedString)&&TextUtils.isEmpty(weightString)&&mGender==PetEntry.GENDER_UNKNOWN){
            return;
        }

        if(!TextUtils.isEmpty(weightString)){
             weight = Integer.parseInt(weightString);
        }

        PetEntry petEntry = new PetEntry(nameString, breedString, mGender, weight);

        if(mPetId == DEFAULT_PET_ID){
            //This is a NEW PET
            new InsertPetTask(getApplicationContext()).execute(petEntry);
              // Show a toast message depending on whether or not the insertion was successful
        }else {
            //Otherwise this is an Existing pet
            petEntry.setId(mPetId);
            new UpdatePetTask(getApplicationContext()).execute(petEntry);
        }

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    //Hides delete if it is a new pet
    //This method is called after invalidateOptionsMenu(), so that the menu can be updated
    //some menu items can be hidden or made visible
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        if(mPetId == DEFAULT_PET_ID){
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                savePet();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                //If the pet hasn't changed
                // Navigate back to parent activity (CatalogActivity)
                if(!mPetHasChanged){
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet(){
// Only perform the delete if this is an existing pet.
        if (mPetId != DEFAULT_PET_ID) {
            new DeletePetTask(getApplicationContext()).execute(mPetId);
        }
        finish();
    }

    private static class InsertPetTask extends AsyncTask<PetEntry, Void, Long>{
        private final WeakReference<Context> weakAppContext;

        InsertPetTask(Context appContext){
            this.weakAppContext = new WeakReference<>(appContext);
        }

        @Override
        protected Long doInBackground(PetEntry... petEntries){
            PetsDatabase database = PetsDatabase.getInstance(weakAppContext.get());
            return database.petDao().insertPet(petEntries[0]);
        }

        @Override
        protected void onPostExecute(Long result){
            if(result != (long) -1){
                Toast.makeText(weakAppContext.get(), weakAppContext.get().getString(R.string.editor_insert_pet_successful), Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(weakAppContext.get(), weakAppContext.get().getString(R.string.editor_insert_pet_failed), Toast.LENGTH_LONG).show();
            }
        }
    }

    private static class UpdatePetTask extends AsyncTask<PetEntry, Void, Integer> {

        private final WeakReference<Context> weakAppContext;

        UpdatePetTask(Context AppContext) {
            this.weakAppContext = new WeakReference<>(AppContext);
        }

        @Override
        protected Integer doInBackground(PetEntry... petEntries) {
            PetsDatabase database = PetsDatabase.getInstance(weakAppContext.get());
            return database.petDao().updatePet(petEntries[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Show a toast message depending on whether or not the insertion was successful.
            if (result == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(weakAppContext.get(), weakAppContext.get().getString(R.string.editor_update_pet_failed),
                        Toast.LENGTH_SHORT).show();

            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(weakAppContext.get(), weakAppContext.get().getString(R.string.editor_update_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static class DeletePetTask extends AsyncTask<Integer, Void, Integer> {

        private final WeakReference<Context> weakAppContext;

        DeletePetTask(Context AppContext) {
            this.weakAppContext = new WeakReference<>(AppContext);
        }

        @Override
        protected Integer doInBackground(Integer... ids) {
            PetsDatabase database = PetsDatabase.getInstance(weakAppContext.get());
            return database.petDao().deletePet(ids[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Show a toast message depending on whether or not the insertion was successful.
            if (result == 0) {
                // If no rows were affected, then there was an error with the delete.
                Toast.makeText(weakAppContext.get(), weakAppContext.get().getString(R.string.editor_delete_pet_failed),
                        Toast.LENGTH_SHORT).show();

            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(weakAppContext.get(), weakAppContext.get().getString(R.string.editor_delete_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}