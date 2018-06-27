package tech.linjiang.android.pandora.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Update;

import tech.linjiang.android.pandora.db.entity.Drink;


/**
 * Created by linjiang on 2018/3/13.
 *
 */

@Dao
public interface DrinkDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(Drink... drinks);

    @Delete
    int delete(Drink... drinks);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(Drink... drinks);

}
