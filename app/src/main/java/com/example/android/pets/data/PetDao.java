package com.example.android.pets.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

//Data Access Object or DAO
@Dao
public interface PetDao {

    //We need to create a new interface — let’s call it PetDao. Add the @Dao annotation and then we need to define the methods we need. For insert, update, or delete (if done passing an object, not just an id) we don’t need to type in any query, for example, the insert looks like this:
    @Insert
    long insertPet(PetEntry petEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updatePet(PetEntry petEntry);

    @Query("DELETE FROM pet WHERE id = :id")
    int deletePet(int id);

//    To get a Pet from a particular id, a list of PetEntries, delete by id, or delete all the PetEntries on the table, we need to use the @Query
//    annotation, for example:
    @Query("SELECT * FROM pet")
    LiveData<List<PetEntry>> loadAllPets();

    @Query("SELECT * FROM pet WHERE id = :id")
    LiveData<PetEntry> loadPetById(int id);

    @Query("DELETE FROM pet")
    void deleteAllPets();
}
