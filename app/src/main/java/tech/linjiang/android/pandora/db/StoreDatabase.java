package tech.linjiang.android.pandora.db;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.annotation.NonNull;

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
        version = 4,
        exportSchema = false)
public abstract class StoreDatabase extends RoomDatabase {

    public static final String TAG = "StoreDatabase";

    public static final String NAME = "store.db";

    public abstract DrinkDao drinkDao();


    private static StoreDatabase db =
            Room.databaseBuilder(MyApp.getContext(), StoreDatabase.class, NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
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
