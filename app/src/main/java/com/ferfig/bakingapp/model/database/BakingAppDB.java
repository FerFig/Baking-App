package com.ferfig.bakingapp.model.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.ferfig.bakingapp.model.dao.RecipDao;
import com.ferfig.bakingapp.model.entity.Recip;
import com.ferfig.bakingapp.utils.Utils;

@Database(entities = {Recip.class}, version = 1)
public abstract class BakingAppDB extends RoomDatabase {
    private static BakingAppDB mDbInstance;

    public abstract RecipDao recipDao();

    public static BakingAppDB getInstance(Context context){
        if (mDbInstance == null)
        {
            mDbInstance = Room.databaseBuilder(context.getApplicationContext(),
                    BakingAppDB.class,
                    Utils.DATABASE_NAME)
                    .build();
        }
        return mDbInstance;
    }
}
