package tech.linjiang.android.pandora.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import tech.linjiang.android.pandora.db.entity.Drink;
import tech.linjiang.android.pandora.db.entity.KeyValue;


/**
 * Created by linjiang on 2018/3/13.
 *
 */

@Dao
public interface KeyDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(KeyValue keyValue);

    @Query(value = "select * from KeyValue where `key` = :queryKey")
    KeyValue get(String queryKey);
}
