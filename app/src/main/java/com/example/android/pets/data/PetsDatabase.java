/**
 * Here we will create an abstract class that extends RoomDatabase — this class will tie the entities and the DAOs together. We list the entities right next to the database annotation, and the DAOs will be listed inside the class:
 */
package com.example.android.pets.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {PetEntry.class}, version = 1)
public abstract class PetsDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "pets_shelter";
    private static PetsDatabase INSTANCE;

    public static PetsDatabase getInstance(Context context){
        if(INSTANCE == null){
            synchronized (LOCK){
                INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        PetsDatabase.class,
                        PetsDatabase.DATABASE_NAME)
                        .build();
            }
        }

        return INSTANCE;
    }

    public abstract PetDao petDao();
}
