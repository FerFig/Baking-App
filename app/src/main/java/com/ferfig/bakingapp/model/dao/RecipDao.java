package com.ferfig.bakingapp.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.ferfig.bakingapp.model.entity.Recip;
import com.ferfig.bakingapp.utils.Utils;

import java.util.List;

@Dao
public interface RecipDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Recip> recips);

    @Query("select * from " + Utils.DB_TABLE_RECIPES)
    List<Recip> getAllRecips();

    @Query("select * from " + Utils.DB_TABLE_RECIPES + " where name like :name")
    Recip getRecipByName(String name);

    @Query("select * from " + Utils.DB_TABLE_RECIPES + " where id = :id")
    Recip getRecipById(Integer id);

    @Update
    int updateRecip(Recip recip);

    @Delete
    void deleteRecip(Recip recip);

    @Query("delete from " + Utils.DB_TABLE_RECIPES)
    void deleteAllRecips();
}
