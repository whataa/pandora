package tech.linjiang.android.pandora.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

import tech.linjiang.android.pandora.MyApp;
import tech.linjiang.android.pandora.db.dao.DrinkDao;
import tech.linjiang.android.pandora.db.entity.Drink;


/**
 * Created by linjiang on 2018/3/13.
 */

@Database(
        entities = {
                Drink.class
        },
        version = 3,
        exportSchema = false)
public abstract class StoreDatabase extends RoomDatabase {

    public static final String TAG = "StoreDatabase";

    public abstract DrinkDao drinkDao();


    private static StoreDatabase db =
            Room.databaseBuilder(MyApp.getContext(), StoreDatabase.class, "store.db")
                    .allowMainThreadQueries()
                    .addMigrations(
                            new MIGRATE_1_2()
                    )
                    .build();

    public static StoreDatabase get() {
        return db;
    }

    private static class MIGRATE_1_2 extends Migration {

        MIGRATE_1_2() {
            super(1, 2);
        }

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    }
}
